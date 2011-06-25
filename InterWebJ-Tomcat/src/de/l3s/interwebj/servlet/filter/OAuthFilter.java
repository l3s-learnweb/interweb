package de.l3s.interwebj.servlet.filter;


import static de.l3s.interwebj.webutil.RestUtils.*;

import javax.ws.rs.core.*;

import com.sun.jersey.oauth.server.*;
import com.sun.jersey.oauth.signature.*;
import com.sun.jersey.spi.container.*;

import de.l3s.interwebj.core.*;
import de.l3s.interwebj.db.*;
import de.l3s.interwebj.jaxb.*;


public class OAuthFilter
    implements ContainerRequestFilter
{
	
	@Context
	SecurityContext context;
	

	@SuppressWarnings("null")
	@Override
	public ContainerRequest filter(ContainerRequest containerRequest)
	{
		Environment.logger.info("OAuth filter processing.");
		Environment.logger.info("request path: [" + containerRequest.getPath()
		                        + "]");
		Environment.logger.info("authorization: "
		                        + containerRequest.getRequestHeader("authorization"));
		if (containerRequest.getPath().equals("oauth/OAuthAuthorizeToken"))
		{
			return containerRequest;
		}
		OAuthServerRequest request = new OAuthServerRequest(containerRequest);
		OAuthParameters params = new OAuthParameters();
		OAuthSecrets secrets = new OAuthSecrets();
		params.readRequest(request);
		String consumerKey = params.getConsumerKey();
		if (consumerKey == null)
		{
			throwWebApplicationException(ErrorResponse.NO_CONSUMER_KEY_GIVEN);
		}
		Database database = Environment.getInstance().getDatabase();
		Consumer consumer = database.readConsumerByKey(consumerKey);
		if (consumer == null)
		{
			throwWebApplicationException(ErrorResponse.INVALID_SIGNATURE);
		}
		String consumerSecret = consumer.getAuthCredentials().getSecret();
		secrets.consumerSecret(consumerSecret);
		String token = params.getToken();
		if (token != null)
		{
			String tokenSecret = null;
			Engine engine = Environment.getInstance().getEngine();
			InterWebPrincipal principal = (InterWebPrincipal) engine.getExpirableMap().get("principal:"
			                                                                               + token);
			if (principal != null && principal.getOauthCredentials() != null)
			{
				Environment.logger.info("temporary token");
			}
			else
			{
				principal = database.readPrincipalByKey(token);
				if (principal != null
				    && principal.getOauthCredentials() != null)
				{
					Environment.logger.info("permanent token");
				}
			}
			if (principal != null && principal.getOauthCredentials() != null)
			{
				tokenSecret = principal.getOauthCredentials().getSecret();
			}
			secrets.tokenSecret(tokenSecret);
		}
		try
		{
			if (!OAuthSignature.verify(request, params, secrets))
			{
				Environment.logger.severe("failed to verify signature");
				Environment.logger.severe("received signature: ["
				                          + params.getSignature() + "]");
				Environment.logger.severe("generated signature: ["
				                          + OAuthSignature.generate(request,
				                                                    params,
				                                                    secrets)
				                          + "]");
				throwWebApplicationException(ErrorResponse.INVALID_SIGNATURE);
			}
		}
		catch (OAuthSignatureException e)
		{
			ErrorResponse errorResponse = new ErrorResponse(999, e.getMessage());
			throwWebApplicationException(errorResponse);
		}
		return containerRequest;
	}
	
}
