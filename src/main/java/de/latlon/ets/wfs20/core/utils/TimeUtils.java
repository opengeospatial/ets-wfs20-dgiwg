package de.latlon.ets.wfs20.core.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

/**
 * Contains utility methods for parsing/reading dates.
 *
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public final class TimeUtils {

	private TimeUtils() {
	}

	/**
	 * Calculates the min and max date values from the given string.
	 * @param dateValues a list of dates, never <code>null</code>
	 * @return the min and max value from the list ([0]= min , [1]=max). If the values
	 * list contains only one entry, min and max are equal. An empty array if the values
	 * list is empty.
	 */
	public static Calendar[] calculateDateRange(String[] dateValues) {
		List<Calendar> parseAndSortValues = parseAndSortValues(dateValues);
		if (parseAndSortValues.isEmpty())
			return new Calendar[] {};
		if (parseAndSortValues.size() == 1) {
			Calendar value = parseAndSortValues.get(0);
			return new Calendar[] { value, value };
		}
		Calendar min = parseAndSortValues.get(0);
		Calendar max = parseAndSortValues.get(parseAndSortValues.size() - 1);
		return new Calendar[] { min, max };
	}

	/**
	 * Calculates a date value in the range.
	 * @param range must contain exactly two values ([0]= min , [1]=max), min and may may
	 * be equal, never <code>null</code>
	 * @return a value in the range, if min == max this value is returned, never
	 * <code>null</code>
	 */
	public static String calculateValueBetween(Calendar[] range) {
		Calendar min = range[0];
		Calendar max = range[1];
		if (min.equals(max))
			return DatatypeConverter.printDateTime(min);
		return calculateValueBetween(min.getTimeInMillis(), max.getTimeInMillis());
	}

	/**
	 * Calculates a date value in the range.
	 * @param minTimeInMillis min datetime, never <code>null</code>
	 * @param maxTimeInMillis max dateTime, never <code>null</code>
	 * @return a value in the range, never <code>null</code>
	 */
	public static String calculateValueBetween(long minTimeInMillis, long maxTimeInMillis) {
		long midTimeInMillis = minTimeInMillis + ((maxTimeInMillis - minTimeInMillis) / 2);

		Calendar midTime = Calendar.getInstance();
		midTime.setTimeInMillis(midTimeInMillis);
		return DatatypeConverter.printDateTime(midTime);
	}

	private static List<Calendar> parseAndSortValues(String[] values) {
		if ((null == values) || values.length == 0) {
			return Collections.emptyList();
		}
		List<Calendar> dateValues = new ArrayList<Calendar>();
		for (String value : values) {
			if (value.indexOf('T') > 0) {
				dateValues.add(DatatypeConverter.parseDateTime(value));
			}
			else {
				dateValues.add(DatatypeConverter.parseDate(value));
			}
		}
		Collections.sort(dateValues);
		return dateValues;
	}

}
