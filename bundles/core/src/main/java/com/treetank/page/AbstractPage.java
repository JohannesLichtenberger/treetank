/*
 * Copyright (c) 2008, Marc Kramis (Ph.D. Thesis), University of Konstanz
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
 * $Id: AbstractPage.java 4443 2008-08-30 16:28:14Z kramis $
 */

package com.treetank.page;

import com.treetank.access.WriteTransactionState;
import com.treetank.exception.TreetankException;
import com.treetank.io.ITTSink;
import com.treetank.io.ITTSource;

/**
 * <h1>Page</h1>
 * 
 * <p>
 * Class to provide basic reference handling functionality.
 * </p>
 */
public abstract class AbstractPage {

    /** Page references. */
    private final PageReference[] mReferences;

    /** revision of this page */
    private final long mRevision;

    /**
     * Constructor to initialize instance.
     * 
     * @param referenceCount
     *            Number of references of page.
     */
    protected AbstractPage(final int referenceCount, final long revision) {

        mReferences = new PageReference[referenceCount];
        mRevision = revision;
    }

    /**
     * Read constructor.
     * 
     * @param referenceCount
     *            Number of references of page.
     * @param in
     *            Input reader to read from.
     */
    protected AbstractPage(final int referenceCount, final ITTSource in) {
        this(referenceCount, in.readLong());
        final int[] values = new int[referenceCount];
        for (int i = 0; i < values.length; i++) {
            values[i] = in.readInt();
        }
        for (int offset = 0; offset < referenceCount; offset++) {
            if (values[offset] == 1) {
                getReferences()[offset] = new PageReference(in);
            }
        }
    }

    /**
     * Clone constructor used for COW.
     * 
     * @param referenceCount
     *            Number of references of page.
     * @param committedPage
     *            Page to clone.
     */
    protected AbstractPage(final int referenceCount,
            final AbstractPage committedPage, final long revision) {

        this(referenceCount, revision);
        for (int offset = 0; offset < referenceCount; offset++) {
            if (committedPage.getReferences()[offset] != null) {
                final PageReference ref = committedPage.getReferences()[offset];
                getReferences()[offset] = new PageReference(ref);
            }
        }
    }

    /**
     * Get page reference of given offset.
     * 
     * @param offset
     *            Offset of page reference.
     * @return PageReference at given offset.
     */
    public final PageReference getReference(final int offset) {
        if (getReferences()[offset] == null) {
            getReferences()[offset] = new PageReference();
        }
        return getReferences()[offset];
    }

    /**
     * Set page reference at given offset.
     * 
     * @param offset
     *            Offset of page reference.
     * @param reference
     *            Page reference to set.
     */
    public final void setReference(final int offset,
            final PageReference reference) {
        getReferences()[offset] = reference;
    }

    /**
     * Recursively call commit on all referenced pages.
     * 
     * @param state
     *            IWriteTransaction state.
     */

    public final void commit(final WriteTransactionState state)
            throws TreetankException {
        for (final PageReference reference : getReferences()) {
            state.commit(reference);
        }
    }

    /**
     * Serialize page references into output.
     * 
     * @param out
     *            Output stream.
     */
    protected void serialize(final ITTSink out) {
        out.writeLong(mRevision);
        for (int i = 0; i < getReferences().length; i++) {
            if (getReferences()[i] != null) {
                out.writeInt(1);
            } else {
                out.writeInt(0);
            }
        }

        for (final PageReference reference : getReferences()) {
            if (reference != null) {
                reference.serialize(out);
            }
        }
    }

    /**
     * @return the mReferences
     */
    public PageReference[] getReferences() {
        return mReferences;
    }

    /**
     * @return the mRevision
     */
    public final long getRevision() {
        return mRevision;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        if (getReferences().length > 0) {
            builder.append("References: ");
            for (final PageReference ref : getReferences()) {
                if (ref != null) {
                    builder.append(ref.getKey().getIdentifier()).append(",");
                    ;
                }
            }
        } else {
            builder.append("No references");
        }
        return builder.toString();
    }

}