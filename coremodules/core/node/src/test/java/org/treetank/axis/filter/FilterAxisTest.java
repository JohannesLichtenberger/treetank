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

package org.treetank.axis.filter;

import static org.treetank.node.IConstants.ROOT_NODE;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.treetank.Holder;
import org.treetank.TestHelper;
import org.treetank.api.INodeReadTrx;
import org.treetank.axis.AbsAxisTest;
import org.treetank.axis.AttributeAxis;
import org.treetank.axis.DescendantAxis;
import org.treetank.axis.FilterAxis;
import org.treetank.exception.AbsTTException;

public class FilterAxisTest {

    private Holder holder;

    @Before
    public void setUp() throws AbsTTException {
        TestHelper.deleteEverything();
        TestHelper.createTestDocument();
        holder = Holder.generateRtx();
    }

    @After
    public void tearDown() throws AbsTTException {
        holder.close();
        TestHelper.closeEverything();
    }

    @Test
    public void testNameAxisTest() throws AbsTTException {
        // Build simple test tree.
        final INodeReadTrx rtx = holder.getRtx();

        rtx.moveTo(ROOT_NODE);
        AbsAxisTest.testIAxisConventions(new FilterAxis(
                new DescendantAxis(rtx), rtx, new NameFilter(rtx, "b")),
                new long[] { 5L, 9L });
    }

    @Test
    public void testValueAxisTest() throws AbsTTException {
        // Build simple test tree.
        final INodeReadTrx rtx = holder.getRtx();

        rtx.moveTo(ROOT_NODE);
        AbsAxisTest.testIAxisConventions(new FilterAxis(
                new DescendantAxis(rtx), rtx, new ValueFilter(rtx, "foo")),
                new long[] { 6L });
    }

    @Test
    public void testValueAndNameAxisTest() throws AbsTTException {
        // Build simple test tree.
        final INodeReadTrx rtx = holder.getRtx();

        rtx.moveTo(1L);
        AbsAxisTest.testIAxisConventions(new FilterAxis(new AttributeAxis(rtx),
                rtx, new NameFilter(rtx, "i"), new ValueFilter(rtx, "j")),
                new long[] { 2L });

        rtx.moveTo(9L);
        AbsAxisTest.testIAxisConventions(new FilterAxis(new AttributeAxis(rtx),
                rtx, new NameFilter(rtx, "y"), new ValueFilter(rtx, "y")),
                new long[] {});

    }

}
