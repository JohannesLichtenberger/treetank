/*
 * Copyright (c) 2007, Marc Kramis
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
 * $Id: XPathStringChecker.java 4362 2008-08-24 11:46:16Z kramis $
 */

package com.treetank.service.xml.xpath;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;

import com.treetank.TestHelper;
import com.treetank.api.IAxis;
import com.treetank.api.IReadTransaction;
import com.treetank.exception.TreetankException;
import com.treetank.utils.TypedValue;

public class XPathStringChecker {

    @Before
    public void setUp() throws TreetankException {
        TestHelper.deleteEverything();
    }

    @After
    public void tearDown() throws TreetankException {
        TestHelper.closeEverything();
    }

    public static void testIAxisConventions(final IAxis axis,
            final String[] expectedValues) {

        final IReadTransaction rtx = axis.getTransaction();

        // IAxis Convention 1.
        final long startKey = rtx.getNode().getNodeKey();

        final String[] strValues = new String[expectedValues.length];
        int offset = 0;
        while (axis.hasNext()) {
            axis.next();
            // IAxis results.
            if (offset >= expectedValues.length) {
                fail("More nodes found than expected.");
            }
            strValues[offset++] = TypedValue.parseString(rtx.getNode()
                    .getRawValue());

            // IAxis Convention 2.
            try {
                axis.next();
                fail("Should only allow to call next() once.");
            } catch (Exception e) {
                // Must throw exception.
            }

            // IAxis Convention 3.
            rtx.moveToDocumentRoot();

        }

        // IAxis Convention 5.
        assertEquals(startKey, rtx.getNode().getNodeKey());

        // IAxis results.
        assertArrayEquals(expectedValues, strValues);

    }

}