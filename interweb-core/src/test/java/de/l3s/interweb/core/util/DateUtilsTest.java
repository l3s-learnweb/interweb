package de.l3s.interweb.core.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;

import org.junit.jupiter.api.Test;

class DateUtilsTest {

    @Test
    void parse() {
        assertEquals(Instant.parse("2013-08-01T12:41:48Z"), DateUtils.parse("2013-08-01 12:41:48"));
        assertEquals(Instant.parse("2015-09-23T16:15:57Z"), DateUtils.parse("2015-09-23 16:15:57 UTC"));
        assertEquals(Instant.parse("2020-01-28T17:18:45Z"), DateUtils.parse("2020-01-28T17:18:45"));
        assertEquals(Instant.parse("2020-01-28T17:18:45Z"), DateUtils.parse("2020-01-28T17:18:45.0000000"));
        assertEquals(Instant.parse("2020-11-28T23:17:00Z"), DateUtils.parse("2020-11-28T23:17:00.0000000Z"));
        assertEquals(Instant.parse("2020-04-21T09:44:08Z"), DateUtils.parse("2020-04-21T09:44:08+00:00"));
        assertEquals(Instant.parse("2020-10-04T20:23:33Z"), DateUtils.parse("2020-10-04T20:23:33Z"));
    }

    @Test
    void toEpochSecond() {
        assertEquals(1580231925, DateUtils.toEpochSecond(Instant.parse("2020-01-28T17:18:45Z")));
    }

    @Test
    void toRfc3339() {
        assertEquals("2020-01-28T17:18:45Z", DateUtils.toRfc3339(Instant.parse("2020-01-28T17:18:45Z")));
    }
}