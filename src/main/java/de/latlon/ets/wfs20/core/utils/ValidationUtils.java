package de.latlon.ets.wfs20.core.utils;

import java.net.URL;
import java.util.logging.Level;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;

import org.opengis.cite.iso19142.util.TestSuiteLogger;
import org.opengis.cite.validation.XmlSchemaCompiler;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * A utility class that provides convenience methods to support schema validation.
 */
public class ValidationUtils {

    /**
     * Creates a single Schema object representing the complete set of XML Schema constraints that apply to WFS 2.0
     * message entities.
     *
     * @param wfsMetadata
     *            metadata document of WFS. It is used to determine exact WFS 2.0 version (2.0.0 or 2.0.2).
     * 
     * @return An immutable Schema object, or <code>null</code> if one cannot be constructed.
     * @see <a href="http://schemas.opengis.net/wfs/2.0/wfs.xsd" target="_blank">XML Schema for WFS 2.0</a>
     */
    public static Schema createWFSSchema( Document wfsMetadata ) {
        URL entityCatalog = ValidationUtils.class.getResource( "schema-catalog.xml" );
        XmlSchemaCompiler xsdCompiler = new XmlSchemaCompiler( entityCatalog );
        Schema wfsSchema = null;
        try {
            URL schemaURL = createSchemaUrl( wfsMetadata );
            Source xsdSource = new StreamSource( schemaURL.toString() );
            wfsSchema = xsdCompiler.compileXmlSchema( new Source[] { xsdSource } );
        } catch ( SAXException e ) {
            TestSuiteLogger.log( Level.WARNING, "Failed to create WFS Schema object.", e );
        }
        return wfsSchema;
    }

    private static URL createSchemaUrl( Document wfsMetadata ) {
        if ( "2.0.0".equals( wfsMetadata.getDocumentElement().getAttribute( "version" ) ) )
            return ValidationUtils.class.getResource( "xsd/opengis/wfs/2.0/wfs-2.0.0.xsd" );
        else
            return ValidationUtils.class.getResource( "xsd/opengis/wfs/2.0/wfs-2.0.2.xsd" );
    }

}