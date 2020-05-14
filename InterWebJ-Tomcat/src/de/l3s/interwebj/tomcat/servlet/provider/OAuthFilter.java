package de.l3s.interwebj.tomcat.servlet.provider;

import static de.l3s.interwebj.tomcat.webutil.RestUtils.throwWebApplicationException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import de.l3s.interwebj.core.core.Consumer;
import de.l3s.interwebj.core.core.Engine;
import de.l3s.interwebj.core.core.Environment;
import de.l3s.interwebj.core.core.InterWebPrincipal;
import de.l3s.interwebj.core.db.Database;
import de.l3s.interwebj.tomcat.jaxb.ErrorResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.oauth1.signature.*;
import org.glassfish.jersey.server.oauth1.internal.OAuthServerRequest;

import java.io.IOException;

@Provider
public class OAuthFilter implements ContainerRequestFilter
{
	private static final Logger log = LogManager.getLogger(OAuthFilter.class);

    @Context
    SecurityContext context;

    @Context
    OAuth1Signature oAuth1Signature;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		log.info("OAuth filter processing.");
		log.info("request path: [" + requestContext.getUriInfo().getPath() + "]");
		log.info("authorization: " + requestContext.getHeaderString("authorization"));
		if(requestContext.getUriInfo().getPath().equals("oauth/OAuthAuthorizeToken"))
			return;

		OAuthServerRequest osr = new OAuthServerRequest(requestContext);
		OAuth1Parameters params = new OAuth1Parameters().readRequest(osr);
		OAuth1Secrets secrets = new OAuth1Secrets();
		String consumerKey = params.getConsumerKey();
		if(consumerKey == null)
		{
			throwWebApplicationException(ErrorResponse.NO_CONSUMER_KEY_GIVEN);
		}
		final Database database = Environment.getInstance().getDatabase();
		final Consumer consumer = database.readConsumerByKey(consumerKey);
		if(consumer == null)
		{
			throwWebApplicationException(ErrorResponse.INVALID_SIGNATURE);
		}
		String consumerSecret = consumer.getAuthCredentials().getSecret();
		secrets.consumerSecret(consumerSecret);
		String token = params.getToken();

		if(token != null)
		{
			String tokenSecret = null;
			Engine engine = Environment.getInstance().getEngine();
			InterWebPrincipal principal = (InterWebPrincipal) engine.getExpirableMap().get("principal:" + token);
			if(principal != null && principal.getOauthCredentials() != null)
			{
				log.info("temporary token");
			}
			else
			{
				principal = database.readPrincipalByKey(token);
				if(principal != null && principal.getOauthCredentials() != null)
				{
					log.info("permanent token");
				}
			}
			if(principal != null && principal.getOauthCredentials() != null)
			{
				tokenSecret = principal.getOauthCredentials().getSecret();
			}
			secrets.tokenSecret(tokenSecret);
		}

		try
		{
			if(!oAuth1Signature.verify(osr, params, secrets))
			{
				log.error("failed to verify signature");
				log.error("received signature: [" + params.getSignature() + "]");
				log.error("generated signature: [" + oAuth1Signature.generate(osr, params, secrets) + "]");
				throwWebApplicationException(ErrorResponse.INVALID_SIGNATURE);
			}
		}
		catch(OAuth1SignatureException e)
		{
			ErrorResponse errorResponse = new ErrorResponse(999, e.getMessage());
			throwWebApplicationException(errorResponse);
		}
    }
}
