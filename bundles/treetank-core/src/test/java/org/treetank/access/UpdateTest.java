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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.xml.namespace.QName;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.treetank.Holder;
import org.treetank.TestHelper;
import org.treetank.api.IReadTransaction;
import org.treetank.api.IWriteTransaction;
import org.treetank.exception.AbsTTException;
import org.treetank.exception.TTUsageException;
import org.treetank.node.AbsStructNode;
import org.treetank.settings.EFixed;
import org.treetank.utils.DocumentCreater;
import org.treetank.utils.TypedValue;

public class UpdateTest {

    private Holder holder;

    @Before
    public void setUp() throws AbsTTException {
        TestHelper.deleteEverything();
        holder = Holder.generateSession();
    }

    @After
    public void tearDown() throws AbsTTException {
        TestHelper.closeEverything();
    }

    @Test
    public void testInsertChild() throws AbsTTException {

        IWriteTransaction wtx = holder.getSession().beginWriteTransaction();
        wtx.commit();
        wtx.close();

        IReadTransaction rtx = holder.getSession().beginReadTransaction();
        assertEquals(0L, rtx.getRevisionNumber());
        rtx.close();

        // Insert 100 children.
        for (int i = 1; i <= 10; i++) {
            wtx = holder.getSession().beginWriteTransaction();
            wtx.moveToDocumentRoot();
            wtx.insertTextAsFirstChild(Integer.toString(i));
            wtx.commit();
            wtx.close();

            rtx = holder.getSession().beginReadTransaction();
            rtx.moveToDocumentRoot();
            rtx.moveToFirstChild();
            assertEquals(Integer.toString(i), TypedValue.parseString(rtx.getNode().getRawValue()));
            assertEquals(i, rtx.getRevisionNumber());
            rtx.close();
        }

        rtx = holder.getSession().beginReadTransaction();
        rtx.moveToDocumentRoot();
        rtx.moveToFirstChild();
        assertEquals("10", TypedValue.parseString(rtx.getNode().getRawValue()));
        assertEquals(10L, rtx.getRevisionNumber());
        rtx.close();

    }

    @Test
    public void testInsertPath() throws AbsTTException {

        IWriteTransaction wtx = holder.getSession().beginWriteTransaction();

        wtx.commit();
        wtx.close();

        wtx = holder.getSession().beginWriteTransaction();
        assertNotNull(wtx.moveToDocumentRoot());
        assertEquals(1L, wtx.insertElementAsFirstChild(new QName("")));

        assertEquals(2L, wtx.insertElementAsFirstChild(new QName("")));
        assertEquals(3L, wtx.insertElementAsFirstChild(new QName("")));

        assertNotNull(wtx.moveToParent());
        assertEquals(4L, wtx.insertElementAsRightSibling(new QName("")));

        wtx.commit();
        wtx.close();

        final IWriteTransaction wtx2 = holder.getSession().beginWriteTransaction();

        assertNotNull(wtx2.moveToDocumentRoot());
        assertEquals(5L, wtx2.insertElementAsFirstChild(new QName("")));

        wtx2.commit();
        wtx2.close();

    }

    @Test
    public void testPageBoundary() throws AbsTTException {
        final IWriteTransaction wtx = holder.getSession().beginWriteTransaction();

        // Document root.
        wtx.insertElementAsFirstChild(new QName(""));
        for (int i = 0; i < 256 * 256 + 1; i++) {
            // wtx.insertTextAsRightSibling("");
            wtx.insertElementAsRightSibling(new QName(""));
        }

        assertTrue(wtx.moveTo(2L));
        assertEquals(2L, wtx.getNode().getNodeKey());

        wtx.abort();
        wtx.close();
    }

    @Test(expected = TTUsageException.class)
    public void testRemoveDocument() throws AbsTTException {
        final IWriteTransaction wtx = holder.getSession().beginWriteTransaction();
        DocumentCreater.create(wtx);

        wtx.moveToDocumentRoot();

        try {
            wtx.remove();
        } finally {
            wtx.abort();
            wtx.close();
        }

    }

