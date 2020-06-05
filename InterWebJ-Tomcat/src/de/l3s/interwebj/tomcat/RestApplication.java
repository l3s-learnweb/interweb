package de.l3s.interwebj.tomcat;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.oauth1.signature.OAuth1SignatureFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import de.l3s.interwebj.tomcat.servlet.provider.OAuthFilter;
import de.l3s.interwebj.tomcat.servlet.provider.ServletExceptionMapper;

@ApplicationPath("/api")
public class RestApplication extends ResourceConfig {
    public RestApplication() {
        // auto scan for providers and endpoints
        packages("de.l3s.interwebj.tomcat.rest");

        property(ServerProperties.WADL_FEATURE_DISABLE, true);

        register(MultiPartFeature.class);
        register(JacksonFeature.class);
        // register(OAuth1ServerFeature.class);
        // register(InterwebOAuth1Provider.class);
        register(OAuth1SignatureFeature.class);
        register(ServletExceptionMapper.class);
        register(OAuthFilter.class);
    }
}
