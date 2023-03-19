package de.l3s.interweb.tomcat.rest;

import java.net.URI;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.cache.Cache;

import de.l3s.interweb.core.AuthCredentials;
import de.l3s.interweb.tomcat.app.Engine;
import de.l3s.interweb.tomcat.app.InterWebPrincipal;
import de.l3s.interweb.tomcat.db.Database;
import de.l3s.interweb.tomcat.jaxb.auth.OAuthAccessTokenResponse;
import de.l3s.interweb.tomcat.jaxb.auth.OAuthRequestTokenResponse;

@Path("/oauth")
public class OAuth extends Endpoint {
    private static final Logger log = LogManager.getLogger(OAuth.class);

    @Inject
    private Engine engine;
    @Inject
    private Database database;

    @GET
    @Path("/OAuthAuthorizeToken")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response authorizeToken(@QueryParam("oauth_token") String requestToken, @QueryParam("oauth_callback") String callbackUrl) {
        log.info("callbackUrl: [{}]", callbackUrl);
        URI uri = getBaseUri().resolve("../view/authorize_consumer.xhtml");
        UriBuilder builder = UriBuilder.fromUri(uri);
        builder = builder.queryParam("oauth_token", requestToken);
        if (callbackUrl != null) {
            builder = builder.queryParam("oauth_callback", callbackUrl);
        }
        uri = builder.build();

        return Response.seeOther(uri).build();
    }

    @GET
    @Path("/OAuthGetAccessToken")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public OAuthAccessTokenResponse getAccessToken() {
        String token = getOAuthParameters().getToken();

        Cache<String, Object> cache = engine.getGeneralCache();
        InterWebPrincipal principal = (InterWebPrincipal) cache.getIfPresent("principal:" + token);
        cache.invalidate("access_token:" + token);
        cache.invalidate("consumer_token:" + token);
        cache.invalidate("principal:" + token);

        AuthCredentials accessToken = AuthCredentials.random();
        principal.setOauthCredentials(accessToken);
        database.updatePrincipal(principal);

        return new OAuthAccessTokenResponse(accessToken);
    }

    @GET
    @Path("/OAuthGetRequestToken")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public OAuthRequestTokenResponse getRequestToken() {
        AuthCredentials authCredentials = AuthCredentials.random();
        String consumerKey = getOAuthParameters().getConsumerKey();

        Cache<String, Object> cache = engine.getGeneralCache();
        cache.put("request_token:" + authCredentials.getKey(), authCredentials);
        cache.put("consumer_token:" + authCredentials.getKey(), consumerKey);
        return new OAuthRequestTokenResponse(authCredentials);
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public OAuthAccessTokenResponse registerUser(@FormParam("username") String userName, @FormParam("password") String password,
        @FormParam("mediator_username") String mediatorUserName, @FormParam("mediator_password") String mediatorPassword) {

        InterWebPrincipal mediator = null;
        if (mediatorUserName != null) {
            mediator = database.authenticate(mediatorUserName, mediatorPassword);
            if (mediator == null) {
                throw new WebApplicationException("No account for this token", Response.Status.BAD_REQUEST);
            }
        }

        InterWebPrincipal principal = InterWebPrincipal.createDefault(userName);
        if (database.hasPrincipal(userName)) {
            throw new WebApplicationException("User already exists", Response.Status.CONFLICT);
        }

        AuthCredentials accessToken = AuthCredentials.random();
        principal.setOauthCredentials(accessToken);
        log.info(principal.toString());
        database.savePrincipal(principal, password);
        if (mediator != null) {
            database.saveMediator(principal.getName(), mediator.getName());
        }

        return new OAuthAccessTokenResponse(accessToken);
    }

    // @POST
    // @Path("/set_defaults")
    // @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    // @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    // public XMLResponse registerUser(@FormParam("default_token") String defaultToken,
    //                                 @FormParam("default_secret") String defaultTokenSecret)
    // {
    //     Database database = Environment.getInstance().getDatabase();
    //     InterWebPrincipal principal = InterWebPrincipal.createDefault(userName);
    //     if (database.hasPrincipal(userName))
    //     {
    //         throwWebApplicationException(ErrorResponse.USER_EXISTS);
    //     }
    //     AuthCredentials accessToken = RandomGenerator.getInstance().nextOAuthCredentials();
    //     principal.setOauthCredentials(accessToken);
    //     database.savePrincipal(principal, password);
    //     OAuthAccessTokenResponse response = new OAuthAccessTokenResponse(accessToken);
    //     return response;
    // }
}
