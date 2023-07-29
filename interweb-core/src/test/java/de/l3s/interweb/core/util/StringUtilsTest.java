package de.l3s.interweb.core.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.Test;

class StringUtilsTest {

    @Test
    void toIdSet() {
        assertEquals(Set.of("test", "nospace", "example"), StringUtils.toIdSet("test, Example, example,nospace"));
    }

    @Test
    void shortnString() {
        assertEquals("Lorem Ipsum is simply dummy...", StringUtils.shorten("Lorem Ipsum is simply dummy text of the printing and typesetting industry.", 32));
    }

    @Test
    void parseSourceUrlTest() {
        String embeddedCode = "<iframe src=\"https://www.slideshare.net/slideshow/embed_code/key/zqB09yYwWmHWCw\" width=\"427\" height=\"356\" frameborder=\"0\" marginwidth=\"0\" marginheight=\"0\" scrolling=\"no\" style=\"border:1px solid #CCC; border-width:1px; margin-bottom:5px; max-width: 100%;\" allowfullscreen> </iframe> <div style=\"margin-bottom:5px\"> <strong> <a href=\"https://www.slideshare.net/AlexMinin1/hello-world-238543571\" title=\"Hello World!\" target=\"_blank\">Hello World!</a> </strong> from <strong><a href=\"https://www.slideshare.net/AlexMinin1\" target=\"_blank\">AlexMinin1</a></strong> </div>";
        assertEquals("https://www.slideshare.net/slideshow/embed_code/key/zqB09yYwWmHWCw", StringUtils.parseSourceUrl(embeddedCode));
    }

    @Test
    void parseSourceUrlEncodedTest() {
        String embeddedCode2 = "&lt;iframe height=400 width=480 src=&#39;https://player.youku.com/embed/XNDczMDkxNjc2OA==&#39; frameborder=0 &#39;allowfullscreen&#39;&gt;&lt;/iframe&gt;";
        assertEquals("https://player.youku.com/embed/XNDczMDkxNjc2OA==", StringUtils.parseSourceUrl(embeddedCode2));
    }
}
