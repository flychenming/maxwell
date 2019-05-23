package com.zendesk.maxwell.schema.columndef;

import com.zendesk.maxwell.producer.MaxwellOutputConfig;

import java.sql.Timestamp;

public class TimestampColumnDef extends ColumnDefWithLength {

	public TimestampColumnDef(String name, String type, short pos, Long columnLength) {
		super(name, type, pos, columnLength);
	}

	@Override
	protected String formatValue(Object value, MaxwellOutputConfig config) {
		// special case for those broken mysql dates.
		if (value instanceof Long) {
			Long v = (Long) value;
			if (v == Long.MIN_VALUE || v == 0L)
				if (config.zeroDatesAsNull)
					return null;
				else
					return appendFractionalSeconds("0000-00-00 00:00:00", 0, columnLength);
		}

		Timestamp ts = DateFormatter.extractTimestamp(value);
		String dateString = TimestampFormatter.formatDateTime(value, ts);
		return appendFractionalSeconds(dateString, ts.getNanos(), columnLength);
	}
}
