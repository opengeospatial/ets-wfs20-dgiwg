package de.latlon.ets.wfs20.core.dgiwg.testsuite.dataprovider;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.opengis.cite.iso19142.ProtocolBinding.GET;

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
public class WfsDataProviderTest {

	@Test
	public void testProvideFeatureTypeNodes() throws Exception {
		Object[][] featureTypeNodes = WfsDataProvider.provideFeatureTypeNodes(GET, wfsCapabilities());

		assertThat(featureTypeNodes.length, is(18));
	}

	@Test(expected = Exception.class)
	public void testProvideFeatureTypeNodesWithNullShouldThrowException() throws Exception {
		WfsDataProvider.provideFeatureTypeNodes(GET, null);
	}

	@Test
	public void testProvideStoredQueryDescriptionNodes() throws Exception {
		Object[][] storedQueryDesciptionNodes = WfsDataProvider.provideStoredQueryDescriptionNodes(GET,
				describeStoredQueries());

		assertThat(storedQueryDesciptionNodes.length, is(2));
	}

	@Test(expected = Exception.class)
	public void testProvideStoredQueryDescriptionNodesWithNullShouldThrowException() throws Exception {
		WfsDataProvider.provideStoredQueryDescriptionNodes(GET, null);
	}

	private Document wfsCapabilities() throws SAXException, IOException, ParserConfigurationException {
		return capabilities("../../../capabilities_wfs200.xml");
	}

	private Document describeStoredQueries() throws SAXException, IOException, ParserConfigurationException {
		return capabilities("../../../describeStoredQueries_wfs200.xml");
	}

	private Document capabilities(String resource) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputStream wfsCapabilities = WfsDataProviderTest.class.getResourceAsStream(resource);
		return builder.parse(new InputSource(wfsCapabilities));
	}

}