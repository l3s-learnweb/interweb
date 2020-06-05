package de.l3s.interwebj.core;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Parameters {

    public static final String AUTHORIZATION_URL = "authorization_url";
    public static final String USER_KEY = "user_key";
    public static final String USER_SECRET = "user_secret";
    public static final String OAUTH_TOKEN = "oauth_token";
    public static final String OAUTH_TOKEN_SECRET = "oauth_token_secret";
    public static final String OAUTH_VERIFIER = "oauth_verifier";
    public static final String OAUTH_CONSUMER_KEY = "oauth_consumer_key";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String FILENAME = "filename";
    public static final String TAGS = "tags";
    public static final String PRIVACY = "privacy";
    public static final String CLIENT_TYPE = "client_type";
    public static final String CONNECTOR_NAME = "connector_name";
    public static final String TOKEN = "token";
    public static final String CONSUMER_KEY = "consumer_key";
    public static final String CALLBACK = "callback";
    public static final String IWJ_USER_ID = "iwj_user_id";
    public static final String IWJ_CONNECTOR_ID = "iwj_connector_id";
    public static final String ERROR = "error";

    private final Map<String, String> parameters;

    public Parameters() {
        parameters = new TreeMap<>();
    }

    public void add(Map<String, String> parameters) {
        this.parameters.putAll(parameters);
    }

    public void add(Parameters parameters, boolean replace) {
        for (String parameter : parameters.keySet()) {
            if (replace || !this.parameters.containsKey(parameter)) {
                this.parameters.put(parameter, parameters.get(parameter));
            }
        }
    }

    public void add(String name, String value) {
        parameters.put(name, value);
    }

    public void addDecoded(String name, String value) {
        String decodedValue = URLDecoder.decode(value, StandardCharsets.UTF_8);
        add(name, decodedValue);
    }

    public void addMultivaluedParams(Map<String, String[]> parameters) {
        for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
            String[] values = entry.getValue();
            String value = null;
            if (values != null && values.length > 0) {
                value = values[0];
            }
            add(entry.getKey(), value);
        }
    }

    public void addQueryParameters(String query) {
        int startQueryIndex = !query.contains("?") ? 0 : query.indexOf('?') + 1;
        query = query.substring(startQueryIndex);
        String[] params = query.split("&");
        for (String param : params) {
            String[] paramPair = param.split("=");
            String name = paramPair[0];
            String value = null;
            if (paramPair.length > 1) {
                value = paramPair[1];
            }
            addDecoded(name, value);
        }
    }

    public boolean containsKey(String name) {
        return parameters.containsKey(name);
    }

    public String get(String name) {
        return parameters.get(name);
    }

    public String get(String name, String defaultValue) {
        String value = get(name);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public boolean hasParameter(String key) {
        return parameters.containsKey(key);
    }

    public Set<String> keySet() {
        return parameters.keySet();
    }

    public String remove(String name) {
        return parameters.remove(name);
    }

    public String toQueryString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append('&');
        }

        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append("parameters", parameters)
            .toString();
    }
}
