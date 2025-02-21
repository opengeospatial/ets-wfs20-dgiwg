package de.latlon.ets.wfs20.core.wfs20.testsuite.basic.filter;

import static org.opengis.cite.iso19142.ETSAssert.assertXPath;
import static org.opengis.cite.iso19142.ETSAssert.assertXPath2;
import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;

import javax.xml.XMLConstants;
import javax.xml.bind.DatatypeConverter;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;
import javax.xml.transform.dom.DOMSource;

import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSTypeDefinition;
import org.opengis.cite.iso19142.ErrorMessage;
import org.opengis.cite.iso19142.ErrorMessageKeys;
import org.opengis.cite.iso19142.Namespaces;
import org.opengis.cite.iso19142.ProtocolBinding;
import org.opengis.cite.iso19142.WFS2;
import org.opengis.cite.iso19142.basic.filter.QueryFilterFixture;
import org.opengis.cite.iso19142.util.AppSchemaUtils;
import org.opengis.cite.iso19142.util.TestSuiteLogger;
import org.opengis.cite.iso19142.util.WFSMessage;
import org.testng.SkipException;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * Tests the response to a GetFeature request that includes a {@code PropertyIsBetween} filter that compares the value
 * of a property against the specified range.
 *
 * <ul>
 * <li>ISO 19142:2010, cl. A.1.2: Basic WFS</li>
 * <li>ISO 19143:2010, cl. 7.7.3.7: PropertyIsBetween operator</li>
 * <li>ISO 19143:2010, cl. A.6: Test cases for standard filter</li>
 * </ul>
 */
public class PropertyIsBetweenOperatorTests extends QueryFilterFixture {

