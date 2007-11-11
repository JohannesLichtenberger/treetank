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
 * $Id: NodeTestAxisIterator.java 3174 2007-10-22 13:44:43Z kramis $
 */

package org.treetank.axislayer;

import org.treetank.api.IAxis;

/**
 * <h1>FullTextLeafTestAxis</h1>
 * 
 * <p>
 * Only select nodes of kind FULLTEXT_LEAF.
 * </p>
 */
public class FullTextLeafTestAxis extends AbstractAxis {

  /** Remember next key to visit. */
  private final IAxis mAxis;

  /**
   * Constructor initializing internal state.
   * 
   * @param axis Axis to iterate over.
   */
  public FullTextLeafTestAxis(final IAxis axis) {
    super(axis.getTransaction());
    mAxis = axis;
  }

  /**
   * {@inheritDoc}
   */
  public final boolean hasNext() {
    resetToLastKey();
    while (mAxis.hasNext()) {
      mAxis.next();
      if (getTransaction().isFullTextLeaf()) {
        return true;
      }
    }
    resetToStartKey();
    return false;
  }

}