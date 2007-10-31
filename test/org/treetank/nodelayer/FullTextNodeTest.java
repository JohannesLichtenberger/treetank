/*
 * TreeTank - Embedded Native XML Database
 * 
 * Copyright 2007 Marc Kramis
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * $Id: NodePageTest.java 3317 2007-10-29 12:45:25Z kramis $
 */

package org.treetank.nodelayer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.treetank.api.IConstants;
import org.treetank.utils.FastByteArrayReader;
import org.treetank.utils.FastByteArrayWriter;

public class FullTextNodeTest {

  @Test
  public void testFullTextNode() {

    // Create empty node.
    final AbstractNode node1 =
        new FullTextNode(13L, 14L, 15L, 16L, 17L, 18L, 19);
    final FastByteArrayWriter out = new FastByteArrayWriter();

    // Modify it.
    node1.incrementChildCount();
    node1.decrementChildCount();

    // Serialize and deserialize node.
    node1.serialize(out);
    final FastByteArrayReader in = new FastByteArrayReader(out.getBytes());
    final AbstractNode node2 = new FullTextNode(13L, in);

    // Clone node.
    final AbstractNode node3 = new FullTextNode(node2);

    // Now compare.
    assertEquals(13L, node3.getNodeKey());
    assertEquals(14L, node3.getParentKey());
    assertEquals(15L, node3.getFirstChildKey());
    assertEquals(16L, node3.getLeftSiblingKey());
    assertEquals(17L, node3.getRightSiblingKey());
    assertEquals(18L, node3.getReferenceKey());
    assertEquals(0, node3.getChildCount());
    assertEquals(0, node3.getAttributeCount());
    assertEquals(0, node3.getNamespaceCount());
    assertEquals(19, node3.getLocalPartKey());
    assertEquals(IConstants.NULL_NAME, node3.getURIKey());
    assertEquals(IConstants.NULL_NAME, node3.getPrefixKey());
    assertEquals(null, node3.getValue());
    assertEquals(IConstants.FULLTEXT, node3.getKind());
    assertEquals(true, node3.hasFirstChild());
    assertEquals(true, node3.hasParent());
    assertEquals(true, node3.hasLeftSibling());
    assertEquals(true, node3.hasRightSibling());
    assertEquals(false, node3.isAttribute());
    assertEquals(false, node3.isDocumentRoot());
    assertEquals(false, node3.isElement());
    assertEquals(true, node3.isFullText());
    assertEquals(false, node3.isFullTextAttribute());
    assertEquals(false, node3.isFullTextRoot());
    assertEquals(false, node3.isText());

  }

}