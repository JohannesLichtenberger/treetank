/*
 * Copyright (c) 2008, Tina Scherer (Master Thesis), University of Konstanz
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
 * $Id: UnionAxisTest.java 4417 2008-08-27 21:19:26Z scherer $
 */

package com.treetank.service.xml.xpath.expr;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.treetank.TestHelper;
import com.treetank.TestHelper.PATHS;
import com.treetank.api.IDatabase;
import com.treetank.api.IReadTransaction;
import com.treetank.api.ISession;
import com.treetank.api.IWriteTransaction;
import com.treetank.axis.IAxisTest;
import com.treetank.exception.TreetankException;
import com.treetank.service.xml.xpath.XPathAxis;
import com.treetank.utils.DocumentCreater;

/**
 * JUnit-test class to test the functionality of the UnionAxis.
 * 
 * @author Tina Scherer
 * 
 */
public class UnionAxisTest {

    @Before
    public void setUp() throws TreetankException {

        TestHelper.deleteEverything();
    }

    @After
    public void tearDown() throws TreetankException {
        TestHelper.closeEverything();
    }

    @Test
    public void testUnion() throws TreetankException {
        // Build simple test tree.
        final IDatabase database = TestHelper
                .getDatabase(PATHS.PATH1.getFile());
        final ISession session = database.getSession();
        final IWriteTransaction wtx = session.beginWriteTransaction();
        DocumentCreater.create(wtx);
        wtx.commit();
        IReadTransaction rtx = session.beginReadTransaction();

        rtx.moveTo(1L);

        IAxisTest.testIAxisConventions(new XPathAxis(rtx,
                "child::node()/parent::node() union child::node()"),
                new long[] { 1L, 4L, 5L, 8L, 9L, 13L });

        IAxisTest.testIAxisConventions(new XPathAxis(rtx,
                "child::node()/parent::node() | child::node()"), new long[] {
                1L, 4L, 5L, 8L, 9L, 13L });

        IAxisTest.testIAxisConventions(new XPathAxis(rtx,
                "child::node()/parent::node() | child::node() | self::node()"),
                new long[] { 1L, 4L, 5L, 8L, 9L, 13L });

        IAxisTest.testIAxisConventions(new XPathAxis(rtx,
                "child::node()/parent::node() | child::node() | self::node()"
                        + "union parent::node()"), new long[] { 1L, 4L, 5L, 8L,
                9L, 13L, 0L });

        IAxisTest.testIAxisConventions(new XPathAxis(rtx,
                "b/preceding::node() union text() | descendant::node()"),
                new long[] { 4L, 8L, 7L, 6L, 5L, 13L, 9L, 11L, 12L });

        IAxisTest.testIAxisConventions(new XPathAxis(rtx,
                "//c/ancestor::node() | //node()"), new long[] { 5L, 1L, 9L,
                4L, 8L, 13L, 6L, 7L, 11L, 12L });

        rtx.close();
        wtx.abort();
        wtx.close();
        session.close();
        database.close();
    }

}
