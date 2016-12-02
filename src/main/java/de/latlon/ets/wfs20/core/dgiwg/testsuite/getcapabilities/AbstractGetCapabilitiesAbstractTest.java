package de.latlon.ets.wfs20.core.dgiwg.testsuite.getcapabilities;

import static org.testng.Assert.assertTrue;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.w3c.dom.Document;

import de.latlon.ets.wfs20.core.dgiwg.testsuite.WfsBaseFixture;

/**
 * Abstract test class to assert the abstract contains the expected string.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public abstract class AbstractGetCapabilitiesAbstractTest extends WfsBaseFixture {

    protected void assertThatCapabilitiesContainsExpectedAbstract( Document wfsCapabilities, String expectedAbstract )
                    throws XPathFactoryConfigurationException, XPathExpressionException {
        String abstractValue = parseAbstract( wfsCapabilities );
        assertTrue( abstractValue.contains( expectedAbstract ), "Abstract is not valid, must contain the string '"
                                                                + expectedAbstract + " but is '" + abstractValue + "'" );
    }

    private String parseAbstract( Document wfsCapabilities )
                    throws XPathFactoryConfigurationException, XPathExpressionException {
        String xPathAbstract = "//wfs:WFS_Capabilities/ows:ServiceIdentification/ows:Abstract ";
        XPath xpath = createXPath();
        return (String) xpath.evaluate( xPathAbstract, wfsCapabilities, XPathConstants.STRING );
    }

}
