/*
 * Copyright (c) 2008, Tina Scherer (Master Thesis), University of Konstanz
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
 * $Id: $
 */
package org.treetank.xpath.filter;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.treetank.api.ISession;
import org.treetank.api.IWriteTransaction;
import org.treetank.axislayer.IAxisTest;
import org.treetank.sessionlayer.Session;
import org.treetank.utils.TestDocument;
import org.treetank.xpath.XPathAxis;


/**
 * JUnit-test class to test the functionality of the DubFilter.
 * 
 * @author Tina Scherer
 * 
 */
public class DubFilterTest {

  public static final String PATH =
      "generated" + File.separator + "DubFilterTest.tnk";

  @Before
  public void setUp() {
    
    Session.removeSession(PATH);
  }

  
  
  @Test
  public void testDupElemination() throws IOException {

    // Build simple test tree.
    final ISession session = Session.beginSession(PATH);
    final IWriteTransaction wtx = session.beginWriteTransaction();
    TestDocument.create(wtx);

    
    wtx.moveTo(2L);

    IAxisTest.testIAxisConventions(new XPathAxis(
        wtx, "child::node()/parent::node()"), new long[] {2L});

    
    IAxisTest.testIAxisConventions(new XPathAxis(
        wtx, "b/following-sibling::node()"), new long[] {7L, 8L, 11L});

    
    IAxisTest.testIAxisConventions(new XPathAxis(
        wtx, "b/preceding::node()"), new long[] {3L, 7L, 6L, 5L, 4L});
    
    
    
    IAxisTest.testIAxisConventions(new XPathAxis(
        wtx, "//c/ancestor::node()"), new long[] {4L, 2L, 8L});
    
    
    wtx.abort();
    wtx.close();
    session.close();

  }

}

