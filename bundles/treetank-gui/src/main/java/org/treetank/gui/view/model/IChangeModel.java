/**
 * 
 */
package org.treetank.gui.view.model;

import javax.xml.stream.XMLStreamException;

import org.treetank.exception.AbsTTException;

/**
 * Allows changes to the underlying model.
 * 
 * @author Johannes Lichtenberger, University of Konstanz
 * 
 */
public interface IChangeModel {
    
    /**
     * Commit changes.
     * 
     * @throws AbsTTException
     *             if something fails
     */
    void commit() throws AbsTTException;

    /**
     * Add XML fragment, that is add it to a PUL but don't commit them.
     * 
     * @param paramFragment
     *            the XML fragment to insert
     * @throws AbsTTException
     *             if something fails
     * @throws XMLStreamException
     *             if parsing XML fragment fails
     */
    void addXMLFragment(final String paramFragment) throws AbsTTException, XMLStreamException;
}
