/**
 * Copyright (c) 2010, Distributed Systems Group, University of Konstanz
 * 
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 * 
 * THE SOFTWARE IS PROVIDED AS IS AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 * 
 */
package org.treetank.gui;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treetank.access.Database;
import org.treetank.access.DatabaseConfiguration;
import org.treetank.api.IDatabase;
import org.treetank.api.ISession;
import org.treetank.api.IWriteTransaction;
import org.treetank.exception.AbsTTException;
import org.treetank.service.xml.shredder.EShredderCommit;
import org.treetank.service.xml.shredder.EShredderInsert;
import org.treetank.service.xml.shredder.XMLShredder;
import org.treetank.service.xml.shredder.XMLUpdateShredder;

/**
 * Determines how to shred.
 * 
 * @author Johannes Lichtenberger, University of Konstanz
 * 
 */
enum EShredder {

    /** Determines normal shredding. */
    NORMAL {
        @Override
        boolean shred(final File paramSource, final File paramTarget) {
            boolean retVal = true;
            try {
                Database.truncateDatabase(paramTarget);
                Database.createDatabase(new DatabaseConfiguration(paramTarget));
                final IDatabase database = Database.openDatabase(paramTarget);
                final ISession session = database.getSession();
                final IWriteTransaction wtx = session.beginWriteTransaction();

                final XMLEventReader reader = XMLShredder.createReader(paramSource);
                final ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.submit(new XMLShredder(wtx, reader, EShredderInsert.ADDASFIRSTCHILD));
                executor.shutdown();
                executor.awaitTermination(5, TimeUnit.SECONDS);
                
                wtx.close();
                session.close();
                database.close();
            } catch (final InterruptedException e) {
                LOGWRAPPER.error(e.getMessage(), e);
                retVal = false;
            } catch (final AbsTTException e) {
                LOGWRAPPER.error(e.getMessage(), e);
                retVal = false;
            } catch (final IOException e) {
                LOGWRAPPER.error(e.getMessage(), e);
                retVal = false;
            } catch (final XMLStreamException e) {
                LOGWRAPPER.error(e.getMessage(), e);
                retVal = false;
            }

            return retVal;
        }
    },

    /** Determines update only shredding. */
    UPDATEONLY {
        @Override
        boolean shred(final File paramSource, final File paramTarget) {
            boolean retVal = true;
            try {
                final IDatabase database = Database.openDatabase(paramTarget);
                final ISession session = database.getSession();
                final IWriteTransaction wtx = session.beginWriteTransaction();

                final XMLEventReader reader = XMLShredder.createReader(paramSource);
                final ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.submit(new XMLUpdateShredder(wtx, reader, EShredderInsert.ADDASFIRSTCHILD,
                    paramSource, EShredderCommit.COMMIT));
                executor.shutdown();
                executor.awaitTermination(5, TimeUnit.SECONDS);
                
                wtx.close();  
                session.close();
                database.close();
            } catch (final InterruptedException e) {
                LOGWRAPPER.error(e.getMessage(), e);
                retVal = false;
            } catch (final AbsTTException e) {
                LOGWRAPPER.error(e.getMessage(), e);
                retVal = false;
            } catch (final IOException e) {
                LOGWRAPPER.error(e.getMessage(), e);
                retVal = false;
            } catch (final XMLStreamException e) {
                LOGWRAPPER.error(e.getMessage(), e);
                retVal = false;
            }

            return retVal;
        }
    };

    /** Logger. */
    private static final Logger LOGWRAPPER = LoggerFactory.getLogger(EShredder.class);

    /**
     * Shred XML file.
     * 
     * @param paramSource
     *            source XML file
     * @param paramTarget
     *            target folder
     * @return true if successfully shreddered, false otherwise
     */
    abstract boolean shred(final File paramSource, final File paramTarget);
}