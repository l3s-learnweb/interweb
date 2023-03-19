package de.l3s.interweb.core.util;

import static org.junit.jupiter.api.Assertions.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

class DateUtilsTest {

    @Test
    void format() {
        assertEquals("2020-10-02 16:14:14", DateUtils.format(1601648054000L));
        assertEquals("2020-10-02 16:14:14", DateUtils.format(ZonedDateTime.of(2020, 10, 2, 16, 14, 14, 0, ZoneId.systemDefault())));
        assertNull(DateUtils.format(null));
    }

    @Test
    void parse() {
        assertEquals(1601648054000L, DateUtils.parse("2020-10-02 16:14:14").toInstant().toEpochMilli());
    }
}