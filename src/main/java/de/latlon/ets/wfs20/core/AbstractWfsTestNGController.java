package de.latlon.ets.wfs20.core;

import java.util.Locale;
import java.util.Map;

import de.latlon.ets.core.AbstractTestNGController;

/**
 * Main test run controller oversees execution of TestNG test suites.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public abstract class AbstractWfsTestNGController extends AbstractTestNGController {

    /**
     * Default constructor uses the location given by the "user.home" system property as the root output directory.
     */
    public AbstractWfsTestNGController() {
        super();
        setLocale();
    }

    /**
     * Construct a controller that writes results to the given output directory.
     * 
     * @param outputDirUri
     *            A file URI that specifies the location of the directory in which test results will be written. It will
     *            be created if it does not exist.
     */
    public AbstractWfsTestNGController( String outputDirUri ) {
        super( outputDirUri );
        setLocale();
    }

    @Override
    protected void validateTestRunArgs( Map<String, String> args ) {
        if ( !args.containsKey( TestRunArg.WFS.toString() ) ) {
            throw new IllegalArgumentException( String.format( "Missing argument: '%s' must be present.",
                                                               TestRunArg.WFS ) );
        }
    }

    private void setLocale() {
        Locale.setDefault( Locale.ENGLISH );
    }

}