/**
 * Copyright (c) 2010, Distributed Systems Group, University of Konstanz
 * 
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 * 
 * THE SOFTWARE IS PROVIDED AS IS AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 * 
 */

package org.treetank.utils;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <h1>WeakHashMap</h1>
 * 
 * <p>
 * Based on the SoftHashMap implemented by Dr. Heinz Kabutz.
 * </p>
 * 
 * <p>
 * Hash map based on weak references.
 * </p>
 * 
 * <p>
 * Note that the put and remove methods always return null.
 * </p>
 * 
 * @param <K>
 *            Key object of type K.
 * @param <V>
 *            Value object of type V.
 */
@SuppressWarnings("unchecked")
public final class FastWeakHashMap<K, V> extends AbstractMap<K, V> {

    /** The internal HashMap that will hold the WeakReference. */
    private final Map<K, WeakReference<V>> mInternalMap;

    /** Reference queue for cleared WeakReference objects. */
    private final ReferenceQueue mQueue;

    /**
     * Default constructor internally using 32 strong references.
     * 
     */
    public FastWeakHashMap() {
        mInternalMap = new ConcurrentHashMap();
        mQueue = new ReferenceQueue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V get(final Object mKey) {
        V value = null;
        final WeakReference<V> weakReference = mInternalMap.get(mKey);
        if (weakReference != null) {
            // Weak reference was garbage collected.
            value = weakReference.get();
            if (value == null) {
                // Reflect garbage collected weak reference in internal hash
                // map.
                mInternalMap.remove(mKey);
            }
        }
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V put(final K mKey, final V mValue) {
        processQueue();
        mInternalMap.put(mKey, new WeakValue<V>(mValue, mKey, mQueue));
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V remove(final Object mKey) {
        processQueue();
        mInternalMap.remove(mKey);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void clear() {
        processQueue();
        mInternalMap.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        processQueue();
        return mInternalMap.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException();
    }

    /**
     * Remove garbage collected weak values with the help of the reference
     * queue.
     * 
     */
    private void processQueue() {
        WeakValue<V> weakValue;
        while ((weakValue = (WeakValue<V>)mQueue.poll()) != null) {
            mInternalMap.remove(weakValue.mKey);
        }
    }

    /**
     * Internal subclass to store keys and values for more convenient lookups.
     */
    @SuppressWarnings("hiding")
    private final class WeakValue<V> extends WeakReference<V> {
        private final K mKey;

        /**
         * Constructor.
         * 
         * @param mInitValue
         *            Value wrapped as weak reference.
         * @param mInitKey
         *            Key for given value.
         * @param mInitReferenceQueue
         *            Reference queue for cleanup.
         */
        private WeakValue(final V mInitValue, final K mInitKey, final ReferenceQueue mInitReferenceQueue) {
            super(mInitValue, mInitReferenceQueue);
            mKey = mInitKey;
        }
    }

}