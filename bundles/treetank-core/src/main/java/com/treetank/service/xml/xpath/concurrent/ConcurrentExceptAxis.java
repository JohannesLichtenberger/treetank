/*
 * Copyright (c) 2008, Tina Scherer (Master Thesis), University of Konstanz
 * Copyright (c) 2010, Patrick Lang (Master Project), University of Konstanz
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
 * $Id: ConcurrentExceptAxis.java 4517 2008-11-24 15:40:37Z scherer $
 */

package com.treetank.service.xml.xpath.concurrent;

import com.treetank.api.IReadTransaction;
import com.treetank.axis.AbsAxis;
import com.treetank.exception.TTXPathException;
import com.treetank.service.xml.xpath.EXPathError;
import com.treetank.settings.EFixed;

/**
 * <h1>ConcurrentExceptAxis</h1>
 * <p>
 * Computes concurrently and returns the nodes of the first operand except those of the second operand. This
 * axis takes two node sequences as operands and returns a sequence containing all the nodes that occur in the
 * first, but not in the second operand. Document order is preserved.
 * </p>
 */
public class ConcurrentExceptAxis extends AbsAxis {

    /** First operand sequence. */
    private final ConcurrentAxis mOp1;

    /** Second operand sequence. */
    private final ConcurrentAxis mOp2;

    /** Is axis called for the first time? */
    private boolean mFirst;

    /** Current result of the 1st axis */
    private long mCurrentResult1;

    /** Current result of the 2nd axis. */
    private long mCurrentResult2;

    /**
     * Constructor. Initializes the internal state.
     * 
     * @param rtx
     *            Exclusive (immutable) trx to iterate with.
     * @param operand1
     *            First operand
     * @param operand2
     *            Second operand
     */
    public ConcurrentExceptAxis(final IReadTransaction rtx, final AbsAxis operand1, final AbsAxis operand2) {

        super(rtx);
        mOp1 = new ConcurrentAxis(rtx, operand1);
        mOp2 = new ConcurrentAxis(rtx, operand2);
        mFirst = true;
        mCurrentResult1 = (Long)EFixed.NULL_NODE_KEY.getStandardProperty();
        mCurrentResult2 = (Long)EFixed.NULL_NODE_KEY.getStandardProperty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void reset(final long nodeKey) {

        super.reset(nodeKey);

        if (mOp1 != null) {
            mOp1.reset(nodeKey);
        }
        if (mOp2 != null) {
            mOp2.reset(nodeKey);
        }

        mFirst = true;
        mCurrentResult1 = (Long)EFixed.NULL_NODE_KEY.getStandardProperty();
        mCurrentResult2 = (Long)EFixed.NULL_NODE_KEY.getStandardProperty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized boolean hasNext() {

        resetToLastKey();

        if (mFirst) {
            mFirst = false;
            mCurrentResult1 = getNext(mOp1);
            mCurrentResult2 = getNext(mOp2);
        }

        final long nodeKey;

        // if 1st axis has a result left that is not contained in the 2nd it is returned
        while (!mOp1.isFinished()) {
            while (!mOp2.isFinished()) {
                if (mOp1.isFinished())
                    break;

                while (mCurrentResult1 >= mCurrentResult2 && !mOp1.isFinished() && !mOp2.isFinished()) {

                    // don't return if equal
                    while (mCurrentResult1 == mCurrentResult2 && !mOp1.isFinished() && !mOp2.isFinished()) {
                        mCurrentResult1 = getNext(mOp1);
                        mCurrentResult2 = getNext(mOp2);
                    }

                    // a1 has to be smaller than a2 to check for equality
                    while (mCurrentResult1 > mCurrentResult2 && !mOp1.isFinished() && !mOp2.isFinished()) {
                        mCurrentResult2 = getNext(mOp2);
                    }
                }

                if (!mOp1.isFinished() && !mOp2.isFinished()) {
                    // as long as a1 is smaller than a2 it can be returned
                    assert (mCurrentResult1 < mCurrentResult2);
                    nodeKey = mCurrentResult1;
                    if (isValid(nodeKey)) {
                        mCurrentResult1 = getNext(mOp1);
                        getTransaction().moveTo(nodeKey);
                        return true;
                    }
                    // should never come here!
                    throw new IllegalStateException(nodeKey + " is not valid!");
                }
            }

            if (!mOp1.isFinished()) {
                // only operand1 has results left, so return all of them
                nodeKey = mCurrentResult1;
                if (isValid(nodeKey)) {
                    mCurrentResult1 = getNext(mOp1);
                    getTransaction().moveTo(nodeKey);
                    return true;
                }
                // should never come here!
                throw new IllegalStateException(nodeKey + " is not valid!");
            }

        }
        // no results left
        resetToStartKey();
        return false;

    }

    /**
     * @return the next result of the axis. If the axis has no next result, the
     *         null node key is returned.
     */
    private long getNext(final AbsAxis axis) {
        return (axis.hasNext()) ? axis.next() : (Long)EFixed.NULL_NODE_KEY.getStandardProperty();

    }

    /**
     * Checks, whether the given node key belongs to a node or an atomic value.
     * Returns true for a node and throws an exception for an atomic value,
     * because these are not allowed in the except expression.
     * 
     * @param nodeKey
     *            the nodekey to validate
     * @return true, if key is a key of a node, otherwise throws an exception
     * @throws TTXPathException
     */
    private boolean isValid(final long nodeKey) {
        if (nodeKey < 0) {
            try {
                throw EXPathError.XPTY0004.getEncapsulatedException();
            } catch (final TTXPathException mExp) {
                mExp.printStackTrace();
            }
        }
        return true;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTransaction(final IReadTransaction rtx) {
        super.setTransaction(rtx);
        mOp1.setTransaction(rtx);
        mOp2.setTransaction(rtx);
    }
}