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
package org.treetank.diff;

import java.util.Set;

import org.treetank.api.IDatabase;
import org.treetank.api.IReadTransaction;
import org.treetank.diff.DiffFactory.EDiffKind;
import org.treetank.exception.AbsTTException;
import org.treetank.node.ENodes;
import org.treetank.node.ElementNode;

/**
 * Full diff including attributes and namespaces. Note that this class is thread safe.
 * 
 * @author Johannes Lichtenberger, University of Konstanz
 * 
 */
final class FullDiff extends AbsDiff {

    /**
     * Constructor.
     * 
     * @param paramDb
     *            {@link IDatabase} instance
     * @param paramKey
     *            key of (sub)tree to check
     * @param paramNewRev
     *            new revision key
     * @param paramOldRev
     *            old revision key
     * @param paramDiffKind
     *            kind of diff (optimized or not)
     * @param paramObservers
     *            {@link Set} of Observers, which listen for the kinds of diff between two nodes
     * @throws AbsTTException
     *             if retrieving session fails
     */
    FullDiff(final IDatabase paramDb, final long paramKey, final long paramNewRev, final long paramOldRev,
        final EDiffKind paramDiffKind, final Set<IDiffObserver> paramObservers) throws AbsTTException {
        super(paramDb, paramKey, paramNewRev, paramOldRev, paramDiffKind, paramObservers);
    }

    /** {@inheritDoc} */
    @Override
    boolean checkNodes(final IReadTransaction paramFirstRtx, final IReadTransaction paramSecondRtx) {
        assert paramFirstRtx != null;
        assert paramSecondRtx != null;

        boolean found = false;

        if (paramFirstRtx.getNode().getNodeKey() == paramSecondRtx.getNode().getNodeKey()
            && paramFirstRtx.getNode().equals(paramSecondRtx.getNode())) {
            final long nodeKey = paramFirstRtx.getNode().getNodeKey();

            if (paramFirstRtx.getNode().getKind() == ENodes.ELEMENT_KIND) {
                if (((ElementNode)paramFirstRtx.getNode()).getNamespaceCount() == 0
                    && ((ElementNode)paramFirstRtx.getNode()).getAttributeCount() == 0
                    && ((ElementNode)paramSecondRtx.getNode()).getAttributeCount() == 0
                    && ((ElementNode)paramSecondRtx.getNode()).getNamespaceCount() == 0) {
                    found = true;
                } else {
                    if (((ElementNode)paramFirstRtx.getNode()).getNamespaceCount() == 0) {
                        found = true;
                    } else {
                        for (int i = 0; i < ((ElementNode)paramFirstRtx.getNode()).getNamespaceCount(); i++) {
                            paramFirstRtx.moveToNamespace(i);
                            for (int j = 0; j < ((ElementNode)paramSecondRtx.getNode()).getNamespaceCount(); j++) {
                                paramSecondRtx.moveToNamespace(i);

                                if (paramFirstRtx.getNode().equals(paramSecondRtx.getNode())) {
                                    found = true;
                                    break;
                                }
                            }
                            paramFirstRtx.moveTo(nodeKey);
                            paramSecondRtx.moveTo(nodeKey);
                        }
                    }

                    if (found) {
                        for (int i = 0; i < ((ElementNode)paramFirstRtx.getNode()).getAttributeCount(); i++) {
                            paramFirstRtx.moveToAttribute(i);
                            for (int j = 0; j < ((ElementNode)paramSecondRtx.getNode()).getAttributeCount(); j++) {
                                paramSecondRtx.moveToAttribute(i);

                                if (paramFirstRtx.getNode().equals(paramSecondRtx.getNode())) {
                                    found = true;
                                    break;
                                }
                            }
                            paramFirstRtx.moveTo(nodeKey);
                            paramSecondRtx.moveTo(nodeKey);
                        }
                    }
                }
            } else {
                found = true;
            }
        }

        return found;
    }
}