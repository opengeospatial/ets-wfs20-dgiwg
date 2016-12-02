package de.latlon.ets.wfs20.core;

/**
 * An enumerated type defining all recognized test run arguments.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public enum TestRunArg {

    /**
     * An absolute URI referring to metadata about the WMS implementation under test. This is expected to be a WFS 2.0
     * capabilities document where the document element is {@code http://www.opengis.net/wfs} WFS_Capabilities}.
     */
    WFS,

    /**
     * Namespace of the FeatureType being used for tests including transactions.
     */
    TESTFEATURETYPENAMESPACE,

    /**
     * Name of the FeatureType being used for tests including transactions.
     */
    TESTFEATURETYPENAME;

    @Override
    public String toString() {
        return name().toLowerCase();
    }

}