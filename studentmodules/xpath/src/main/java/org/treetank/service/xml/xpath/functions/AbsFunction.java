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

package org.treetank.service.xml.xpath.functions;

import java.util.List;

import org.treetank.api.INodeReadTransaction;
import org.treetank.axis.AbsAxis;
import org.treetank.exception.TTXPathException;
import org.treetank.node.AtomicValue;
import org.treetank.service.xml.xpath.EXPathError;
import org.treetank.service.xml.xpath.expr.AbsExpression;

/**
 * <h1>AbstractFunction</h1>
 * <p>
 * Abstract super class for all function classes.
 * </p>
 * <p>
 * All functions that extend the abstract class only need take care of the result computation. Everything
 * else, like checking if arguments are valid and adding the result with the corresponding type to the
 * transaction list is done by the abstract super class.
 * </p>
 * <h2>Developer Example</h2>
 * 
 * <p>
 * 
 * <pre>
 *   Must extend &lt;code&gt;AbstractFunction&lt;/code&gt; and implement &lt;code&gt;IAxis&lt;/code&gt;.
 *   And the implement the abstract method computeResult(), that returns the 
 *   computed value as a bye-array
 *   
 *   
 *   
 *   public class ExampleFunctionAxis extends AbstractFunction implements IAxis {
 *  
 *     public ExampleAxis(final INodeReadTransaction rtx, final List&lt;IAxis&gt; args,
 *       final int min, final int max, final int returnType) {
 *       // Must be called as first.
 *       super(rtx, args, min, max, returnType);
 *     }
 *     protected byte[] computeResult() {
 *       .... compute value and return as byte array
 *     }
 *   
 *   }
 * </pre>
 * 
 * </p>
 */
public abstract class AbsFunction extends AbsExpression {

    /** The function's arguments. */
    private final List<AbsAxis> mArgs;

    /** Minimum number of possible function arguments. */
    private final int mMin;

    /** Maximum number of possible function arguments. */
    private final int mMax;

    /** The function's return type. */
    private final int mReturnType;

    /**
     * Constructor. Initializes internal state and do a statical analysis
     * concerning the function's arguments.
     * 
     * @param rtx
     *            Transaction to operate on
     * @param args
     *            List of function arguments
     * @param min
     *            min number of allowed function arguments
     * @param max
     *            max number of allowed function arguments
     * @param returnType
     *            the type that the function's result will have
     * @throws TTXPathException
     *             if the verify process is failing.
     */
    public AbsFunction(final INodeReadTransaction rtx, final List<AbsAxis> args, final int min,
        final int max, final int returnType) throws TTXPathException {

        super(rtx);
        mArgs = args;
        mMin = min;
        mMax = max;
        mReturnType = returnType;
        varifyParam(args.size());
    }

    /**
     * Checks if the number of input arguments of this function is a valid
     * according to the function specification in <a
     * href="http://www.w3.org/TR/xquery-operators/"> XQuery 1.0 and XPath 2.0
     * Functions and Operators</a>. Throws an XPath error in case of a non-valid
     * number.
     * 
     * @param mNumber
     *            number of given function arguments
     * @throws TTXPathException
     *             if function call fails.
     */
    public final void varifyParam(final int mNumber) throws TTXPathException {

        if (mNumber < mMin || mNumber > mMax) {
            throw EXPathError.XPST0017.getEncapsulatedException();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset(final long mNodeKey) {

        super.reset(mNodeKey);
        if (mArgs != null) {
            for (AbsAxis ax : mArgs) {
                ax.reset(mNodeKey);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AtomicValue evaluate() throws TTXPathException {

        // compute the function's result
        final byte[] value = computeResult();

        // create an atomic value, add it to the list and move the cursor to it.
        AtomicValue val = new AtomicValue(value, mReturnType);
        final int itemKey = getItemList().addItem(val);
        moveTo(itemKey);
        return val;

    }

    /**
     * Computes the result value of the function. This implementation is acts as
     * a hook operation and needs to be overridden by the concrete function
     * classes, otherwise an exception is thrown.
     * 
     * @return value of the result
     * @throws TTXPathException
     *             if anythin odd happens while execution
     */
    protected abstract byte[] computeResult() throws TTXPathException;

    /**
     * @return the list of function arguments
     */
    protected List<AbsAxis> getArgs() {

        return mArgs;
    }

}
