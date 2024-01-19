package de.l3s.interweb.core.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;

import de.l3s.interweb.core.ConnectorException;

public final class DateUtils {
    private static final DateTimeFormatter PARSE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd[ ]['T']HH:mm:ss[.SSSSSSS][XXX][ z]").withZone(ZoneOffset.UTC);

    private DateUtils() {
    }

    public static Instant parse(String str) {
        if (str == null) return null;

        try {
            return Instant.from(PARSE_FORMAT.parse(str));
        } catch (DateTimeParseException e) {
            throw new ConnectorException("Unable to parse date: " + str, e);
        }
    }

    public static Integer toEpochSecond(TemporalAccessor temporal) {
        return temporal == null ? null : Math.toIntExact(Instant.from(temporal).getEpochSecond());
    }

    public static String toRfc3339(TemporalAccessor temporal) {
        return temporal == null ? null : DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.systemDefault()).format(temporal);
    }
}
