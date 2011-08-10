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

package org.treetank.access;

import java.io.File;
import java.util.Random;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.treetank.Holder;
import org.treetank.TestHelper;
import org.treetank.access.conf.ResourceConfiguration;
import org.treetank.exception.AbsTTException;
import org.treetank.node.ENodes;
import org.treetank.node.ElementNode;
import org.treetank.service.xml.shredder.EShredderInsert;
import org.treetank.service.xml.shredder.XMLShredder;

public final class OverallTest {

    private static int NUM_CHARS = 3;
    private static int ELEMENTS = 1000;
    private static int COMMITPERCENTAGE = 20;
    private static int REMOVEPERCENTAGE = 20;
    private static final Random ran = new Random(0l);
    public static String chars = "abcdefghijklm";

    private Holder holder;

    @Before
    public void setUp() throws AbsTTException {
        TestHelper.deleteEverything();
        holder = Holder.generateWtx();
    }

    @Test
    public void testJustEverything() throws AbsTTException {
        holder.getWtx().insertElementAsFirstChild(new QName(getString()));
        for (int i = 0; i < ELEMENTS; i++) {
            if (ran.nextBoolean()) {
                switch (holder.getWtx().getNode().getKind()) {
                case ELEMENT_KIND:
                    holder.getWtx().setQName(new QName(getString()));
                    holder.getWtx().setURI(getString());
                    break;
                case ATTRIBUTE_KIND:
                    holder.getWtx().setQName(new QName(getString()));
                    holder.getWtx().setURI(getString());
                    holder.getWtx().setValue(getString());
                    break;
                case NAMESPACE_KIND:
                    holder.getWtx().setQName(new QName(getString()));
                    holder.getWtx().setURI(getString());
                    break;
                case TEXT_KIND:
                    holder.getWtx().setValue(getString());
                    break;
                default:
                }
            } else {
                if (holder.getWtx().getNode() instanceof ElementNode) {
                    if (ran.nextBoolean()) {
                        holder.getWtx().insertElementAsFirstChild(new QName(getString()));
                    } else {
                        holder.getWtx().insertElementAsRightSibling(new QName(getString()));
                    }
                    while (ran.nextBoolean()) {
                        holder.getWtx().insertAttribute(new QName(getString()), getString());
                        holder.getWtx().moveToParent();
                    }
                    while (ran.nextBoolean()) {
                        holder.getWtx().insertNamespace(new QName(getString(), getString()));
                        holder.getWtx().moveToParent();
                    }
                }

                if (ran.nextInt(100) < REMOVEPERCENTAGE) {
                    holder.getWtx().remove();
                }

                if (ran.nextInt(100) < COMMITPERCENTAGE) {
                    holder.getWtx().commit();
                }
                do {
                    final int newKey = ran.nextInt(i + 1) + 1;
                    holder.getWtx().moveTo(newKey);
                } while (holder.getWtx().getNode() == null);
                // TODO Check if reference check can occur on "=="
                if (holder.getWtx().getNode().getKind() != ENodes.ELEMENT_KIND) {
                    holder.getWtx().moveToParent();
                }
            }
        }
        final long key = holder.getWtx().getNode().getNodeKey();
        holder.getWtx().remove();
        holder.getWtx().insertElementAsFirstChild(new QName(getString()));
        holder.getWtx().moveTo(key);
        holder.getWtx().commit();
        holder.getWtx().close();
    }

    @After
    public void tearDown() throws AbsTTException {
        holder.close();
        TestHelper.closeEverything();
    }

    private static String getString() {
        char[] buf = new char[NUM_CHARS];

        for (int i = 0; i < buf.length; i++) {
            buf[i] = chars.charAt(ran.nextInt(chars.length()));
        }

        return new String(buf);
    }

}
