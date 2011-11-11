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

package org.treetank.node;

import org.treetank.api.IVisitor;
import org.treetank.io.ITTSink;
import org.treetank.node.delegates.NodeDelegate;
import org.treetank.node.delegates.StructNodeDelegate;
import org.treetank.node.interfaces.IStructNode;

/**
 * <h1>DocumentNode</h1>
 * 
 * <p>
 * Node representing the root of a document. This node is guaranteed to exist in revision 0 and can not be
 * removed.
 * </p>
 */
public final class DocumentRootNode implements IStructNode {

    private final NodeDelegate mDel;
    private final StructNodeDelegate mStrucDel;

    /**
     * Constructor.
     * 
     * @param mLongBuilder
     *            long array to set
     * @param mIntBuilder
     *            int array to set
     */
    public DocumentRootNode(final NodeDelegate pNodeDel, final StructNodeDelegate pStrucDel) {
        mDel = pNodeDel;
        mStrucDel = pStrucDel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ENodes getKind() {
        return ENodes.ROOT_KIND;
    }

    @Override
    public DocumentRootNode clone() {
        return new DocumentRootNode(mDel.clone(), mStrucDel.clone());
    }

    /** {@inheritDoc} */
    @Override
    public void acceptVisitor(final IVisitor paramVisitor) {
        paramVisitor.visit(this);
    }

    /**
     * Delegate method for setHash.
     * 
     * @param paramHash
     * @see org.treetank.node.delegates.StructNodeDelegate#setHash(long)
     */
    public void setHash(long paramHash) {
        mStrucDel.setHash(paramHash);
    }

    /**
     * Delegate method for getHash.
     * 
     * @return
     * @see org.treetank.node.delegates.StructNodeDelegate#getHash()
     */
    public long getHash() {
        return mStrucDel.getHash();
    }

    /**
     * Delegate method for setNodeKey.
     * 
     * @param paramKey
     * @see org.treetank.node.delegates.StructNodeDelegate#setNodeKey(long)
     */
    public void setNodeKey(long paramKey) {
        mStrucDel.setNodeKey(paramKey);
    }

    /**
     * Delegate method for getNodeKey.
     * 
     * @return
     * @see org.treetank.node.delegates.StructNodeDelegate#getNodeKey()
     */
    public long getNodeKey() {
        return mStrucDel.getNodeKey();
    }

    /**
     * Delegate method for getParentKey.
     * 
     * @return
     * @see org.treetank.node.delegates.StructNodeDelegate#getParentKey()
     */
    public long getParentKey() {
        return mStrucDel.getParentKey();
    }

    /**
     * Delegate method for hasParent.
     * 
     * @return
     * @see org.treetank.node.delegates.StructNodeDelegate#hasParent()
     */
    public boolean hasParent() {
        return mStrucDel.hasParent();
    }

    /**
     * Delegate method for getTypeKey.
     * 
     * @return
     * @see org.treetank.node.delegates.StructNodeDelegate#getTypeKey()
     */
    public int getTypeKey() {
        return mStrucDel.getTypeKey();
    }

    /**
     * Delegate method for hasFirstChild.
     * 
     * @return
     * @see org.treetank.node.delegates.StructNodeDelegate#hasFirstChild()
     */
    public boolean hasFirstChild() {
        return mStrucDel.hasFirstChild();
    }

    /**
     * Delegate method for hasLeftSibling.
     * 
     * @return
     * @see org.treetank.node.delegates.StructNodeDelegate#hasLeftSibling()
     */
    public boolean hasLeftSibling() {
        return mStrucDel.hasLeftSibling();
    }

    /**
     * Delegate method for hasRightSibling.
     * 
     * @return
     * @see org.treetank.node.delegates.StructNodeDelegate#hasRightSibling()
     */
    public boolean hasRightSibling() {
        return mStrucDel.hasRightSibling();
    }

    /**
     * Delegate method for getChildCount.
     * 
     * @return
     * @see org.treetank.node.delegates.StructNodeDelegate#getChildCount()
     */
    public long getChildCount() {
        return mStrucDel.getChildCount();
    }

    /**
     * Delegate method for getFirstChildKey.
     * 
     * @return
     * @see org.treetank.node.delegates.StructNodeDelegate#getFirstChildKey()
     */
    public long getFirstChildKey() {
        return mStrucDel.getFirstChildKey();
    }

    /**
     * Delegate method for getLeftSiblingKey.
     * 
     * @return
     * @see org.treetank.node.delegates.StructNodeDelegate#getLeftSiblingKey()
     */
    public long getLeftSiblingKey() {
        return mStrucDel.getLeftSiblingKey();
    }

    /**
     * Delegate method for getRightSiblingKey.
     * 
     * @return
     * @see org.treetank.node.delegates.StructNodeDelegate#getRightSiblingKey()
     */
    public long getRightSiblingKey() {
        return mStrucDel.getRightSiblingKey();
    }

    /**
     * Delegate method for serialize.
     * 
     * @param paramSink
     * @see org.treetank.node.delegates.StructNodeDelegate#serialize(org.treetank.io.ITTSink)
     */
    public void serialize(ITTSink paramSink) {
        mDel.serialize(paramSink);
        mStrucDel.serialize(paramSink);
    }

    /**
     * Delegate method for setRightSiblingKey.
     * 
     * @param paramKey
     * @see org.treetank.node.delegates.StructNodeDelegate#setRightSiblingKey(long)
     */
    public void setRightSiblingKey(long paramKey) {
        mStrucDel.setRightSiblingKey(paramKey);
    }

    /**
     * Delegate method for setLeftSiblingKey.
     * 
     * @param paramKey
     * @see org.treetank.node.delegates.StructNodeDelegate#setLeftSiblingKey(long)
     */
    public void setLeftSiblingKey(long paramKey) {
        mStrucDel.setLeftSiblingKey(paramKey);
    }

    /**
     * Delegate method for setFirstChildKey.
     * 
     * @param paramKey
     * @see org.treetank.node.delegates.StructNodeDelegate#setFirstChildKey(long)
     */
    public void setFirstChildKey(long paramKey) {
        mStrucDel.setFirstChildKey(paramKey);
    }

    /**
     * Delegate method for decrementChildCount.
     * 
     * @see org.treetank.node.delegates.StructNodeDelegate#decrementChildCount()
     */
    public void decrementChildCount() {
        mStrucDel.decrementChildCount();
    }

    /**
     * Delegate method for incrementChildCount.
     * 
     * @see org.treetank.node.delegates.StructNodeDelegate#incrementChildCount()
     */
    public void incrementChildCount() {
        mStrucDel.incrementChildCount();
    }

    /**
     * Delegate method for setParentKey.
     * 
     * @param paramKey
     * @see org.treetank.node.delegates.StructNodeDelegate#setParentKey(long)
     */
    public void setParentKey(long paramKey) {
        mStrucDel.setParentKey(paramKey);
    }

    /**
     * Delegate method for setTypeKey.
     * 
     * @param paramType
     * @see org.treetank.node.delegates.StructNodeDelegate#setTypeKey(int)
     */
    public void setTypeKey(int paramType) {
        mStrucDel.setTypeKey(paramType);
    }

    /**
     * Delegate method for toString.
     * 
     * @return
     * @see org.treetank.node.delegates.StructNodeDelegate#toString()
     */
    public String toString() {
        return mStrucDel.toString();
    }
}
