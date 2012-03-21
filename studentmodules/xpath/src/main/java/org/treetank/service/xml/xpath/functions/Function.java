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

package org.treetank.service.xml.xpath.functions;

import java.util.ArrayList;
import java.util.List;

import org.treetank.axis.AbsAxis;
import org.treetank.exception.TTXPathException;
import org.treetank.service.xml.xpath.AtomicValue;
import org.treetank.service.xml.xpath.functions.sequences.FNBoolean;

public class Function {

    public static boolean ebv(final AbsAxis axis, final List<AtomicValue> pToStore) throws TTXPathException {
        final FuncDef ebv = FuncDef.BOOLEAN;
        final List<AbsAxis> param = new ArrayList<AbsAxis>();
        param.add(axis);
        final AbsAxis bAxis =
            new FNBoolean(axis.getTransaction(), param, ebv.getMin(), ebv.getMax(), axis.getTransaction()
                .keyForName(ebv.getReturnType()), pToStore);
        if (bAxis.hasNext()) {
            bAxis.next();
            final boolean result = Boolean.parseBoolean(bAxis.getTransaction().getValueOfCurrentNode());
            if (!bAxis.hasNext()) {
                bAxis.reset(axis.getTransaction().getNode().getNodeKey());

                return result;
            }
        }
        throw new IllegalStateException("This should not happen!"); // TODO!!
    }

}
