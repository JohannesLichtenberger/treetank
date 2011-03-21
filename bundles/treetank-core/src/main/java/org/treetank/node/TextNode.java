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

package org.treetank.node;

import java.util.Arrays;

import org.treetank.api.IItem;
import org.treetank.api.IReadTransaction;
import org.treetank.api.IVisitor;
import org.treetank.io.ITTSink;
import org.treetank.settings.EFixed;

/**
 * <h1>TextNode</h1>
 * 
 * <p>
 * Node representing a text node.
 * </p>
 */
public final class TextNode extends AbsStructNode {

    protected static final int VALUE_LENGTH = 1;

    /** Typed value of node. */
    private byte[] mValue;

    /**
     * Constructor for TextNode.
     * 
     * @param mLongBuilder
     *            vals of longs to set
     * @param mIntBuilder
     *            vals of ints to set
     * @param mValue
     *            val to set
     */
    TextNode(final long[] mLongBuilder, final int[] mIntBuilder, final byte[] mValue) {
        super(mLongBuilder, mIntBuilder);
        this.mValue = mValue;
        mIntData[VALUE_LENGTH] = mValue.length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ENodes getKind() {
        return ENodes.TEXT_KIND;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getRawValue() {
        return mValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(final int mValueType, final byte[] mValue) {
        mIntData[AbsNode.TYPE_KEY] = mValueType;
        mIntData[VALUE_LENGTH] = mValue.length;
        this.mValue = mValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setType(final int mValueType) {
        mIntData[AbsNode.TYPE_KEY] = mValueType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final ITTSink mOut) {
        super.serialize(mOut);
        for (final byte byteVal : mValue) {
            mOut.writeByte(byteVal);
        }
    }

    /** {@inheritDoc} */
    @Override
    public long getFirstChildKey() {
        return (Long)EFixed.NULL_NODE_KEY.getStandardProperty();
    }

    /** {@inheritDoc} */
    @Override
    public void setFirstChildKey(final long mFirstChildKey) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public void decrementChildCount() {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public void incrementChildCount() {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public void setChildCount(final long paramChildCount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
        final int prime = 17;
        int result = 1;
        result = prime * result + Arrays.hashCode(mIntData);
        result = prime * result + Arrays.hashCode(mValue);
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public AbsNode clone() {
        final AbsNode toClone =
            new TextNode(ENodes.cloneData(mLongData), ENodes.cloneData(mIntData), ENodes.cloneData(mValue));
        return toClone;
    }

    public static AbsNode createData(final long mNodeKey, final long mParentKey, final long mLeftSibKey,
        final long rightSibKey, final int mType, final byte[] mValue) {
        final long[] longData = new long[ENodes.TEXT_KIND.getLongSize()];
        final int[] intData = new int[ENodes.TEXT_KIND.getIntSize()];
        longData[AbsNode.NODE_KEY] = mNodeKey;
        longData[AbsNode.PARENT_KEY] = mParentKey;
        longData[AbsStructNode.LEFT_SIBLING_KEY] = mLeftSibKey;
        longData[AbsStructNode.RIGHT_SIBLING_KEY] = rightSibKey;
        longData[AbsStructNode.FIRST_CHILD_KEY] = (Long)EFixed.NULL_NODE_KEY.getStandardProperty();
        intData[AbsNode.TYPE_KEY] = mType;
        return new TextNode(longData, intData, mValue);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        final StringBuilder returnVal = new StringBuilder(super.toString());
        returnVal.append("\n\ttype key: ").append(getTypeKey()).append("\n\tvalueLength: ")
            .append(mIntData[VALUE_LENGTH]).append("\n\tvalue:").append(new String(mValue)).toString();
        return returnVal.toString();
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends IItem> T accept(final IReadTransaction paramTransaction) {
        return (T)paramTransaction.getNode(this);
    }

    /** {@inheritDoc} */
    @Override
    public void acceptVisitor(final IVisitor paramVisitor) {
        paramVisitor.visit(this);
    }
}