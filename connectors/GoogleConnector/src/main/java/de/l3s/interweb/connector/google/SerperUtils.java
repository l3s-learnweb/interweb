package de.l3s.interweb.connector.google;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.l3s.interweb.connector.google.serper.SearchRequest;

public final class SerperUtils {
    private static final DateTimeFormatter[] ABSOLUTE_EN_FORMATTERS = {
        DateTimeFormatter.ofPattern("d MMM uuuu", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("d MMMM uuuu", Locale.ENGLISH),
    };

    private static final DateTimeFormatter ABSOLUTE_DE_FORMATTER = DateTimeFormatter.ofPattern("d.M.uuuu", Locale.GERMAN);

    private static final Pattern RELATIVE_EN_PATTERN = Pattern.compile("(?i)^(\\d+)\\s+(year|month|week|day|hour|minute)s?\\s+ago$");
    private static final Pattern RELATIVE_DE_PATTERN = Pattern.compile("(?i)^vor\\s+(\\d+)\\s+(jahr|jahre|jahren|monat|monate|monaten|woche|wochen|tag|tage|tagen|stunde|stunden|minute|minuten)$");

    public static Instant parseDate(String dateString, String lang) {
        if (dateString == null) return null;
        String s = dateString.trim();
        if (s.isEmpty()) return null;

        String normalizedEn = normalizeEnglish(s);

        Instant i;
        // Prefer parsers based on the provided language but be permissive across locales
        if ("de".equalsIgnoreCase(lang)) {
            if ((i = parseRelativeDE(s)) != null) return i;
            if ((i = parseAbsoluteDE(s)) != null) return i;
        }

        if ((i = parseRelativeEN(normalizedEn)) != null) return i;
        if ((i = parseAbsoluteEN(normalizedEn)) != null) return i;
        return null;
    }

    private static String normalizeEnglish(String s) {
        // Normalize non-standard abbreviation for September and remove commas/extra spaces
        s = s.replaceAll("(?i)\\bsept\\b", "Sep");
        s = s.replace(",", " ").replaceAll("\\s+", " ").trim();
        return s;
    }

    private static Instant parseAbsoluteEN(String s) {
        for (DateTimeFormatter f : ABSOLUTE_EN_FORMATTERS) {
            try {
                return LocalDate.parse(s, f).atStartOfDay(ZoneOffset.UTC).toInstant();
            } catch (DateTimeParseException ignored) {
                // try next formatter
            }
        }
        return null;
    }

    private static Instant parseAbsoluteDE(String s) {
        try {
            return LocalDate.parse(s, ABSOLUTE_DE_FORMATTER).atStartOfDay(ZoneOffset.UTC).toInstant();
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }

    private static Instant parseRelativeEN(String s) {
        Matcher m = RELATIVE_EN_PATTERN.matcher(s);
        if (!m.find()) return null;

        int n = Integer.parseInt(m.group(1));
        String unit = m.group(2).toLowerCase(Locale.ROOT);
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);

        return switch (unit) {
            case "year" -> now.minusYears(n).toInstant();
            case "month" -> now.minusMonths(n).toInstant();
            case "week" -> now.minusWeeks(n).toInstant();
            case "day" -> now.minusDays(n).toInstant();
            case "hour" -> now.minusHours(n).toInstant();
            case "minute" -> now.minusMinutes(n).toInstant();
            default -> null;
        };
    }

    private static Instant parseRelativeDE(String s) {
        Matcher m = RELATIVE_DE_PATTERN.matcher(s);
        if (!m.find()) return null;

        int n = Integer.parseInt(m.group(1));
        String unit = m.group(2).toLowerCase(Locale.ROOT);
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);

        if (unit.startsWith("jahr")) {
            return now.minusYears(n).toInstant();
        } else if (unit.startsWith("monat")) {
            return now.minusMonths(n).toInstant();
        } else if (unit.startsWith("woche")) {
            return now.minusWeeks(n).toInstant();
        } else if (unit.startsWith("tag")) {
            return now.minusDays(n).toInstant();
        } else if (unit.startsWith("stunde")) {
            return now.minusHours(n).toInstant();
        } else if (unit.startsWith("minute")) {
            return now.minusMinutes(n).toInstant();
        }
        return null;
    }

    public static SearchRequest.DateRange mapDateRange(LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            return SearchRequest.DateRange.AnyTime;
        }

        LocalDate lookbackDate = from != null ? from : to;
        long days = ChronoUnit.DAYS.between(lookbackDate, LocalDate.now());

        long lookbackDays = Math.max(0L, days); // Guard against future dates
        if (lookbackDays <= 1) {
            return SearchRequest.DateRange.Past24Hours;
        } else if (lookbackDays <= 7) {
            return SearchRequest.DateRange.PastWeek;
        } else if (lookbackDays <= 31) {
            return SearchRequest.DateRange.PastMonth;
        } else if (lookbackDays <= 366) {
            return SearchRequest.DateRange.PastYear;
        }

        return SearchRequest.DateRange.AnyTime;
    }
}
