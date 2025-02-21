package de.latlon.ets.wfs20.core.assertion;

import static de.latlon.ets.core.assertion.ETSAssert.assertUriIsResolvable;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class ETSAssertIT {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testAssertUrlIsResolvable() throws Exception {
		assertUriIsResolvable("http://schemas.opengis.net/wfs/2.0/capabilities_2_2_0.xsd");
	}

	@Test
	public void testAssertUrlIsResolvable_UnresolvableUri() throws Exception {
		thrown.expect(AssertionError.class);
		assertUriIsResolvable(null);
	}

	@Test
	public void testAssertUrlIsResolvable_InvalidUri() throws Exception {
		thrown.expect(AssertionError.class);
		assertUriIsResolvable("http://schemas.opengis.net/wfs/2.0.0/capabilities_1_3_0.png");
	}

}
