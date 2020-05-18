package de.l3s.interwebj.tomcat;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.oauth1.signature.OAuth1SignatureFeature;
import org.glassfish.jersey.server.ResourceConfig;

import de.l3s.interwebj.tomcat.servlet.provider.OAuthFilter;

@ApplicationPath("/api")
public class RestApplication extends ResourceConfig {
    public RestApplication() {
        // auto scan for providers and endpoints
        packages("de.l3s.interwebj.tomcat.rest");

        register(MultiPartFeature.class);
        // register(OAuth1ServerFeature.class);
        // register(InterwebOAuth1Provider.class);
        register(OAuth1SignatureFeature.class);
        register(OAuthFilter.class);
    }
}
