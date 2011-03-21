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

package org.treetank.io.berkeley;

import com.sleepycat.bind.tuple.TupleInput;

import org.treetank.io.ITTSource;

/**
 * {@link ITTSource} implementation for the BerkeleyDB-Layer.
 * 
 * @author Sebastian Graf, University of Konstanz
 * 
 */
public final class TupleInputSource implements ITTSource {

    /** {@link TupleInput} to be wrapped. */
    private transient final TupleInput mInput;

    /**
     * Constructor.
     * 
     * @param paramInput
     *            to be wrapped
     */
    public TupleInputSource(final TupleInput paramInput) {
        mInput = paramInput;
    }

    /**
     * {@inheritDoc}
     */
    public byte readByte() {
        return mInput.readByte();
    }

    /**
     * {@inheritDoc}
     */
    public long readLong() {
        return mInput.readLong();
    }

    /**
     * {@inheritDoc}
     */
    public int readInt() {
        return mInput.readInt();
    }

}