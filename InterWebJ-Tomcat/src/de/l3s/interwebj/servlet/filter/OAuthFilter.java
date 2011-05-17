package de.l3s.interwebj.servlet.filter;


import java.security.*;

import javax.ws.rs.*;
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
	

	@Override
	public ContainerRequest filter(ContainerRequest containerRequest)
	{
		Environment.logger.debug("OAuth filter processing.");
		Environment.logger.debug("request path: [" + containerRequest.getPath()
		                         + "]");
		Environment.logger.debug("authorization: "
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
		Environment.logger.debug("consumerKey: [" + consumerKey + "]");
		if (consumerKey == null)
		{
			ErrorResponse errorResponse = new ErrorResponse(101,
			                                                "No consumer key given");
			Response response = Response.ok(errorResponse,
			                                MediaType.APPLICATION_XML).build();
			throw new WebApplicationException(response);
		}
		Database database = Environment.getInstance().getDatabase();
		Consumer consumer = database.readConsumerByKey(consumerKey);
		if (consumer == null)
		{
			Environment.logger.error("bad consumer key");
			ErrorResponse errorResponse = new ErrorResponse(104,
			                                                "Invalid signature");
			Response response = Response.ok(errorResponse,
			                                MediaType.APPLICATION_XML).build();
			throw new WebApplicationException(response);
		}
		String consumerSecret = consumer.getAuthCredentials().getSecret();
		Environment.logger.debug("consumerSecret: [" + consumerSecret + "]");
		secrets.consumerSecret(consumerSecret);
		String token = params.getToken();
		Environment.logger.debug("tokenKey: [" + token + "]");
		if (token != null)
		{
			String tokenSecret = null;
			Engine engine = Environment.getInstance().getEngine();
			InterWebPrincipal principal = (InterWebPrincipal) engine.getExpirableMap().get("principal:"
			                                                                               + token);
			if (principal != null && principal.getOauthCredentials() != null)
			{
				Environment.logger.debug("temporary token");
			}
			else
			{
				principal = database.readPrincipalByKey(token);
				if (principal != null
				    && principal.getOauthCredentials() != null)
				{
					Environment.logger.debug("permanent token");
				}
			}
			if (principal != null && principal.getOauthCredentials() != null)
			{
				tokenSecret = principal.getOauthCredentials().getSecret();
				containerRequest.setSecurityContext(createSecurityContext(principal));
			}
			Environment.logger.debug("tokenSecret: [" + tokenSecret + "]");
			secrets.tokenSecret(tokenSecret);
		}
		try
		{
			Environment.logger.debug("received signature: ["
			                         + params.getSignature() + "]");
			Environment.logger.debug("signature: ["
			                         + OAuthSignature.generate(request,
			                                                   params,
			                                                   secrets) + "]");
			if (!OAuthSignature.verify(request, params, secrets))
			{
				Environment.logger.error("failed to verify signature");
				ErrorResponse errorResponse = new ErrorResponse(104,
				                                                "Invalid signature");
				Response response = Response.ok(errorResponse,
				                                MediaType.APPLICATION_XML).build();
				throw new WebApplicationException(response);
			}
		}
		catch (OAuthSignatureException e)
		{
			ErrorResponse errorResponse = new ErrorResponse(999, e.getMessage());
			Response response = Response.ok(errorResponse,
			                                MediaType.APPLICATION_XML).build();
			throw new WebApplicationException(response);
		}
		return containerRequest;
	}
	

	private SecurityContext createSecurityContext(final InterWebPrincipal principal)
	{
		return new SecurityContext()
		{
			
			@Override
			public String getAuthenticationScheme()
			{
				return context.getAuthenticationScheme();
			}
			

			@Override
			public Principal getUserPrincipal()
			{
				return principal;
			}
			

			@Override
			public boolean isSecure()
			{
				return context.isSecure();
			}
			

			@Override
			public boolean isUserInRole(String role)
			{
				return principal.hasRole(role);
			}
		};
	}
}
