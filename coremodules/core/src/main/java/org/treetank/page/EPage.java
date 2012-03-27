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
package org.treetank.page;

import java.util.HashMap;
import java.util.Map;

import org.treetank.io.EStorage;
import org.treetank.io.ITTSource;
import org.treetank.page.interfaces.IPage;

public enum EPage {

    Node(1, NodePage.class) {

    },

    Name(2, NamePage.class) {

    },
    Uber(3, UberPage.class) {
    },
    Indirect(4, IndirectPage.class) {
    },
    Revision(5, RevisionRootPage.class) {
    };

    /** Getting identifier mapping. */
    private static final Map<Integer, EPage> INSTANCEFORID = new HashMap<Integer, EPage>();
    private static final Map<Class<? extends IPage>, EPage> INSTANCEFORCLASS =
        new HashMap<Class<? extends IPage>, EPage>();
    static {
        for (EPage page : values()) {
            INSTANCEFORID.put(page.mIdent, page);
            INSTANCEFORCLASS.put(page.mClass, page);
        }
    }

    /** Identifier for the storage. */
    final int mIdent;

    /** Class for Key. */
    final Class<? extends IPage> mClass;

    /**
     * Constructor.
     * 
     * @param paramIdent
     *            identifier to be set.
     */
    EPage(final int paramIdent, final Class<? extends IPage> paramClass) {
        mIdent = paramIdent;
        mClass = paramClass;
    }

    /**
     * Getting an instance of this enum for the identifier.
     * 
     * @param paramId
     *            the identifier of the enum.
     * @return a concrete enum
     */
    public static final EPage getInstance(final Integer paramId) {
        return INSTANCEFORID.get(paramId);
    }

    /**
     * Getting an instance of this enum for the identifier.
     * 
     * @param paramKey
     *            the identifier of the enum.
     * @return a concrete enum
     */
    public static final EPage getInstance(final Class<? extends IPage> paramKey) {
        return INSTANCEFORCLASS.get(paramKey);
    }

    public static final PageReference[] deserializeRef(final int paramRefSize, final ITTSource paramIn) {
        final PageReference[] refs = new PageReference[paramRefSize];
        for (int offset = 0; offset < refs.length; offset++) {
            refs[offset] = new PageReference();
            final EStorage storage = EStorage.getInstance(paramIn.readInt());
            if (storage != null) {
                refs[offset].setKey(storage.deserialize(paramIn));
            }
        }
        return refs;
    }

}
