package de.l3s.interweb.core.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public final class DateUtils {
    private static final DateTimeFormatter DEFAULT_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    public static String format(long millis) {
        return format(Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()));
    }

    public static String format(TemporalAccessor dateTime) {
        return format(DEFAULT_DATE_TIME_FORMAT, dateTime);
    }

    public static String format(DateTimeFormatter formatter, TemporalAccessor dateTime) {
        return (dateTime == null) ? null : formatter.format(dateTime);
    }
}
