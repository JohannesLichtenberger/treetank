package com.treetank.service.revIndex;

import java.io.File;
import java.util.LinkedList;
import java.util.Stack;

import javax.xml.namespace.QName;

import com.treetank.access.Database;
import com.treetank.api.IDatabase;
import com.treetank.api.IReadTransaction;
import com.treetank.api.ISession;
import com.treetank.api.IWriteTransaction;
import com.treetank.exception.TreetankException;
import com.treetank.exception.TreetankIOException;
import com.treetank.utils.NamePageHash;

/**
 * Revisioned Index Structure. Consisting of a trie and a document-trie. Both
 * structures are merged on one treetank structure and can be distinguished with
 * the help of different namespaces.
 * 
 * @author Sebastian Graf, University of Konstanz
 * 
 */
public final class RevIndex {

    final static String EMPTY_STRING = "";

    // MetaRoot Elemens
    final static String META_ELEMENT = "indexRevision";

    // MetaRoot Atts
    final static String INDEXREV_ATTRIBUTEKEY = "indexRevision";
    final static String FIRSTTTREV_ATTRIBUTEKEY = "firstTTRevision";
    final static String LASTTTREV_ATTRIBUTEKEY = "lastTTRevision";

    // Trie Elems
    final static String TRIE_ELEMENT = "t";
    final static String DOCUMENTS_REF_ROOTELEMENT = "documentReferences";
    final static String DOCUMENTREFERENCE_ELEMENTNAME = "elementReference";

    // Trie Atts
    final static String TRIE_PREFIX_ATTRIBUTEKEY = "prefix";

    // Document Elems
    final static String DOCUMENT_ELEMENT = "d";

    // Document Atts
    final static String DOCUMENT_NODE_ATTIBUTEKEY = "node";

    // Root Elemens
    final static String METAROOT_ELEMENTNAME = "metaRevRoot";
    final static String TRIEROOT_ELEMENTNAME = "trieRoot";
    final static String DOCUMENTROOT_ELEMENTNAME = "documentRoot";

    // private final static Pattern PATTERN = Pattern.compile("[a-zA-Z]+");

    private long indexRev;

    private long currentDocKey = -1;

    /**
     * {@link ISession} to the index structure
     */
    private final ISession indexSession;
    /**
     * {@link IWriteTransaction} to trie session.
     */
    private final IReadTransaction rtx;

    /**
     * Constructor
     * 
     * 
     * @param index
     *            folder to be access.
     * @param rev
     *            revision to be accessed
     * @throws TreetankException
     *             if any access to Treetank fails
     */
    public RevIndex(final File index, final long rev) throws TreetankException {
        final IDatabase db = Database.openDatabase(index);
        indexSession = db.getSession();
        if (rev < 0) {
            rtx = indexSession.beginWriteTransaction();
        } else {
            this.rtx = indexSession.beginReadTransaction(rev);
        }
    }

    public void compact(final double threshold) {

    }

    /**
     * Inserting the next node in the document tree
     * 
     * @param docs
     *            stack with order to be inserted.
     */
    public void insertNextNode(final Stack<String> docs)
            throws TreetankException {
        if (rtx instanceof IWriteTransaction) {
            final IWriteTransaction wtx = (IWriteTransaction) rtx;
            currentDocKey = DocumentTreeNavigator.adaptDocTree(wtx, docs);
        }
    }

