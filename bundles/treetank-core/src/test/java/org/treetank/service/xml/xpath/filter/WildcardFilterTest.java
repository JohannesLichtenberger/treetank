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
 * $Id: WildcardFilterTest.java 4417 2008-08-27 21:19:26Z scherer $
 */

package org.treetank.service.xml.xpath.filter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import org.treetank.TestHelper;
import org.treetank.TestHelper.PATHS;
import org.treetank.api.IDatabase;
import org.treetank.api.ISession;
import org.treetank.api.IWriteTransaction;
import org.treetank.axis.filter.IFilterTest;
import org.treetank.axis.filter.WildcardFilter;
import org.treetank.exception.AbsTTException;
import org.treetank.exception.TTIOException;
import org.treetank.utils.DocumentCreater;

public class WildcardFilterTest {

    @Before
    public void setUp() throws AbsTTException {
        TestHelper.deleteEverything();
    }

    @After
    public void tearDown() throws AbsTTException {
        TestHelper.closeEverything();
    }

    @Test
    public void testIFilterConvetions() throws AbsTTException {
        try {
            // Build simple test tree.
            final IDatabase database = TestHelper.getDatabase(PATHS.PATH1.getFile());
            final ISession session = database.getSession();
            final IWriteTransaction wtx = session.beginWriteTransaction();
            DocumentCreater.create(wtx);

            wtx.moveTo(9L);
            IFilterTest.testIFilterConventions(new WildcardFilter(wtx, "b", true), true);
            wtx.moveToAttribute(0);
            try {
                IFilterTest.testIFilterConventions(new WildcardFilter(wtx, "p", false), true);
                fail("Expected an Exception, because attributes are not supported.");
            } catch (IllegalStateException e) {
                assertThat(e.getMessage(), is("Wildcards are not supported in attribute names yet."));

            }
            // IFilterTest.testIFilterConventions(new WildcardFilter(wtx, "b",
            // true), true);

            // wtx.moveTo(3L);
            // IFilterTest.testIFilterConventions(new ItemFilter(wtx), true);

            wtx.moveTo(1L);
            IFilterTest.testIFilterConventions(new WildcardFilter(wtx, "p", false), true);
            IFilterTest.testIFilterConventions(new WildcardFilter(wtx, "a", true), true);
            IFilterTest.testIFilterConventions(new WildcardFilter(wtx, "c", true), false);
            IFilterTest.testIFilterConventions(new WildcardFilter(wtx, "b", false), false);

            wtx.abort();
            wtx.close();
            session.close();
            database.close();
        } catch (final TTIOException exc) {
            fail(exc.toString());
        }

    }
}