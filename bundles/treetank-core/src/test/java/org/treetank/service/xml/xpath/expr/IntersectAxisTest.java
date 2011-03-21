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
 * $Id: IntersectAxisTest.java 4417 2008-08-27 21:19:26Z scherer $
 */
package org.treetank.service.xml.xpath.expr;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import org.treetank.TestHelper;
import org.treetank.TestHelper.PATHS;
import org.treetank.api.IDatabase;
import org.treetank.api.IReadTransaction;
import org.treetank.api.ISession;
import org.treetank.api.IWriteTransaction;
import org.treetank.axis.AbsAxisTest;
import org.treetank.exception.AbsTTException;
import org.treetank.service.xml.xpath.XPathAxis;
import org.treetank.utils.DocumentCreater;

/**
 * JUnit-test class to test the functionality of the UnionAxis.
 * 
 * @author Tina Scherer
 * 
 */
public class IntersectAxisTest {

    @Before
    public void setUp() throws AbsTTException {

        TestHelper.deleteEverything();
    }

    @After
    public void tearDown() throws AbsTTException {
        TestHelper.closeEverything();
    }

    @Test
    public void testIntersect() throws AbsTTException {
        // Build simple test tree.
        final IDatabase database = TestHelper.getDatabase(PATHS.PATH1.getFile());
        final ISession session = database.getSession();
        final IWriteTransaction wtx = session.beginWriteTransaction();
        DocumentCreater.create(wtx);
        wtx.commit();
        IReadTransaction rtx = session.beginReadTransaction();

        rtx.moveTo(1L);

        AbsAxisTest.testIAxisConventions(new XPathAxis(rtx, "child::node() intersect b"), new long[] {
            5L, 9L
        });

        AbsAxisTest.testIAxisConventions(new XPathAxis(rtx,
            "child::node() intersect b intersect child::node()[@p:x]"), new long[] {
            9L
        });

        AbsAxisTest.testIAxisConventions(new XPathAxis(rtx,
            "child::node() intersect child::node()[attribute::p:x]"), new long[] {
            9L
        });

        AbsAxisTest.testIAxisConventions(new XPathAxis(rtx,
            "child::node()/parent::node() intersect self::node()"), new long[] {
            1L
        });

        AbsAxisTest.testIAxisConventions(new XPathAxis(rtx, "//node() intersect //text()"), new long[] {
            4L, 8L, 13L, 6L, 12L
        });

        rtx.moveTo(1L);
        AbsAxisTest.testIAxisConventions(new XPathAxis(rtx, "b/preceding::node() intersect text()"),
            new long[] {
                4L, 8L
            });

        rtx.close();
        wtx.abort();
        wtx.close();
        session.close();
        database.close();
    }

}