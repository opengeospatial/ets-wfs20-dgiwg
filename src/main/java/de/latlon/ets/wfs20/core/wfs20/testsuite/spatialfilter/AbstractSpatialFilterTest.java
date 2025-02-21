package de.latlon.ets.wfs20.core.wfs20.testsuite.spatialfilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.validation.Schema;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSTypeDefinition;
import org.opengis.cite.iso19142.Namespaces;
import org.opengis.cite.iso19142.WFS2;
import org.opengis.cite.iso19142.basic.filter.QueryFilterFixture;
import org.opengis.cite.iso19142.util.AppSchemaUtils;
import org.testng.SkipException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.latlon.ets.core.util.NamespaceBindings;
import de.latlon.ets.wfs20.core.domain.WfsNamespaces;

/**
 * Base class for test swith spatial filters.
 *
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public abstract class AbstractSpatialFilterTest extends QueryFilterFixture {

	protected static final NamespaceBindings NS_BINDINGS = WfsNamespaces.withStandardBindings();

	protected static final List<String> EXPECTED_SPATIAL_OPERATORS = Arrays.asList("Equals", "Disjoint", "Touches",
			"Within", "Overlaps", "Crosses", "Intersects", "Contains", "DWithin", "Beyond");

	protected XSTypeDefinition gmlGeomBaseType;

	protected Schema wfsSchema;

	protected XSElementDeclaration findGeometryProperty(QName featureType) {
		List<XSElementDeclaration> geomProps = AppSchemaUtils.getFeaturePropertiesByType(model, featureType,
				gmlGeomBaseType);
		if (geomProps.isEmpty()) {
			throw new SkipException("Feature type has no geometry properties: " + featureType);
		}
		return geomProps.get(0);
	}

	protected void addSpatialOperatorPredicate(Document request, String spatialOperatorName, Element envelope,
			Element valueRef) {
		if (!request.getDocumentElement().getLocalName().equals(WFS2.GET_FEATURE)) {
			throw new IllegalArgumentException(
					"Not a GetFeature request: " + request.getDocumentElement().getNodeName());
		}
		Element queryElem = (Element) request.getElementsByTagNameNS(Namespaces.WFS, WFS2.QUERY_ELEM).item(0);
		Element filter = request.createElementNS(Namespaces.FES, "fes:Filter");
		queryElem.appendChild(filter);
		Element spatialOperator = request.createElementNS(Namespaces.FES, "fes:" + spatialOperatorName);
		filter.appendChild(spatialOperator);
		if (null != valueRef) {
			spatialOperator.appendChild(request.importNode(valueRef, true));
		}
		spatialOperator.appendChild(request.importNode(envelope, true));
		if ("DWithin".equals(spatialOperatorName) || "Beyond".equals(spatialOperatorName)) {
			Element distance = request.createElementNS(Namespaces.FES, "fes:Distance");
			distance.setAttribute("uom", "m");
			distance.setTextContent("10");
			spatialOperator.appendChild(request.importNode(distance, true));
		}
	}

	protected List<String> parseExpectedSpatialOperators(Document wfsMetadata)
			throws XPathFactoryConfigurationException, XPathExpressionException {
		List<String> spatialOperatorNames = new ArrayList<String>();
		String xPathAccessConstraints = "//wfs:WFS_Capabilities/fes:Filter_Capabilities/fes:Spatial_Capabilities/fes:SpatialOperators/fes:SpatialOperator";
		XPath xpath = createXPath();
		NodeList spatialOperators = (NodeList) xpath.evaluate(xPathAccessConstraints, wfsMetadata,
				XPathConstants.NODESET);
		for (int spatialOperatorIndex = 0; spatialOperatorIndex < spatialOperators
			.getLength(); spatialOperatorIndex++) {
			Node spatialOperator = spatialOperators.item(spatialOperatorIndex);
			String spatialOperatorName = (String) xpath.evaluate("@name", spatialOperator, XPathConstants.STRING);
			if (spatialOperatorName != null && !spatialOperatorName.isEmpty()
					&& EXPECTED_SPATIAL_OPERATORS.contains(spatialOperatorName))
				spatialOperatorNames.add(spatialOperatorName);
		}
		return spatialOperatorNames;
	}

	private XPath createXPath() throws XPathFactoryConfigurationException {
		XPathFactory factory = XPathFactory.newInstance(XPathConstants.DOM_OBJECT_MODEL);
		XPath xpath = factory.newXPath();
		xpath.setNamespaceContext(NS_BINDINGS);
		return xpath;
	}

}