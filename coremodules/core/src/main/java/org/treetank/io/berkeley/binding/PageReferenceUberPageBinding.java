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

package org.treetank.io.berkeley.binding;

import org.treetank.io.EStorage;
import org.treetank.io.berkeley.TupleInputSource;
import org.treetank.io.berkeley.TupleOutputSink;
import org.treetank.page.PageReference;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 * Binding for the PageReference of the UberPage.
 * 
 * @author Sebastian Graf, University of Konstanz
 * 
 */
public final class PageReferenceUberPageBinding extends TupleBinding<PageReference> {

    /**
     * {@inheritDoc}
     */
    @Override
    public PageReference entryToObject(final TupleInput arg0) {
        final PageReference ref = new PageReference();
        final int storageId = arg0.readInt();
        ref.setKey(EStorage.getInstance(storageId).deserialize(new TupleInputSource(arg0)));
        return ref;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void objectToEntry(final PageReference arg0, final TupleOutput arg1) {
        EStorage storage = EStorage.getInstance(arg0.getKey().getClass());
        if (storage != null) {
            storage.serialize(new TupleOutputSink(arg1), arg0.getKey());
        }
    }

}
