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

package org.treetank.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An LRU cache, based on <code>LinkedHashMap</code>. This cache can hold an
 * possible second cache as a second layer for example for storing data in a
 * persistent way.
 * 
 * @author Sebastian Graf, University of Konstanz
 */
public final class LRUCache implements ICache {

    /**
     * Capacity of the cache. Number of stored pages
     */
    static final int CACHE_CAPACITY = 10;

    /**
     * The collection to hold the maps.
     */
    private final Map<Long, NodePageContainer> map;

    /**
     * The reference to the second cache.
     */
    private final ICache mSecondCache;

    /**
     * Creates a new LRU cache.
     * 
     * @param paramSecondCache
     *            the reference to the second cache where the data is stored
     *            when it gets removed from the first one.
     * 
     */
    public LRUCache(final ICache paramSecondCache) {
        mSecondCache = paramSecondCache;
        map = new LinkedHashMap<Long, NodePageContainer>(CACHE_CAPACITY) {
            // (an anonymous inner class)
            private static final long serialVersionUID = 1;

            @Override
            protected boolean removeEldestEntry(final Map.Entry<Long, NodePageContainer> mEldest) {
                boolean returnVal = false;
                if (size() > CACHE_CAPACITY) {
                    mSecondCache.put(mEldest.getKey(), mEldest.getValue());
                    returnVal = true;
                }
                return returnVal;

            }
        };
    }

    /**
     * Constructor with no second cache.
     */
    public LRUCache() {
        this(new NullCache());
    }

    /**
     * Retrieves an entry from the cache.<br>
     * The retrieved entry becomes the MRU (most recently used) entry.
     * 
     * @param mKey
     *            the key whose associated value is to be returned.
     * @return the value associated to this key, or null if no value with this
     *         key exists in the cache.
     */
    public NodePageContainer get(final long mKey) {
        NodePageContainer page = map.get(mKey);
        if (page == null) {
            page = mSecondCache.get(mKey);
        }
        return page;
    }

    /**
     * 
     * Adds an entry to this cache. If the cache is full, the LRU (least
     * recently used) entry is dropped.
     * 
     * @param mKey
     *            the key with which the specified value is to be associated.
     * @param mValue
     *            a value to be associated with the specified key.
     */
    public void put(final long mKey, final NodePageContainer mValue) {
        map.put(mKey, mValue);
    }

    /**
     * Clears the cache.
     */
    public void clear() {
        map.clear();
        mSecondCache.clear();
    }

    /**
     * Returns the number of used entries in the cache.
     * 
     * @return the number of entries currently in the cache.
     */
    public int usedEntries() {
        return map.size();
    }

    /**
     * Returns a <code>Collection</code> that contains a copy of all cache
     * entries.
     * 
     * @return a <code>Collection</code> with a copy of the cache content.
     */
    public Collection<Map.Entry<Long, NodePageContainer>> getAll() {
        return new ArrayList<Map.Entry<Long, NodePageContainer>>(map.entrySet());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("First Cache: ");
        builder.append(this.map.toString());
        builder.append("\n");
        builder.append("Second Cache: ");
        builder.append(this.mSecondCache.toString());
        return builder.toString();
    }

}
