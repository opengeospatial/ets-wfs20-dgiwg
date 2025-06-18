package de.latlon.ets.wfs20.core.wfs20.testsuite.temporalfilter;

import static de.latlon.ets.core.assertion.ETSAssert.assertSchemaValid;
import static de.latlon.ets.core.assertion.ETSAssert.assertXPath;
import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.opengis.cite.iso19142.ErrorMessage;
import org.opengis.cite.iso19142.ErrorMessageKeys;
import org.opengis.cite.iso19142.ProtocolBinding;
import org.opengis.cite.iso19142.util.WFSMessage;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Element;

import de.latlon.ets.wfs20.core.utils.ValidationUtils;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * Extends the 09-026R2 Temporal Filter CC by testing if After and Before are supported.
 *
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class ExtendedTemporalFilterTest extends AbstractTemporalFilterTest {

	private static final List<String> EXPECTED_TEMPORAL_OPERATORS = Arrays.asList("After", "Before");

	@BeforeClass
	public void init(ITestContext testContext) {
		// Mechanism can be replaced when bug regarding WFS 2.0.2 schema is fixed in
		// ets-wfs20. Uncomment two lines
		// below and delete last line (plus method).
		// this.wfsSchema = (Schema) testContext.getSuite().getAttribute(
		// SuiteAttribute.WFS_SCHEMA.getName() );
		// assertNotNull( this.wfsSchema, "WFS schema not found in suite fixture." );
		this.wfsSchema = ValidationUtils.createWFSSchema(wfsMetadata);
	}

	@DataProvider(name = "protocol-featureType-additionalTemporalOperator")
	public Iterator<Object[]> protocolsAndFeatureTypesAndTemporalOperators()
			throws XPathFactoryConfigurationException, XPathExpressionException {
		return createDataProviderWithProtocolsFeatureTypesAndTemporalOperators(EXPECTED_TEMPORAL_OPERATORS);
	}

	@Test(description = "DGIWG - Web Feature Service 2.0 Profile, 7.3.3., S.24, Requirement 11")
	public void afterOperatorIsEnabled() {
		String xPathXml = "//wfs:WFS_Capabilities/fes:Filter_Capabilities/fes:Temporal_Capabilities/fes:TemporalOperators/fes:TemporalOperator[@name='After']";
		assertXPath(xPathXml, this.wfsMetadata, NS_BINDINGS);
	}

	@Test(description = "DGIWG - Web Feature Service 2.0 Profile, 7.3.3., S.24, Requirement 11")
	public void beforeOperatorIsEnabled() {
		String xPathXml = "//wfs:WFS_Capabilities/fes:Filter_Capabilities/fes:Temporal_Capabilities/fes:TemporalOperators/fes:TemporalOperator[@name='Before']";
		assertXPath(xPathXml, this.wfsMetadata, NS_BINDINGS);
	}

	@Test(description = "DGIWG - Web Feature Service 2.0 Profile, 7.3.3., S.24, Requirement 11",
			dataProvider = "protocol-featureType-additionalTemporalOperator")
	public void withTemporalOperatorFilter(ProtocolBinding binding,
			FeatureTypeToPropertyAndValue featureTypeToPropertyAndValue, String temporalOperatorName) {
		checkIfDataProviderFoundTestableData(binding, featureTypeToPropertyAndValue, temporalOperatorName);
		Element valueRef = WFSMessage.createValueReference(featureTypeToPropertyAndValue.getProperty());
		WFSMessage.appendSimpleQuery(this.reqEntity, featureTypeToPropertyAndValue.getFeatureType());
		addTemporalOperatorPredicate(this.reqEntity, temporalOperatorName, valueRef,
				featureTypeToPropertyAndValue.getValueRange());

		Response rsp = wfsClient.submitRequest(reqEntity, binding);
		assertEquals(rsp.getStatus(), Status.OK.getStatusCode(), ErrorMessage.get(ErrorMessageKeys.UNEXPECTED_STATUS));
		this.rspEntity = extractBodyAsDocument(rsp);
		assertSchemaValid(wfsSchema, this.rspEntity);
	}

}