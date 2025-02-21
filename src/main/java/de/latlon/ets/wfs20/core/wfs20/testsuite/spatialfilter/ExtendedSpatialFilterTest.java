package de.latlon.ets.wfs20.core.wfs20.testsuite.spatialfilter;

import static de.latlon.ets.core.assertion.ETSAssert.assertSchemaValid;
import static de.latlon.ets.core.assertion.ETSAssert.assertXPath;
import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.apache.xerces.xs.XSElementDeclaration;
import org.opengis.cite.geomatics.Extents;
import org.opengis.cite.iso19142.ErrorMessage;
import org.opengis.cite.iso19142.ErrorMessageKeys;
import org.opengis.cite.iso19142.Namespaces;
import org.opengis.cite.iso19142.ProtocolBinding;
import org.opengis.cite.iso19142.WFS2;
import org.opengis.cite.iso19142.util.ServiceMetadataUtils;
import org.opengis.cite.iso19142.util.WFSMessage;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.latlon.ets.wfs20.core.utils.ValidationUtils;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * Extends the 09-026R2 Spatial Filter CC by testing if all SpatialOperators are
 * supported.
 *
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class ExtendedSpatialFilterTest extends AbstractSpatialFilterTest {

	@BeforeClass
	public void init(ITestContext testContext) {
		this.gmlGeomBaseType = model.getTypeDefinition("AbstractGeometryType", Namespaces.GML);

		// Mechanism can be replaced when bug regarding WFS 2.0.2 schema is fixed in
		// ets-wfs20. Uncomment two lines
		// below and delete last line (plus method).
		// this.wfsSchema = (Schema) testContext.getSuite().getAttribute(
		// SuiteAttribute.WFS_SCHEMA.getName() );
		// assertNotNull( this.wfsSchema, "WFS schema not found in suite fixture." );
		this.wfsSchema = ValidationUtils.createWFSSchema(wfsMetadata);
	}

	@DataProvider(name = "spatialOperator")
	public Iterator<Object[]> spatialOperators() throws XPathFactoryConfigurationException, XPathExpressionException {
		List<Object[]> params = new ArrayList<Object[]>();
		for (String spatialOperator : EXPECTED_SPATIAL_OPERATORS) {
			params.add(new Object[] { ProtocolBinding.POST, spatialOperator });
		}
		return params.iterator();
	}

	@DataProvider(name = "protocol-featureType-allSpatialOperator")
	public Iterator<Object[]> protocolsAndFeatureTypesAndSpatialOperators()
			throws XPathFactoryConfigurationException, XPathExpressionException {
		List<Object[]> params = new ArrayList<Object[]>();
		Set<ProtocolBinding> operationBindings = ServiceMetadataUtils.getOperationBindings(this.wfsMetadata,
				WFS2.GET_FEATURE);
		for (ProtocolBinding binding : operationBindings) {
			for (QName typeName : featureTypes) {
				for (String spatialOperator : EXPECTED_SPATIAL_OPERATORS) {
					params.add(new Object[] { binding, typeName, spatialOperator });
				}
			}
		}
		return params.iterator();
	}

	@Test(description = "DGIWG - Web Feature Service 2.0 Profile, 7.3.2., S.24, Requirement 9",
			dataProvider = "spatialOperator")
	public void spatialOperatorIsEnabled(ProtocolBinding binding, String spatialOperator) {
		String xPathXml = String.format(
				"//wfs:WFS_Capabilities/fes:Filter_Capabilities/fes:Spatial_Capabilities/fes:SpatialOperators/fes:SpatialOperator[@name='%s']",
				spatialOperator);
		assertXPath(xPathXml, this.wfsMetadata, NS_BINDINGS);
	}

	@Test(description = "DGIWG - Web Feature Service 2.0 Profile, 7.3.2., S.24, Requirement 9",
			dataProvider = "protocol-featureType-allSpatialOperator")
	public void spatialOperatorFilterIsSupported(ProtocolBinding binding, QName featureType,
			String spatialOperatorName) {
		XSElementDeclaration geomProp = findGeometryProperty(featureType);
		Element valueRef = WFSMessage.createValueReference(geomProp);
		WFSMessage.appendSimpleQuery(this.reqEntity, featureType);
		Document gmlEnv = Extents.envelopeAsGML(featureInfo.get(featureType).getSpatialExtent());
		addSpatialOperatorPredicate(this.reqEntity, spatialOperatorName, gmlEnv.getDocumentElement(), valueRef);

		Response rsp = wfsClient.submitRequest(reqEntity, binding);
		assertEquals(rsp.getStatus(), Status.OK.getStatusCode(), ErrorMessage.get(ErrorMessageKeys.UNEXPECTED_STATUS));
		this.rspEntity = extractBodyAsDocument(rsp);
		assertSchemaValid(wfsSchema, this.rspEntity);
	}

}