package de.l3s.interweb.core.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public final class UrlUtils {
    public static String encode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    public static String encodeQueryParams(Map<String, ?> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }

            if (sb.length() > 0) {
                sb.append("&");
            }

            sb.append(entry.getKey()).append("=").append(encode(entry.getValue().toString()));
        }
        return sb.isEmpty() ? null : sb.toString();
    }

    public static UriBuilder buildUri(String url) {
        URI uri = URI.create(url);
        UriBuilder builder = new UriBuilder();
        builder.scheme = uri.getScheme();
        builder.host = uri.getHost();
        builder.port = uri.getPort();
        builder.path = uri.getPath();
        return builder;
    }

    public static class UriBuilder {
        private String scheme;
        private String host;
        private int port;
        private String path;
        private Map<String, ?> queryParams = new LinkedHashMap<>();

        public UriBuilder scheme(String scheme) {
            this.scheme = scheme;
            return this;
        }

        public UriBuilder host(String host) {
            this.host = host;
            return this;
        }

        public UriBuilder port(int port) {
            this.port = port;
            return this;
        }

        public UriBuilder path(String path) {
            this.path = path;
            return this;
        }

        public UriBuilder queryParams(Map<String, ?> queryParams) {
            this.queryParams = queryParams;
            return this;
        }

        public URI build() {
            try {
                return new URI(scheme, null, host, port, path, encodeQueryParams(queryParams), null);
            } catch (URISyntaxException x) {
                throw new IllegalArgumentException(x.getMessage(), x);
            }
        }
    }
}
