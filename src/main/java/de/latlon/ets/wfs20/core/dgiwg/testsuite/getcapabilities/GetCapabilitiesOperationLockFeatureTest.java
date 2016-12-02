package de.latlon.ets.wfs20.core.dgiwg.testsuite.getcapabilities;

import static de.latlon.ets.core.assertion.ETSAssert.assertXPath;

import org.testng.annotations.Test;

import de.latlon.ets.wfs20.core.dgiwg.testsuite.WfsBaseFixture;

/**
 * Tests if the capabilities contains an operation LockFeature.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class GetCapabilitiesOperationLockFeatureTest extends WfsBaseFixture {

    @Test(description = "DGIWG - Web Feature Service 2.0 Profile, 8.2.3., S.28, Requirement 17")
    public void wfsCapabilitiesLockFeatureOperationExists() {
        String xPathXml = "//wfs:WFS_Capabilities/ows:OperationsMetadata/ows:Operation[@name = 'LockFeature']";
        assertXPath( xPathXml, this.wfsMetadata, NS_BINDINGS );
    }

}