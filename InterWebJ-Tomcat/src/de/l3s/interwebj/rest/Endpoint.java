package de.l3s.interwebj.rest;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import de.l3s.interwebj.AuthCredentials;
import de.l3s.interwebj.core.Environment;
import de.l3s.interwebj.core.InterWebPrincipal;
import de.l3s.interwebj.db.Database;
import org.glassfish.jersey.client.oauth1.AccessToken;
import org.glassfish.jersey.client.oauth1.ConsumerCredentials;
import org.glassfish.jersey.client.oauth1.OAuth1Builder;
import org.glassfish.jersey.client.oauth1.OAuth1ClientSupport;
import org.glassfish.jersey.oauth1.signature.OAuth1Parameters;
import org.glassfish.jersey.server.oauth1.internal.OAuthServerRequest;

import java.net.URI;

public class Endpoint
{
	@Context
	private UriInfo info;

	@Context
	private ContainerRequestContext requestContext;

    public URI getBaseUri()
    {
		return info.getBaseUri();
    }

    public OAuth1Parameters getOAuthParameters()
    {
		OAuthServerRequest osr = new OAuthServerRequest(requestContext);
		return new OAuth1Parameters().readRequest(osr);
	}

	public InterWebPrincipal getPrincipal()
	{
		OAuth1Parameters params = getOAuthParameters();
		String token = params.getToken();
		if(token == null)
		{
			return null;
		}
		Database database = Environment.getInstance().getDatabase();
		InterWebPrincipal principal = database.readPrincipalByKey(token);
		return principal;
	}

    public static WebTarget createWebTarget(String url, AuthCredentials consumerCredentials, AuthCredentials userCredentials)
    {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(url);

		ConsumerCredentials consumerCred = new ConsumerCredentials(consumerCredentials.getKey(), consumerCredentials.getSecret());

		OAuth1Builder.FilterFeatureBuilder filterFeature = OAuth1ClientSupport.builder(consumerCred).feature();

		if (userCredentials != null)
		{
			AccessToken storedToken = new AccessToken(userCredentials.getKey(), userCredentials.getSecret());
			filterFeature.accessToken(storedToken);
		}
		target.register(filterFeature.build());

		return target;
    }
}
