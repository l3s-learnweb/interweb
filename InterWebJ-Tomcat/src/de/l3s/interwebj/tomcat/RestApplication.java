package de.l3s.interwebj.tomcat;

import de.l3s.interwebj.tomcat.servlet.filter.OAuthFilter;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.oauth1.signature.OAuth1SignatureFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/api")
public class RestApplication extends ResourceConfig {
    public RestApplication() {
        // auto scan for providers and endpoints
        packages("de.l3s.interwebj.tomcat.rest");

        register(MultiPartFeature.class);
        register(OAuth1SignatureFeature.class);
        // register(OAuth1ServerFeature.class);
        // register(OAuth1Provider.class);
        register(OAuthFilter.class);
    }
}
