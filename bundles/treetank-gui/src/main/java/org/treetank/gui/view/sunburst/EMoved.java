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
package org.treetank.gui.view.sunburst;

import java.util.Stack;

import org.treetank.api.IReadTransaction;
import org.treetank.node.AbsStructNode;

/**
 * Determines movement of transaction and updates stacks accordingly.
 * 
 * @author Johannes Lichtenberger, University of Konstanz
 * 
 */
enum EMoved {

    /** Start of traversal or cursor moved to a right sibling of a node. */
    STARTRIGHTSIBL {
        /**
         * {@inheritDoc}
         */
        @Override
        void processMove(final IReadTransaction paramRtx, final Item paramItem,
            final Stack<Float> paramAngleStack, final Stack<Float> paramExtensionStack,
            final Stack<Long> paramChildrenPerDepth, final Stack<Integer> paramParentStack,
            final Stack<Integer> paramDescendantsStack) {
            // Do nothing.
        }

        /**
         * {@inheritDoc}
         */
        @Override
        void processCompareMove(final IReadTransaction paramRtx, final Item paramItem,
            final Stack<Float> paramAngleStack, final Stack<Float> paramExtensionStack,
            final Stack<Integer> paramDescendants, final Stack<Integer> paramParentStack,
            final Stack<Integer> paramModificationStack) {
            // Do nothing.
        }
    },

