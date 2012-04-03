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
package org.treetank.axis;

import java.util.LinkedList;
import java.util.List;
import static org.treetank.access.NodeReadTransaction.NULL_NODE;
import org.treetank.api.INodeReadTransaction;
import org.treetank.node.interfaces.IStructNode;

/**
 * Iterates over {@link AbsStructuralNode}s in a breath first traversal.
 * 
 * @author Johannes Lichtenberger, University of Konstanz
 * 
 */
public final class LevelOrderAxis extends AbsAxis {

    /** {@link List} for remembering next nodeKey in document order. */
    private List<Long> mFirstChildKeyList;

    /** The nodeKey of the next node to visit. */
    private long mNextKey;

    /**
     * Constructor initializing internal state.
     * 
     * @param paramRtx
     *            exclusive (immutable) trx to iterate with
     */
    public LevelOrderAxis(final INodeReadTransaction paramRtx) {
        super(paramRtx);
    }

    /**
     * Constructor initializing internal state.
     * 
     * @param paramRtx
     *            exclusive (immutable) trx to iterate with
     * @param paramIncludeSelf
     *            determines if self included
     */
    public LevelOrderAxis(final INodeReadTransaction paramRtx, final boolean paramIncludeSelf) {
        super(paramRtx, paramIncludeSelf);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset(final long paramNodeKey) {
        super.reset(paramNodeKey);
        mFirstChildKeyList = new LinkedList<Long>();
        if (isSelfIncluded()) {
            mNextKey = getNode().getNodeKey();
        } else {
            if (((IStructNode)getNode()).hasRightSibling()) {
                mNextKey = ((IStructNode)getNode()).getRightSiblingKey();
            } else if (((IStructNode)getNode()).hasFirstChild()) {
                mNextKey = ((IStructNode)getNode()).getFirstChildKey();
            } else {
                mNextKey = NULL_NODE;
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasNext() {
        resetToLastKey();

        // Fail if there is no node anymore.
        if (mNextKey == NULL_NODE) {
            resetToStartKey();
            return false;
        }

        // First move to next key.
        moveTo(mNextKey);

        // Follow right sibling if there is one.
        if (((IStructNode)getNode()).hasRightSibling()) {
            if (((IStructNode)getNode()).hasFirstChild()) {
                mFirstChildKeyList.add(((IStructNode)getNode()).getFirstChildKey());
            }
            mNextKey = ((IStructNode)getNode()).getRightSiblingKey();
            return true;
        }

        // Then follow first child on stack.
        if (mFirstChildKeyList.size() > 0) {
            mNextKey = mFirstChildKeyList.remove(0);
            return true;
        }

        // Then follow first child if there is one.
        if (((IStructNode)getNode()).hasFirstChild()) {
            mNextKey = ((IStructNode)getNode()).getFirstChildKey();
            return true;
        }

        // Then end.
        mNextKey = NULL_NODE;
        return true;
    }

}