package de.latlon.ets.wfs20.core.dgiwg.testsuite.getcapabilities;

import static de.latlon.ets.core.assertion.ETSAssert.assertXPath;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.testng.annotations.Test;
import org.w3c.dom.Document;

import de.latlon.ets.wfs20.core.dgiwg.testsuite.WfsBaseFixture;

/**
 * Tests if the capabilities contains a valid value for AccessConstraint.
 *
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class GetCapabilitiesAccessConstraintTest extends WfsBaseFixture {

	private static final List<String> EXPECTED_ACCESS_CONSTRAINTS = Arrays.asList("unclassified", "restricted",
			"confidential", "secret", "topSecret");

	@Test(description = "DGIWG - Web Feature Service 2.0 Profile, 7.2.1., S.19, Requirement 4")
	public void wfsCapabilitiesAccessConstraintsExists() {
		String xPathXml = "//wfs:WFS_Capabilities/ows:ServiceIdentification/ows:AccessConstraints  != ''";
		assertXPath(xPathXml, this.wfsMetadata, NS_BINDINGS);
	}

	@Test(description = "DGIWG - Web Feature Service 2.0 Profile, 7.2.1., S.19, Requirement 4",
			dependsOnMethods = "wfsCapabilitiesAccessConstraintsExists")
	public void wfsCapabilitiesAccessConstraintsContainsValueFromDMF()
			throws XPathFactoryConfigurationException, XPathExpressionException {
		String accessConstraints = parseAccessConstraints(wfsMetadata);
		assertTrue(EXPECTED_ACCESS_CONSTRAINTS.contains(accessConstraints),
				"AccessConstraints are not valid, must be one of " + EXPECTED_ACCESS_CONSTRAINTS + " but was "
						+ accessConstraints);
	}

	private String parseAccessConstraints(Document wfsCapabilities)
			throws XPathFactoryConfigurationException, XPathExpressionException {
		String xPathAccessConstraints = "//wfs:WFS_Capabilities/ows:ServiceIdentification/ows:AccessConstraints ";
		XPath xpath = createXPath();
		return (String) xpath.evaluate(xPathAccessConstraints, wfsCapabilities, XPathConstants.STRING);
	}

}