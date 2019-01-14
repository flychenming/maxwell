package com.zendesk.maxwell.schema.columndef;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;

public class TimestampFormatter {
	private static TimeZone CST_ZONE = TimeZone.getTimeZone("Asia/Shanghai");
	private static ThreadLocal<Calendar> calendarThreadLocal = ThreadLocal.withInitial(Calendar::getInstance);
	private static ThreadLocal<Calendar> calendarCSTThreadLocal = ThreadLocal.withInitial(() -> Calendar.getInstance(CST_ZONE));
	private static ThreadLocal<StringBuilder> stringBuilderThreadLocal = ThreadLocal.withInitial(() -> new StringBuilder(32));


	private static void zeroPad(StringBuilder sb, int val, int width) {
		int digits;
		if (val < 10) digits = 1;
		else if (val < 100) digits = 2;
		else if (val < 1000) digits = 3;
		else if (val < 10000) digits = 4;
		else digits = 99999999;
		int padding = width - digits;
		for (int i = 0; i < padding; i++) {
			sb.append(0);
		}
		sb.append(val);
	}

	private static String formatDate(Calendar cal) {
		StringBuilder sb = stringBuilderThreadLocal.get();
		sb.setLength(0);

		zeroPad(sb, cal.get(Calendar.YEAR), 4);
		sb.append("-");
		zeroPad(sb, cal.get(Calendar.MONTH) + 1, 2);
		sb.append("-");
		zeroPad(sb, cal.get(Calendar.DAY_OF_MONTH), 2);
		return sb.toString();

	}

	private static String formatDateTime(Calendar cal) {
		StringBuilder sb = stringBuilderThreadLocal.get();
		sb.setLength(0);

		zeroPad(sb, cal.get(Calendar.YEAR), 4);
		sb.append("-");
		zeroPad(sb, cal.get(Calendar.MONTH) + 1, 2);
		sb.append("-");
		zeroPad(sb, cal.get(Calendar.DAY_OF_MONTH), 2);
		sb.append(" ");
		zeroPad(sb, cal.get(Calendar.HOUR_OF_DAY), 2);
		sb.append(":");
		zeroPad(sb, cal.get(Calendar.MINUTE), 2);
		sb.append(":");
		zeroPad(sb, cal.get(Calendar.SECOND), 2);
		return sb.toString();
	}

	public static String formatDate(Object value) {
		Calendar cal;
		if (value instanceof Long) {
			cal = calendarCSTThreadLocal.get();
			cal.setTimeInMillis(floorDiv((Long) value, 1000L));
		} else {
			cal = calendarThreadLocal.get();
			cal.setTimeInMillis(DateFormatter.extractTimestamp(value).getTime());
		}
		return formatDate(cal);
	}


	public static String formatDateTime(Object value, Timestamp ts) {
		Calendar cal;
		if (value instanceof Long) {
			cal = calendarCSTThreadLocal.get();
		} else {
			cal = calendarThreadLocal.get();
		}
		cal.setTimeInMillis(ts.getTime());
		return formatDateTime(cal);
	}

	private static long floorDiv(long a, long b) {
		return ((a < 0) ? (a - (b - 1)) : a) / b;
	}

	private static long floorMod(long x) {
		return x - floorDiv(x, 1000000L) * 1000000L;
	}
}
