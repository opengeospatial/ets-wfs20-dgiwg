package de.latlon.ets.wfs20.core.dgiwg.testsuite.getcapabilities;

import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.testng.annotations.Test;

/**
 * Tests if the capabilities contains a valid value for Abstract.
 *
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class GetCapabilitiesAbstractTest extends AbstractGetCapabilitiesAbstractTest {

	private static final String EXPECTED_ABSTRACT = "This server implements the DGIWG BASIC WFS profile of WFS 2.0";

	@Test(description = "DGIWG - Web Feature Service 2.0 Profile, 7.2.1., S.18, Requirement 2")
	public void wfsCapabilitiesAbstractContainsProfile()
			throws XPathFactoryConfigurationException, XPathExpressionException {
		assertThatCapabilitiesContainsExpectedAbstract(wfsMetadata, EXPECTED_ABSTRACT);
	}

}