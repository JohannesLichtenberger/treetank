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
 * $Id: SAXGenerator.java 4147 2008-05-08 07:58:28Z kramis $
 */

package org.treetank.api;


/**
 * <h1>ItemList</h1>
 * <p>
 * Data structure to store XPath items.
 * </p>
 * <p>
 * This structure is used for atomic values that are needed for the evaluation
 * of a query. They can be results of a query expression or be specified
 * directly in the query e.g. as literals perform an arithmetic operation or a
 * comparison.
 * </p>
 * <p>
 * Since these items have to be distinguishable from nodes their key will be a
 * negative long value (node key is always a positive long value). This value is
 * retrieved by negate their index in the internal data structure.
 * </p>
 * 
 * @author Tina Scherer
 */
public interface IItemList {

  /**
   * Adds an item to the item list and assigns a unique item key to the item and
   * return it. The item key is the negatived index of the item in the item list
   * The key is negatived to make it distinguishable from a node
   * 
   * @param item
   *          The item to add.
   * @return The item key.
   */
  public int addItem(final IItem item);

  /**
   * Returns the item at a given index in the item list. If the given index is
   * the item key, it has to be negated before.
   * 
   * @param key
   *          key of the item, that should be returned
   * @return item at the given index.
   */
  public IItem getItem(final long key);
}