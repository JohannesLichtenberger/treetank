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

package org.treetank.io;

import org.treetank.exception.TTIOException;
import org.treetank.page.PageReference;
import org.treetank.page.delegates.PageDelegate;
import org.treetank.page.interfaces.IPage;

/**
 * Interface for reading the stored pages in every backend.
 * 
 * @author Sebastian Graf, University of Konstanz
 * 
 */
public interface IReader {

    /**
     * Getting the first reference of the <code>Uberpage</code>.
     * 
     * @return a {@link PageReference} with link to the first reference
     * @throws TTIOException
     *             if something bad happens
     */
    PageReference readFirstReference() throws TTIOException;

    /**
     * Getting a reference for the given pointer.
     * 
     * @param pKey
     *            the reference for the page to be determined
     * @return a {@link PageDelegate} as the base for a page
     * @throws TTIOException
     *             if something bad happens during read
     */
    IPage read(final IKey pKey) throws TTIOException;

    /**
     * Closing the storage.
     * 
     * @throws TTIOException
     *             if something bad happens while access
     */
    void close() throws TTIOException;

}
