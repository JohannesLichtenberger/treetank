/**
 * Copyright (c) 2011, University of Konstanz, Distributed Systems Group
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of Konstanz nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
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
package org.treetank.node.delegates;

import org.treetank.api.IVisitor;
import org.treetank.node.ENode;
import org.treetank.node.interfaces.IStructNode;
import org.treetank.settings.EFixed;

/**
 * Delegate method for all nodes building up the structure. That means that all
 * nodes representing trees in Treetank are represented by an instance of the
 * interface {@link IStructNode} namely containing the position of all related
 * siblings, the first-child and all nodes defined by the {@link NodeDelegate}
 * as well.
 * 
 * @author Sebastian Graf, University of Konstanz
 * 
 */
public class StructNodeDelegate implements IStructNode {

    /** Pointer to the first child of the current node. */
    private long mFirstChild;
    /** Pointer to the right sibling of the current node. */
    private long mRightSibling;
    /** Pointer to the left sibling of the current node. */
    private long mLeftSibling;
    /** Pointer to the number of children. */
    private long mChildCount;
    /** Delegate for common node information. */
    private final NodeDelegate mDelegate;

    /**
     * Constructor.
     * 
     * @param pDel
     *            to be set
     * @param pFirstChild
     *            to be set
     * @param pRightSib
     *            to be set
     * @param pLeftSib
     *            to be set
     * @param pChildCount
     *            to be set
     */
    public StructNodeDelegate(final NodeDelegate pDel, final long pFirstChild,
            final long pRightSib, final long pLeftSib, final long pChildCount) {
        mDelegate = pDel;
        mFirstChild = pFirstChild;
        mRightSibling = pRightSib;
        mLeftSibling = pLeftSib;
        mChildCount = pChildCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ENode getKind() {
        return mDelegate.getKind();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasFirstChild() {
        return mFirstChild != (Long) EFixed.NULL_NODE_KEY.getStandardProperty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasLeftSibling() {
        return mLeftSibling != (Long) EFixed.NULL_NODE_KEY
                .getStandardProperty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasRightSibling() {
        return mRightSibling != (Long) EFixed.NULL_NODE_KEY
                .getStandardProperty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getChildCount() {
        return mChildCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getFirstChildKey() {
        return mFirstChild;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getLeftSiblingKey() {
        return mLeftSibling;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getRightSiblingKey() {
        return mRightSibling;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setRightSiblingKey(final long pKey) {
        mRightSibling = pKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLeftSiblingKey(final long pKey) {
        mLeftSibling = pKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFirstChildKey(final long pKey) {
        mFirstChild = pKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void decrementChildCount() {
        mChildCount--;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void incrementChildCount() {
        mChildCount++;
    }

    /**
     * Delegate method for getNodeKey.
     * 
     * @return
     * @see org.treetank.node.delegates.NodeDelegate#getNodeKey()
     */
    public long getNodeKey() {
        return mDelegate.getNodeKey();
    }

    /**
     * Delegate method for setNodeKey.
     * 
     * @param pNodeKey
     * @see org.treetank.node.delegates.NodeDelegate#setNodeKey(long)
     */
    public void setNodeKey(long pNodeKey) {
        mDelegate.setNodeKey(pNodeKey);
    }

    /**
     * Delegate method for getParentKey.
     * 
     * @return
     * @see org.treetank.node.delegates.NodeDelegate#getParentKey()
     */
    public long getParentKey() {
        return mDelegate.getParentKey();
    }

    /**
     * Delegate method for setParentKey.
     * 
     * @param pParentKey
     * @see org.treetank.node.delegates.NodeDelegate#setParentKey(long)
     */
    public void setParentKey(long pParentKey) {
        mDelegate.setParentKey(pParentKey);
    }

    /**
     * Delegate method for getHash.
     * 
     * @return
     * @see org.treetank.node.delegates.NodeDelegate#getHash()
     */
    public long getHash() {
        return mDelegate.getHash();
    }

    /**
     * Delegate method for setHash.
     * 
     * @param pHash
     * @see org.treetank.node.delegates.NodeDelegate#setHash(long)
     */
    public void setHash(long pHash) {
        mDelegate.setHash(pHash);
    }

    /**
     * Delegate method for acceptVisitor.
     * 
     * @param pVisitor
     * @see org.treetank.node.delegates.NodeDelegate#acceptVisitor(org.treetank.api.IVisitor)
     */
    public void acceptVisitor(final IVisitor pVisitor) {
        mDelegate.acceptVisitor(pVisitor);
    }

    /**
     * Delegate method for getTypeKey.
     * 
     * @return
     * @see org.treetank.node.delegates.NodeDelegate#getTypeKey()
     */
    public int getTypeKey() {
        return mDelegate.getTypeKey();
    }

    /**
     * Delegate method for setTypeKey.
     * 
     * @param pTypeKey
     * @see org.treetank.node.delegates.NodeDelegate#setTypeKey(int)
     */
    public void setTypeKey(int pTypeKey) {
        mDelegate.setTypeKey(pTypeKey);
    }

    /**
     * Delegate method for hasParent.
     * 
     * @return
     * @see org.treetank.node.delegates.NodeDelegate#hasParent()
     */
    public boolean hasParent() {
        return mDelegate.hasParent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (mChildCount ^ (mChildCount >>> 32));
        result = prime * result
                + ((mDelegate == null) ? 0 : mDelegate.hashCode());
        result = prime * result + (int) (mFirstChild ^ (mFirstChild >>> 32));
        result = prime * result + (int) (mLeftSibling ^ (mLeftSibling >>> 32));
        result = prime * result
                + (int) (mRightSibling ^ (mRightSibling >>> 32));
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object pObj) {
        if (this == pObj)
            return true;
        if (pObj == null)
            return false;
        if (getClass() != pObj.getClass())
            return false;
        StructNodeDelegate other = (StructNodeDelegate) pObj;
        if (mChildCount != other.mChildCount)
            return false;
        if (mDelegate == null) {
            if (other.mDelegate != null)
                return false;
        } else if (!mDelegate.equals(other.mDelegate))
            return false;
        if (mFirstChild != other.mFirstChild)
            return false;
        if (mLeftSibling != other.mLeftSibling)
            return false;
        if (mRightSibling != other.mRightSibling)
            return false;
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("\nfirst child: ");
        builder.append(getFirstChildKey());
        builder.append("\nleft sib: ");
        builder.append(getLeftSiblingKey());
        builder.append("\nright sib: ");
        builder.append(getRightSiblingKey());
        builder.append("\nfirst child: ");
        builder.append(getFirstChildKey());
        builder.append("\nchild count: ");
        builder.append(getChildCount());
        return builder.toString();
    }
}
