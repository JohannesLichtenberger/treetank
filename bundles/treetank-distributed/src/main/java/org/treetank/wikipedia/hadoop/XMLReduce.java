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
package org.treetank.wikipedia.hadoop;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.slf4j.LoggerFactory;

import com.treetank.service.xml.shredder.ListEventReader;
import com.treetank.utils.LogWrapper;

/**
 * <h1>XMLReducer</h1>
 * 
 * <p>
 * After sorting and grouping key's the reducer just emits the results (identity reducer).
 * </p>
 * 
 * @author Johannes Lichtenberger, University of Konstanz
 * 
 */
public final class XMLReduce extends Reducer<DateWritable, Text, DateWritable, Text> {

    /**
     * Log wrapper for better output.
     */
    private static final LogWrapper LOGWRAPPER = new LogWrapper(LoggerFactory.getLogger(XMLReduce.class));

    /** Path to stylesheet for XSLT transformation. */
    private static final String STYLESHEET =
        "src" + File.separator + "main" + File.separator + "resources" + File.separator + "wikipedia.xsl";

    /**
     * Empty Constructor.
     */
    public XMLReduce() {
        // To make Checkstyle happy.
    }

    @Override
    public void reduce(final DateWritable paramKey, final Iterable<Text> paramValue,
        final Context paramContext) throws IOException, InterruptedException {
        final Text combined = new Text();

        combined.append("<root>".getBytes(), 0, "<root>".length());
        for (final Text event : paramValue) {
            combined.append(event.getBytes(), 0, event.getLength());
        }
        combined.append("</root>".getBytes(), 0, "</root>".length());

        final Processor proc = new Processor(false);
        final XsltCompiler compiler = proc.newXsltCompiler();
        try {
            final XsltExecutable exec = compiler.compile(new StreamSource(new File(STYLESHEET)));
            final XsltTransformer transform = exec.load();
            transform.setSource(new StreamSource(new StringReader(combined
                .toString())));
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final Serializer serializer = new Serializer();
            serializer.setOutputStream(out);
            transform.setDestination(serializer);
            transform.transform();
            final String value = out.toString();
            System.out.println(value);
            paramContext.write(null, new Text(value));
        } catch (final SaxonApiException e) {
            LOGWRAPPER.error(e);
        }
    }
}
