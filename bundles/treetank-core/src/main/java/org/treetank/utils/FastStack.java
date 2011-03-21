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

/**
 * <h1>FastStack</h1>
 * 
 * <p>
 * Unsynchronized stack optimized for generic type. Is significantly faster than Stack.
 * </p>
 * 
 * @param <E>
 *            Generic type.
 */
public final class FastStack<E> {

    /** Internal array to store stack elements. */
    private E[] mStack;

    /** Current size of stack. */
    private int mSize;

    /**
     * Constructor.
     */
    @SuppressWarnings("unchecked")
    public FastStack() {
        mStack = (E[])new Object[16];
        mSize = 0;
    }

    /**
     * Private constructor used for clone method.
     * 
     * @param mObject
     *            The array from which to create a new stack.
     */
    private FastStack(E[] mObject) {
        mStack = mObject;
        mSize = 0;
    }

    /**
     * Place new element on top of stack. This might require to double the size
     * of the internal array.
     * 
     * @param mElement
     *            Element to push.
     */
    @SuppressWarnings("unchecked")
    public void push(final E mElement) {
        if (mStack.length == mSize) {
            final E[] biggerStack = (E[])new Object[mStack.length << 1];
            System.arraycopy(mStack, 0, biggerStack, 0, mStack.length);
            mStack = biggerStack;
        }
        mStack[mSize++] = mElement;
    }

    /**
     * Get the element on top of the stack. The internal array performs boundary
     * checks.
     * 
     * @return Topmost stack element.
     */
    public E peek() {
        return mStack[mSize - 1];
    }

    /**
     * Get element at given position in stack. The internal array performs
     * boundary checks.
     * 
     * @param position
     *            Position in stack from where to get the element.
     * @return Stack element at given position.
     */
    public E get(final int position) {
        return mStack[position];
    }

    /**
     * Remove topmost element from stack.
     * 
     * @return Removed topmost element of stack.
     */
    public E pop() {
        return mStack[--mSize];
    }

    /**
     * Reset the stack.
     * 
     */
    public void clear() {
        mSize = 0;
    }

    /**
     * Get the current size of the stack.
     * 
     * @return Current size of stack.
     */
    public int size() {
        return mSize;
    }

    /**
     * Is the stack empty?
     * 
     * @return True if there are no elements anymore. False else.
     */
    public boolean empty() {
        return (mSize == 0);
    }

    /**
     * Clone a stack.
     * 
     * @return Cloned stack.
     */
    @Override
    @SuppressWarnings("unchecked")
    public FastStack<E> clone() {
        final E[] object = (E[])new Object[mStack.length];
        System.arraycopy(mStack, 0, object, 0, mStack.length);
        return new FastStack(object);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < mSize; i++) {
            builder.append(mStack[i]);
            if (i < mSize) {
                builder.append(",");
            }
        }
        builder.append("]");
        return builder.toString();
    }

}