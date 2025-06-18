package de.latlon.ets.wfs20.core.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Calendar;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import jakarta.xml.bind.DatatypeConverter;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class TimeUtilsTest {

	@Test
	public void testCalculateDateRange() throws Exception {
		String[] dateValues = new String[] { "2015-10-02", "2015-09-02T00:10:08", "2014-10-02", "2015-10-10" };
		Calendar[] dateRange = TimeUtils.calculateDateRange(dateValues);
		assertThat(dateRange[0], is(asCalendar("2014-10-02")));
		assertThat(dateRange[1], is(asCalendar("2015-10-10")));
	}

	@Test
	public void testCalculateDateRange_OneValue() throws Exception {
		String[] dateValues = new String[] { "2015-10-10" };
		Calendar[] dateRange = TimeUtils.calculateDateRange(dateValues);
		assertThat(dateRange[0], is(asCalendar("2015-10-10")));
		assertThat(dateRange[1], is(asCalendar("2015-10-10")));
	}

	@Test
	public void testCalculateDateRange_Empty() throws Exception {
		String[] dateValues = new String[] {};
		Calendar[] dateRange = TimeUtils.calculateDateRange(dateValues);
		assertThat(dateRange.length, is(0));
	}

	@Test
	public void testCalculatValueBetween() throws Exception {
		Calendar min = asCalendar("2014-10-02");
		Calendar max = asCalendar("2015-10-02");
		Calendar[] range = new Calendar[] { min, max };
		String betweenValue = TimeUtils.calculateValueBetween(range);
		Calendar between = asCalendar(betweenValue);
		assertThat(between.after(min), is(true));
		assertThat(between.before(max), is(true));
	}

	@Test
	public void testCalculatValueBetween_minMaxEqual() throws Exception {
		String date = "2015-10-02";
		Calendar min = asCalendar(date);
		Calendar max = asCalendar(date);
		Calendar[] range = new Calendar[] { min, max };
		String betweenValue = TimeUtils.calculateValueBetween(range);
		assertThat(betweenValue, CoreMatchers.startsWith(date));
	}

	private Calendar asCalendar(String date) {
		return DatatypeConverter.parseDate(date);
	}

}