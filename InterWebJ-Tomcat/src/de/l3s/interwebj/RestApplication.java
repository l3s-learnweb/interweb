package de.l3s.interwebj;

import de.l3s.interwebj.servlet.filter.OAuthFilter;
import de.l3s.interwebj.servlet.provider.OAuth1Provider;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.oauth1.signature.OAuth1SignatureFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.oauth1.OAuth1ServerFeature;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/api")
public class RestApplication extends ResourceConfig {
    public RestApplication() {
        // auto scan for providers and endpoints
        packages("de.l3s.interwebj.rest");

        register(MultiPartFeature.class);
        register(OAuth1SignatureFeature.class);
        // register(OAuth1ServerFeature.class);
        // register(OAuth1Provider.class);
        register(OAuthFilter.class);
    }
}
