package de.latlon.ets.wfs20.core.dgiwg.testsuite.getcapabilities;

import static de.latlon.ets.core.assertion.ETSAssert.checkXPath;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import de.latlon.ets.wfs20.core.dgiwg.testsuite.WfsBaseFixture;

/**
 * Tests if the capabilities contains the mandatory srs EPSG:4326 and CRS:84..
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class GetCapabilitiesSrsTest extends WfsBaseFixture {

    private static final List<String> EPSG_4326 = initEpsg4326();

    private static final List<String> CRS_84 = initCrs84();

    @Test(description = "DGIWG - Web Feature Service 2.0 Profile, 9.2., S.30, Requirement 21")
    public void wfsCapabilitiesSrsEpsg4326Supported() {
        assertSrs( EPSG_4326 );
    }

    @Test(description = "DGIWG - Web Feature Service 2.0 Profile, 9.2., S.30, Requirement 21")
    public void wfsCapabilitiesSrsCrs84Supported() {
        assertSrs( CRS_84 );
    }

    private static List<String> initEpsg4326() {
        List<String> list = new ArrayList();
        list.add( "http://www.opengis.net/def/crs/EPSG/0/4326" );
        list.add( "urn:ogc:def:crs:EPSG::4326" );
        return list;
    }

    private static List<String> initCrs84() {
        List<String> list = new ArrayList();
        list.add( "http://www.opengis.net/def/crs/OGC/1.3/CRS84" );
        list.add( "urn:ogc:def:crs:OGC:1.3:CRS84" );
        return list;
    }

    private void assertSrs( List<String> expectedSrs ) {
        String expr = "//wfs:WFS_Capabilities/ows:OperationsMetadata/ows:Parameter[@name='srsName']/ows:AllowedValues/ows:Value = '%s'";
        boolean isOneExpectedSrsSupported = false;
        for ( String srs : expectedSrs ) {
            if ( !isOneExpectedSrsSupported ) {
                String xPathXml = String.format( expr, srs );
                isOneExpectedSrsSupported = checkXPath( xPathXml, this.wfsMetadata, NS_BINDINGS );
            }
        }
        assertTrue( isOneExpectedSrsSupported, "Capabilities do not contain mandatory SRS in one of the following notation: " + expectedSrs );
    }

}