    @Test
    public void testRemoveDescendant() throws AbsTTException {
        final IWriteTransaction wtx = holder.getSession().beginWriteTransaction();
        DocumentCreater.create(wtx);
        wtx.commit();
        wtx.moveTo(5L);
        wtx.remove();
        wtx.commit();
        wtx.close();
        final IReadTransaction rtx = holder.getSession().beginReadTransaction();
        assertEquals(0, rtx.getNode().getNodeKey());
        assertTrue(rtx.moveToFirstChild());
        assertEquals(1, rtx.getNode().getNodeKey());
        assertEquals(4, ((AbsStructNode)rtx.getNode()).getChildCount());
        assertTrue(rtx.moveToFirstChild());
        assertEquals(4, rtx.getNode().getNodeKey());
        assertTrue(rtx.moveToRightSibling());
        assertEquals(8, rtx.getNode().getNodeKey());
        assertTrue(rtx.moveToRightSibling());
        assertEquals(9, rtx.getNode().getNodeKey());
        assertTrue(rtx.moveToRightSibling());
        assertEquals(13, rtx.getNode().getNodeKey());
        rtx.close();
    }

    @Test
    public void testFirstMoveToFirstChild() throws AbsTTException {
        final IWriteTransaction wtx = holder.getSession().beginWriteTransaction();
        DocumentCreater.create(wtx);
        wtx.moveTo(7);
        wtx.moveSubtreeToFirstChild(6);
        wtx.commit();
        wtx.close();
        final IReadTransaction rtx = holder.getSession().beginReadTransaction();
        assertTrue(rtx.moveTo(4));
        assertEquals(rtx.getValueOfCurrentNode(), "oops1");
        assertTrue(rtx.moveTo(7));
        assertFalse(rtx.getStructuralNode().hasLeftSibling());
        assertTrue(rtx.getStructuralNode().hasFirstChild());
        assertTrue(rtx.moveToFirstChild());
        assertFalse(rtx.getStructuralNode().hasFirstChild());
        assertFalse(rtx.getStructuralNode().hasLeftSibling());
        assertFalse(rtx.getStructuralNode().hasRightSibling());
        assertEquals("foo", rtx.getValueOfCurrentNode());
        rtx.close();
    }

    @Test
    public void testSecondMoveToFirstChild() throws AbsTTException {
        final IWriteTransaction wtx = holder.getSession().beginWriteTransaction();
        DocumentCreater.create(wtx);
        wtx.moveTo(5);
        wtx.moveSubtreeToFirstChild(4);
        wtx.commit();
        wtx.close();
        final IReadTransaction rtx = holder.getSession().beginReadTransaction();
        assertTrue(rtx.moveTo(5));
        assertEquals(Long.parseLong(EFixed.NULL_NODE_KEY.getStandardProperty().toString()), rtx
            .getStructuralNode().getLeftSiblingKey());
        assertEquals(4L, rtx.getStructuralNode().getFirstChildKey());
        assertFalse(rtx.moveTo(6));
        assertTrue(rtx.moveTo(4));
        assertEquals("oops1foo", rtx.getValueOfCurrentNode());
        assertEquals(Long.parseLong(EFixed.NULL_NODE_KEY.getStandardProperty().toString()), rtx
            .getStructuralNode().getLeftSiblingKey());
        assertEquals(5L, rtx.getStructuralNode().getParentKey());
        assertEquals(7L, rtx.getStructuralNode().getRightSiblingKey());
        assertTrue(rtx.moveTo(7));
        assertEquals(4L, rtx.getStructuralNode().getLeftSiblingKey());
        rtx.close();
    }

    @Test
    public void testThirdMoveToFirstChild() throws AbsTTException {
        final IWriteTransaction wtx = holder.getSession().beginWriteTransaction();
        DocumentCreater.create(wtx);
        wtx.moveTo(5);
        wtx.moveSubtreeToFirstChild(11);
        wtx.commit();
        wtx.close();
        final IReadTransaction rtx = holder.getSession().beginReadTransaction();
        assertTrue(rtx.moveTo(5));
        assertEquals(11L, rtx.getStructuralNode().getFirstChildKey());
        assertTrue(rtx.moveTo(11));
        assertEquals(Long.parseLong(EFixed.NULL_NODE_KEY.getStandardProperty().toString()), rtx
            .getStructuralNode().getLeftSiblingKey());
        assertEquals(5L, rtx.getStructuralNode().getParentKey());
        assertEquals(6L, rtx.getStructuralNode().getRightSiblingKey());
        assertTrue(rtx.moveTo(6L));
        assertEquals(11L, rtx.getStructuralNode().getLeftSiblingKey());
        assertEquals(7L, rtx.getStructuralNode().getRightSiblingKey());
        rtx.close();
    }

    @Test(expected = TTUsageException.class)
    public void testFourthMoveToFirstChild() throws AbsTTException {
        final IWriteTransaction wtx = holder.getSession().beginWriteTransaction();
        DocumentCreater.create(wtx);
        wtx.moveTo(4);
        wtx.moveSubtreeToFirstChild(11);
        wtx.commit();
        wtx.close();
    }

