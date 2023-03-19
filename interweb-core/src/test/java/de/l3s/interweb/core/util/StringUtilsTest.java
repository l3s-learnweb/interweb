package de.l3s.interweb.core.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class StringUtilsTest {

    @Test
    void convertToUniqueList() {
        assertEquals(Arrays.asList("test", "nospace", "example"), StringUtils.convertToUniqueList("test, example, example,nospace"));
    }

    @Test
    void shortnString() {
        assertEquals("Lorem Ipsum is simply dummy...", StringUtils.shorten("Lorem Ipsum is simply dummy text of the printing and typesetting industry.", 32));
    }

    @Test
    void getEmbeddedUrl() {
        String embeddedCode = "<iframe src=\"https://www.slideshare.net/slideshow/embed_code/key/zqB09yYwWmHWCw\" width=\"427\" height=\"356\" frameborder=\"0\" marginwidth=\"0\" marginheight=\"0\" scrolling=\"no\" style=\"border:1px solid #CCC; border-width:1px; margin-bottom:5px; max-width: 100%;\" allowfullscreen> </iframe> <div style=\"margin-bottom:5px\"> <strong> <a href=\"https://www.slideshare.net/AlexMinin1/hello-world-238543571\" title=\"Hello World!\" target=\"_blank\">Hello World!</a> </strong> from <strong><a href=\"https://www.slideshare.net/AlexMinin1\" target=\"_blank\">AlexMinin1</a></strong> </div>";
        assertEquals("https://www.slideshare.net/slideshow/embed_code/key/zqB09yYwWmHWCw", StringUtils.parseSourceUrl(embeddedCode));

        String embeddedCode2 = "&lt;iframe height=400 width=480 src=&#39;https://player.youku.com/embed/XNDczMDkxNjc2OA==&#39; frameborder=0 &#39;allowfullscreen&#39;&gt;&lt;/iframe&gt;";
        embeddedCode2 = StringUtils.replaceEachRepeatedly(embeddedCode2, new String[] {"'", "&#34;", "&#39;", "&quot;", "&apos;"}, new String[]{"\"", "\"", "\"", "\"", "\""});
        assertEquals("https://player.youku.com/embed/XNDczMDkxNjc2OA==", StringUtils.parseSourceUrl(embeddedCode2));
    }
}
