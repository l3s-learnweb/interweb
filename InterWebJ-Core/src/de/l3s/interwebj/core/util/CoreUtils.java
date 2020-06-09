package de.l3s.interwebj.core.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CoreUtils {
    private static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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

    public static String formatDate(DateFormat df, Date date) {
        return (date == null) ? null : df.format(date);
    }

    public static String formatDate(Date date) {
        return formatDate(DEFAULT_DATE_FORMAT, date);
    }

    public static String formatDate(DateFormat df, long millis) {
        return df.format(new Date(millis));
    }

    public static String formatDate(long millis) {
        return formatDate(DEFAULT_DATE_FORMAT, millis);
    }

    public static long parseDate(DateFormat df, String dateString) throws ParseException {
        return df.parse(dateString).getTime();
    }

    public static long parseDate(String dateString) throws ParseException {
        return parseDate(DEFAULT_DATE_FORMAT, dateString);
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

    /**
     * Removes width, height, autoplay, styles from embedded code.
     */
    public static String cleanupEmbedHtml(String embeddedCode) {
        if (null == embeddedCode) {
            return null;
        }

        // remove autoplay attribute
        embeddedCode = embeddedCode.replace("?autoplay=1", "");
        embeddedCode = embeddedCode.replaceAll("\\s(style|width|height)=\"[^\"]*\"", "");
        return embeddedCode;
    }
}
