package de.l3s.interweb.connector.slideshare;

import static org.junit.jupiter.api.Assertions.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

import de.l3s.interweb.core.search.Thumbnail;

class SlideShareUtilsTest {

    @Test
    void parseThumbnail() {
        Thumbnail thumbnail = SlideShareUtils.parseThumbnail("https://cdn.slidesharecdn.com/ss_thumbnails/hello-world94-thumbnail.jpg?width=640&amp;amp;height=520&amp;amp;fit=bounds");
        assertEquals("https://cdn.slidesharecdn.com/ss_thumbnails/hello-world94-thumbnail.jpg?width=640&amp;amp;height=520&amp;amp;fit=bounds", thumbnail.getUrl());
        assertEquals(640, thumbnail.getWidth());
        assertEquals(520, thumbnail.getHeight());
    }

    @Test
    void parseDate() {
        ZonedDateTime localDateTime = SlideShareUtils.parseDate("2015-09-23 16:15:57 UTC");
        assertEquals(ZonedDateTime.of(2015, 9, 23, 16, 15, 57, 0, ZoneId.of("Etc/UTC")), localDateTime);
    }
}