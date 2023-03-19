package de.l3s.interweb.tomcat;

import jakarta.ws.rs.ApplicationPath;

import org.glassfish.jersey.jsonb.JsonBindingFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.oauth1.signature.OAuth1SignatureFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import de.l3s.interweb.tomcat.servlet.provider.OAuthFilter;
import de.l3s.interweb.tomcat.servlet.provider.RequestUriFilter;
import de.l3s.interweb.tomcat.servlet.provider.ServletExceptionMapper;

@ApplicationPath("/api")
public class RestApplication extends ResourceConfig {
    public RestApplication() {
        // auto scan for providers and endpoints
        packages("de.l3s.interweb.tomcat.rest");

        property(ServerProperties.WADL_FEATURE_DISABLE, true);

        register(RequestUriFilter.class);
        register(MultiPartFeature.class);
        register(JsonBindingFeature.class);
        register(OAuth1SignatureFeature.class);
        register(ServletExceptionMapper.class);
        register(OAuthFilter.class);
    }
}
