package de.latlon.ets.wfs20.core.dgiwg.testsuite;

import java.io.InputStream;
import java.util.Random;
import java.util.logging.Level;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.opengis.cite.iso19142.BaseFixture;
import org.opengis.cite.iso19142.util.TestSuiteLogger;

import de.latlon.ets.core.util.NamespaceBindings;
import de.latlon.ets.wfs20.core.domain.WfsNamespaces;

/**
 * A supporting base class that provides common configuration methods and data providers. The configuration methods are
 * invoked before any that may be defined in a subclass.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class WfsBaseFixture extends BaseFixture {

    private static final Random RANDOM = new Random();

    protected static final NamespaceBindings NS_BINDINGS = WfsNamespaces.withStandardBindings();

    protected XPath createXPath()
                    throws XPathFactoryConfigurationException {
        XPathFactory factory = XPathFactory.newInstance( XPathConstants.DOM_OBJECT_MODEL );
        XPath xpath = factory.newXPath();
        xpath.setNamespaceContext( NS_BINDINGS );
        return xpath;
    }

    protected void initRequestEntity( InputStream requestXml ) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware( true );
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            this.reqEntity = builder.parse( requestXml );
        } catch ( Exception e ) {
            TestSuiteLogger.log( Level.WARNING, "Failed to parse request entity from classpath", e );
            this.reqEntity = null;
        }
    }

    protected QName retrieveRandomFeatureType() {
        int nextInt = RANDOM.nextInt( featureTypes.size() );
        return featureTypes.get( nextInt );
    }

}