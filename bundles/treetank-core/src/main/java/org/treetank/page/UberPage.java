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

package org.treetank.page;

import org.treetank.io.ITTSink;
import org.treetank.io.ITTSource;
import org.treetank.node.DocumentRootNode;
import org.treetank.settings.EFixed;
import org.treetank.utils.IConstants;

/**
 * <h1>UberPage</h1>
 * 
 * <p>
 * Uber page holds a reference to the static revision root page tree.
 * </p>
 */
public final class UberPage extends AbsPage {

    /** Offset of indirect page reference. */
    private static final int INDIRECT_REFERENCE_OFFSET = 0;

    /** Number of revisions. */
    private final long mRevisionCount;

    /** True if this uber page is the uber page of a fresh TreeTank file. */
    private boolean mBootstrap;

    /**
     * s Create uber page.
     */
    public UberPage() {
        super(1, IConstants.UBP_ROOT_REVISION_NUMBER);
        mRevisionCount = IConstants.UBP_ROOT_REVISION_COUNT;
        mBootstrap = true;

        // --- Create revision tree
        // ------------------------------------------------

        // Initialize revision tree to guarantee that there is a revision root
        // page.
        AbsPage page = null;
        PageReference reference = getReference(INDIRECT_REFERENCE_OFFSET);

        // Remaining levels.
        for (int i = 0, l = IConstants.INP_LEVEL_PAGE_COUNT_EXPONENT.length; i < l; i++) {
            page = new IndirectPage(IConstants.UBP_ROOT_REVISION_NUMBER);
            reference.setPage(page);
            reference = page.getReference(0);
        }

        final RevisionRootPage rrp = new RevisionRootPage();
        reference.setPage(rrp);

        // --- Create node tree
        // ----------------------------------------------------

        // Initialize revision tree to guarantee that there is a revision root
        // page.
        page = null;
        reference = rrp.getIndirectPageReference();

        // Remaining levels.
        for (int i = 0, l = IConstants.INP_LEVEL_PAGE_COUNT_EXPONENT.length; i < l; i++) {
            page = new IndirectPage(IConstants.UBP_ROOT_REVISION_NUMBER);
            reference.setPage(page);
            reference = page.getReference(0);
        }

        final NodePage ndp =
            new NodePage((Long)EFixed.ROOT_PAGE_KEY.getStandardProperty(),
                IConstants.UBP_ROOT_REVISION_NUMBER);
        reference.setPage(ndp);

        ndp.setNode(0, DocumentRootNode.createData());
        rrp.incrementMaxNodeKey();

    }

    /**
     * Read uber page.
     * 
     * @param mIn
     *            Input bytes.
     */
    protected UberPage(final ITTSource mIn) {
        super(1, mIn);
        mRevisionCount = mIn.readLong();
        mBootstrap = false;
    }

    /**
     * Clone uber page.
     * 
     * @param mCommittedUberPage
     *            Page to clone.
     * @param revisionToUse
     *            Revision number to use.
     */
    public UberPage(final UberPage mCommittedUberPage, final long revisionToUse) {
        super(1, mCommittedUberPage, revisionToUse);
        if (mCommittedUberPage.isBootstrap()) {
            mRevisionCount = mCommittedUberPage.mRevisionCount;
            mBootstrap = mCommittedUberPage.mBootstrap;
        } else {
            mRevisionCount = mCommittedUberPage.mRevisionCount + 1;
            mBootstrap = false;
        }

    }

    /**
     * Get indirect page reference.
     * 
     * @return Indirect page reference.
     */
    public PageReference getIndirectPageReference() {
        return getReference(INDIRECT_REFERENCE_OFFSET);
    }

    /**
     * Get number of revisions.
     * 
     * @return Number of revisions.
     */
    public long getRevisionCount() {
        return mRevisionCount;
    }

    /**
     * Get key of last committed revision.
     * 
     * @return Key of last committed revision.
     */
    public long getLastCommittedRevisionNumber() {
        if (mRevisionCount == IConstants.UBP_ROOT_REVISION_COUNT) {
            return IConstants.UBP_ROOT_REVISION_NUMBER;
        } else {
            return mRevisionCount - 2;
        }
    }

    /**
     * Get revision key of current in-memory state.
     * 
     * @return Revision key.
     */
    public long getRevisionNumber() {
        if (mRevisionCount == IConstants.UBP_ROOT_REVISION_COUNT) {
            return IConstants.UBP_ROOT_REVISION_NUMBER;
        } else {
            return mRevisionCount - 1;
        }
    }

    /**
     * Flag to indicate whether this uber page is the first ever.
     * 
     * @return True if this uber page is the first one of the TreeTank file.
     */
    public boolean isBootstrap() {
        return mBootstrap;
    }

    // /**
    // * {@inheritDoc}
    // */
    // @Override
    // public final void commit(final WriteTransactionState state) {
    // super.commit(state);
    // mBootstrap = false;
    // }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void serialize(final ITTSink mOut) {
        mBootstrap = false;
        super.serialize(mOut);
        mOut.writeLong(mRevisionCount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return super.toString() + ": revisionCount=" + mRevisionCount + ", indirectPage=("
            + getReference(INDIRECT_REFERENCE_OFFSET) + "), isBootstrap=" + mBootstrap;
    }

}