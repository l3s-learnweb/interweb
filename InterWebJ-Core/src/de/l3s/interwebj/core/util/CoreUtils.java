package de.l3s.interwebj.core.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoreUtils {
    private static final DateTimeFormatter DEFAULT_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
    private static final Pattern IFRAME_SRC_PATTERN = Pattern.compile("src=\"([^\"]+)\"");

    public static String formatDate(long millis) {
        return formatDate(Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()));
    }

    public static String formatDate(TemporalAccessor dateTime) {
        return formatDate(DEFAULT_DATE_TIME_FORMAT, dateTime);
    }

    public static String formatDate(DateTimeFormatter formatter, TemporalAccessor dateTime) {
        return (dateTime == null) ? null : formatter.format(dateTime);
    }

    public static ZonedDateTime parseDate(String dateString) throws DateTimeParseException {
        return parseDate(DEFAULT_DATE_TIME_FORMAT, dateString);
    }

    public static ZonedDateTime parseDate(DateTimeFormatter formatter, String dateString) throws DateTimeParseException {
        return ZonedDateTime.parse(dateString, formatter);
    }

    /**
     * Splits CSV string to list removing duplicates.
     */
    public static List<String> convertToUniqueList(String s) {
        Set<String> list = new HashSet<>();
        String[] tokens = s.split("[,\\s]");
        for (String token : tokens) {
            if (token.length() > 0) {
                list.add(token);
            }
        }
        return new ArrayList<>(list);
    }

    /**
     * If the string is longer than maxLength it is split at the nearest blank space.
     */
    public static String shortnString(final String str, final int maxLength) {
        if (null == str) {
            return null;
        }

        if (str.length() > maxLength) {
            int endIdx = maxLength - 3;
            while (endIdx > 0 && str.charAt(endIdx) != ' ' && str.charAt(endIdx) != '\n') {
                endIdx--;
            }

            return str.substring(0, endIdx) + "...";
        }
        return str;
    }

    public static String getEmbeddedUrl(String embeddedCode) {
        Matcher matcher = IFRAME_SRC_PATTERN.matcher(embeddedCode);
        return matcher.find() ? matcher.group(1) : null;
    }
}
