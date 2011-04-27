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

import java.util.Arrays;

import org.treetank.encryption.NodeEncryption;
import org.treetank.io.ITTSink;
import org.treetank.io.ITTSource;
import org.treetank.node.AbsNode;
import org.treetank.node.ENodes;
import org.treetank.node.io.NodeInputSource;
import org.treetank.node.io.NodeOutputSink;
import org.treetank.utils.IConstants;

/**
 * <h1>NodePage</h1>
 * 
 * <p>
 * A node page stores a set of nodes.
 * </p>
 */
public class NodePage extends AbsPage {

    /** Key of node page. This is the base key of all contained nodes. */
    private final long mNodePageKey;

    /** Array of nodes. This can have null nodes that were removed. */
    private final AbsNode[] mNodes;

    private final static boolean NODE_ENCRYPTION = false;

    /**
     * Create node page.
     * 
     * @param nodePageKey
     *            Base key assigned to this node page.
     */
    public NodePage(final long nodePageKey, final long mRevision) {
        super(0, mRevision);
        mNodePageKey = nodePageKey;
        mNodes = new AbsNode[IConstants.NDP_NODE_COUNT];
    }

    /**
     * Read node page.
     * 
     * @param mIn
     *            Input bytes to read page from.
     */
    protected NodePage(final ITTSource mIn) {
        super(0, mIn);
        mNodePageKey = mIn.readLong();
        mNodes = new AbsNode[IConstants.NDP_NODE_COUNT];

        if (NODE_ENCRYPTION) {

            for (int i = 0; i < mNodes.length; i++) {
                final int mRightKey = mIn.readInt();

                if (mRightKey == 1) {
                    final int mNodeBytes = mIn.readInt();

                    final byte[] mEncryptedNode = new byte[mNodeBytes];

                    for (int j = 0; j < mNodeBytes; j++) {
                        mEncryptedNode[j] = mIn.readByte();
                    }
                    final byte[] mDecryptedNode = NodeEncryption.getInstance().decrypt(mEncryptedNode);
                    final NodeInputSource mNodeInput = new NodeInputSource(mDecryptedNode);

                    final int mElementKind = mNodeInput.readInt();
                    final ENodes mEnumKind = ENodes.getEnumKind(mElementKind);

                    if (mEnumKind != ENodes.UNKOWN_KIND) {
                        getNodes()[i] = mEnumKind.createNodeFromPersistence(mNodeInput);
                    }

                } else {
                    // TODO: what happens if user has no permission for that node
                }
            }
        } else {
            final int[] kinds = new int[IConstants.NDP_NODE_COUNT];
            for (int i = 0; i < kinds.length; i++) {
                kinds[i] = mIn.readInt();
            }

            for (int offset = 0; offset < IConstants.NDP_NODE_COUNT; offset++) {
                final int kind = kinds[offset];
                final ENodes enumKind = ENodes.getEnumKind(kind);
                if (enumKind != ENodes.UNKOWN_KIND) {
                    getNodes()[offset] = enumKind.createNodeFromPersistence(mIn);
                }
            }

        }
    }

    /**
     * Clone node page.
     * 
     * @param mCommittedNodePage
     *            Node page to clone.
     */
    protected NodePage(final NodePage mCommittedNodePage, final long mRevisionToUse) {
        super(0, mCommittedNodePage, mRevisionToUse);
        mNodePageKey = mCommittedNodePage.mNodePageKey;
        mNodes = new AbsNode[IConstants.NDP_NODE_COUNT];
        // Deep-copy all nodes.
        for (int offset = 0; offset < IConstants.NDP_NODE_COUNT; offset++) {
            final AbsNode node = mCommittedNodePage.getNodes()[offset];
            if (node != null) {
                getNodes()[offset] = node.clone();
                // getNodes()[offset] = NodePersistenter.createNode(node);
            }
        }
    }

    /**
     * Get key of node page.
     * 
     * @return Node page key.
     */
    public final long getNodePageKey() {
        return mNodePageKey;
    }

    /**
     * Get node at a given offset.
     * 
     * @param mOffset
     *            Offset of node within local node page.
     * @return Node at given offset.
     */
    public AbsNode getNode(final int mOffset) {
        return getNodes()[mOffset];
    }

    /**
     * Overwrite a single node at a given offset.
     * 
     * @param mOffset
     *            Offset of node to overwrite in this node page.
     * @param mNode
     *            Node to store at given nodeOffset.
     */
    public void setNode(final int mOffset, final AbsNode mNode) {
        getNodes()[mOffset] = mNode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void serialize(final ITTSink mOut) {
        super.serialize(mOut);
        mOut.writeLong(mNodePageKey);

        if (NODE_ENCRYPTION) {
            NodeOutputSink mNodeOut = null;

            for (final AbsNode node : getNodes()) {

                mOut.writeInt(1); // write right key (in that case 1 for all)
                mNodeOut = new NodeOutputSink();

                if (node != null) {
                    final int kind = node.getKind().getNodeIdentifier();
                    mNodeOut.writeInt(kind);

                    node.serialize(mNodeOut);

                } else {
                    mNodeOut.writeInt(ENodes.UNKOWN_KIND.getNodeIdentifier());
                }

                final byte[] mStreamAsByteArray = mNodeOut.getOutputStream().toByteArray();

                final byte[] mEncrypted = NodeEncryption.getInstance().encrypt(mStreamAsByteArray);

                mOut.writeInt(mEncrypted.length);

                for (byte aByte : mEncrypted) {
                    mOut.writeByte(aByte);
                }

            }
        } else {
            for (int i = 0; i < getNodes().length; i++) {
                if (getNodes()[i] != null) {
                    final int kind = getNodes()[i].getKind().getNodeIdentifier();
                    mOut.writeInt(kind);
                } else {
                    mOut.writeInt(ENodes.UNKOWN_KIND.getNodeIdentifier());
                }
            }

            for (final AbsNode node : getNodes()) {
                if (node != null) {
                    node.serialize(mOut);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        final StringBuilder returnString = new StringBuilder();
        returnString.append(": nodePageKey=");
        returnString.append(mNodePageKey);
        returnString.append(" nodes: \n");
        for (final AbsNode node : getNodes()) {
            if (node != null) {
                returnString.append(node.getNodeKey());
                returnString.append(",");
            }
        }
        returnString.append("\n");
        return returnString.toString();
    }

    /**
     * @return the mNodes
     */
    public final AbsNode[] getNodes() {
        return mNodes;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int)(mNodePageKey ^ (mNodePageKey >>> 32));
        result = prime * result + Arrays.hashCode(mNodes);
        return result;
    }

    @Override
    public boolean equals(final Object mObj) {
        if (this == mObj) {
            return true;
        }

        if (mObj == null) {
            return false;
        }

        if (getClass() != mObj.getClass()) {
            return false;
        }

        final NodePage mOther = (NodePage)mObj;
        if (mNodePageKey != mOther.mNodePageKey) {
            return false;
        }

        if (!Arrays.equals(mNodes, mOther.mNodes)) {
            return false;
        }

        return true;
    }

}
