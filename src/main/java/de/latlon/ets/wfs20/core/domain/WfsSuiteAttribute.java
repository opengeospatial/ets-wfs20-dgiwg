package de.latlon.ets.wfs20.core.domain;

import javax.xml.namespace.QName;

/**
 * An enumerated type defining ISuite attributes that may be set to constitute a shared test fixture.
 * 
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 */
public enum WfsSuiteAttribute {

    TEST_FEATURE_TYPE( "testFeatureType", QName.class );

    private final String attrName;

    private final Class<?> attrType;

    private WfsSuiteAttribute( String attrName, Class<?> attrType ) {
        this.attrName = attrName;
        this.attrType = attrType;
    }

    public String getName() {
        return attrName;
    }

    public Class<?> getType() {
        return attrType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder( attrName );
        sb.append( '(' ).append( attrType.getName() ).append( ')' );
        return sb.toString();
    }

}