    /** Next node is a child of the current node. */
    CHILD {
        /**
         * {@inheritDoc}
         */
        @Override
        void processMove(final IReadTransaction paramRtx, final Item paramItem,
            final Stack<Float> paramAngleStack, final Stack<Float> paramExtensionStack,
            final Stack<Long> paramChildrenPerDepth, final Stack<Integer> paramParentStack,
            final Stack<Integer> paramDescendantsStack) {
            assert !paramAngleStack.empty();
            paramItem.mAngle = paramAngleStack.peek();
            assert !paramExtensionStack.empty();
            paramItem.mExtension = paramExtensionStack.peek();
            assert !paramChildrenPerDepth.empty();
            paramItem.mChildCountPerDepth = childCountPerDepth(paramRtx);
            assert !paramParentStack.empty();
            paramItem.mIndexToParent = paramParentStack.peek();
            assert !paramDescendantsStack.empty();
            paramItem.mParentDescendantCount = paramDescendantsStack.peek();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        void processCompareMove(final IReadTransaction paramRtx, final Item paramItem,
            final Stack<Float> paramAngleStack, final Stack<Float> paramExtensionStack,
            final Stack<Integer> paramDescendantsStack, final Stack<Integer> paramParentStack,
            final Stack<Integer> paramModificationStack) {
            assert !paramAngleStack.empty();
            paramItem.mAngle = paramAngleStack.peek();
            assert !paramExtensionStack.empty();
            paramItem.mExtension = paramExtensionStack.peek();
            assert !paramDescendantsStack.empty();
            paramItem.mParentDescendantCount = paramDescendantsStack.peek();
            assert !paramParentStack.empty();
            paramItem.mIndexToParent = paramParentStack.peek();
            assert !paramModificationStack.empty();
            paramItem.mParentModificationCount = paramModificationStack.peek();
        }
    },

    /** Next node is the rightsibling of the first anchestor node which has one. */
    ANCHESTSIBL {
        /**
         * {@inheritDoc}
         */
        @Override
        void processMove(final IReadTransaction paramRtx, final Item paramItem,
            final Stack<Float> paramAngleStack, final Stack<Float> paramExtensionStack,
            final Stack<Long> paramChildrenPerDepth, final Stack<Integer> paramParentStack,
            final Stack<Integer> paramDescendantsStack) {
            assert !paramAngleStack.empty();
            paramItem.mAngle = paramAngleStack.pop();
            assert !paramExtensionStack.empty();
            paramItem.mAngle += paramExtensionStack.pop();
            assert !paramExtensionStack.empty();
            paramItem.mExtension = paramExtensionStack.peek();
            assert !paramParentStack.empty();
            paramParentStack.pop();
            assert !paramParentStack.empty();
            paramItem.mIndexToParent = paramParentStack.peek();
            assert !paramChildrenPerDepth.empty();
            paramItem.mChildCountPerDepth = paramChildrenPerDepth.pop();
            assert !paramDescendantsStack.empty();
            paramDescendantsStack.pop();
            assert !paramDescendantsStack.empty();
            paramItem.mParentDescendantCount = paramDescendantsStack.peek();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        void processCompareMove(final IReadTransaction paramRtx, final Item paramItem,
            final Stack<Float> paramAngleStack, final Stack<Float> paramExtensionStack,
            final Stack<Integer> paramDescendantsStack, final Stack<Integer> paramParentStack,
            final Stack<Integer> paramModificationStack) {
            assert !paramAngleStack.empty();
            paramItem.mAngle = paramAngleStack.pop();
            assert !paramExtensionStack.empty();
            paramItem.mAngle += paramExtensionStack.pop();
            assert !paramExtensionStack.empty();
            paramItem.mExtension = paramExtensionStack.peek();
            assert !paramParentStack.empty();
            paramParentStack.pop();
            assert !paramParentStack.empty();
            paramItem.mIndexToParent = paramParentStack.peek();
            assert !paramDescendantsStack.empty();
            paramDescendantsStack.pop();
            assert !paramDescendantsStack.empty();
            paramItem.mParentDescendantCount = paramDescendantsStack.peek();
            assert !paramModificationStack.empty();
            paramModificationStack.pop();
            assert !paramModificationStack.empty();
            paramItem.mParentModificationCount = paramModificationStack.peek();
        }
    };

    /**
     * Process movement of Treetank {@link IReadTransaction}.
     * 
     * @param paramRtx
     *            Treetank {@link IReadTransaction}
     * @param paramItem
     *            Item which does hold sunburst item angles and extensions
     * @param paramAngleStack
     *            stack for angles
     * @param paramExtensionStack
     *            stack for extensions
     * @param paramChildrenPerDepth
     *            stack for children per depth
     * @param paramParentStack
     *            stack for parent indexes
     * @param paramDescendantsStack
     *            stack for descendants
     */
    abstract void processMove(final IReadTransaction paramRtx, final Item paramItem,
        final Stack<Float> paramAngleStack, final Stack<Float> paramExtensionStack,
        final Stack<Long> paramChildrenPerDepth, final Stack<Integer> paramParentStack,
        final Stack<Integer> paramDescendantsStack);

    /**
     * Process movement of Treetank {@link IReadTransaction}, while comparing revisions.
     * 
     * @param paramRtx
     *            Treetank {@link IReadTransaction}
     * @param paramItem
     *            Item which does hold sunburst item angles and extensions
     * @param paramAngleStack
     *            stack for angles
     * @param paramExtensionStack
     *            stack for extensions
     * @param paramDescendantsStack
     *            stack for descendants
     * @param paramParentStack
     *            stack for parent indexes
     * @param paramModificationStack
     *            stack for modifications
     */
    abstract void processCompareMove(final IReadTransaction paramRtx, final Item paramItem,
        final Stack<Float> paramAngleStack, final Stack<Float> paramExtensionStack,
        final Stack<Integer> paramDescendantsStack, final Stack<Integer> paramParentStack,
        final Stack<Integer> paramModificationStack);

    /**
     * Traverses all right siblings and sums up child count. Thus a precondition to invoke the method
     * is that it must be called on the first child node.
     * 
     * @param paramRtx
     *            Treetank {@link IReadTransaction}
     * @return child count per depth
     */
    private static long childCountPerDepth(final IReadTransaction paramRtx) {
        long retVal = 0;
        final long key = paramRtx.getNode().getNodeKey();
        do {
            retVal += ((AbsStructNode)paramRtx.getNode()).getChildCount();
        } while (((AbsStructNode)paramRtx.getNode()).hasRightSibling() && paramRtx.moveToRightSibling());
        paramRtx.moveTo(key);
        return retVal;
    }
}