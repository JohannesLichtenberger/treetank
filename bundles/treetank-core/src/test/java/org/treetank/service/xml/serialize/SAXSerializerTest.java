/**
 * Copyright (c) 2011, University of Konstanz, Distributed Systems Group
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of Konstanz nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.treetank.service.xml.serialize;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

import org.custommonkey.xmlunit.XMLTestCase;
import org.treetank.TestHelper;
import org.treetank.TestHelper.PATHS;
import org.treetank.access.SessionConfiguration;
import org.treetank.api.IDatabase;
import org.treetank.api.ISession;
import org.treetank.api.IWriteTransaction;
import org.treetank.exception.AbsTTException;
import org.treetank.utils.DocumentCreater;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


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
            final ISession session = database.getSession(new SessionConfiguration.Builder().build());
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
