package de.latlon.ets.wfs20.core.assertion;

import static de.latlon.ets.core.assertion.ETSAssert.assertXPath;

import javax.xml.namespace.QName;

import org.opengis.cite.iso19142.WFS2;
import org.w3c.dom.Document;

import de.latlon.ets.core.assertion.ETSAssert;
import de.latlon.ets.wfs20.core.domain.WfsNamespaces;

/**
 * Provides WFS 2.0 specific test assertion methods
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public final class WfsAssertion {

    private WfsAssertion() {
    }

    /**
     * Asserts that the given DOM document has the expected root element 'WMS_Capabilities' in namespace
     * {http://www.opengis.net/wfs}.
     * 
     * @param doc
     *            A Document node having {http://www.opengis.net/wfs} {@value WFS2#WFS_CAPABILITIES} as the root
     *            element.
     */
    public static void assertSimpleWFSCapabilities( Document doc ) {
        ETSAssert.assertQualifiedName( doc.getDocumentElement(), new QName( WfsNamespaces.WFS20, WFS2.WFS_CAPABILITIES ) );
    }

    /**
     * Asserts that the version of the capabilities response is 2.0.2.
     * 
     * @param response
     *            The capabilities response.
     */
    public static void assertVersion202( Document response ) {
        assertXPath( "//wfs:WFS_Capabilities/@version = '2.0.2'", response, WfsNamespaces.withStandardBindings() );
    }

}