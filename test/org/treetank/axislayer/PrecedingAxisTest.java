package org.treetank.axislayer;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.treetank.api.ISession;
import org.treetank.api.IWriteTransaction;
import org.treetank.sessionlayer.Session;
import org.treetank.utils.TestDocument;

public class PrecedingAxisTest {

  public static final String PATH =
      "generated" + File.separator + "PrecedingAxisTest.tnk";

  @Before
  public void setUp() {
    Session.removeSession(PATH);
  }

  @Test
  public void testAxisConventions() {
    final ISession session = Session.beginSession(PATH);
    final IWriteTransaction wtx = session.beginWriteTransaction();
    TestDocument.create(wtx);

    wtx.moveTo(10L);
    IAxisTest
        .testIAxisConventions(new PrecedingAxis(wtx), 
            new long[] {0L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L });

    wtx.moveTo(4L);
    IAxisTest.testIAxisConventions(new PrecedingAxis(wtx), 
        new long[] {0L, 2L, 3L});

    wtx.moveTo(11L);
    IAxisTest.testIAxisConventions(new PrecedingAxis(wtx), 
        new long[] {0L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L});


    wtx.moveTo(2L);
    IAxisTest.testIAxisConventions(new PrecedingAxis(wtx), 
        new long[] {0L});
    
    wtx.moveTo(8L);
    wtx.moveToAttribute(0);
    IAxisTest.testIAxisConventions(new PrecedingAxis(wtx), 
        new long[] {});

    wtx.abort();
    wtx.close();
    session.close();

  }

 
}

