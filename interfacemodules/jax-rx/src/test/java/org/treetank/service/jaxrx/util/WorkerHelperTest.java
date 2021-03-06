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

package org.treetank.service.jaxrx.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.treetank.TestHelper;
import org.treetank.access.Database;
import org.treetank.access.conf.SessionConfiguration;
import org.treetank.api.IDatabase;
import org.treetank.api.INodeReadTrx;
import org.treetank.api.INodeWriteTrx;
import org.treetank.api.ISession;
import org.treetank.exception.AbsTTException;
import org.treetank.service.jaxrx.implementation.DatabaseRepresentation;
import org.treetank.service.xml.shredder.EShredderInsert;

/**
 * This class is responsible to test the {@link WorkerHelper} class.
 * 
 * @author Patrick Lang, Lukas Lewandowski, University of Konstanz
 * 
 */
public class WorkerHelperTest {
    /**
     * The WorkerHelper reference.
     */
    private transient static WorkerHelper workerHelper;
    /**
     * The Treetank reference.
     */
    private transient static DatabaseRepresentation treeTank;
    /**
     * The resource name.
     */
    private static final transient String RESOURCENAME = "factyTest";
    /**
     * The test file that has to be saved on the server.
     */
    private final static File DBFILE = new File(TestHelper.PATHS.PATH1.getFile(), RESOURCENAME);

    /**
     * The test file that has to be saved on the server.
     */
    private final transient InputStream INPUTFILE = WorkerHelperTest.class.getClass().getResourceAsStream(
        "/factbook.xml");

    /**
     * A simple set up.
     * 
     * @throws FileNotFoundException
     */
    @Before
    public void setUp() throws FileNotFoundException, AbsTTException {
        TestHelper.closeEverything();
        TestHelper.deleteEverything();
        TestHelper.getDatabase(TestHelper.PATHS.PATH1.getFile());
        workerHelper = WorkerHelper.getInstance();
        treeTank = new DatabaseRepresentation(TestHelper.PATHS.PATH1.getFile());
        treeTank.shred(INPUTFILE, RESOURCENAME);
    }

    @After
    public void after() throws AbsTTException {
        TestHelper.closeEverything();
        TestHelper.deleteEverything();
    }

    /**
     * This method tests {@link WorkerHelper#checkExistingResource(File)}
     */
    @Test
    public void testCheckExistingResource() {
        assertEquals("test check existing resource", true, WorkerHelper.checkExistingResource(
            TestHelper.PATHS.PATH1.getFile(), RESOURCENAME));
    }

    /**
     * This method tests {@link WorkerHelper#createStringBuilderObject()}
     */
    @Test
    public void testCreateStringBuilderObject() {
        assertNotNull("test create string builder object", workerHelper.createStringBuilderObject());
    }

    /**
     * This method tests {@link WorkerHelper#serializeXML(ISession, OutputStream, boolean, boolean,Long)}
     */
    @Test
    public void testSerializeXML() throws AbsTTException, IOException {
        final IDatabase database = Database.openDatabase(DBFILE.getParentFile());
        final ISession session =
            database.getSession(new SessionConfiguration.Builder(DBFILE.getName()).build());
        final OutputStream out = new ByteArrayOutputStream();

        assertNotNull("test serialize xml", WorkerHelper.serializeXML(session, out, true, true, null));
        session.close();
        database.close();
        out.close();
    }

    /**
     * This method tests {@link WorkerHelper#shredInputStream(INodeWriteTrx, InputStream, EShredderInsert)}
     */
    @Test
    public void testShredInputStream() throws AbsTTException, IOException {

        long lastRevision = treeTank.getLastRevision(RESOURCENAME);

        final IDatabase database = Database.openDatabase(DBFILE.getParentFile());
        final ISession session =
            database.getSession(new SessionConfiguration.Builder(DBFILE.getName()).build());
        final INodeWriteTrx wtx = session.beginNodeWriteTransaction();

        final InputStream inputStream = new ByteArrayInputStream("<testNode/>".getBytes());

        WorkerHelper.shredInputStream(wtx, inputStream, EShredderInsert.ADDASFIRSTCHILD);

        assertEquals("test shred input stream", treeTank.getLastRevision(RESOURCENAME), ++lastRevision);
        wtx.close();
        session.close();
        database.close();
        inputStream.close();
    }

    /**
     * This method tests {@link WorkerHelper#closeWTX(boolean, INodeWriteTrx, ISession, IDatabase)}
     */
    @Test(expected = IllegalStateException.class)
    public void testClose() throws AbsTTException {
        IDatabase database = Database.openDatabase(DBFILE.getParentFile());
        ISession session = database.getSession(new SessionConfiguration.Builder(DBFILE.getName()).build());
        final INodeWriteTrx wtx = session.beginNodeWriteTransaction();

        WorkerHelper.closeWTX(false, wtx, session, database);

        wtx.commit();

        database = Database.openDatabase(DBFILE.getParentFile());
        session = database.getSession(new SessionConfiguration.Builder(DBFILE.getName()).build());
        final INodeReadTrx rtx = session.beginNodeReadTransaction();
        WorkerHelper.closeRTX(rtx, session, database);

        rtx.moveTo(11);

    }

}
