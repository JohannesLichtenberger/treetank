/*
 * TreeTank - Embedded Native XML Database
 * 
 * Copyright (C) 2007 Marc Kramis
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * $Id$
 */

package org.treetank.utils;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <h1>SoftHashMap</h1>
 * 
 * <p>
 * Based on the SoftHashMap implemented by Dr. Heinz Kabutz.
 * </p>
 * 
 * <p>
 * Hash map based on soft references. The hash map always makes sure a limited
 * amount of strong references it maintained in FIFO order to simulate LRU.
 * </p>
 * 
 * <p>
 * Note that the put and remove methods always return null.
 * </p>
 * 
 * @param <K> Key object of type K.
 * @param <V> Value object of type V.
 */
public final class SoftHashMap<K, V> extends AbstractMap<K, V> {

  /** Default strong reference count. */
  private static final int DEFAULT_STRONG_REFERENCE_COUNT = 32;

  /** The internal HashMap that will hold the SoftReference. */
  private final Map<K, SoftReference<V>> internalMap;

  /** Reference queue for cleared SoftReference objects. */
  private final ReferenceQueue queue;

  /**
   * Default constructor internally using 32 strong references.
   *
   */
  public SoftHashMap() {
    this(DEFAULT_STRONG_REFERENCE_COUNT);
  }

  /**
   * Constructor that allows to specify how many strong references should
   * be used internally.
   * 
   * @param initStrongReferenceCount Number of internal strong references.
   */
  public SoftHashMap(final int initStrongReferenceCount) {
    internalMap = new HashMap<K, SoftReference<V>>();
    queue = new ReferenceQueue();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final V get(final Object key) {
    V value = null;
    final SoftReference<V> softReference = internalMap.get(key);
    if (softReference != null) {
      // Soft reference was garbage collected.
      value = softReference.get();
      if (value == null) {
        // Reflect garbage collected soft reference in internal hash map.
        internalMap.remove(key);
      }
    }
    return value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final V put(final K key, final V value) {
    processQueue();
    internalMap.put(key, new SoftValue<V>(value, key, queue));
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final V remove(final Object key) {
    processQueue();
    internalMap.remove(key);
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final synchronized void clear() {
    processQueue();
    internalMap.clear();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final int size() {
    processQueue();
    return internalMap.size();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final Set<Map.Entry<K, V>> entrySet() {
    throw new UnsupportedOperationException();
  }

  /**
   * Remove garbage collected soft values with the help of the reference queue.
   *
   */
  private final void processQueue() {
    SoftValue<V> softValue;
    while ((softValue = (SoftValue) queue.poll()) != null) {
      internalMap.remove(softValue.key);
    }
  }

  /**
   * Internal subclass to store keys and values for more convenient lookups.
   */
  private final class SoftValue<V> extends SoftReference<V> {
    private final K key;

    /**
     * Constructor.
     * 
     * @param initValue Value wrapped as soft reference.
     * @param initKey Key for given value.
     * @param initReferenceQueue Reference queue for cleanup.
     */
    private SoftValue(
        final V initValue,
        final K initKey,
        final ReferenceQueue initReferenceQueue) {
      super(initValue, initReferenceQueue);
      key = initKey;
    }
  }

}