    /**
     * Indexing a term with the given Stack full of uuids of an hierarchical
     * structure.
     * 
     * @param term
     *            the term to be indexed.
     */
    public void insertNextTermForCurrentNode(final String term)
            throws TreetankException {
        // check if rtx is instance of WriteTransaction
        if (rtx instanceof IWriteTransaction) {
            final IWriteTransaction wtx = (IWriteTransaction) rtx;

            // Navigating in the trie
            TrieNavigator.adaptTrie(wtx, term);

            // If the trienode has no child, insert the root for the
            // references..
            if (!wtx.moveToFirstChild()) {
                wtx.insertElementAsFirstChild(new QName(
                        DOCUMENTS_REF_ROOTELEMENT));
            } else
            // ..otherwise go to the first child..
            {
                boolean found = false;
                // over there, search for the document reference root in the
                // combined trie/reference structure
                do {
                    if (wtx.getNode().getNameKey() == NamePageHash
                            .generateHashForString(DOCUMENTS_REF_ROOTELEMENT)) {
                        found = true;
                        break;
                    }
                } while (wtx.moveToRightSibling());

                // if no document reference root was found, insert it, otherwise
                // go for it.
                if (!found) {
                    wtx.insertElementAsFirstChild(new QName(
                            DOCUMENTS_REF_ROOTELEMENT));
                }

            }
            if (wtx.moveToFirstChild()) {
                if (!wtx.moveToFirstChild()) {
                    throw new IllegalStateException(
                            "At each reference, there must be a corresponding text containing the reference!");
                }
                final long lastKey = Long
                        .parseLong(wtx.getValueOfCurrentNode());
                if (lastKey != currentDocKey) {
                    wtx.moveToParent();
                    wtx.moveToParent();
                    wtx.insertElementAsFirstChild(new QName(
                            DOCUMENTREFERENCE_ELEMENTNAME));
                    wtx.insertTextAsFirstChild(new StringBuilder().append(
                            currentDocKey).toString());
                }
            } else {
                wtx.insertElementAsFirstChild(new QName(
                        DOCUMENTREFERENCE_ELEMENTNAME, EMPTY_STRING));
                wtx.insertTextAsFirstChild(new StringBuilder().append(
                        currentDocKey).toString());
            }
        }
    }

    /**
     * Finishing the input for indexing one source. This results in a change of
     * the identifier stored with every field in the structure to provide an
     * unique identifier.
     * 
     * @return the index revision number.
     */
    public long finishIndexInput() throws TreetankException {
        if (rtx instanceof IWriteTransaction) {
            final IWriteTransaction wtx = (IWriteTransaction) rtx;
            try {
                wtx.commit();
            } catch (final TreetankIOException exc) {
                throw new IllegalStateException(exc);
            }
            wtx.moveToDocumentRoot();
            return indexRev;
        } else {
            return -1;
        }
    }

    /**
     * Closing the index structure. No automatic commit is performed.
     */
    public void close() {
        if (rtx instanceof IWriteTransaction) {
            try {
                ((IWriteTransaction) rtx).commit();
                rtx.close();
                indexSession.close();
            } catch (final TreetankException exc) {
                throw new IllegalStateException(exc);
            }
        }

    }

    /**
     * Getting all leave nodes for a doc-root node. If a leave in the trie is
     * reached, the following method is getting all the leaves in the document
     * structure and gives back the results as a list of keys.
     * 
     * @param docLong
     *            the root of the document-root structure
     * @return the results as a list of nodes
     */
    public LinkedList<Long> getDocumentsForDocRoot(final long docLong) {

        final LinkedList<Long> returnVal = new LinkedList<Long>();
        rtx.moveTo(docLong);
        rtx.moveToFirstChild();
        do {
            // got doc-ref-root, taking all childs
            if (rtx.getNode().getNameKey() == NamePageHash
                    .generateHashForString(DOCUMENTS_REF_ROOTELEMENT)) {
                rtx.moveToFirstChild();
                do {
                    rtx.moveToFirstChild();
                    final long key = Long
                            .parseLong(rtx.getValueOfCurrentNode());
                    returnVal.add(key);
                    rtx.moveToParent();
                } while (rtx.moveToRightSibling());
                break;
            }
        } while (rtx.moveToRightSibling());

        return returnVal;

    }

