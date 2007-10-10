/*
 * TreeTank - Embedded Native XML Database
 * 
 * Copyright (C) 2007 Marc Kramis
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * $Id$
 */

package org.treetank.pagelayer;

import org.treetank.api.IConstants;
import org.treetank.api.IPage;
import org.treetank.utils.FastByteArrayReader;
import org.treetank.utils.FastByteArrayWriter;
import org.treetank.utils.StaticTree;

final public class RevisionRootPage extends AbstractPage implements IPage {

  private final long mRevisionKey;

  private long mRevisionSize;

  /** Map the hash of a name to its name. */
  private PageReference mNamePageReference;

  private long mMaxNodeKey;

  private PageReference mIndirectNodePageReference;

  private StaticTree mStaticTree;

  private RevisionRootPage(final PageCache pageCache, final long revisionKey) {
    super(pageCache);
    mRevisionKey = revisionKey;
    mNamePageReference = null;
    mIndirectNodePageReference = null;
    mStaticTree = null;
  }

  public static final RevisionRootPage create(
      final PageCache pageCache,
      final long revisionKey) {

    final RevisionRootPage revisionRootPage =
        new RevisionRootPage(pageCache, revisionKey);

    // Revisioning (deep init).
    revisionRootPage.mRevisionSize = 0L;

    // Name page (shallow init).
    revisionRootPage.mNamePageReference = createPageReference();
    revisionRootPage.mNamePageReference.setPage(NamePage.create(pageCache));

    // Node pages (shallow init).
    revisionRootPage.mMaxNodeKey = -1L;

    // Indirect pages (shallow init).
    revisionRootPage.mIndirectNodePageReference = createPageReference();
    revisionRootPage.mStaticTree =
        new StaticTree(revisionRootPage.mIndirectNodePageReference, pageCache);

    return revisionRootPage;

  }

  public static final RevisionRootPage read(
      final PageCache pageCache,
      final FastByteArrayReader in) throws Exception {

    final RevisionRootPage revisionRootPage =
        new RevisionRootPage(pageCache, in.readPseudoLong());

    // Revisioning (deep load).
    revisionRootPage.mRevisionSize = in.readPseudoLong();

    // Name page (shallow load without name page instance).
    revisionRootPage.mNamePageReference = readPageReference(in);

    // Node pages (shallow load without node page instances).
    revisionRootPage.mMaxNodeKey = in.readPseudoLong();

    // Indirect node pages (shallow load without indirect page instances).
    revisionRootPage.mIndirectNodePageReference = readPageReference(in);
    revisionRootPage.mStaticTree =
        new StaticTree(revisionRootPage.mIndirectNodePageReference, pageCache);

    return revisionRootPage;

  }

  public static final RevisionRootPage clone(
      final long initRevisionKey,
      final RevisionRootPage committedRevisionRootPage) {

    final RevisionRootPage revisionRootPage =
        new RevisionRootPage(
            committedRevisionRootPage.mPageCache,
            initRevisionKey);

    // Revisioning (deep COW).
    revisionRootPage.mRevisionSize = committedRevisionRootPage.mRevisionSize;

    // Names (deep COW).
    revisionRootPage.mNamePageReference =
        clonePageReference(committedRevisionRootPage.mNamePageReference);

    // INode pages (shallow COW without node page instances).
    revisionRootPage.mMaxNodeKey = committedRevisionRootPage.mMaxNodeKey;

    // Indirect node pages (shallow COW without node page instances).
    revisionRootPage.mIndirectNodePageReference =
        clonePageReference(committedRevisionRootPage.mIndirectNodePageReference);
    revisionRootPage.mStaticTree =
        new StaticTree(
            revisionRootPage.mIndirectNodePageReference,
            revisionRootPage.mPageCache);

    return revisionRootPage;
  }

  /**
   * Get key of revision.
   * 
   * @return Revision key.
   */
  public final long getRevisionKey() {
    return mRevisionKey;
  }

