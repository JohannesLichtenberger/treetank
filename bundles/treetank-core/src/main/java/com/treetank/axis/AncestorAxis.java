/**
 * Copyright (c) 2010, Distributed Systems Group, University of Konstanz
 * 
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 * 
 * THE SOFTWARE IS PROVIDED AS IS AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 * 
 */

package com.treetank.axis;

import com.treetank.api.IAxis;
import com.treetank.api.IReadTransaction;
import com.treetank.node.ENodes;
import com.treetank.settings.EFixed;

/**
 * <h1>AncestorAxis</h1>
 * 
 * <p>
 * Iterate over all descendants of kind ELEMENT or TEXT starting at a given node. Self is not included.
 * </p>
 */
public class AncestorAxis extends AbsAxis {

    /**
     * First touch of node.
     */
    private boolean mFirst;

    /**
     * Constructor initializing internal state.
     * 
     * @param rtx
     *            Exclusive (immutable) trx to iterate with.
     */
    public AncestorAxis(final IReadTransaction rtx) {
        super(rtx);
    }

    /**
     * Constructor initializing internal state.
     * 
     * @param rtx
     *            Exclusive (immutable) trx to iterate with.
     * @param mIncludeSelf
     *            Is self included?
     */
    public AncestorAxis(final IReadTransaction rtx, final boolean mIncludeSelf) {
        super(rtx, mIncludeSelf);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void reset(final long mNodeKey) {
        super.reset(mNodeKey);
        mFirst = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean hasNext() {
        resetToLastKey();

        // Self
        if (mFirst && isSelfIncluded()) {
            mFirst = false;
            return true;
        }

        if (getTransaction().getNode().getKind() != ENodes.ROOT_KIND
            && getTransaction().getNode().hasParent()
            && getTransaction().getNode().getParentKey() != (Long)EFixed.ROOT_NODE_KEY.getStandardProperty()) {
            getTransaction().moveToParent();
            return true;
        }
        resetToStartKey();
        return false;
    }

}