    /**
     * Getting the root of the document tree structure for a given term. The
     * search start at the root of the trie and searches the whole term in the
     * trie. If nothing is found, the <code>ENodes.UNKOWN</code> key is given
     * back.
     * 
     * @param term
     *            to be searched in the trie structure
     * @return the root for the document key
     * @throws TreetankException
     *             for handling treetank errors
     */
    public long getDocRootForTerm(final String term) throws TreetankException {
        return TrieNavigator.getDocRootInTrie(rtx, term);

    }

    /**
     * Getting all the ancestors related to a key in the document structure
     * 
     * @param key
     *            the key in the structure
     * @return a stack containing all the ancestors
     */
    public Stack<String> getAncestors(final long key) {
        this.rtx.moveTo(key);
        return DocumentTreeNavigator.getDocElements(rtx);
    }

    // private final void checkAndUpdateLeafAttribute() {
    // if (rtx instanceof IWriteTransaction) {
    // final IWriteTransaction wtx = (IWriteTransaction) rtx;
    // // if the leaf has already attributes, check against the last revs
    // if (wtx.getNode().getAttributeCount() > 0) {
    // wtx.moveToAttribute(0);
    // final long indexRev = Long.parseLong(wtx
    // .getValueOfCurrentNode());
    // if (indexRev < this.indexRev + 1) {
    // wtx.remove();
    // wtx
    // .insertAttribute(INDEXREV_ATTRIBUTEKEY, "",
    // new StringBuilder().append(indexRev + 1)
    // .toString());
    // }
    // } else {
    // // ...or insert a new attribute
    // wtx.insertAttribute(INDEXREV_ATTRIBUTEKEY, "",
    // new StringBuilder().append(indexRev + 1).toString());
    // }
    // wtx.moveToParent();
    // }
    // }

    IReadTransaction getTrans() {
        return rtx;
    }

    /**
     * Initialising basic structure.
     */
    static void initialiseBasicStructure(final IReadTransaction rtx)
            throws TreetankException {
        if (rtx instanceof IWriteTransaction) {
            final IWriteTransaction wtx = (IWriteTransaction) rtx;
            wtx.moveToDocumentRoot();
            wtx.insertElementAsFirstChild(new QName(METAROOT_ELEMENTNAME));
            wtx.insertElementAsRightSibling(new QName(TRIEROOT_ELEMENTNAME));
            wtx
                    .insertElementAsRightSibling(new QName(
                            DOCUMENTROOT_ELEMENTNAME));
            try {
                wtx.commit();
            } catch (TreetankIOException exc) {
                throw new IllegalStateException(exc);
            }
        }

    }
    // private final void cleanUpStructure() {
    // if (rtx instanceof IWriteTransaction) {
    // final IWriteTransaction wtx = (IWriteTransaction) rtx;
    //
    // moveToTrieRoot();
    //
    // final IAxis postorder = new PostOrderAxis(wtx);
    //
    // while (postorder.hasNext()) {
    //
    // boolean leaf = false;
    // for (int i = 0, inidices = wtx.getNode().getAttributeCount(); i <
    // inidices; i++) {
    // wtx.moveToAttribute(i);
    // if (wtx.getNode().getNameKey() == NamePageHash
    // .generateHashForString(INDEXREV_ATTRIBUTEKEY)) {
    // leaf = true;
    // final long nodeRev = Long.parseLong(wtx
    // .getValueOfCurrentNode());
    // if (nodeRev < indexRev + 1) {
    // wtx.moveToParent();
    // wtx.remove();
    // leaf = false;
    // // where does break end up
    // break;
    // }
    // }
    // wtx.moveToParent();
    // }
    // if (!leaf
    // && !wtx.getNode().hasFirstChild()
    // && !(wtx.getNode().getNameKey() == NamePageHash
    // .generateHashForString(TRIEROOT_ELEMENTNAME))) {
    // wtx.remove();
    // }
    //
    // postorder.next();
    // }
    //
    // }
    // }

}