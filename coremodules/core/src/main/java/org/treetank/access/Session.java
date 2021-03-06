/**
 * Copyright (c) 2011, University of Konstanz, Distributed Systems Group
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of the University of Konstanz nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.treetank.access;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.treetank.access.conf.ResourceConfiguration;
import org.treetank.access.conf.SessionConfiguration;
import org.treetank.api.INodeReadTrx;
import org.treetank.api.INodeWriteTrx;
import org.treetank.api.IPageReadTrx;
import org.treetank.api.IPageWriteTrx;
import org.treetank.api.ISession;
import org.treetank.exception.AbsTTException;
import org.treetank.exception.TTIOException;
import org.treetank.io.EStorage;
import org.treetank.io.IReader;
import org.treetank.io.IStorage;
import org.treetank.io.IWriter;
import org.treetank.page.PageReference;
import org.treetank.page.UberPage;

/**
 * <h1>Session</h1>
 * 
 * <p>
 * Makes sure that there only is a single session instance bound to a TreeTank
 * file.
 * </p>
 */
public final class Session implements ISession {

    /** Session configuration. */
    protected final ResourceConfiguration mResourceConfig;

    /** Session configuration. */
    protected final SessionConfiguration mSessionConfig;

    /** Database for centralized closure of related Sessions. */
    private final Database mDatabase;

    /** Strong reference to uber page before the begin of a write transaction. */
    private UberPage mLastCommittedUberPage;

    /** Remember the write seperatly because of the concurrent writes. */
    private final Set<IPageReadTrx> mPageTrxs;

    /** abstract factory for all interaction to the storage. */
    private final IStorage mFac;

    /** Determines if session was closed. */
    private transient boolean mClosed;

    /**
     * Hidden constructor.
     * 
     * @param paramDatabase
     *            Database for centralized operations on related sessions.
     * @param paramDatabaseConf
     *            DatabaseConfiguration for general setting about the storage
     * @param paramSessionConf
     *            SessionConfiguration for handling this specific session
     * @throws AbsTTException
     *             Exception if something weird happens
     */
    protected Session(final Database paramDatabase,
            final ResourceConfiguration paramResourceConf,
            final SessionConfiguration paramSessionConf) throws AbsTTException {
        mDatabase = paramDatabase;
        mResourceConfig = paramResourceConf;
        mSessionConfig = paramSessionConf;
        mPageTrxs = new CopyOnWriteArraySet<IPageReadTrx>();

        mFac = EStorage.getStorage(mResourceConfig);
        if (!mFac.exists()) {
            // Bootstrap uber page and make sure there already is a root
            // node.
            mLastCommittedUberPage = new UberPage();
        } else {
            final IReader reader = mFac.getReader();
            final PageReference firstRef = reader.readFirstReference();
            mLastCommittedUberPage = (UberPage) firstRef.getPage();
            reader.close();
        }
        mClosed = false;
    }

    /**
     * {@inheritDoc}
     */
    public INodeReadTrx beginNodeReadTransaction() throws AbsTTException {
        return beginNodeReadTransaction(mLastCommittedUberPage
                .getRevisionNumber());
    }

    /**
     * {@inheritDoc}
     */
    public synchronized INodeReadTrx beginNodeReadTransaction(final long pRevKey)
            throws AbsTTException {
        return new NodeReadTrx(beginPageReadTransaction(pRevKey));
    }

    public IPageReadTrx beginPageReadTransaction(final long pRevKey)
            throws AbsTTException {
        assertAccess(pRevKey);
        final PageReadTrx trx = new PageReadTrx(this, mLastCommittedUberPage,
                pRevKey, mFac.getReader());
        mPageTrxs.add(trx);
        return trx;
    }

    public IPageWriteTrx beginPageWriteTransaction() throws AbsTTException {
        assertAccess(mLastCommittedUberPage.getRevision());

        final IPageWriteTrx trx = beginPageWriteTransaction(
                mLastCommittedUberPage.getRevisionNumber(),
                mLastCommittedUberPage.getRevisionNumber());
        mPageTrxs.add(trx);

        return trx;

    }

    /**
     * {@inheritDoc}
     */
    public synchronized INodeWriteTrx beginNodeWriteTransaction()
            throws AbsTTException {

        // Create new write transaction.
        final INodeWriteTrx wtx = new NodeWriteTrx(this,
                beginPageWriteTransaction());

        return wtx;

    }

    protected IPageWriteTrx beginPageWriteTransaction(
            final long mRepresentRevision, final long mStoreRevision)
            throws TTIOException {
        final IWriter writer = mFac.getWriter();

        return new PageWriteTrx(this, new UberPage(mLastCommittedUberPage,
                mStoreRevision + 1), writer, mRepresentRevision, mStoreRevision);
    }

    protected void deregisterTrx(final IPageReadTrx pReadTrx) {
        mPageTrxs.remove(pReadTrx);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void close() throws AbsTTException {
        if (!mClosed) {
            // Forcibly close all open transactions.
            for (final IPageReadTrx rtx : mPageTrxs) {
                rtx.close();
            }

            // Immediately release all ressources.
            mLastCommittedUberPage = null;
            mPageTrxs.clear();

            mFac.close();
            mDatabase.removeSession(mResourceConfig.mPath);
            mClosed = true;
        }
    }

    /**
     * Checks for valid revision.
     * 
     * @param paramRevision
     *            revision parameter to check
     * @throws IllegalArgumentException
     *             if revision isn't valid
     */
    protected void assertAccess(final long paramRevision) {
        if (mClosed) {
            throw new IllegalStateException("Session is already closed.");
        }
        if (paramRevision < 0) {
            throw new IllegalArgumentException("Revision must be at least 0");
        } else if (paramRevision > mLastCommittedUberPage.getRevision()) {
            throw new IllegalArgumentException(new StringBuilder(
                    "Revision must not be bigger than").append(
                    Long.toString(mLastCommittedUberPage.getRevision()))
                    .toString());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.mSessionConfig);
        return builder.toString();
    }

    protected void setLastCommittedUberPage(final UberPage paramPage) {
        this.mLastCommittedUberPage = paramPage;
    }
}
