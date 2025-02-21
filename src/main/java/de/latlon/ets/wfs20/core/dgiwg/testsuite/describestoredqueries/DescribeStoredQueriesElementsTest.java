package de.latlon.ets.wfs20.core.dgiwg.testsuite.describestoredqueries;

import static de.latlon.ets.core.assertion.ETSAssert.assertXPath;
import static org.opengis.cite.iso19142.ProtocolBinding.GET;

import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.opengis.cite.iso19142.ProtocolBinding;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import de.latlon.ets.wfs20.core.dgiwg.testsuite.WfsBaseFixture;
import de.latlon.ets.wfs20.core.dgiwg.testsuite.dataprovider.WfsDataProvider;
import jakarta.ws.rs.core.Response;

/**
 * Tests if the StoredQueryDescribtion nodes from DescribeStoredQueries response contains the expected elements.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class DescribeStoredQueriesElementsTest extends WfsBaseFixture {

    @DataProvider(name = "storedQueryDescriptionNodes")
    public Object[][] parseStoredQueryDescriptionNodes( ITestContext testContext )
                    throws XPathFactoryConfigurationException, XPathExpressionException {
        initBaseFixture( testContext );
        initRequestEntity( getClass().getResourceAsStream( "/org/opengis/cite/iso19142/simple/DescribeStoredQueries.xml" ) );

        Response rsp = wfsClient.submitRequest( reqEntity, GET );
        return WfsDataProvider.provideStoredQueryDescriptionNodes( GET, rsp.readEntity( Document.class ) );
    }

    @Test(description = "DGIWG - Web Feature Service 2.0 Profile, 7.2.1., S.22, Requirement 6", dataProvider = "storedQueryDescriptionNodes")
    public
                    void describeStoredQueriesStoredQueriesDesriptionElementsExists( ProtocolBinding protocolBinding,
                                                                                     Node storedQueryDescriptionNode,
                                                                                     String id ) {
        String requiredElements = "@id and wfs:Title and wfs:Parameter/@name and "
                                  + "wfs:Parameter/@type and wfs:Parameter/wfs:Title and "
                                  + "wfs:QueryExpressionText and wfs:QueryExpressionText/@returnFeatureTypes and "
                                  + "wfs:QueryExpressionText/@language";
        assertXPath( requiredElements, storedQueryDescriptionNode, NS_BINDINGS );
    }

}