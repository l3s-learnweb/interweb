/*
 * The MIT License
 *
 * Copyright (c) 2019 Trievo, LLC. https://trievosoftware.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 *
 */

package com.trievosoftware.giphy4j.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * This class provides methods to build an URL String.
 *
 * @author Mark Tripoli
 */
public class UrlUtil {

    private static final String PAIR_SEPARATOR = "=";

    private static final String PARAM_SEPARATOR = "&";

    private static final char QUERY_STRING_SEPARATOR = '?';

    /**
     * Builds the URL String.
     *
     * @param baseUrl the base URL.
     * @param params the URL parameter.
     * @return the URL as String.
     */
    public static String buildUrlQuery(String baseUrl, Map<String, String> params) {
        if (baseUrl.isEmpty() || params.isEmpty()) {
            return baseUrl;
        } else {
            StringBuilder query = new StringBuilder(baseUrl);
            query.append(QUERY_STRING_SEPARATOR);

            for (Map.Entry<String, String> entry : params.entrySet()) {
                query.append(entry.getKey());
                query.append(PAIR_SEPARATOR);
                query.append(encodeString(entry.getValue()));
                query.append(PARAM_SEPARATOR);
            }

            return query.substring(0, query.length() - 1);
        }
    }

    private static String encodeString(String text) {
        return URLEncoder.encode(text, StandardCharsets.UTF_8);
    }
}
