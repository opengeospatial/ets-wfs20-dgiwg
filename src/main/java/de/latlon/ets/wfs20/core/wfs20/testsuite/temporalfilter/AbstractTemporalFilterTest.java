package de.latlon.ets.wfs20.core.wfs20.testsuite.temporalfilter;

import static de.latlon.ets.wfs20.core.utils.TimeUtils.calculateValueBetween;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.validation.Schema;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSTypeDefinition;
import org.opengis.cite.iso19142.Namespaces;
import org.opengis.cite.iso19142.ProtocolBinding;
import org.opengis.cite.iso19142.WFS2;
import org.opengis.cite.iso19142.basic.filter.QueryFilterFixture;
import org.opengis.cite.iso19142.util.AppSchemaUtils;
import org.opengis.cite.iso19142.util.ServiceMetadataUtils;
import org.opengis.cite.iso19142.util.TestSuiteLogger;
import org.testng.SkipException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.latlon.ets.core.util.NamespaceBindings;
import de.latlon.ets.wfs20.core.domain.WfsNamespaces;
import de.latlon.ets.wfs20.core.utils.TimeUtils;

/**
 * Base class for TemporalFilterTests.
 *
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public abstract class AbstractTemporalFilterTest extends QueryFilterFixture {

	protected static final NamespaceBindings NS_BINDINGS = WfsNamespaces.withStandardBindings();

	protected Schema wfsSchema;

	protected void addTemporalOperatorPredicate(Document request, String temporalOperatorName, Element valueRef,
			Calendar[] dateRange) {
		if (!request.getDocumentElement().getLocalName().equals(WFS2.GET_FEATURE)) {
			throw new IllegalArgumentException(
					"Not a GetFeature request: " + request.getDocumentElement().getNodeName());
		}
		Element queryElem = (Element) request.getElementsByTagNameNS(Namespaces.WFS, WFS2.QUERY_ELEM).item(0);
		Element filter = request.createElementNS(Namespaces.FES, "fes:Filter");
		queryElem.appendChild(filter);
		Element temporalOperator = request.createElementNS(Namespaces.FES, "fes:" + temporalOperatorName);
		filter.appendChild(temporalOperator);
		if (null != valueRef)
			temporalOperator.appendChild(request.importNode(valueRef, true));
		appendTimeElement(request, temporalOperatorName, dateRange, temporalOperator);
	}

	protected Iterator<Object[]> createDataProviderWithProtocolsFeatureTypesAndTemporalOperators(
			List<String> expectedTemporalOperators)
			throws XPathFactoryConfigurationException, XPathExpressionException {
		List<Object[]> params = new ArrayList<Object[]>();
		Set<ProtocolBinding> operationBindings = ServiceMetadataUtils.getOperationBindings(this.wfsMetadata,
				WFS2.GET_FEATURE);
		for (ProtocolBinding binding : operationBindings) {
			for (QName featureType : featureTypes) {
				for (String temporalOperator : parseExpectedTemporalOperators(this.wfsMetadata,
						expectedTemporalOperators)) {
					FeatureTypeToPropertyAndValue datePropertyAndValueRange = findDatePropertyValue(featureType);
					if (datePropertyAndValueRange != null)
						params.add(new Object[] { binding, datePropertyAndValueRange, temporalOperator });
				}
			}
		}
		if (params.isEmpty())
			params.add(new Object[] { null, null, null });
		return params.iterator();
	}

	protected List<String> parseExpectedTemporalOperators(Document wfsMetadata, List<String> expectedTemporalOperators)
			throws XPathFactoryConfigurationException, XPathExpressionException {
		List<String> temporalOperatorNames = new ArrayList<String>();
		String xPathAccessConstraints = "//wfs:WFS_Capabilities/fes:Filter_Capabilities/fes:Temporal_Capabilities/fes:TemporalOperators/fes:TemporalOperator";
		XPath xpath = createXPath();
		NodeList temporalOperators = (NodeList) xpath.evaluate(xPathAccessConstraints, wfsMetadata,
				XPathConstants.NODESET);
		for (int temporalOperatorIndex = 0; temporalOperatorIndex < temporalOperators
			.getLength(); temporalOperatorIndex++) {
			Node temporalOperator = temporalOperators.item(temporalOperatorIndex);
			String temporalOperatorName = (String) xpath.evaluate("@name", temporalOperator, XPathConstants.STRING);
			if (temporalOperatorName != null && !temporalOperatorName.isEmpty()
					&& expectedTemporalOperators.contains(temporalOperatorName))
				temporalOperatorNames.add(temporalOperatorName);
		}
		return temporalOperatorNames;
	}

	protected void checkIfDataProviderFoundTestableData(ProtocolBinding binding,
			FeatureTypeToPropertyAndValue featureTypeToPropertyAndValue, String temporalOperatorName) {
		if (binding == null && featureTypeToPropertyAndValue == null && temporalOperatorName == null)
			throw new SkipException("No feature type with date property found!");
	}

	private FeatureTypeToPropertyAndValue findDatePropertyValue(QName featureType) {
		List<XSElementDeclaration> dateProps = findDateProperties(featureType);
		for (XSElementDeclaration prop : dateProps) {
			QName propName = new QName(prop.getNamespace(), prop.getName());
			List<String> valueList = this.dataSampler.getSimplePropertyValues(featureType, propName, null);
			if (!valueList.isEmpty()) {
				String[] values = new String[valueList.size()];
				for (int i = 0; i < values.length; i++) {
					values[i] = valueList.get(i);
				}
				Calendar[] valueRange = TimeUtils.calculateDateRange(values);
				if (valueRange.length > 1) {
					TestSuiteLogger.log(Level.FINE,
							String.format("Found property values of %s, %s", featureType, propName));
					return new FeatureTypeToPropertyAndValue(featureType, prop, valueRange);
				}
			}
		}
		return null;
	}

	private void appendTimeElement(Document request, String temporalOperatorName, Calendar[] dateRange,
			Element temporalOperator) {
		if (isPeriod(temporalOperatorName)) {
			// <gml:TimePeriod gml:id="tp_1">
			// <gml:beginPosition>2009-01-01T00:00:00.000</gml:beginPosition>
			// <gml:endPosition indeterminatePosition="unknown" />
			// </gml:TimePeriod>
			Element timePeriod = request.createElementNS(Namespaces.GML, "gml:TimePeriod");
			timePeriod.setAttributeNS(Namespaces.GML, "gml:id", "tp_1");
			createAndAppendTimePosition(request, "beginPosition", dateRange, timePeriod);
			createAndAppendTimePosition(request, "endPosition", null, timePeriod);
			temporalOperator.appendChild(timePeriod);
		}
		else {
			// <gml:TimeInstant gml:id="tp_1">
			// <gml:timePosition>2009-01-01T00:00:00.000</gml:timePosition>
			// </gml:TimeInstant>
			Element timeInstant = request.createElementNS(Namespaces.GML, "gml:TimeInstant");
			timeInstant.setAttributeNS(Namespaces.GML, "gml:id", "tp_1");
			createAndAppendTimePosition(request, "timePosition", dateRange, timeInstant);
			temporalOperator.appendChild(timeInstant);
		}
	}

	private void createAndAppendTimePosition(Document request, String gmlName, Calendar[] dateRange, Element parent) {
		Element position = request.createElementNS(Namespaces.GML, "gml:" + gmlName);
		if (dateRange == null)
			position.setAttribute("indeterminatePosition", "unknown");
		else
			position.setTextContent(calculateValueBetween(dateRange));
		parent.appendChild(position);
	}

	private XPath createXPath() throws XPathFactoryConfigurationException {
		XPathFactory factory = XPathFactory.newInstance(XPathConstants.DOM_OBJECT_MODEL);
		XPath xpath = factory.newXPath();
		xpath.setNamespaceContext(NS_BINDINGS);
		return xpath;
	}

	private List<XSElementDeclaration> findDateProperties(QName featureType) {
		List<XSElementDeclaration> dateProperties = new ArrayList<XSElementDeclaration>();
		for (XSTypeDefinition dataType : getTemporalDataTypes(model)) {
			List<XSElementDeclaration> props = AppSchemaUtils.getFeaturePropertiesByType(model, featureType, dataType);
			if (!props.isEmpty()) {
				dateProperties.addAll(props);
			}
		}
		if (!dateProperties.isEmpty()) {
			Collections.sort(dateProperties, Collections.reverseOrder());
			return dateProperties;
		}
		return Collections.emptyList();
	}

	/**
	 * Returns a set of primitive, non-recurring temporal data type definitions,
	 * including:
	 *
	 * <ul>
	 * <li>xsd:dateTime ("yyyy-MM-dd'T'HH:mm:ssZ")</li>
	 * <li>xsd:date ("yyyy-MM-ddZ")</li>
	 * <li>xsd:gYearMonth ("yyyy-MM")</li>
	 * <li>xsd:gYear ("yyyy")</li>
	 * </ul>
	 * @param model An XSModel object representing an application schema.
	 * @return A Set of simple type definitions corresponding to temporal data types.
	 */
	private Set<XSTypeDefinition> getTemporalDataTypes(XSModel model) {
		Set<XSTypeDefinition> dataTypes = new HashSet<XSTypeDefinition>();
		dataTypes.add(model.getTypeDefinition("dateTime", XMLConstants.W3C_XML_SCHEMA_NS_URI));
		dataTypes.add(model.getTypeDefinition("date", XMLConstants.W3C_XML_SCHEMA_NS_URI));
		dataTypes.add(model.getTypeDefinition("gYearMonth", XMLConstants.W3C_XML_SCHEMA_NS_URI));
		dataTypes.add(model.getTypeDefinition("gYear", XMLConstants.W3C_XML_SCHEMA_NS_URI));
		return dataTypes;
	}

	private boolean isPeriod(String temporalOperatorName) {
		return Arrays.asList("During").contains(temporalOperatorName);
	}

	/**
	 * Encapsulates the FeatureType a date property of the feature type and the value
	 * range. None of them must be <code>null</code>!
	 *
	 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
	 */
	class FeatureTypeToPropertyAndValue {

		private final QName featureType;

		private final XSElementDeclaration property;

		private final Calendar[] valueRange;

		public FeatureTypeToPropertyAndValue(QName featureType, XSElementDeclaration property, Calendar[] valueRange) {
			this.featureType = featureType;
			this.property = property;
			this.valueRange = valueRange;
		}

		public QName getFeatureType() {
			return featureType;
		}

		public XSElementDeclaration getProperty() {
			return property;
		}

		public Calendar[] getValueRange() {
			return valueRange;
		}

		@Override
		public String toString() {
			return "FeatureTypeToPropertyAndValue [featureType=" + featureType + ", property=" + property + "]";
		}

	}

}