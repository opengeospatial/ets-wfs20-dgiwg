package de.latlon.ets.wfs20.core.dgiwg.testsuite.dataprovider;

import static javax.xml.xpath.XPathConstants.NODESET;
import static javax.xml.xpath.XPathConstants.STRING;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.opengis.cite.iso19142.ProtocolBinding;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.latlon.ets.core.util.NamespaceBindings;
import de.latlon.ets.wfs20.core.domain.WfsNamespaces;

/**
 * Provides data from WFS 2.0 capabilities.
 *
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public final class WfsDataProvider {

	private static final NamespaceBindings NS_BINDINGS = WfsNamespaces.withStandardBindings();

	private WfsDataProvider() {
	}

	/**
	 * Parses all FeatureType elements from the WFS 2.0 capabilities document.
	 * @param protocolBinding used to retrieve the data, never <code>null</code>
	 * @param wfsCapabilities the capabilities document (wfs:WFS_Capabilities), never
	 * <code>null</code>
	 * @return an array of an array with the FeatureType node and parsed Name, never
	 * <code>null</code>
	 * @throws XPathExpressionException if the xpath expression could not be evaluated
	 */
	public static Object[][] provideFeatureTypeNodes(ProtocolBinding protocolBinding, Document wfsCapabilities)
			throws XPathExpressionException {
		String xPathMainNodeToParse = "//wfs:FeatureType";
		String xPathSubElementToParse = "wfs:Name";
		return parseNode(protocolBinding, wfsCapabilities, xPathMainNodeToParse, xPathSubElementToParse);
	}

	/**
	 * Parses all StoredQueryDescription nodes from a WFS 2.0 DescribeStoredQueries
	 * response document.
	 * @param protocolBinding used to retrieve the data, never <code>null</code>
	 * @param describeStoredQueryResponse the DescribeStoredQueries response document,
	 * never <code>null</code>
	 * @return an array of an array with the StoredQueryDescription node and parsed Id
	 * element, never <code>null</code>
	 * @throws XPathExpressionException if the xpath expression could not be evaluated
	 */
	public static Object[][] provideStoredQueryDescriptionNodes(ProtocolBinding protocolBinding,
			Document describeStoredQueryResponse) throws XPathExpressionException {
		String xPathMainNodeToParse = "//wfs:StoredQueryDescription";
		String xPathSubElementToParse = "@id";
		return parseNode(protocolBinding, describeStoredQueryResponse, xPathMainNodeToParse, xPathSubElementToParse);
	}

	private static Object[][] parseNode(ProtocolBinding protocolBinding, Document doc, String xPathMainNodeToParse,
			String xPathSubElementToParse) throws XPathExpressionException {
		XPath xPath = createXPath();
		NodeList mainNodes = (NodeList) xPath.evaluate(xPathMainNodeToParse, doc, NODESET);
		Object[][] resultNodesAndKeys = new Object[mainNodes.getLength()][];
		for (int mainNodeIndex = 0; mainNodeIndex < mainNodes.getLength(); mainNodeIndex++) {
			Node mainNode = mainNodes.item(mainNodeIndex);
			String key = (String) xPath.evaluate(xPathSubElementToParse, mainNode, STRING);
			resultNodesAndKeys[mainNodeIndex] = new Object[] { protocolBinding, mainNode, key };
		}
		return resultNodesAndKeys;
	}

	private static XPath createXPath() {
		try {
			XPathFactory factory = XPathFactory.newInstance(XPathConstants.DOM_OBJECT_MODEL);
			XPath xpath = factory.newXPath();
			xpath.setNamespaceContext(NS_BINDINGS);
			return xpath;
		}
		catch (XPathFactoryConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

}