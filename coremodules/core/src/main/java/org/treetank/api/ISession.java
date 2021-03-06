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

package org.treetank.api;

import org.treetank.exception.AbsTTException;

/**
 * <h1>ISession</h1>
 * 
 * <h2>Description</h2>
 * 
 * <p>
 * Each <code>IDatabase</code> is bound to multiple instances implementing <code>ISession</code>. Transactions
 * can then be started from this instance. There can only be one <code>INodeWriteTrx</code> at the time.
 * However, multiple <code>IReadTransactions</code> can coexist concurrently.
 * </p>
 * 
 */
public interface ISession {

    /**
     * Begin a read-only transaction on the latest committed revision key.
     * 
     * @throws AbsTTException
     *             If can't begin Read Transaction.
     * @return INodeReadTrx instance.
     */
    INodeReadTrx beginNodeReadTransaction() throws AbsTTException;

    /**
     * Begin a read-only transaction on the given revision key.
     * 
     * @param pRev
     *            Revision key to read from.
     * @throws AbsTTException
     *             If can't begin Read Transaction.
     * @return {@link INodeReadTrx} instance
     */
    INodeReadTrx beginNodeReadTransaction(final long pRev) throws AbsTTException;

    /**
     * Begin exclusive read/write transaction .
     * 
     * @throws AbsTTException
     *             If can't begin Write Transaction.
     * @return INodeWriteTrx instance.
     */
    INodeWriteTrx beginNodeWriteTransaction() throws AbsTTException;

    /**
     * Begin exclusive read transaction on the page layer
     * 
     * @param pRevKey
     *            revision key for the revision ask
     * @return a {@link IPageReadTrx} instance
     * @throws AbsTTException
     */
    IPageReadTrx beginPageReadTransaction(final long pRevKey) throws AbsTTException;

    /**
     * Safely close session and immediately release all resources. If there are
     * running transactions, they will automatically be closed.
     * 
     * This is an idempotent operation and does nothing if the session is
     * already closed.
     * 
     * @throws AbsTTException
     *             If can't close session.
     */
    void close() throws AbsTTException;

}
