package de.latlon.ets.wfs20.core.client;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class WfsKvpRequestTest {

	@Test
	public void testAsQueryString() {
		WfsKvpRequest wfsKvpRequest = new WfsKvpRequest();
		wfsKvpRequest.addKvp("key1", "value1");
		wfsKvpRequest.addKvp("key2", "value2");

		String queryString = wfsKvpRequest.asQueryString();

		assertThat(queryString, CoreMatchers.anyOf(is("key1=value1&key2=value2"), is("key2=value2&key1=value1")));
	}

	@Test
	public void testAsQueryStringOverwriteKey() {
		WfsKvpRequest wfsKvpRequest = new WfsKvpRequest();
		wfsKvpRequest.addKvp("key2", "value2");
		wfsKvpRequest.addKvp("key2", "value3");

		String queryString = wfsKvpRequest.asQueryString();

		assertThat(queryString, is("key2=value3"));
	}

	@Test
	public void testAsQueryStringNullKey() {
		WfsKvpRequest wfsKvpRequest = new WfsKvpRequest();
		wfsKvpRequest.addKvp("key1", "value1");
		wfsKvpRequest.addKvp(null, "value2");

		String queryString = wfsKvpRequest.asQueryString();

		assertThat(queryString, is("key1=value1"));
	}

}