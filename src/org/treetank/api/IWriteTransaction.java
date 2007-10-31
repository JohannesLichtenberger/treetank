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
 * $Id$
 */

package org.treetank.api;

import java.io.IOException;

/**
 * <h1>IWriteTransaction</h1>
 * 
 * <p>
 * Interface to access and modify nodes based on the
 * Key/ParentKey/FirstChildKey/LeftSiblingKey/RightSiblingKey/ChildCount
 * encoding. This encoding keeps the children ordered but has no knowledge of
 * the global node ordering. Nodes must first be selected before they can be
 * read. Modifying methods always work from the current node and select the 
 * newly inserted node.
 * </p>
 * 
 * <p>
 * The interface has a single-threaded semantics, this is, each thread accessing
 * the ISession in a modifying way needs its own IWriteTransaction instance.
 * </p>
 * 
 * <p>
 * Exceptions are only thrown if an internal error occurred which must be
 * resolved at a higher layer.
 * </p>
 */
public interface IWriteTransaction extends IReadTransaction {

  // --- Node Modifiers --------------------------------------------------------

  /**
   * Insert new full text attribute as first child.
   * 
   * @param fullTextKey Key of full text leaf.
   */
  public void insertFullTextAttributeAsFirstChild(final long fullTextKey);

  /**
   * Insert new element node as first child of currently selected node.
   * The cursor is moved to the inserted node.
   * 
   * @param localPart Local part of inserted node.
   * @param uri URI of inserted node.
   * @param prefix Prefix of inserted node.
   * @return Key of inserted node.
   * already has a first child.
   */
  public long insertElementAsFirstChild(
      final String localPart,
      final String uri,
      final String prefix);

  /**
   * Insert new text node as first child of currently selected node.
   * The cursor is moved to the inserted node.
   * 
   * @param value Value of inserted node.
   * @return Key of inserted node.
   * already has a first child.
   */
  public long insertTextAsFirstChild(final byte[] value);

  /**
   * Insert new fulltext node as first child of currently selected node.
   * The cursor is moved to the inserted node.
   * 
   * @param localPartKey Local part key of inserted node.
   * @return Key of inserted node.
   * already has a first child.
   */
  public long insertFullTextAsFirstChild(final int localPartKey);

  /**
   * Insert new element node as right sibling of currently selected node.
   * The cursor is moved to the inserted node.
   * 
   * @param localPart Local part of inserted node.
   * @param uri URI of inserted node.
   * @param prefix Prefix of inserted node.
   * @return Key of inserted node.
   * already has a first child.
   */
  public long insertElementAsRightSibling(
      final String localPart,
      final String uri,
      final String prefix);

  /**
   * Insert new element node as right sibling of currently selected node.
   * The cursor is moved to the inserted node.
   * 
   * @param value Value of inserted node.
   * @return Key of inserted node.
   * the root node which is not allowed to have right siblings.
   */
  public long insertTextAsRightSibling(final byte[] value);

  /**
   * Insert new fulltext node as right sibling of currently selected node.
   * The cursor is moved to the inserted node.
   * 
   * @param localPartKey Local part key of inserted node.
   * @return Key of inserted node.
   * already has a first child.
   */
  public long insertFullTextAsRightSibling(final int localPartKey);

  /**
   * Insert attribute in currently selected node.
   * The cursor is moved to the inserted node.
   * 
   * @param localPart Local part of inserted node.
   * @param uri URI of inserted node.
   * @param prefix Prefix of inserted node.
   * @param value Value of inserted node.
   */
  public void insertAttribute(
      final String localPart,
      final String uri,
      final String prefix,
      final byte[] value);

  /**
   * Insert namespace declaration in currently selected node.
   * The cursor is moved to the inserted node.
   * 
   * @param uri URI of inserted node.
   * @param prefix Prefix of inserted node.
   */
  public void insertNamespace(final String uri, final String prefix);

  /**
   * Remove currently selected node. This does not automatically remove
   * descendants.
   */
  public void remove();

  //--- Node Setters -----------------------------------------------------------

  /**
   * Set an attribute of the currently selected node.
   * 
   * @param index Index of attribute to set.
   * @param localPart Local part of inserted node.
   * @param uri URI of inserted node.
   * @param prefix Prefix of inserted node.
   * @param value Value of inserted node.
   */
  public void setAttribute(
      final int index,
      final String localPart,
      final String uri,
      final String prefix,
      final byte[] value);

  /**
   * Set a namespace of the currently selected node.
   * 
   * @param index Index of attribute to set.
   * @param uri URI of inserted node.
   * @param prefix Prefix of inserted node.
   */
  public void setNamespace(
      final int index,
      final String uri,
      final String prefix);

  /**
   * Set local part of node.
   * 
   * @param localPart New local part of node.
   */
  public void setLocalPart(final String localPart);

  /**
   * Set URI of node.
   * 
   * @param uri New URI of node.
   */
  public void setURI(final String uri);

  /**
   * Set prefix of node.
   * 
   * @param prefix New prefix of node.
   */
  public void setPrefix(final String prefix);

  /**
   * Set value of node.
   * 
   * @param value New value of node.
   */
  public void setValue(final byte[] value);

  /**
   * Commit all modifications of the exclusive write transaction. Calling
   * <code>close()</code> is semantiacally equivalent.
   * 
   * @throws IOException if commit to file failed.
   */
  public void commit() throws IOException;

  /**
   * Abort all modifications of the exclusive write transaction.
   */
  public void abort();

}
