package com.treetank.service.xml.shredder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.treetank.TestHelper;
import com.treetank.TestHelper.PATHS;
import com.treetank.api.IDatabase;
import com.treetank.api.ISession;
import com.treetank.api.IWriteTransaction;
import com.treetank.exception.TreetankException;
import com.treetank.service.xml.serialize.XMLSerializer;
import com.treetank.service.xml.serialize.XMLSerializer.XMLSerializerBuilder;

/**
 * Test XMLUpdateShredder.
 * 
 * @author Johannes Lichtenberger, University of Konstanz
 */
public final class XMLUpdateShredderTest extends XMLTestCase {
    private static final String RESOURCES = "src" + File.separator + "test" + File.separator + "resources";

    private static final String XMLINSERTFIRST = RESOURCES + File.separator + "revXMLsInsert";

    private static final String XMLINSERTSECOND = RESOURCES + File.separator + "revXMLsInsert1";

    private static final String XMLDELETEFIRST = RESOURCES + File.separator + "revXMLsDelete";

    private static final String XMLDELETESECOND = RESOURCES + File.separator + "revXMLsDelete1";

    private static final String XMLSAME = RESOURCES + File.separator + "revXMLsSame";

    private static final String XMLALLFIRST = RESOURCES + File.separator + "revXMLsAll";

    private static final String XMLALLSECOND = RESOURCES + File.separator + "revXMLsAll1";

    private static final String XMLALLTHIRD = RESOURCES + File.separator + "revXMLsAll2";

    private static final String XMLALLFOURTH = RESOURCES + File.separator + "revXMLsAll3";

    private static final String XMLALLFIFTH = RESOURCES + File.separator + "revXMLsAll4";

    @Override
    @Before
    public void setUp() throws TreetankException {
        TestHelper.deleteEverything();
    }

    @Override
    @After
    public void tearDown() throws TreetankException {
        TestHelper.closeEverything();
    }

    @Test
    public void testSame() throws Exception {
        test(XMLSAME);
    }

    @Test
    public void testInsertsFirst() throws Exception {
        test(XMLINSERTFIRST);
    }

    @Test
    public void testInsertsSecond() throws Exception {
        test(XMLINSERTSECOND);
    }

    @Test
    public void testDeletesFirst() throws Exception {
        test(XMLDELETEFIRST);
    }

    @Test
    public void testDeletesSecond() throws Exception {
        test(XMLDELETESECOND);
    }

    @Test
    public void testAllFirst() throws Exception {
        test(XMLALLFIRST);
    }

    @Test
    public void testAllSecond() throws Exception {
        test(XMLALLSECOND);
    }

    @Test
    public void testAllThird() throws Exception {
        test(XMLALLTHIRD);
    }

    @Test
    public void testAllFourth() throws Exception {
        test(XMLALLFOURTH);
    }

    @Test
    public void testAllFifth() throws Exception {
        test(XMLALLFIFTH);
    }

    private void test(final String FOLDER) throws Exception {
        final IDatabase database = TestHelper.getDatabase(PATHS.PATH1.getFile());
        final ISession session = database.getSession();
        final File folder = new File(FOLDER);
        int i = 1;
        final File[] filesList = folder.listFiles();
        final List<File> list = new ArrayList<File>();
        for (final File file : filesList) {
            if (file.getName().endsWith(".xml")) {
                list.add(file);
            }
        }

        // Sort files array according to file names.
        Collections.sort(list, new Comparator<Object>() {
            public int compare(final Object paramFirst, final Object paramSecond) {
                final String firstName =
                    ((File)paramFirst).getName().toString().substring(0,
                        ((File)paramFirst).getName().toString().indexOf('.'));
                final String secondName =
                    ((File)paramSecond).getName().toString().substring(0,
                        ((File)paramSecond).getName().toString().indexOf('.'));
                if (Integer.parseInt(firstName) < Integer.parseInt(secondName)) {
                    return -1;
                } else if (Integer.parseInt(firstName) > Integer.parseInt(secondName)) {
                    return +1;
                } else {
                    return 0;
                }
            }
        });

        boolean first = true;

        // Shredder files.
        for (final File file : list) {
            if (file.getName().endsWith(".xml")) {
                final IWriteTransaction wtx = session.beginWriteTransaction();
                if (first) {
                    final XMLShredder shredder = new XMLShredder(wtx, XMLShredder.createReader(file), true);
                    shredder.call();
                    first = false;
                } else {
                    final XMLShredder shredder =
                        new XMLUpdateShredder(wtx, XMLShredder.createReader(file), true, file, true);
                    shredder.call();
                }
                assertEquals(i, wtx.getRevisionNumber());

                i++;

                final OutputStream out = new ByteArrayOutputStream();
                final XMLSerializer serializer = new XMLSerializerBuilder(session, out).build();
                serializer.call();
                final StringBuilder sBuilder = TestHelper.readFile(file.getAbsoluteFile(), false);

                System.out.println(out.toString());

                final Diff myDiff = new Diff(sBuilder.toString(), out.toString());
                assertTrue("pieces of XML are similar " + myDiff, myDiff.similar());
                assertTrue("but are they identical? " + myDiff, myDiff.identical());
                wtx.close();
            }
        }
    }

}
