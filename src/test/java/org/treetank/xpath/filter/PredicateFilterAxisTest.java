
package org.treetank.xpath.filter;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.treetank.api.IReadTransaction;
import org.treetank.api.ISession;
import org.treetank.api.IWriteTransaction;
import org.treetank.axislayer.IAxisTest;
import org.treetank.sessionlayer.ItemList;
import org.treetank.sessionlayer.Session;
import org.treetank.utils.TestDocument;
import org.treetank.xpath.XPathAxis;

/**
 * JUnit-test class to test the functionality of the PredicateAxis.
 * 
 * @author Tina Scherer
 */
public class PredicateFilterAxisTest {

  public static final String PATH = "generated" + File.separator
      + "PredicateFilterAxisTest.tnk";

  @Before
  public void setUp() {

    Session.removeSession(PATH);
  }

  @Test
  public void testPredicates() throws IOException {

    // Build simple test tree.
    final ISession session = Session.beginSession(PATH);
    final IWriteTransaction wtx = session.beginWriteTransaction();
    TestDocument.create(wtx);
    IReadTransaction rtx = session.beginReadTransaction(new ItemList());

    // Find descendants starting from nodeKey 0L (root).
    rtx.moveToDocumentRoot();

    IAxisTest.testIAxisConventions(new XPathAxis(rtx, "/p:a[@i]"),
        new long[] { 2L });

//    IAxisTest.testIAxisConventions(new XPathAxis(rtx, "p:a/b[@p:x]"),
//        new long[] { 8L });

    IAxisTest.testIAxisConventions(new XPathAxis(rtx, "p:a[text()]"),
        new long[] { 2L });

    IAxisTest.testIAxisConventions(new XPathAxis(rtx, "p:a[element()]"),
        new long[] { 2L });

    IAxisTest.testIAxisConventions(new XPathAxis(rtx, "p:a[node()/text()]"),
        new long[] { 2L });

    IAxisTest.testIAxisConventions(new XPathAxis(rtx, "p:a[./node()]"),
        new long[] { 2L });

    IAxisTest.testIAxisConventions(new XPathAxis(rtx,
        "p:a[./node()/node()/node()]"), new long[] {});

    IAxisTest.testIAxisConventions(new XPathAxis(rtx, "p:a[//element()]"),
        new long[] { 2L });

    IAxisTest.testIAxisConventions(new XPathAxis(rtx, "p:a[/text()]"),
        new long[] {});

    IAxisTest.testIAxisConventions(new XPathAxis(rtx, "p:a[3<4]"),
        new long[] { 2L });

    IAxisTest.testIAxisConventions(new XPathAxis(rtx, "p:a[13>=4]"),
        new long[] { 2L });

    IAxisTest.testIAxisConventions(new XPathAxis(rtx, "p:a[13.0>=4]"),
        new long[] { 2L });

    IAxisTest.testIAxisConventions(new XPathAxis(rtx, "p:a[4 = 4]"),
        new long[] { 2L });

    IAxisTest.testIAxisConventions(new XPathAxis(rtx, "p:a[3=4]"),
        new long[] {});

    IAxisTest.testIAxisConventions(new XPathAxis(rtx, "p:a[3.2 = 3.22]"),
        new long[] {});

    rtx.moveTo(2L);

    IAxisTest.testIAxisConventions(new XPathAxis(rtx, "child::b[child::c]"),
        new long[] { 4L, 8L });

    IAxisTest.testIAxisConventions(new XPathAxis(rtx, "child::*[text() or c]"),
        new long[] { 4l, 8L });

    IAxisTest.testIAxisConventions(new XPathAxis(rtx,
        "child::*[text() or c], /node(), //c"),
        new long[] { 4l, 8L, 2L, 6L, 9L });

    rtx.close();
    wtx.abort();
    wtx.close();
    session.close();

  }

}