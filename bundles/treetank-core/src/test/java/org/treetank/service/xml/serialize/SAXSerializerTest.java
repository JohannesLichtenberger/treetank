package org.treetank.service.xml.serialize;

import org.custommonkey.xmlunit.XMLTestCase;
import org.treetank.TestHelper;
import org.treetank.TestHelper.PATHS;
import org.treetank.api.IDatabase;
import org.treetank.api.ISession;
import org.treetank.api.IWriteTransaction;
import org.treetank.exception.AbsTTException;
import org.treetank.service.xml.serialize.SAXSerializer;
import org.treetank.utils.DocumentCreater;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;


/**
 * Test SAXSerializer.
 * 
 * @author Johannes Lichtenberger, University of Konstanz
 * 
 */
public class SAXSerializerTest extends XMLTestCase {
    @Override
    @Before
    public void setUp() throws AbsTTException {
        TestHelper.deleteEverything();
    }

    @Override
    @After
    public void tearDown() throws AbsTTException {
        TestHelper.closeEverything();
    }

    @Test
    public void testSAXSerializer() {
        try {
            // Setup test file.
            final IDatabase database = TestHelper.getDatabase(PATHS.PATH1.getFile());
            final ISession session = database.getSession();
            final IWriteTransaction testTrx = session.beginWriteTransaction();
            DocumentCreater.create(testTrx);
            testTrx.commit();
            testTrx.close();

            final StringBuilder strBuilder = new StringBuilder();
            final ContentHandler contHandler = new XMLFilterImpl() {

                @Override
                public void startDocument() {
                    strBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
                }

                @Override
                public void startElement(final String uri, final String localName, final String qName,
                    final Attributes atts) throws SAXException {
                    strBuilder.append("<" + qName);

                    for (int i = 0; i < atts.getLength(); i++) {
                        strBuilder.append(" " + atts.getQName(i));
                        strBuilder.append("=\"" + atts.getValue(i) + "\"");
                    }

                    strBuilder.append(">");
                }

                @Override
                public void endElement(String uri, String localName, String qName) throws SAXException {
                    strBuilder.append("</" + qName + ">");
                }

                @Override
                public void characters(final char[] ch, final int start, final int length)
                    throws SAXException {
                    for (int i = start; i < start + length; i++) {
                        strBuilder.append(ch[i]);
                    }
                }
            };

            final SAXSerializer serializer = new SAXSerializer(session, contHandler);
            serializer.call();

            assertXMLEqual(DocumentCreater.XML, strBuilder.toString());
        } catch (final AbsTTException e) {
            fail("Treetank exception occured!");
        } catch (final Exception e) {
            e.printStackTrace();
            fail("Any exception occured!");
        }
    }
}