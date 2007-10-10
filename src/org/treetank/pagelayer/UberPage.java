/*
 * TreeTank - Embedded Native XML Database
 * 
 * Copyright 2007 Marc Kramis
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

final public class UberPage extends AbstractPage implements IPage {

  private long mMaxRevisionKey;

  private PageReference mIndirectRevisionRootPageReference;

  private RevisionRootPage mCurrentRevisionRootPage;

  private StaticTree mStaticTree;

  /**
   * Constructor to assure minimal common setup.
   * 
   * @param pageCache IPageCache to read from.
   */
  private UberPage(final PageCache pageCache) {
    super(pageCache);
    mIndirectRevisionRootPageReference = null;
    mStaticTree = null;
  }

  /**
   * Create new uncommitted in-memory uber page.
   * 
   * @param pageCache
   * @return
   * @throws Exception
   */
  public static final UberPage create(final PageCache pageCache)
      throws Exception {

    final UberPage uberPage = new UberPage(pageCache);

    // Make sure that all references are instantiated.
    uberPage.mMaxRevisionKey = IConstants.UBP_INIT_ROOT_REVISION_KEY;

    // Indirect pages (shallow init).
    uberPage.mIndirectRevisionRootPageReference = createPageReference();
    uberPage.mStaticTree =
        new StaticTree(uberPage.mIndirectRevisionRootPageReference, pageCache);

    // Make sure that the first empty revision root page already exists.
    uberPage.mCurrentRevisionRootPage =
        RevisionRootPage.create(pageCache, IConstants.UBP_ROOT_REVISION_KEY);

    return uberPage;

  }

  /**
   * Read committed uber page from disk.
   * 
   * @param pageCache
   * @param in
   * @throws Exception
   */
  public static final UberPage read(
      final PageCache pageCache,
      final FastByteArrayReader in) throws Exception {

    final UberPage uberPage = new UberPage(pageCache);

    // Deserialize uber page.
    uberPage.mMaxRevisionKey = in.readPseudoLong();

    // Indirect pages (shallow load without indirect page instances).
    uberPage.mIndirectRevisionRootPageReference = readPageReference(in);
    uberPage.mStaticTree =
        new StaticTree(uberPage.mIndirectRevisionRootPageReference, pageCache);

    // Make sure latest revision root page is active.
    uberPage.mCurrentRevisionRootPage =
        uberPage.getRevisionRootPage(uberPage.mMaxRevisionKey);

    return uberPage;
  }

  /**
   * COW committed uber page to modify it.
   * 
   * @param committedUberPage
   * @return
   */
  public static final UberPage clone(final UberPage committedUberPage) {

    final UberPage uberPage = new UberPage(committedUberPage.mPageCache);

    // COW uber page.
    uberPage.mMaxRevisionKey = committedUberPage.mMaxRevisionKey;

    // Indirect pages (shallow COW without page instances).
    uberPage.mIndirectRevisionRootPageReference =
        clonePageReference(committedUberPage.mIndirectRevisionRootPageReference);
    uberPage.mStaticTree =
        new StaticTree(
            uberPage.mIndirectRevisionRootPageReference,
            uberPage.mPageCache);

    uberPage.mCurrentRevisionRootPage =
        committedUberPage.mCurrentRevisionRootPage;

    return uberPage;
  }

  public final long getMaxRevisionKey() {
    return mMaxRevisionKey;
  }

  public final RevisionRootPage getRevisionRootPage(final long revisionKey)
      throws Exception {

    RevisionRootPage page =
        mPageCache.dereferenceRevisionRootPage(mStaticTree.get(revisionKey));

    return RevisionRootPage.clone(revisionKey, page);

  }

  public final RevisionRootPage prepareRevisionRootPage() throws Exception {

    // Calculate number of levels and offsets of these levels.
    final int[] offsets =
        StaticTree.calcIndirectPageOffsets(mMaxRevisionKey + 1);

    // Which page reference to COW on immediate level 0?
    mCurrentRevisionRootPage =
        RevisionRootPage.clone(mMaxRevisionKey + 1, mCurrentRevisionRootPage);

    // Indirect reference.
    PageReference reference = mIndirectRevisionRootPageReference;
    IPage page = null;

    //    Remaining levels.
    for (int i = 0; i < offsets.length; i++) {
      page = prepareIndirectPage(reference);
      reference = ((IndirectPage) page).getPageReference(offsets[i]);
    }
    reference.setPage(mCurrentRevisionRootPage);

    return mCurrentRevisionRootPage;

  }

  /**
   * {@inheritDoc}
   */
  public final void commit(final PageWriter pageWriter) throws Exception {
    commit(pageWriter, mIndirectRevisionRootPageReference);
    mMaxRevisionKey += 1;
  }

  /**
   * {@inheritDoc}
   */
  public final void serialize(final FastByteArrayWriter out) throws Exception {
    out.writePseudoLong(mMaxRevisionKey);
    serialize(out, mIndirectRevisionRootPageReference);
  }

}
