package de.l3s.interweb.core.util;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.util.LinkedHashMap;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

class UrlUtilsTest {

    @Test
    void encode() {
        assertEquals("hello+world", UrlUtils.encode("hello world"));
    }

    @Test
    void encodeQueryParams() {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        params.put("name", "Tesla, Nikola");
        params.put("year", 1856);
        assertEquals("name=Tesla%2C+Nikola&year=1856", UrlUtils.encodeQueryParams(params));
    }

    @Test
    void encodeQueryParamsWithNull() {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        params.put("name", "Tesla, Nikola");
        params.put("year", null);
        assertEquals("name=Tesla%2C+Nikola", UrlUtils.encodeQueryParams(params));
    }

    @Test
    void uriBuilder() {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        params.put("name", "Tesla, Nikola");
        params.put("year", 1856);
        URI uri = UrlUtils.buildUri("https://example.com/test").queryParams(params).build();
        assertEquals("https://example.com/test?name=Tesla%252C+Nikola&year=1856", uri.toString());
    }
}