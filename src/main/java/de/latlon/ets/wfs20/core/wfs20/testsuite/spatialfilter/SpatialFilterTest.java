package de.latlon.ets.wfs20.core.wfs20.testsuite.spatialfilter;

import static de.latlon.ets.core.assertion.ETSAssert.assertSchemaValid;
import static de.latlon.ets.core.assertion.ETSAssert.assertXPath;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

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
 * Tests the 09-026R2 Spatial Filter CC.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class SpatialFilterTest extends AbstractSpatialFilterTest {

    @BeforeClass
    public void init( ITestContext testContext ) {
        this.gmlGeomBaseType = model.getTypeDefinition( "AbstractGeometryType", Namespaces.GML );

        // Mechanism can be replaced when bug regarding WFS 2.0.2 schema is fixed in ets-wfs20. Uncomment two lines
        // below and delete last line (plus method).
        // this.wfsSchema = (Schema) testContext.getSuite().getAttribute( SuiteAttribute.WFS_SCHEMA.getName() );
        // assertNotNull( this.wfsSchema, "WFS schema not found in suite fixture." );
        this.wfsSchema = ValidationUtils.createWFSSchema( wfsMetadata );
    }

    @DataProvider(name = "protocol-featureType-spatialOperator")
    public Iterator<Object[]> protocolsAndFeatureTypesAndSpatialOperators()
                    throws XPathFactoryConfigurationException, XPathExpressionException {
        List<Object[]> params = new ArrayList<Object[]>();
        Set<ProtocolBinding> operationBindings = ServiceMetadataUtils.getOperationBindings( this.wfsMetadata,
                                                                                            WFS2.GET_FEATURE );
        for ( ProtocolBinding binding : operationBindings ) {
            for ( QName typeName : featureTypes ) {
                for ( String spatialOperator : parseExpectedSpatialOperators( this.wfsMetadata ) ) {
                    params.add( new Object[] { binding, typeName, spatialOperator } );
                }
            }
        }
        return params.iterator();
    }

    @Test(description = "DGIWG - Web Feature Service 2.0 Profile, 7.3.2., S.24, Requirement 8; "
                        + "OGC 09-026R2 Spatial Filter CC, A.8")
    public void inCapabilitiesImplementsSpatialFilterIsEnabled() {
        String xPathXml = "//wfs:WFS_Capabilities/fes:Filter_Capabilities/fes:Conformance/fes:Constraint[@name='ImplementsSpatialFilter']/ows:DefaultValue = 'TRUE'";
        assertXPath( xPathXml, this.wfsMetadata, NS_BINDINGS );
    }

    @Test(description = "DGIWG - Web Feature Service 2.0 Profile, 7.3.2., S.24, Requirement 8; "
                        + "OGC 09-026R2 Spatial Filter CC, A.8")
    public void inCapabilitiesBBOXOperatorIsEnabled() {
        String xPathXml = "//wfs:WFS_Capabilities/fes:Filter_Capabilities/fes:Spatial_Capabilities/fes:SpatialOperators/fes:SpatialOperator[@name='BBOX']";
        assertXPath( xPathXml, this.wfsMetadata, NS_BINDINGS );
    }

    @Test(description = "DGIWG - Web Feature Service 2.0 Profile, 7.3.2., S.24, Requirement 8; "
                        + "OGC 09-026R2 Spatial Filter CC, A.8")
    public void inCapabilitiesOneMoreSpatialOperatorIsEnabled()
                    throws XPathFactoryConfigurationException, XPathExpressionException {
        List<String> supportedSpatialOperators = parseExpectedSpatialOperators( this.wfsMetadata );
        assertTrue( supportedSpatialOperators.size() > 0,
                    "SpatialOperators are not valid. At least one of the spatial operators " + EXPECTED_SPATIAL_OPERATORS
                                    + " is expected" );
    }

    @Test(description = "DGIWG - Web Feature Service 2.0 Profile, 7.3.2., S.24, Requirement 8; "
                        + "OGC 09-026R2 Spatial Filter CC, A.8", dataProvider = "protocol-featureType-spatialOperator")
    public void withSpatialOperatorFilter( ProtocolBinding binding, QName featureType, String spatialOperatorName ) {
        XSElementDeclaration geomProp = findGeometryProperty( featureType );
        Element valueRef = WFSMessage.createValueReference( geomProp );
        WFSMessage.appendSimpleQuery( this.reqEntity, featureType );
        Document gmlEnv = Extents.envelopeAsGML( featureInfo.get( featureType ).getSpatialExtent() );
        addSpatialOperatorPredicate( this.reqEntity, spatialOperatorName, gmlEnv.getDocumentElement(), valueRef );

        Response rsp = wfsClient.submitRequest( reqEntity, binding );
        assertEquals( rsp.getStatus(), Status.OK.getStatusCode(),
                      ErrorMessage.get( ErrorMessageKeys.UNEXPECTED_STATUS ) );
        this.rspEntity = extractBodyAsDocument(rsp);
        assertSchemaValid( wfsSchema, this.rspEntity );
    }

}