package de.latlon.ets.wfs20.core.dgiwg.testsuite.getcapabilities;

import static de.latlon.ets.wfs20.core.assertion.WfsAssertion.assertVersion202;

import org.testng.annotations.Test;

import de.latlon.ets.wfs20.core.dgiwg.testsuite.WfsBaseFixture;

/**
 * Tests if the version of the capabilities response is 2.0.2.
 *
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 */
public class GetCapabilitiesVersionTest extends WfsBaseFixture {

    @Test(description = "DGIWG - Web Feature Service 2.0 Profile, 7.2.1., S.15")
    public void wfsCapabilitiesIsVersion202() {
        assertVersion202( this.wfsMetadata );
    }

}