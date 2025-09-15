package de.l3s.interweb.connector.google;

import static org.junit.jupiter.api.Assertions.*;

import java.time.*;

import org.junit.jupiter.api.Test;

class SerperUtilsTest {

    @Test
    void parsesAbsoluteEnglishShortMonth() {
        Instant got = SerperUtils.parseDate("19 Oct 2022", "en");
        Instant expected = LocalDate.of(2022, 10, 19).atStartOfDay(ZoneOffset.UTC).toInstant();
        assertEquals(expected, got);
    }

    @Test
    void parsesAbsoluteEnglishSeptNormalization() {
        Instant got = SerperUtils.parseDate("18 Sept 2024", "en");
        Instant expected = LocalDate.of(2024, 9, 18).atStartOfDay(ZoneOffset.UTC).toInstant();
        assertEquals(expected, got);
    }

    @Test
    void parsesAbsoluteGerman() {
        Instant got = SerperUtils.parseDate("05.06.2020", "de");
        Instant expected = LocalDate.of(2020, 6, 5).atStartOfDay(ZoneOffset.UTC).toInstant();
        assertEquals(expected, got);
    }

    @Test
    void parsesRelativeEnglishVariousUnits() {
        ZonedDateTime nowUtc = ZonedDateTime.now(ZoneOffset.UTC);
        assertInstantNear(SerperUtils.parseDate("5 years ago", "en"), nowUtc.minusYears(5).toInstant());
        assertInstantNear(SerperUtils.parseDate("1 month ago", "en"), nowUtc.minusMonths(1).toInstant());
        assertInstantNear(SerperUtils.parseDate("3 weeks ago", "en"), nowUtc.minusWeeks(3).toInstant());
        assertInstantNear(SerperUtils.parseDate("2 days ago", "en"), nowUtc.minusDays(2).toInstant());
        assertInstantNear(SerperUtils.parseDate("1 hour ago", "en"), nowUtc.minusHours(1).toInstant());
        assertInstantNear(SerperUtils.parseDate("15 minutes ago", "en"), nowUtc.minusMinutes(15).toInstant());
    }

    @Test
    void parsesRelativeGermanVariousUnits() {
        ZonedDateTime nowUtc = ZonedDateTime.now(ZoneOffset.UTC);
        assertInstantNear(SerperUtils.parseDate("vor 1 Jahr", "de"), nowUtc.minusYears(1).toInstant());
        assertInstantNear(SerperUtils.parseDate("vor 5 Jahren", "de"), nowUtc.minusYears(5).toInstant());
        assertInstantNear(SerperUtils.parseDate("vor 1 Monat", "de"), nowUtc.minusMonths(1).toInstant());
        assertInstantNear(SerperUtils.parseDate("vor 2 Wochen", "de"), nowUtc.minusWeeks(2).toInstant());
        assertInstantNear(SerperUtils.parseDate("vor 3 Tagen", "de"), nowUtc.minusDays(3).toInstant());
        assertInstantNear(SerperUtils.parseDate("vor 4 Stunden", "de"), nowUtc.minusHours(4).toInstant());
        assertInstantNear(SerperUtils.parseDate("vor 30 Minuten", "de"), nowUtc.minusMinutes(30).toInstant());
    }

    @Test
    void handlesNullAndEmpty() {
        assertNull(SerperUtils.parseDate(null, "en"));
        assertNull(SerperUtils.parseDate("   ", "de"));
    }

    @Test
    void returnsNullOnUnknown() {
        assertNull(SerperUtils.parseDate("not a date", "en"));
        assertNull(SerperUtils.parseDate("unbekannt", "de"));
    }

    private static void assertInstantNear(Instant actual, Instant expected) {
        assertNotNull(actual, "Instant should not be null");

        long diff = Math.abs(Duration.between(actual, expected).getSeconds());
        assertTrue(diff <= (long) 180, () -> "Expected within "+ (long) 180 +"s, diff=\""+diff+"\". actual="+actual+", expected="+expected);
    }

    @Test
    void parseDuration() {
        assertNull(SerperUtils.parseDuration(null));
        assertNull(SerperUtils.parseDuration("hello"));
        assertEquals(460L, SerperUtils.parseDuration("7:40"));
        assertEquals(4060L, SerperUtils.parseDuration("1:7:40"));
    }
}

