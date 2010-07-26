/*
 * Copyright (c) 2008, Marc Kramis (Ph.D. Thesis), University of Konstanz
 * 
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 * 
 * $Id: ValueFilterTest.java 4417 2008-08-27 21:19:26Z scherer $
 */

package com.treetank.axis;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.treetank.TestHelper;
import com.treetank.TestHelper.PATHS;
import com.treetank.api.IDatabase;
import com.treetank.api.ISession;
import com.treetank.api.IWriteTransaction;
import com.treetank.exception.TreetankException;
import com.treetank.utils.DocumentCreater;

public class ValueFilterTest {

    @Before
    public void setUp() throws TreetankException {
        TestHelper.deleteEverything();
    }

    @Test
    public void testIFilterConvetions() throws TreetankException {
        // Build simple test tree.
        final IDatabase database = TestHelper.getDatabase(PATHS.PATH1.getFile());
        final ISession session = database.getSession();
        final IWriteTransaction wtx = session.beginWriteTransaction();
        DocumentCreater.create(wtx);

        wtx.moveTo(4L);
        IFilterTest.testIFilterConventions(new ValueFilter(wtx, "oops1"), true);
        IFilterTest.testIFilterConventions(new ValueFilter(wtx, "foo"), false);

        wtx.moveTo(1L);
        wtx.moveToAttribute(0);
        IFilterTest.testIFilterConventions(new ValueFilter(wtx, "j"), true);

        wtx.moveTo(2L);
        IFilterTest.testIFilterConventions(new ValueFilter(wtx, "j"), true);

        wtx.abort();
        wtx.close();
        session.close();
        database.close();
    }

    @After
    public void tearDown() throws TreetankException {
        TestHelper.closeEverything();
    }

}
