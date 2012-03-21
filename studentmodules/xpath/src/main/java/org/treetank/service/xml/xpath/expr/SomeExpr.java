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

package org.treetank.service.xml.xpath.expr;

import java.util.List;

import org.treetank.api.INodeReadTransaction;
import org.treetank.axis.AbsAxis;
import org.treetank.service.xml.xpath.AtomicValue;
import org.treetank.utils.TypedValue;

/**
 * <h1>Some expression</h1>
 * <p>
 * IAxis that represents the quantified expression "some".
 * </p>
 * <p>
 * The quantified expression is true if at least one evaluation of the test expression has the effective
 * boolean value true; otherwise the quantified expression is false. This rule implies that, if the in-clauses
 * generate zero binding tuples, the value of the quantified expression is false.
 * </p>
 */
public class SomeExpr extends AbsExpression {

    private final List<AbsAxis> mVars;

    private final AbsAxis mSatisfy;

    private final List<AtomicValue> mToStore;

    /**
     * Constructor. Initializes the internal state.
     * 
     * @param rtx
     *            Exclusive (immutable) trx to iterate with.
     * @param mVars
     *            Variables for which the condition must be satisfied
     * @param mSatisfy
     *            condition that must be satisfied by at least one item of the
     *            variable results in order to evaluate expression to true
     */
    public SomeExpr(final INodeReadTransaction rtx, final List<AbsAxis> mVars, final AbsAxis mSatisfy,
        final List<AtomicValue> pToStore) {

        super(rtx);
        this.mVars = mVars;
        this.mSatisfy = mSatisfy;
        mToStore = pToStore;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset(final long mNodeKey) {

        super.reset(mNodeKey);

        if (mVars != null) {
            for (AbsAxis var : mVars) {
                var.reset(mNodeKey);
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
    public AtomicValue evaluate() {

        boolean satisfiesCond = false;

        for (AbsAxis axis : mVars) {
            while (axis.hasNext()) {
                if (mSatisfy.hasNext()) {
                    // condition is satisfied for this item -> expression is
                    // true
                    satisfiesCond = true;
                    break;
                }
            }
        }

        AtomicValue val =
            new AtomicValue(TypedValue.getBytes(Boolean.toString(satisfiesCond)), getTransaction()
                .keyForName("xs:boolean"));
        mToStore.add(val);
        final int mItemKey = getTransaction().getItemList().addItem(val);
        getTransaction().moveTo(mItemKey);
        return val;

    }

}
