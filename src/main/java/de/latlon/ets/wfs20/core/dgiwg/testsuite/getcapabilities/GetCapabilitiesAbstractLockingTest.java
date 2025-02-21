package de.latlon.ets.wfs20.core.dgiwg.testsuite.getcapabilities;

import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.testng.annotations.Test;

/**
 * Tests if the capabilities contains a valid value for Abstract.
 *
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class GetCapabilitiesAbstractLockingTest extends AbstractGetCapabilitiesAbstractTest {

	private static final String EXPECTED_ABSTRACT = "This server implements the DGIWG LOCKING WFS profile of WFS 2.0";

	@Test(description = "DGIWG - Web Feature Service 2.0 Profile, 8.2.1., S.27, Requirement 16")
	public void wfsCapabilitiesAbstractContainsLockingProfile()
			throws XPathFactoryConfigurationException, XPathExpressionException {
		assertThatCapabilitiesContainsExpectedAbstract(wfsMetadata, EXPECTED_ABSTRACT);
	}

}