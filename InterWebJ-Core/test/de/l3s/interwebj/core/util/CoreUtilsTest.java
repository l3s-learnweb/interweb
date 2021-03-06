package de.l3s.interwebj.core.util;

import static org.junit.jupiter.api.Assertions.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
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
    void getEmbeddedUrl() {
        String embeddedCode = "<iframe src=\"https://www.slideshare.net/slideshow/embed_code/key/zqB09yYwWmHWCw\" width=\"427\" height=\"356\" frameborder=\"0\" marginwidth=\"0\" marginheight=\"0\" scrolling=\"no\" style=\"border:1px solid #CCC; border-width:1px; margin-bottom:5px; max-width: 100%;\" allowfullscreen> </iframe> <div style=\"margin-bottom:5px\"> <strong> <a href=\"https://www.slideshare.net/AlexMinin1/hello-world-238543571\" title=\"Hello World!\" target=\"_blank\">Hello World!</a> </strong> from <strong><a href=\"https://www.slideshare.net/AlexMinin1\" target=\"_blank\">AlexMinin1</a></strong> </div>";
        assertEquals("https://www.slideshare.net/slideshow/embed_code/key/zqB09yYwWmHWCw", CoreUtils.getEmbeddedUrl(embeddedCode));

        String embeddedCode2 = "&lt;iframe height=400 width=480 src=&#39;https://player.youku.com/embed/XNDczMDkxNjc2OA==&#39; frameborder=0 &#39;allowfullscreen&#39;&gt;&lt;/iframe&gt;";
        embeddedCode2 = StringUtils.replaceEachRepeatedly(embeddedCode2, new String[] {"'", "&#34;", "&#39;", "&quot;", "&apos;"}, new String[]{"\"", "\"", "\"", "\"", "\""});
        assertEquals("https://player.youku.com/embed/XNDczMDkxNjc2OA==", CoreUtils.getEmbeddedUrl(embeddedCode2));
    }
}
