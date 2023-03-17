package de.l3s.interweb.tomcat.servlet.provider;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.oauth1.signature.OAuth1Parameters;
import org.glassfish.jersey.oauth1.signature.OAuth1Secrets;
import org.glassfish.jersey.oauth1.signature.OAuth1Signature;
import org.glassfish.jersey.oauth1.signature.OAuth1SignatureException;
import org.glassfish.jersey.server.oauth1.internal.OAuthServerRequest;

import de.l3s.interweb.tomcat.core.Consumer;
import de.l3s.interweb.tomcat.core.Engine;
import de.l3s.interweb.tomcat.core.Environment;
import de.l3s.interweb.tomcat.core.InterWebPrincipal;
import de.l3s.interweb.tomcat.db.Database;

@Provider
public class OAuthFilter implements ContainerRequestFilter {
    private static final Logger log = LogManager.getLogger(OAuthFilter.class);

    @Context
    SecurityContext context;

    @Context
    OAuth1Signature oAuth1Signature;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        log.info("OAuth filter processing.");
        log.info("absolute path: [{}]", requestContext.getUriInfo().getAbsolutePath());
        log.info("authorization: {}", requestContext.getHeaderString("authorization"));
        if (requestContext.getUriInfo().getPath().equals("oauth/OAuthAuthorizeToken")) {
            return;
        }

        OAuthServerRequest request = new OAuthServerRequest(requestContext);
        OAuth1Parameters params = new OAuth1Parameters().readRequest(request);
        OAuth1Secrets secrets = new OAuth1Secrets();
        String consumerKey = params.getConsumerKey();
        if (consumerKey == null) {
            throw new WebApplicationException("No consumer key given", Response.Status.UNAUTHORIZED);
        }

        final Database database = Environment.getInstance().getDatabase();
        final Consumer consumer = database.readConsumerByKey(consumerKey);
        if (consumer == null) {
            throw new WebApplicationException("Invalid signature", Response.Status.UNAUTHORIZED);
        }

        String consumerSecret = consumer.authCredentials().getSecret();
        secrets.consumerSecret(consumerSecret);
        String token = params.getToken();

        if (token != null) {
            String tokenSecret = null;
            Engine engine = Environment.getInstance().getEngine();
            InterWebPrincipal principal = (InterWebPrincipal) engine.getGeneralCache().getIfPresent("principal:" + token);

            if (principal != null && principal.getOauthCredentials() != null) {
                log.info("temporary token");
            } else {
                principal = database.readPrincipalByKey(token);
                if (principal != null && principal.getOauthCredentials() != null) {
                    log.info("permanent token");
                }
            }

            if (principal != null && principal.getOauthCredentials() != null) {
                tokenSecret = principal.getOauthCredentials().getSecret();
            }

            secrets.tokenSecret(tokenSecret);
        }

        try {
            if (!oAuth1Signature.verify(request, params, secrets)) {
                log.warn("received signature: [{}]", params.getSignature());
                log.warn("generated signature: [{}]", oAuth1Signature.generate(request, params, secrets));
                log.error("Failed to verify signature!");
                throw new WebApplicationException("Invalid signature", Response.Status.UNAUTHORIZED);
            }
        } catch (OAuth1SignatureException e) {
            throw new WebApplicationException(e, Response.Status.UNAUTHORIZED);
        }
    }
}
