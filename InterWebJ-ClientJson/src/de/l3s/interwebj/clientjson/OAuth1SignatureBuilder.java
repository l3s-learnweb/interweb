package de.l3s.interwebj.clientjson;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class OAuth1SignatureBuilder {
    private static final int NONCE_LENGTH = 16;
    private static final String EMPTY_STRING = "";
    private static final String ALPHA_NUMERIC_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private String method = "GET";
    private URI uri;

    private String consumerKey;
    private String consumerSecret;
    private String tokenSecret;

    private final Map<String, String> parameters = new LinkedHashMap<>();

    public String build() {

        // For testing purposes, only add the timestamp if it has not yet been added
        if (!parameters.containsKey("oauth_timestamp")) {
            parameters.put("oauth_timestamp", getTimestamp());
        }

        // For testing purposes, only add the nonce if it has not yet been added
        if (!parameters.containsKey("oauth_nonce")) {
            parameters.put("oauth_nonce", getNonce());
        }

        // Boiler plate parameters
        parameters.put("oauth_consumer_key", consumerKey);
        parameters.put("oauth_signature_method", "HMAC-SHA1");
        parameters.put("oauth_version", "1.0");

        // Normalized URI without query params and fragment
        String baseUri = getBaseUriString(uri);

        // Build the parameter string after sorting the keys in lexicographic order per the OAuth v1 spec.
        TreeMap<String, List<String>> queryParams = extractQueryParams(uri);

        // Combine query and oauth_ parameters into lexicographically sorted string
        String paramString = toOauthParamString(queryParams, parameters);

        // Build the signature base string
        String signatureBaseString = method.toUpperCase() + "&" + percentEncode(baseUri) + "&" + percentEncode(paramString);

        // If the signing key was not provided, build it by encoding the consumer secret + the token secret
        String signingKey = percentEncode(consumerSecret) + "&" + (tokenSecret == null ? "" : percentEncode(tokenSecret));

        // Sign the Signature Base String
        String signature = generateSignature(signingKey, signatureBaseString);

        // Add the signature to be included in the header
        parameters.put("oauth_signature", signature);

        // Build the authorization header value using the order in which the parameters were added
        return "OAuth " + parameters.entrySet().stream()
            .map(e -> percentEncode(e.getKey()) + "=\"" + percentEncode(e.getValue()) + "\"")
            .collect(Collectors.joining(", "));
    }

    public String generateSignature(String secret, String message) {
        try {
            byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(bytes, "HmacSHA1"));
            byte[] result = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(result);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    static TreeMap<String, List<String>> extractQueryParams(URI uri) {
        final String decodedQueryString = uri.getQuery();
        final String rawQueryString = uri.getRawQuery();
        if (decodedQueryString == null || decodedQueryString.isEmpty() || rawQueryString == null || rawQueryString.isEmpty()) {
            // No query params
            return new TreeMap<>();
        }

        boolean mustEncode = !decodedQueryString.equals(rawQueryString);
        final TreeMap<String, List<String>> queryPairs = new TreeMap<>();
        final String[] pairs = decodedQueryString.split("&");
        for (String pair : pairs) {
            final int idx = pair.indexOf('=');
            String key = idx > 0 ? pair.substring(0, idx) : pair;
            if (!queryPairs.containsKey(key)) {
                key = mustEncode ? percentEncode(key) : key;
                queryPairs.put(key, new LinkedList<>());
            }
            String value = idx > 0 && pair.length() > idx + 1 ? pair.substring(idx + 1) : EMPTY_STRING;
            value = mustEncode ? percentEncode(value) : value;
            queryPairs.get(key).add(value);
        }

        return queryPairs;
    }

    static String toOauthParamString(SortedMap<String, List<String>> queryParamsMap, Map<String, String> oauthParamsMap) {
        TreeMap<String, List<String>> consolidatedParams = new TreeMap<>(queryParamsMap);

        // Add OAuth params to consolidated params map
        for (Map.Entry<String, String> entry : oauthParamsMap.entrySet()) {
            if (consolidatedParams.containsKey(entry.getKey())) {
                consolidatedParams.get(entry.getKey()).add(entry.getValue());
            } else {
                consolidatedParams.put(entry.getKey(), Collections.singletonList(entry.getValue()));
            }
        }

        StringBuilder oauthParams = new StringBuilder();

        // Add all parameters to the parameter string for signing
        for (Map.Entry<String, List<String>> entry : consolidatedParams.entrySet()) {
            String key = entry.getKey();

            // Keys with same name are sorted by their values
            if (entry.getValue().size() > 1) {
                Collections.sort(entry.getValue());
            }

            for (String value : entry.getValue()) {
                oauthParams.append(key).append("=").append(value).append("&");
            }
        }

        // Remove trailing ampersand
        int stringLength = oauthParams.length() - 1;
        if (oauthParams.charAt(stringLength) == '&') {
            oauthParams.deleteCharAt(stringLength);
        }

        return oauthParams.toString();
    }

    public static String percentEncode(String str) {
        if (str == null || str.isEmpty()) {
            return EMPTY_STRING;
        }

        return URLEncoder.encode(str, StandardCharsets.UTF_8)
            .replace("+", "%20")
            .replace("*", "%2A")
            .replace("%7E", "~");
    }

    private static String getTimestamp() {
        return Long.toString(System.currentTimeMillis() / 1000L);
    }

    static String getNonce() {
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(NONCE_LENGTH);
        for (int i = 0; i < NONCE_LENGTH; i++) {
            sb.append(ALPHA_NUMERIC_CHARS.charAt(rnd.nextInt(ALPHA_NUMERIC_CHARS.length())));
        }
        return sb.toString();
    }

    /**
     * Normalizes the URL as per.
     * https://tools.ietf.org/html/rfc5849#section-3.4.1.2
     *
     * @param uri URL that will be called as part of this request
     * @return Normalized URL
     */
    static String getBaseUriString(URI uri) {
        // Lowercase scheme and authority
        String scheme = uri.getScheme().toLowerCase();
        String authority = uri.getAuthority().toLowerCase();

        // Remove port if it matches the default for scheme
        if (("http".equals(scheme) && uri.getPort() == 80) || ("https".equals(scheme) && uri.getPort() == 443)) {
            int index = authority.lastIndexOf(':');
            if (index >= 0) {
                authority = authority.substring(0, index);
            }
        }

        String path = uri.getRawPath();
        if (path == null || path.length() <= 0) {
            path = "/";
        }

        return scheme + "://" + authority + path;
    }

    /**
     * Set the Consumer Key.
     *
     * @param consumerKey the Consumer Key
     * @return this
     */
    public OAuth1SignatureBuilder withConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
        return this;
    }

    /**
     * Set the Consumer Secret.
     *
     * @param consumerSecret the Consumer Secret
     * @return this
     */
    public OAuth1SignatureBuilder withConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
        return this;
    }

    /**
     * Set the requested HTTP method.
     *
     * @param method the HTTP method you are requesting
     * @return this
     */
    public OAuth1SignatureBuilder withMethod(String method) {
        this.method = method;
        return this;
    }

    /**
     * Add a parameter to the be included when building the signature.
     *
     * @param name  the parameter name
     * @param value the parameter value
     * @return this
     */
    public OAuth1SignatureBuilder withParameter(String name, String value) {
        parameters.put(name, value);
        return this;
    }

    /**
     * Set the OAuth Token Secret.
     *
     * @param tokenSecret the OAuth Token Secret
     * @return this
     */
    public OAuth1SignatureBuilder withTokenSecret(String tokenSecret) {
        this.tokenSecret = tokenSecret;
        return this;
    }

    /**
     * Set the requested URL in the builder.
     *
     * @param uri the URL you are requesting
     * @return this
     */
    public OAuth1SignatureBuilder withURI(URI uri) {
        this.uri = uri;
        return this;
    }
}