    /**
     * [{@code Test}] Submits a GetFeature request containing a {@code PropertyIsBetween} predicate that applies to some
     * simple feature property. The response entity must include only feature instances with matching property values;
     * 
     * @param binding
     *            The ProtocolBinding to use for this request.
     * @param featureType
     *            A QName representing the qualified name of some feature type.
     */
    @Test(description = "See ISO 19143: 7.7.3.7", dataProvider = "protocol-featureType")
    public void propertyIsBetween( ProtocolBinding binding, QName featureType ) {
        try {
            Map<XSElementDeclaration, String[]> propRangeMap = findNumericAndTemporalFeaturePropertyValue( featureType );
            if ( propRangeMap.isEmpty() ) {
                throw new SkipException( "No numeric or temporal property values found for " + featureType );
            }

            Entry<XSElementDeclaration, String[]> propRange = propRangeMap.entrySet().iterator().next();
            String propValueMin = propRange.getValue()[0];
            String propValueMax = propRange.getValue()[1];
            XSElementDeclaration propDecl = propRange.getKey();
            QName propName = new QName( propDecl.getNamespace(), propDecl.getName() );

            WFSMessage.appendSimpleQuery( this.reqEntity, featureType );
            addPropertyIsBetweenPredicate( this.reqEntity, propName, propValueMin, propValueMax );

            Response rsp = wfsClient.submitRequest( reqEntity, binding );
            this.rspEntity = extractBodyAsDocument(rsp);

            assertEquals( rsp.getStatus(), Status.OK.getStatusCode(),
                          ErrorMessage.get( ErrorMessageKeys.UNEXPECTED_STATUS ) );

            NodeList features = this.rspEntity.getElementsByTagNameNS( featureType.getNamespaceURI(),
                                                                       featureType.getLocalPart() );

            // Add constructor functions for property type (XML Schema datatype)
            QName dataType = AppSchemaUtils.getBuiltInDatatype( propDecl );
            String propTypeName = dataType.getLocalPart();
            String xpath = String.format( "xs:%s(ns1:%s) lt xs:%s('%s') and xs:%s(ns1:%s) gt xs:%s('%s')",
                                          propTypeName, propName.getLocalPart(), propTypeName, propValueMax,
                                          propTypeName, propName.getLocalPart(), propTypeName, propValueMin );
            Map<String, String> nsBindings = new HashMap<String, String>();
            nsBindings.put( propName.getNamespaceURI(), "ns1" );
            for ( int i = 0; i < features.getLength(); i++ ) {
                assertXPath2( xpath, new DOMSource( features.item( i ) ), nsBindings );
            }

        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * [{@code Test}] Submits a GetFeature request containing a PropertyIsBetween predicate that refers to an invalid
     * feature property. An exception report is expected in response with status code 400 and exception code
     * {@code InvalidParameterValue}.
     * 
     * @param binding
     *            The ProtocolBinding to use for this request.
     * 
     * @see "ISO 19143:2010, cl. 8.3: Exceptions"
     */
    @Test(description = "See ISO 19143: 8.3", dataProvider = "protocol-binding")
    public void invalidPropertyReference( ProtocolBinding binding ) {
        try {
            QName propName = new QName( "http://example.org", "undefined", "ex" );
            QName featureType = retrieveRandomFeatureType();

            WFSMessage.appendSimpleQuery( this.reqEntity, featureType );
            addPropertyIsBetweenPredicate( this.reqEntity, propName, "1355941270", "1355941271" );

            Response rsp = wfsClient.submitRequest( reqEntity, binding );
            this.rspEntity = rsp.readEntity( Document.class );
            assertEquals( rsp.getStatus(), Status.BAD_REQUEST.getStatusCode(),
                          ErrorMessage.get( ErrorMessageKeys.UNEXPECTED_STATUS ) );
            String xpath = "//ows:Exception[@exceptionCode='InvalidParameterValue']";
            assertXPath( xpath, this.rspEntity, null );

        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private Map<XSElementDeclaration, String[]> findNumericAndTemporalFeaturePropertyValue( QName featureType ) {
        Set<XSTypeDefinition> dataTypes = getNumericDataTypes( this.model );
        dataTypes.addAll( getTemporalDataTypes( this.model ) );
        Map<XSElementDeclaration, String[]> propRangeMap = findFeaturePropertyValue( this.model, featureType, dataTypes );
        return propRangeMap;
    }

    /**
     * <pre>
     * {@code
     * <Filter xmlns="http://www.opengis.net/fes/2.0">
     *   <PropertyIsBetween matchCase="true" matchAction="Any">
     *     <ValueReference>tns:featureProperty</ValueReference>
     *     <LowerBoundary>
     *       <Literal>value</Literal>
     *     </LowerBoundary>
     *     <UpperBoundary>
     *       <Literal>value</Literal>
     *     </UpperBoundary>
     *   </PropertyIsLessThan>
     * </Filter>
     * }
     * </pre>
     * 
     */
    private void addPropertyIsBetweenPredicate( Document request, QName propertyName, Object lowerBoundaryValue,
                                                Object upperBoundaryValue ) {
        if ( !request.getDocumentElement().getLocalName().equals( WFS2.GET_FEATURE ) ) {
            throw new IllegalArgumentException( "Not a GetFeature request: "
                                                + request.getDocumentElement().getNodeName() );
        }
        if ( null == propertyName ) {
            throw new IllegalArgumentException( "propertyName is required." );
        }
        Element queryElem = (Element) request.getElementsByTagNameNS( Namespaces.WFS, WFS2.QUERY_ELEM ).item( 0 );
        Element filter = request.createElementNS( Namespaces.FES, "Filter" );
        queryElem.appendChild( filter );
        Element predicate = request.createElementNS( Namespaces.FES, "PropertyIsBetween" );
        filter.appendChild( predicate );

        addValueReference( request, propertyName, predicate );
        addBoundary( request, "LowerBoundary", lowerBoundaryValue, predicate );
        addBoundary( request, "UpperBoundary", upperBoundaryValue, predicate );
    }

    private void addValueReference( Document request, QName propertyName, Element predicate ) {
        Element valueRef = request.createElementNS( Namespaces.FES, "ValueReference" );
        predicate.appendChild( valueRef );
        String prefix = ( propertyName.getPrefix().length() > 0 ) ? propertyName.getPrefix() : TNS_PREFIX;
        String nsURI = request.lookupNamespaceURI( prefix );
        if ( null == nsURI ) {
            valueRef.setAttribute( XMLConstants.XMLNS_ATTRIBUTE + ":" + prefix, propertyName.getNamespaceURI() );
        }
        valueRef.setTextContent( prefix + ":" + propertyName.getLocalPart() );
    }

    private void addBoundary( Document request, String boundaryName, Object literalValue, Element predicate ) {
        Element boundaryElem = request.createElementNS( Namespaces.FES, boundaryName );
        predicate.appendChild( boundaryElem );
        Element literalElem = request.createElementNS( Namespaces.FES, "Literal" );
        if ( String.class.isInstance( literalValue ) ) {
            literalElem.setTextContent( (String) literalValue );
        } else {
            Document literalDoc = (Document) literalValue;
            literalElem.appendChild( request.adoptNode( literalDoc.getDocumentElement() ) );
        }
        boundaryElem.appendChild( literalElem );
    }

    /**
     * Inspects sample data retrieved from the SUT and determines the range of simple property values for the specified
     * feature type. The method finds the first feature property that (a) conforms to one of the given type definitions,
     * and (b) has at least one value in the data sample.
     * 
     * @param model
     *            An XSModel object representing an application schema.
     * @param featureType
     *            The qualified name of some feature type.
     * @param dataTypes
     *            A Set of simple data types that possess an interval or ratio scale of measurement (e.g. numeric or
     *            temporal data types).
     * @return A Map containing a single entry where the key is an element declaration and the value is a
     *         {@code String[]} array containing two String objects representing the minimum and maximum values of the
     *         property.
     */
    private Map<XSElementDeclaration, String[]> findFeaturePropertyValue( XSModel model, QName featureType,
                                                                          Set<XSTypeDefinition> dataTypes ) {
        List<XSElementDeclaration> featureProps = null;
        // look for properties of the given data types
        for ( XSTypeDefinition dataType : dataTypes ) {
            featureProps = AppSchemaUtils.getFeaturePropertiesByType( model, featureType, dataType );
            if ( !featureProps.isEmpty() ) {
                break;
            }
        }
        ListIterator<XSElementDeclaration> listItr = featureProps.listIterator( featureProps.size() );
        XSElementDeclaration prop = null;
        String[] valueRange = null;
        // start with application-specific properties at end of list
        while ( listItr.hasPrevious() ) {
            prop = listItr.previous();
            QName propName = new QName( prop.getNamespace(), prop.getName() );
            List<String> valueList = this.dataSampler.getSimplePropertyValues( featureType, propName, null );
            if ( !valueList.isEmpty() ) {
                String[] values = new String[valueList.size()];
                for ( int i = 0; i < values.length; i++ ) {
                    values[i] = valueList.get( i );
                }
                // use actual datatype to produce valid string representation
                QName datatype = AppSchemaUtils.getBuiltInDatatype( prop );
                valueRange = calculateRange( values, datatype );
                TestSuiteLogger.log( Level.FINE, String.format( "Found property values of %s, %s %n %s", featureType,
                                                                propName, Arrays.toString( valueRange ) ) );
                break;
            }
        }
        Map<XSElementDeclaration, String[]> map = new HashMap<XSElementDeclaration, String[]>();
        if ( null != valueRange ) {
            map.put( prop, valueRange );
        }
        return map;
    }

    /**
     * Calculates the range of the given values and returns the minimum and maximum values as valid string literals.
     * 
     * @param values
     *            An array of strings representing numeric or temporal values.
     * @param datatype
     *            The name of the built-in XML Schema datatype to which the values must conform.
     * @return An array containing the (min, max) values.
     */
    private String[] calculateRange( String[] values, QName datatype ) {
        sortValues( values );
        Set<String> integerDatatypes = new HashSet<String>();
        // WARNING: some subtypes omitted
        Collections.addAll( integerDatatypes, new String[] { "integer", "nonPositiveInteger", "nonNegativeInteger",
                                                            "long", "int" } );
        if ( integerDatatypes.contains( datatype.getLocalPart() ) ) {
            for ( int i = 0; i < values.length; i++ ) {
                int intValue = Double.valueOf( values[i] ).intValue();
                values[i] = Integer.toString( intValue );
            }
        }
        return new String[] { values[0], values[values.length - 1] };
    }

    /**
     * Sorts the given array into ascending order, assuming its elements represent either numeric (Double) or temporal
     * (Calendar) values. Temporal values are expressed in UTC. The corresponding built-in datatypes from XML Schema
     * are:
     * 
     * <ul>
     * <li>xsd:decimal (including xsd:integer and its subtypes)</li>
     * <li>xsd:double</li>
     * <li>xsd:float</li>
     * <li>xsd:dateTime</li>
     * <li>xsd:date</li>
     * </ul>
     * 
     * @param values
     *            An array containing String representations of numeric or temporal values.
     */
    private void sortValues( String[] values ) {
        if ( ( null == values ) || values.length == 0 ) {
            return;
        }
        Object[] objValues = null;
        try {
            objValues = new Double[values.length];
            for ( int i = 0; i < objValues.length; i++ ) {
                objValues[i] = Double.valueOf( values[i] );
            }
            Arrays.sort( objValues );
        } catch ( NumberFormatException nfe ) {
            // try Calendar value
            objValues = new Calendar[values.length];
            for ( int i = 0; i < objValues.length; i++ ) {
                if ( values[i].indexOf( 'T' ) > 0 ) {
                    objValues[i] = DatatypeConverter.parseDateTime( values[i] );
                } else {
                    objValues[i] = DatatypeConverter.parseDate( values[i] );
                }
            }
            Arrays.sort( objValues );
        }
        DatatypeFactory dtFactory;
        try {
            dtFactory = DatatypeFactory.newInstance();
        } catch ( DatatypeConfigurationException e ) {
            throw new RuntimeException( e );
        }
        for ( int i = 0; i < values.length; i++ ) {
            if ( GregorianCalendar.class.isInstance( objValues[i] ) ) {
                GregorianCalendar gCal = (GregorianCalendar) objValues[i];
                values[i] = dtFactory.newXMLGregorianCalendar( gCal ).normalize().toString();
            } else {
                values[i] = objValues[i].toString();
            }
        }
    }

    /**
     * Returns a set of primitive numeric data type definitions (xsd:decimal, xsd:double, xsd:float). Derived data types
     * are also implicitly included (e.g. xsd:integer).
     * 
     * @param model
     *            An XSModel object representing an application schema.
     * @return A Set of simple type definitions corresponding to numeric data types.
     */
    private Set<XSTypeDefinition> getNumericDataTypes( XSModel model ) {
        Set<XSTypeDefinition> dataTypes = new HashSet<XSTypeDefinition>();
        dataTypes.add( model.getTypeDefinition( "decimal", XMLConstants.W3C_XML_SCHEMA_NS_URI ) );
        dataTypes.add( model.getTypeDefinition( "double", XMLConstants.W3C_XML_SCHEMA_NS_URI ) );
        dataTypes.add( model.getTypeDefinition( "float", XMLConstants.W3C_XML_SCHEMA_NS_URI ) );
        return dataTypes;
    }

    /**
     * Returns a set of primitive, non-recurring temporal data type definitions, including:
     * 
     * <ul>
     * <li>xsd:dateTime ("yyyy-MM-dd'T'HH:mm:ssZ")</li>
     * <li>xsd:date ("yyyy-MM-ddZ")</li>
     * <li>xsd:gYearMonth ("yyyy-MM")</li>
     * <li>xsd:gYear ("yyyy")</li>
     * </ul>
     * 
     * @param model
     *            An XSModel object representing an application schema.
     * @return A Set of simple type definitions corresponding to temporal data types.
     */
    private Set<XSTypeDefinition> getTemporalDataTypes( XSModel model ) {
        Set<XSTypeDefinition> dataTypes = new HashSet<XSTypeDefinition>();
        dataTypes.add( model.getTypeDefinition( "dateTime", XMLConstants.W3C_XML_SCHEMA_NS_URI ) );
        dataTypes.add( model.getTypeDefinition( "date", XMLConstants.W3C_XML_SCHEMA_NS_URI ) );
        dataTypes.add( model.getTypeDefinition( "gYearMonth", XMLConstants.W3C_XML_SCHEMA_NS_URI ) );
        dataTypes.add( model.getTypeDefinition( "gYear", XMLConstants.W3C_XML_SCHEMA_NS_URI ) );
        return dataTypes;
    }

    private QName retrieveRandomFeatureType() {
        // randomly select a feature type
        Random rnd = new Random();
        int index = rnd.nextInt( this.featureTypes.size() );
        QName featureType = this.featureTypes.get( index );
        return featureType;
    }

}