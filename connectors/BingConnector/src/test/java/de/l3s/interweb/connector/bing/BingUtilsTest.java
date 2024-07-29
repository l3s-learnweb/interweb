package de.l3s.interweb.connector.bing;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class BingUtilsTest {

    @Test
    void createFreshness() {
        assertEquals("2020-01-15..2020-03-01", BingUtils.createFreshness(LocalDate.of(2020, 1, 15), LocalDate.of(2020, 3, 1)));
        assertEquals("day", BingUtils.createFreshness(null, LocalDate.now().minusDays(1)));
        assertEquals("week", BingUtils.createFreshness(null, LocalDate.now().minusWeeks(1)));
        assertEquals("month", BingUtils.createFreshness(null, LocalDate.now().minusMonths(1)));
        assertEquals(null, BingUtils.createFreshness(null, LocalDate.now().minusMonths(2)));
        assertEquals("2023-07-12", BingUtils.createFreshness(LocalDate.of(2023, 9, 12).minusMonths(2), null));
    }

    @Test
    void getMarket() {
        assertEquals("uk-UA", BingUtils.getMarket("uk"));
    }
}
