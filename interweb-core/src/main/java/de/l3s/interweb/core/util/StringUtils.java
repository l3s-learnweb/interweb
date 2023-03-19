package de.l3s.interweb.core.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils extends org.apache.commons.lang3.StringUtils {
    private static final Pattern SRC_PATTERN = Pattern.compile("src=\"([^\"]+)\"");

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
    public static String shorten(final String str, final int maxLength) {
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

    public static String parseSourceUrl(String embeddedCode) {
        Matcher matcher = SRC_PATTERN.matcher(embeddedCode);
        return matcher.find() ? matcher.group(1) : null;
    }
}
