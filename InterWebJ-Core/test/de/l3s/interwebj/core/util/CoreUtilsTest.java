package de.l3s.interwebj.core.util;

import static org.junit.jupiter.api.Assertions.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

class CoreUtilsTest {

    @Test
    void formatDate() {
        assertEquals("2020-10-02 16:14:14", CoreUtils.formatDate(1601648054000L));
    }

    @Test
    void formatDate1() {
        assertEquals("2020-10-02 16:14:14", CoreUtils.formatDate(ZonedDateTime.of(2020, 10, 2, 16, 14, 14, 0, ZoneId.systemDefault())));
    }

    @Test
    void formatDate2() {
        assertEquals(null, CoreUtils.formatDate(null));
    }

    @Test
    void parseDate() {
        assertEquals(1601648054000L, CoreUtils.parseDate("2020-10-02 16:14:14").toInstant().toEpochMilli());
    }

    @Test
    void convertToUniqueList() {
        assertEquals(Arrays.asList("test", "nospace", "example"), CoreUtils.convertToUniqueList("test, example, example,nospace"));
    }

    @Test
    void shortnString() {
        assertEquals("Lorem Ipsum is simply dummy...", CoreUtils.shortnString("Lorem Ipsum is simply dummy text of the printing and typesetting industry.", 32));
    }

    @Test
    void cleanupEmbedHtml() {
        assertEquals("<iframe src=\"https://www.youtube.com/embed/videoseries?list=PLx0sYbCqOb8TBPRdmBHs5Iftvv9TPboYG\" frameborder=\"0\" allow=\"autoplay; encrypted-media\" allowfullscreen></iframe>", CoreUtils.cleanupEmbedHtml("<iframe width=\"560\" height=\"315\" src=\"https://www.youtube.com/embed/videoseries?list=PLx0sYbCqOb8TBPRdmBHs5Iftvv9TPboYG\" frameborder=\"0\" allow=\"autoplay; encrypted-media\" allowfullscreen></iframe>"));
    }
}