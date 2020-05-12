package de.l3s.interwebj.servlet.filter;

import static de.l3s.interwebj.webutil.RestUtils.throwWebApplicationException;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import com.sun.jersey.oauth.server.OAuthServerRequest;
import com.sun.jersey.oauth.signature.OAuthParameters;
import com.sun.jersey.oauth.signature.OAuthSecrets;
import com.sun.jersey.oauth.signature.OAuthSignature;
import com.sun.jersey.oauth.signature.OAuthSignatureException;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

import de.l3s.interwebj.core.Consumer;
import de.l3s.interwebj.core.Engine;
import de.l3s.interwebj.core.Environment;
import de.l3s.interwebj.core.InterWebPrincipal;
import de.l3s.interwebj.db.Database;
import de.l3s.interwebj.jaxb.ErrorResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OAuthFilter implements ContainerRequestFilter
{
	private static final Logger log = LogManager.getLogger(OAuthFilter.class);

    @Context
    SecurityContext context;

    @Override
    public ContainerRequest filter(ContainerRequest containerRequest)
    {
	log.info("OAuth filter processing.");
	log.info("request path: [" + containerRequest.getPath() + "]");
	log.info("authorization: " + containerRequest.getRequestHeader("authorization"));
	if(containerRequest.getPath().equals("oauth/OAuthAuthorizeToken"))
	{
	    return containerRequest;
	}
	OAuthServerRequest request = new OAuthServerRequest(containerRequest);
	OAuthParameters params = new OAuthParameters();
	OAuthSecrets secrets = new OAuthSecrets();
	params.readRequest(request);
	String consumerKey = params.getConsumerKey();
	if(consumerKey == null)
	{
	    throwWebApplicationException(ErrorResponse.NO_CONSUMER_KEY_GIVEN);
	}
	Database database = Environment.getInstance().getDatabase();
	Consumer consumer = database.readConsumerByKey(consumerKey);
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
	    if(!OAuthSignature.verify(request, params, secrets))
	    {
		log.error("failed to verify signature");
		log.error("received signature: [" + params.getSignature() + "]");
		log.error("generated signature: [" + OAuthSignature.generate(request, params, secrets) + "]");
		throwWebApplicationException(ErrorResponse.INVALID_SIGNATURE);
	    }
	}
	catch(OAuthSignatureException e)
	{
	    ErrorResponse errorResponse = new ErrorResponse(999, e.getMessage());
	    throwWebApplicationException(errorResponse);
	}
	return containerRequest;
    }

}
