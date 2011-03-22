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

package org.treetank.gui.view.sunburst;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Provides methods to add and remove {@link PropertyChangeListener}s as well as firing property changes.
 * 
 * @author Johannes Lichtenberger, University of Konstanz
 * 
 */
abstract class AbsComponent {

    /** {@link PropertyChangeSupport} to register listeners. */
    private final PropertyChangeSupport mPropertyChangeSupport;

    /**
     * Constructor.
     */
    AbsComponent() {
        mPropertyChangeSupport = new PropertyChangeSupport(this);
    }

    /**
     * Add a {@link PropertyChangeListener}.
     * 
     * @param paramListener
     *            The listener to add.
     */
    public final void addPropertyChangeListener(final PropertyChangeListener paramListener) {
        mPropertyChangeSupport.addPropertyChangeListener(paramListener);
    }

    /**
     * Remove a {@link PropertyChangeListener}.
     * 
     * @param paramListener
     *            The listener to remove.
     */
    public final void removePropertyChangeListener(final PropertyChangeListener paramListener) {
        mPropertyChangeSupport.removePropertyChangeListener(paramListener);
    }

    /**
     * Fire a property change.
     * 
     * @param paramPropertyName
     *            Name of the property.
     * @param paramOldValue
     *            Old value.
     * @param paramNewValue
     *            New value.
     */
    protected final void firePropertyChange(final String paramPropertyName, final Object paramOldValue,
        final Object paramNewValue) {
        mPropertyChangeSupport.firePropertyChange(paramPropertyName, paramOldValue, paramNewValue);
    }
}
