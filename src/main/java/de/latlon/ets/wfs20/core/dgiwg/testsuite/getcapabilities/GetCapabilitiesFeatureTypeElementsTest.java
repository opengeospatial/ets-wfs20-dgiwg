package de.latlon.ets.wfs20.core.dgiwg.testsuite.getcapabilities;

import static de.latlon.ets.core.assertion.ETSAssert.assertXPath;
import static org.opengis.cite.iso19142.ProtocolBinding.GET;

import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.opengis.cite.iso19142.ProtocolBinding;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Node;

import de.latlon.ets.wfs20.core.dgiwg.testsuite.WfsBaseFixture;
import de.latlon.ets.wfs20.core.dgiwg.testsuite.dataprovider.WfsDataProvider;

/**
 * Tests if the FeatureTypes contains the expected attributes.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class GetCapabilitiesFeatureTypeElementsTest extends WfsBaseFixture {

    @DataProvider(name = "featureTypeNodes")
    public Object[][] parseFeatureTypeNodes( ITestContext testContext )
                    throws XPathFactoryConfigurationException, XPathExpressionException {
        initBaseFixture( testContext );
        return WfsDataProvider.provideFeatureTypeNodes( GET, wfsMetadata );
    }

    @Test(description = "DGIWG - Web Feature Service 2.0 Profile, 7.2.1., S.20, Requirement 5", dataProvider = "featureTypeNodes")
    public
                    void wfsCapabilitiesFeatureTypeElementsExists( ProtocolBinding protocolBinding,
                                                                   Node featureTypeNode, String name ) {
        String requiredElements = "wfs:Name and wfs:Title and ows:Keywords/ows:Keyword and wfs:DefaultCRS and ows:WGS84BoundingBox";
        assertXPath( requiredElements, featureTypeNode, NS_BINDINGS );
    }

}