  /**
   * Get size of revision, i.e., the node count visible in this revision.
   * 
   * @return Revision size.
   */
  public final long getRevisionSize() {
    return mRevisionSize;
  }

  /**
   * Get name belonging to name key.
   * 
   * @param nameKey Name key identifying name.
   * @return Name of name key.
   */
  public final String getName(final int nameKey) throws Exception {
    final NamePage namePage =
        (NamePage) mPageCache.dereference(
            mNamePageReference,
            IConstants.NAME_PAGE);
    return namePage.getName(nameKey);
  }

  /**
   * Create name key given a name.
   * 
   * @param name Name to create key for.
   * @return Name key.
   */
  public final int createNameKey(final String name) throws Exception {
    final String string = (name == null ? "" : name);
    final int nameKey = string.hashCode();
    if (getName(nameKey) == null) {
      final NamePage namePage = prepareNamePage(mNamePageReference);
      namePage.setName(nameKey, string);
    }
    return nameKey;
  }

  /**
   * Get node page by node page key.
   * 
   * @param nodePageKey Key of node page.
   * @return INode page with this key.
   * @throws Exception of any kind.
   */
  public final NodePage getNodePage(final long nodePageKey) throws Exception {

    return (NodePage) mPageCache.dereference(
        mStaticTree.get(nodePageKey),
        IConstants.NODE_PAGE);

  }

  private final NodePage prepareNodePage(final long nodePageKey)
      throws Exception {

    // Calculate number of levels and offsets of these levels.
    final int[] offsets = StaticTree.calcIndirectPageOffsets(nodePageKey);

    // Which page reference to COW on immediate level 0?
    // Indirect reference.
    PageReference reference = mIndirectNodePageReference;
    IPage page = null;

    //    Remaining levels.
    for (int i = 0; i < offsets.length; i++) {
      page = prepareIndirectPage(reference);
      reference = ((IndirectPage) page).getPageReference(offsets[i]);
    }
    return prepareNodePage(reference, nodePageKey);

  }

  public final Node prepareNode(final long nodeKey) throws Exception {
    return prepareNodePage(Node.nodePageKey(nodeKey)).getNode(
        Node.nodePageOffset(nodeKey));
  }

  public final Node createNode(
      final long parentKey,
      final long firstChildKey,
      final long leftSiblingKey,
      final long rightSiblingKey,
      final int kind,
      final int localPartKey,
      final int uriKey,
      final int prefixKey,
      final byte[] value) throws Exception {

    mMaxNodeKey += 1;
    mRevisionSize += 1;

    final Node node =
        new Node(
            mMaxNodeKey,
            parentKey,
            firstChildKey,
            leftSiblingKey,
            rightSiblingKey,
            kind,
            localPartKey,
            uriKey,
            prefixKey,
            value);

    // Write node into node page.
    prepareNodePage(Node.nodePageKey(mMaxNodeKey)).setNode(
        Node.nodePageOffset(mMaxNodeKey),
        node);

    return node;
  }

  public final void removeNode(final long nodeKey) throws Exception {
    mRevisionSize -= 1;
    prepareNodePage(Node.nodePageKey(nodeKey)).setNode(
        Node.nodePageOffset(nodeKey),
        null);
  }

  /**
   * {@inheritDoc}
   */
  public final void commit(final PageWriter pageWriter) throws Exception {
    commit(pageWriter, mNamePageReference);
    commit(pageWriter, mIndirectNodePageReference);
  }

  /**
   * {@inheritDoc}
   */
  public void serialize(final FastByteArrayWriter out) throws Exception {
    out.writePseudoLong(mRevisionKey);
    out.writePseudoLong(mRevisionSize);
    serialize(out, mNamePageReference);
    out.writePseudoLong(mMaxNodeKey);
    serialize(out, mIndirectNodePageReference);
  }

}