    @Test
    public void testFirstMoveSubtreeToRightSibling() throws AbsTTException {
        final IWriteTransaction wtx = holder.getSession().beginWriteTransaction();
        DocumentCreater.create(wtx);
        wtx.moveTo(7);
        wtx.moveSubtreeToRightSibling(6);
        wtx.commit();
        wtx.close();
        final IReadTransaction rtx = holder.getSession().beginReadTransaction();
        assertTrue(rtx.moveTo(7));
        assertFalse(rtx.getStructuralNode().hasLeftSibling());
        assertTrue(rtx.getStructuralNode().hasRightSibling());
        assertTrue(rtx.moveToRightSibling());
        assertEquals(6L, rtx.getNode().getNodeKey());
        assertEquals("foo", rtx.getValueOfCurrentNode());
        assertTrue(rtx.getStructuralNode().hasLeftSibling());
        assertEquals(7L, rtx.getStructuralNode().getLeftSiblingKey());
        rtx.close();
    }

    @Test
    public void testSecondMoveSubtreeToRightSibling() throws AbsTTException {
        final IWriteTransaction wtx = holder.getSession().beginWriteTransaction();
        DocumentCreater.create(wtx);
        wtx.moveTo(9);
        wtx.moveSubtreeToRightSibling(5);
        wtx.commit();
        wtx.close();
        final IReadTransaction rtx = holder.getSession().beginReadTransaction();
        assertTrue(rtx.moveTo(4));
        // Assert that oops1 and oops2 text nodes merged.
        assertEquals("oops1oops2", rtx.getValueOfCurrentNode());
        assertFalse(rtx.moveTo(8));
        assertTrue(rtx.moveTo(9));
        assertEquals(5L, rtx.getStructuralNode().getRightSiblingKey());
        assertTrue(rtx.moveTo(5));
        assertEquals(9L, rtx.getStructuralNode().getLeftSiblingKey());
        assertEquals(13L, rtx.getStructuralNode().getRightSiblingKey());
        rtx.close();
    }

    @Test
    public void testThirdMoveSubtreeToRightSibling() throws AbsTTException {
        final IWriteTransaction wtx = holder.getSession().beginWriteTransaction();
        DocumentCreater.create(wtx);
        wtx.moveTo(9);
        wtx.moveSubtreeToRightSibling(4);
        wtx.commit();
        wtx.close();
        final IReadTransaction rtx = holder.getSession().beginReadTransaction();
        assertTrue(rtx.moveTo(4));
        // Assert that oops1 and oops3 text nodes merged.
        assertEquals("oops1oops3", rtx.getValueOfCurrentNode());
        assertFalse(rtx.moveTo(13));
        assertEquals(Long.parseLong(EFixed.NULL_NODE_KEY.getStandardProperty().toString()), rtx
            .getStructuralNode().getRightSiblingKey());
        assertEquals(9L, rtx.getStructuralNode().getLeftSiblingKey());
        assertTrue(rtx.moveTo(9));
        assertEquals(4L, rtx.getStructuralNode().getRightSiblingKey());
        rtx.close();
    }

    @Test
    public void testFourthMoveSubtreeToRightSibling() throws AbsTTException {
        final IWriteTransaction wtx = holder.getSession().beginWriteTransaction();
        DocumentCreater.create(wtx);
        wtx.moveTo(8);
        wtx.moveSubtreeToRightSibling(4);
        wtx.commit();
        wtx.close();
        final IReadTransaction rtx = holder.getSession().beginReadTransaction();
        assertTrue(rtx.moveTo(4));
        // Assert that oops2 and oops1 text nodes merged.
        assertEquals("oops2oops1", rtx.getValueOfCurrentNode());
        assertFalse(rtx.moveTo(8));
        assertEquals(9L, rtx.getStructuralNode().getRightSiblingKey());
        assertEquals(5L, rtx.getStructuralNode().getLeftSiblingKey());
        assertTrue(rtx.moveTo(5L));
        assertEquals(4L, rtx.getStructuralNode().getRightSiblingKey());
        assertEquals(Long.parseLong(EFixed.NULL_NODE_KEY.getStandardProperty().toString()), rtx
            .getStructuralNode().getLeftSiblingKey());
        assertTrue(rtx.moveTo(9));
        assertEquals(4L, rtx.getStructuralNode().getLeftSiblingKey());
        rtx.close();
    }

}
