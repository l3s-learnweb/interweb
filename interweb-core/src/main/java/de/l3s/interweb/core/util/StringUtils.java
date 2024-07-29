package de.l3s.interweb.core.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtils {
    private static final Pattern SRC_PATTERN = Pattern.compile("src=\"([^\"]+)\"");

    private StringUtils() {
    }

    public static Set<String> toIdSet(String s) {
        Set<String> set = new HashSet<>();
        String[] tokens = s.split("[,\\s]");
        for (String token : tokens) {
            if (!token.isBlank()) {
                set.add(token.trim().toLowerCase());
            }
        }
        return set;
    }

    public static Set<String> toIdSet(String[] tokens) {
        Set<String> set = new HashSet<>();
        for (String token : tokens) {
            if (!token.isBlank()) {
                set.add(token.trim().toLowerCase());
            }
        }
        return set;
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

    public static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static boolean isNotEmpty(String s) {
        return s != null && !s.isEmpty();
    }

    public static String percentEncode(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }

        return URLEncoder.encode(str, StandardCharsets.UTF_8)
            .replace("+", "%20")
            .replace("*", "%2A")
            .replace("%7E", "~");
    }

    public static String parseSourceUrl(String embeddedCode) {
        // embeddedCode = embeddedCode.replaceAll("'", "\"");
        // embeddedCode = embeddedCode.replaceAll("&#34;", "\"");
        embeddedCode = embeddedCode.replace("&#39;", "\"");
        embeddedCode = embeddedCode.replace("&quot;", "\"");
        // embeddedCode = embeddedCode.replaceAll("&apos;", "\"");
        Matcher matcher = SRC_PATTERN.matcher(embeddedCode);
        return matcher.find() ? matcher.group(1) : null;
    }

    public static String randomAlphanumeric(int length) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
            .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
            .limit(length)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
    }
}
