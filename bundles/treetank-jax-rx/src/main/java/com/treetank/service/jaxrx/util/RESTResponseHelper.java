package com.treetank.service.jaxrx.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.treetank.exception.TreetankException;
import com.treetank.service.jaxrx.implementation.DatabaseRepresentation;

/**
 * This class is a helper class to build the response XML fragment for REST
 * resources.
 * 
 * @author Patrick Lang, Lukas Lewandowski, University of Konstanz
 * 
 */
public final class RESTResponseHelper {

	/** Reference to get access to worker help classes */
	private static transient final WorkerHelper WORKERHELPER = WorkerHelper
			.getInstance();

	/**
	 * The private empty constructor.
	 */
	private RESTResponseHelper() {
		// i do nothing
		// constructor only exists to meet pmd requirements
	}

	/**
	 * This method creates a new {@link Document} instance for the surrounding
	 * XML element for the client response.
	 * 
	 * @return The created {@link Document} instance.
	 * @throws ParserConfigurationException
	 *             The exception occurred.
	 */
	private static Document createSurroundingXMLResp()
			throws ParserConfigurationException {
		final Document document = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().newDocument();
		return document;
	}

	/**
	 * This method creates the TreeTank response XML element.
	 * 
	 * @param document
	 *            The {@link Document} instance for the response.
	 * @return The created XML {@link Element}.
	 */
	private static Element createResultElement(final Document document) {
		final Element ttResponse = document.createElementNS(
				"http://jaxrx.org/", "result");
		ttResponse.setPrefix("jaxrx");
		return ttResponse;
	}

	/**
	 * This method creates the XML element containing a collection. This
	 * collection contains the available resources which are children of the
	 * database.
	 * 
	 * @param resources
	 *            The list with the path of the available resources.
	 * @param collections
	 *            The list with the path of the available collections.
	 * @param document
	 *            The XML {@link Document} instance.
	 * @return A list of XML {@link Element} as the collection.
	 * @throws TreetankException
	 * @throws WebApplicationException
	 */
	private static List<Element> createCollectionElementDBs(
			final List<String> resources, final List<String> collections,
			final Document document) throws WebApplicationException,
			TreetankException {
		final List<Element> collectionsEls = new ArrayList<Element>();
		for (final String res : resources) {
			final Element elRes = document.createElement("resource");
			elRes.setAttribute("name", res);

			// extract db name from path without beginning character "/"
			final StringBuilder dbName = WORKERHELPER
					.createStringBuilderObject();
			for (int i = 1; i < res.length(); i++) {
				dbName.append(res.toCharArray()[i]);
			}

			// get last revision from given db name
			final DatabaseRepresentation dbWorker = WORKERHELPER
					.createTreeTrankObject();
			final String lastRevision = Long.toString(dbWorker
					.getLastRevision(dbName.toString()));

			elRes.setAttribute("lastRevision", lastRevision);
			collectionsEls.add(elRes);
		}
		for (final String col : collections) {
			final Element elRes = document.createElement("collection");
			elRes.setAttribute("name", col);
			collectionsEls.add(elRes);
		}

		return collectionsEls;
	}

	/**
	 * This method builds the overview for the resources and collection we offer
	 * in our implementation.
	 * 
	 * @param availableResources
	 *            A map of available resources or collections.
	 * @return The streaming output for the HTTP response body.
	 */
	public static StreamingOutput buildResponseOfDomLR(
			final Map<String, String> availableRes) {
		final List<String> avRes = new ArrayList<String>();
		final List<String> avCol = new ArrayList<String>();

		for (Map.Entry<String, String> entry : availableRes.entrySet()) {
			final String value = entry.getValue();
			if ("collection".equals(value)) {
				avCol.add("/" + entry.getKey());
			} else if ("resource".equals(value)) {
				avRes.add("/"
						+ entry.getKey().substring(
								0,
								entry.getKey().length()
										- RESTProps.TNKEND.length()));
			}

		}
		final StreamingOutput sOutput = new StreamingOutput() {

			@Override
			public void write(final OutputStream output) throws IOException,
					WebApplicationException {
				Document document;
				try {
					document = createSurroundingXMLResp();
					final Element resElement = RESTResponseHelper
							.createResultElement(document);

					List<Element> collections;
					try {
						collections = RESTResponseHelper
								.createCollectionElementDBs(avRes, avCol,
										document);
					} catch (final TreetankException exce) {
						throw new WebApplicationException(exce);
					}
					for (final Element resource : collections) {
						resElement.appendChild(resource);
					}
					document.appendChild(resElement);
					final DOMSource domSource = new DOMSource(document);
					final StreamResult streamResult = new StreamResult(output);
					final Transformer transformer = TransformerFactory
							.newInstance().newTransformer();
					transformer.transform(domSource, streamResult);

				} catch (final ParserConfigurationException exce) {
					throw new WebApplicationException(exce);
				} catch (final TransformerConfigurationException exce) {
					throw new WebApplicationException(exce);
				} catch (final TransformerFactoryConfigurationError exce) {
					throw new WebApplicationException(exce);
				} catch (final TransformerException exce) {
					throw new WebApplicationException(exce);
				}
			}
		};

		return sOutput;
	}

}