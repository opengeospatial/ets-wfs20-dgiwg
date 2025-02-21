/**
 *
 */
package de.latlon.ets.wfs20.core.assertion;

import static de.latlon.ets.wfs20.core.assertion.WfsAssertion.assertVersion202;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class WfsAssertionTest {

	@Test
	public void testAssertVersion202WithCapabilitiesVersion202() throws Exception {
		assertVersion202(wfsCapabilitiesVersion202());
	}

	@Test(expected = AssertionError.class)
	public void testAssertVersion202WithCapabilitiesVersion200ShouldFail() throws Exception {
		assertVersion202(wfsCapabilitiesVersion200());
	}

	private Document wfsCapabilitiesVersion200() throws ParserConfigurationException, SAXException, IOException {
		return wfsCapabilities("../capabilities_wfs200.xml");
	}

	private Document wfsCapabilitiesVersion202() throws ParserConfigurationException, SAXException, IOException {
		return wfsCapabilities("../capabilities_wfs202.xml");
	}

	private Document wfsCapabilities(String resource) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputStream wfsCapabilities = WfsAssertionTest.class.getResourceAsStream(resource);
		return builder.parse(new InputSource(wfsCapabilities));
	}

}