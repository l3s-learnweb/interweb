package de.l3s.interweb.connector.bing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

class BingUtilsTest {

    @Test
    void parseDate() {
        ZonedDateTime date0 = BingUtils.parseDate("2020-01-28T17:18:45");
        ZonedDateTime date1 = BingUtils.parseDate("2020-01-28T17:18:45.0000000");
        ZonedDateTime date2 = BingUtils.parseDate("2019-11-12T12:37:00.0000000Z");
        ZonedDateTime date3 = BingUtils.parseDate("2020-11-28T23:17:00.0000000Z");

        assertEquals(ZonedDateTime.of(2020, 1, 28, 17, 18, 45, 0, ZoneId.systemDefault()), date0);
        assertEquals(ZonedDateTime.of(2020, 1, 28, 17, 18, 45, 0, ZoneId.systemDefault()), date1);
        assertEquals(ZonedDateTime.of(2019, 11, 12, 12, 37, 0, 0, ZoneId.systemDefault()), date2);
        assertEquals(ZonedDateTime.of(2020, 11, 28, 23, 17, 0, 0, ZoneId.systemDefault()), date3);
    }

    @Test
    void createFreshness() {
        assertEquals("2020-01-15..2020-03-01", BingUtils.createFreshness(LocalDate.of(2020, 1, 15), LocalDate.of(2020, 3, 1)));
        assertEquals("day", BingUtils.createFreshness(null, LocalDate.now().minusDays(1)));
        assertEquals("week", BingUtils.createFreshness(null, LocalDate.now().minusWeeks(1)));
        assertEquals("month", BingUtils.createFreshness(null, LocalDate.now().minusMonths(1)));
        assertEquals(null, BingUtils.createFreshness(null, LocalDate.now().minusMonths(2)));
        assertEquals("2023-07-12", BingUtils.createFreshness(LocalDate.now().minusMonths(2), null));
    }

    @Test
    void getMarket() {
        assertEquals("uk-UA", BingUtils.getMarket("uk"));
    }
}