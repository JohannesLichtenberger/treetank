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
package com.treetank.wikipedia.hadoop;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.hadoop.io.Writable;
import org.slf4j.LoggerFactory;

import com.treetank.utils.LogWrapper;

/**
 * <h1>XMLEventWritable</h1>
 * 
 * <p>
 * 
 * </p>
 * 
 * @author Johannes Lichtenberger, University of Konstanz
 * 
 */
public final class XMLEventWritable implements Writable {

    /**
     * {@link LogWrapper} to log messages.
     */
    private static final LogWrapper LOGWRAPPER = new LogWrapper(LoggerFactory.getLogger(XMLReduce.class));
    
    /** The underlying {@link XMLEvent}. */
    private XMLEvent mEvent;
    
    /** {@link Writer}. */
    private Writer mWriter;
    
    /** 
     * Default constructor. 
     */
    public XMLEventWritable() {
        mWriter = new StringWriter();
    }
    
    /** 
     * Constructor.
     * 
     * @param paramEvent
     *                 The {@link XMLEvent} to wrap.
     */
    public XMLEventWritable(final XMLEvent paramEvent) {
        mWriter = new StringWriter();
        mEvent = paramEvent;
    }

    /**
     * Set an event.
     * 
     * @param paramEvent
     *            The {@link XMLEvent} to set.
     */
    public void setEvent(final XMLEvent paramEvent) {
        mEvent = paramEvent;
    }

    @Override
    public void readFields(final DataInput paramIn) throws IOException {
        final String in = paramIn.readUTF();   
        try {
            final XMLEventReader reader = XMLInputFactory.newFactory().createXMLEventReader(new ByteArrayInputStream(in.getBytes("UTF-8")));
            
            if (reader.hasNext()) {
                mEvent = reader.nextEvent();
            }
        } catch (final XMLStreamException e) {
            LOGWRAPPER.error(e.getMessage(), e);
        } catch (final FactoryConfigurationError e) {
            LOGWRAPPER.error(e.getMessage(), e);
        }
    }

    @Override
    public void write(final DataOutput paramOut) throws IOException {
        try {
        mEvent.writeAsEncodedUnicode(mWriter);
        } catch (final XMLStreamException e) {
            LOGWRAPPER.error(e.getMessage(), e);
        }
        mWriter.flush();
        paramOut.writeUTF(mWriter.toString());
    }

}
