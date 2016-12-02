package de.latlon.ets.wfs20.core.domain;

import javax.xml.XMLConstants;

import org.opengis.cite.iso19142.Namespaces;

import de.latlon.ets.core.util.NamespaceBindings;

/**
 * XML namespace names.
 * 
 * @see <a href="http://www.w3.org/TR/xml-names/">Namespaces in XML 1.0</a>
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public final class WfsNamespaces {

    private WfsNamespaces() {
    }

    /** OGC 09-025r1 (WFS 2.0) */
    public static final String WFS20 = Namespaces.WFS;

    /** OGC 06-121r3 (OWS 1.1) */
    public static final String OWS11 = Namespaces.OWS;

    /** W3C XLink */
    public static final String XLINK = Namespaces.XLINK;

    /** GML */
    public static final String GML = Namespaces.GML;

    /**
     * Creates a NamespaceBindings object that declares the following namespace bindings:
     * 
     * <ul>
     * <li>wfs: {@value de.latlon.ets.wfs20.core.domain.WfsNamespaces#WFS20}</li>
     * <li>xlink: {@value de.latlon.ets.wfs20.core.domain.WfsNamespaces#XLINK}</li>
     * </ul>
     * 
     * @return A NamespaceBindings object.
     */
    public static NamespaceBindings withStandardBindings() {
        NamespaceBindings nsBindings = new NamespaceBindings();
        nsBindings.addNamespaceBinding( WfsNamespaces.WFS20, "wfs" );
        nsBindings.addNamespaceBinding( WfsNamespaces.OWS11, "ows" );
        nsBindings.addNamespaceBinding( WfsNamespaces.XLINK, "xlink" );
        nsBindings.addNamespaceBinding( XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "xsi" );
        nsBindings.addNamespaceBinding( WfsNamespaces.GML, "gml" );
        nsBindings.addNamespaceBinding( Namespaces.FES, "fes" );
        return nsBindings;
    }

}