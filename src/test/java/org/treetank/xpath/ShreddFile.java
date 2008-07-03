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

package org.treetank.xpath;

import java.io.File;

import org.treetank.api.IReadTransaction;
import org.treetank.api.ISession;
import org.treetank.sessionlayer.ItemList;
import org.treetank.sessionlayer.Session;
import org.treetank.sessionlayer.SessionConfiguration;
import org.treetank.xmllayer.XMLShredder;

public class ShreddFile {

  public static final String XML =
      "src"
          + File.separator
          + "test"
          + File.separator
          + "ressources"
          + File.separator
          + "test.xml";

  public static final String PATH =
      "target" + File.separator + "tnk" + File.separator + "test.tnk";

  public static void main(String[] args) {
    // Setup parsed session.
    XMLShredder.shred(XML, new SessionConfiguration(PATH));

    // Verify.
    final ISession session = Session.beginSession(PATH);
    final IReadTransaction rtx = session.beginReadTransaction(new ItemList());
    rtx.moveToDocumentRoot();

  }

}
