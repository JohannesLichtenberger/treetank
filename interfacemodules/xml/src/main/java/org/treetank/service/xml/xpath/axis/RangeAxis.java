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

package org.treetank.service.xml.xpath.axis;

import org.treetank.api.INodeReadTransaction;
import org.treetank.axis.AbsAxis;
import org.treetank.node.AtomicValue;
import org.treetank.node.Type;
import org.treetank.node.interfaces.IValNode;
import org.treetank.utils.NamePageHash;
import org.treetank.utils.TypedValue;

/**
 * <h1>RangeExpr</h1>
 * <p>
 * A range expression can be used to construct a sequence of consecutive integers.
 * </p>
 * <p>
 * If either operand is an empty sequence, or if the integer derived from the first operand is greater than
 * the integer derived from the second operand, the result of the range expression is an empty sequence.
 * </p>
 * <p>
 * If the two operands convert to the same integer, the result of the range expression is that integer.
 * Otherwise, the result is a sequence containing the two integer operands and every integer between the two
 * operands, in increasing order.
 * </p>
 */
public class RangeAxis extends AbsAxis {

    /** The expression the range starts from. */
    private final AbsAxis mFrom;

    /** The expression the range ends. */
    private final AbsAxis mTo;

    /** Is it the first run of range axis? */
    private boolean mFirst;

    /** The integer value the expression starts from. */
    private int mStart;

    /** The integer value the expression ends. */
    private int mEnd;

    /**
     * Constructor. Initializes the internal state.
     * 
     * @param rtx
     *            Exclusive (immutable) trx to iterate with.
     * @param mFrom
     *            start of the range
     * @param mTo
     *            the end of the range
     */
    public RangeAxis(final INodeReadTransaction rtx, final AbsAxis mFrom, final AbsAxis mTo) {

        super(rtx);
        this.mFrom = mFrom;
        this.mTo = mTo;
        mFirst = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext() {

        resetToLastKey();

        if (mFirst) {
            mFirst = false;
            if (mFrom.hasNext() && Type.getType(mFrom.getNode().getTypeKey()).derivesFrom(Type.INTEGER)) {
                mStart = Integer.parseInt(new String(((IValNode)mFrom.getNode()).getRawValue()));

                if (mTo.hasNext() && Type.getType(mTo.getNode().getTypeKey()).derivesFrom(Type.INTEGER)) {

                    mEnd = Integer.parseInt(new String(((IValNode)mTo.getNode()).getRawValue()));

                } else {
                    // at least one operand is the empty sequence
                    resetToStartKey();
                    return false;
                }
            } else {
                // at least one operand is the empty sequence
                resetToStartKey();
                return false;
            }
        }

        if (mStart <= mEnd) {
            AtomicValue val =
                new AtomicValue(TypedValue.getBytes(Integer.toString(mStart)), NamePageHash
                    .generateHashForString("xs:integer"));

            final int itemKey = getItemList().addItem(val);
            moveTo(itemKey);
            mStart++;
            return true;
        } else {
            resetToStartKey();
            return false;
        }
    }
}