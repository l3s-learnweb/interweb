package de.l3s.interwebj.tomcat.servlet.provider;

import de.l3s.interwebj.core.core.Consumer;
import de.l3s.interwebj.core.core.Environment;
import de.l3s.interwebj.core.core.InterWebPrincipal;
import de.l3s.interwebj.core.db.Database;
import org.glassfish.jersey.server.oauth1.OAuth1Consumer;
import org.glassfish.jersey.server.oauth1.OAuth1Token;

import javax.ws.rs.core.MultivaluedMap;
import java.security.Principal;
import java.util.List;
import java.util.Map;

public class OAuth1Provider implements org.glassfish.jersey.server.oauth1.OAuth1Provider {

    @Override
    public InterwebConsumer getConsumer(String consumerKey) {
        final Database database = Environment.getInstance().getDatabase();
        return new InterwebConsumer(database.readConsumerByKey(consumerKey));
    }

    @Override
    public OAuth1Token newRequestToken(String consumerKey, String callbackUrl, Map<String, List<String>> attributes) {
        return null;
    }

    @Override
    public OAuth1Token getRequestToken(String token) {
        return null;
    }

    @Override
    public OAuth1Token newAccessToken(OAuth1Token requestToken, String verifier) {
        return null;
    }

    @Override
    public InterwebToken getAccessToken(String token) {
        final Database database = Environment.getInstance().getDatabase();
        return new InterwebToken(database.readPrincipalByKey(token));
    }

    public static class InterwebToken implements OAuth1Token {
        private final InterWebPrincipal principal;

        public InterwebToken(InterWebPrincipal principal) {
            this.principal = principal;
        }

        @Override
        public String getToken() {
            return principal.getOauthCredentials().getKey();
        }

        @Override
        public String getSecret() {
            return principal.getOauthCredentials().getSecret();
        }

        @Override
        public InterwebConsumer getConsumer() {
            return null;
        }

        @Override
        public MultivaluedMap<String, String> getAttributes() {
            return null;
        }

        @Override
        public Principal getPrincipal() {
            return principal;
        }

        @Override
        public boolean isInRole(String role) {
            return false;
        }
    }

    public static class InterwebConsumer implements OAuth1Consumer {
        private final Consumer consumer;
        
        public InterwebConsumer(Consumer consumer) {
            this.consumer = consumer;
        }

        @Override
        public String getKey() {
            return consumer.getAuthCredentials().getKey();
        }

        @Override
        public String getSecret() {
            return consumer.getAuthCredentials().getSecret();
        }

        @Override
        public Principal getPrincipal() {
            final Database database = Environment.getInstance().getDatabase();
            return database.readPrincipalByName(consumer.getName());
        }

        @Override
        public boolean isInRole(String role) {
            return true;
        }
    }
}
