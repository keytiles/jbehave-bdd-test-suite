package com.keytiles.bdd.plugins;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sixt.tool.bdd_testsuite.jbehave.plugin.JBehaveTableValuePlugin;

/**
 * This plugin provides you UTC millis based timestamp if you write "!millisTS:&lt;instructions&gt;"
 * as value into a table. It will return a Long object as table value
 * <p>
 * where &lt;instructionsgt; could look like this
 * <ul>
 * <li>now [+|-Xd|h|m|s|mi] ... - provides current time and optionally +/- x
 * days|hours|minutes|seconds|millis<br>
 * e.g.: "now +3s" - gives the current time + 3 seconds</li>
 * </ul>
 *
 * @author AttilaW
 *
 */
public class MillisTimestampJBehaveTableValuePlugin implements JBehaveTableValuePlugin {

	private final static long SECONDS_MULTIPLIER = 1000;
	private final static long MINUTE_MULTIPLIER = 1000 * 60;
	private final static long HOUR_MULTIPLIER = 1000 * 60 * 60;
	private final static long DAY_MULTIPLIER = 1000 * 60 * 60 * 24;

	private final static Pattern offsetMatcher = Pattern
			.compile("(?<op>[+-]{1})\\s*(?<amount>[0-9]+)\\s*(?<unit>[dhmsi]{1,2})");

	public MillisTimestampJBehaveTableValuePlugin() {
	}

	@Override
	public Object provideCellValue(String cellStringValue) {

		Long timestamp = null;

		String[] parts = cellStringValue.split("[+|-]", 2);

		if (parts[0].startsWith("now")) {
			timestamp = System.currentTimeMillis();
		} else {
			// try to parse a Long
			try {
				timestamp = Long.parseLong(parts[0]);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException(
						"failed to parse explicit value " + parts[0] + " into a Long in " + this.getClass());
			}
		}

		cellStringValue = cellStringValue.substring(parts[0].length());

		// and now the offsets
		if (timestamp != null) {
			long millisToAdd = parts.length > 1 ? parseMillisToAdd(cellStringValue.trim()) : 0;
			if (millisToAdd != 0) {
				timestamp += millisToAdd;
			}
		}

		return timestamp;
	}

	/**
	 * Calculates how much seconds a complex expression string represents like this one: "+1d -1h +5m +
	 * 10s"
	 *
	 * @return number of seconds it sums up to
	 */
	public static long parseMillisToAdd(String expression) {

		long millisToAdd = 0;
		Matcher m = offsetMatcher.matcher(expression);
		while (m.find()) {
			String op = m.group("op");
			String amount = m.group("amount");
			String unit = m.group("unit");

			long millis = Integer.parseInt(amount);
			switch (unit) {
			case "s":
				millis *= SECONDS_MULTIPLIER;
				break;
			case "m":
				millis *= MINUTE_MULTIPLIER;
				break;
			case "h":
				millis *= HOUR_MULTIPLIER;
				break;
			case "d":
				millis *= DAY_MULTIPLIER;
				break;
			default:
				break;
			}
			if ("+".equals(op)) {
				millisToAdd += millis;
			} else {
				millisToAdd -= millis;
			}
		}

		return millisToAdd;
	}
}
