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
 *     * Neither the name of the <organization> nor the
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

package org.treetank.service.xml.xpath.expr;

import java.util.List;

import org.treetank.api.IReadTransaction;
import org.treetank.axis.AbsAxis;
import org.treetank.service.xml.xpath.AtomicValue;
import org.treetank.utils.TypedValue;

/**
 * <h1>EveryExpr</h1>
 * <p>
 * IAxis that represents the quantified expression "every".
 * </p>
 * <p>
 * The quantified expression is true if every evaluation of the test expression has the effective boolean
 * value true; otherwise the quantified expression is false. This rule implies that, if the in-clauses
 * generate zero binding tuples, the value of the quantified expression is true.
 * </p>
 */
public class EveryExpr extends AbsExpression {

    private final List<AbsAxis> mVars;

    private final AbsAxis mSatisfy;

    /**
     * Constructor. Initializes the internal state.
     * 
     * @param rtx
     *            Exclusive (immutable) trx to iterate with.
     * @param mVars
     *            Variables for which the condition must be satisfied
     * @param mSatisfy
     *            condition every item of the variable results must satisfy in
     *            order to evaluate expression to true
     */
    public EveryExpr(final IReadTransaction rtx, final List<AbsAxis> mVars, final AbsAxis mSatisfy) {

        super(rtx);
        this.mVars = mVars;
        this.mSatisfy = mSatisfy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset(final long mNodeKey) {

        super.reset(mNodeKey);
        if (mVars != null) {
            for (AbsAxis axis : mVars) {
                axis.reset(mNodeKey);
            }
        }

        if (mSatisfy != null) {
            mSatisfy.reset(mNodeKey);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void evaluate() {

        boolean satisfiesCond = true;

        for (AbsAxis axis : mVars) {
            while (axis.hasNext()) {
                axis.next();
                if (!mSatisfy.hasNext()) {
                    // condition is not satisfied for this item -> expression is
                    // false
                    satisfiesCond = false;
                    break;
                }
            }
        }
        final int mItemKey =
            getTransaction().getItemList().addItem(
                new AtomicValue(TypedValue.getBytes(Boolean.toString(satisfiesCond)), getTransaction()
                    .keyForName("xs:boolean")));
        getTransaction().moveTo(mItemKey);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void setTransaction(final IReadTransaction rtx) {
        super.setTransaction(rtx);
        for (AbsAxis axis : mVars) {
            axis.setTransaction(rtx);
        }

        mSatisfy.setTransaction(rtx);
    }

}
