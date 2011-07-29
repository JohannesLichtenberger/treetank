/**
 * Copyright (c) 2011, University of Konstanz, Distributed Systems Group
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of the University of Konstanz nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
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

import java.io.File;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import org.treetank.TestHelper;
import org.treetank.exception.AbsTTException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import static org.treetank.service.xml.serialize.XMLSerializerProperties.S_ID;
import static org.treetank.service.xml.serialize.XMLSerializerProperties.S_INDENT;
import static org.treetank.service.xml.serialize.XMLSerializerProperties.S_INDENT_SPACES;
import static org.treetank.service.xml.serialize.XMLSerializerProperties.S_REST;
import static org.treetank.service.xml.serialize.XMLSerializerProperties.S_XMLDECL;

public class XMLSerializerPropertiesTest {

    @Before
    public void setUp() throws AbsTTException {
        TestHelper.deleteEverything();
    }

    @After
    public void tearDown() throws AbsTTException {
        TestHelper.closeEverything();
    }

    @Test
    public void testXMLSerializerProp() {
        final String path = TestHelper.PATHS.PATH1.getFile().getAbsolutePath();
        if (!new File(path).mkdirs()) {
            fail("Directories couldn't be created!");
        }
        final XMLSerializerProperties props = new XMLSerializerProperties();

        final ConcurrentHashMap<String, Object> map = (ConcurrentHashMap<String, Object>)props.getmProps();
        final Enumeration<String> keys = map.keys();

        assertNotNull(keys);

        while (keys.hasMoreElements()) {
            final String key = keys.nextElement();

            if (key.equals(S_ID[0])) {
                assertEquals(false, (Boolean)map.get(S_ID[0]));
            } else if (key.equals(S_REST[0])) {
                assertEquals(false, (Boolean)map.get(S_REST[0]));
            } else if (key.equals(S_INDENT[0])) {
                assertEquals(true, (Boolean)map.get(S_INDENT[0]));
            } else if (key.equals(S_INDENT_SPACES[0])) {
                assertSame(2, (Integer)map.get(S_INDENT_SPACES[0]));
            } else if (key.equals(S_XMLDECL[0])) {
                assertEquals(true, (Boolean)map.get(S_XMLDECL[0]));
            }
        }
    }
}
