package de.l3s.interwebj.tomcat.rest;

import static de.l3s.interwebj.tomcat.webutil.RestUtils.throwWebApplicationException;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.l3s.interwebj.core.AuthCredentials;
import de.l3s.interwebj.core.core.Engine;
import de.l3s.interwebj.core.core.Environment;
import de.l3s.interwebj.core.core.InterWebPrincipal;
import de.l3s.interwebj.core.db.Database;
import de.l3s.interwebj.core.util.ExpirableMap;
import de.l3s.interwebj.core.util.RandomGenerator;
import de.l3s.interwebj.tomcat.jaxb.ErrorResponse;
import de.l3s.interwebj.tomcat.jaxb.XMLResponse;
import de.l3s.interwebj.tomcat.jaxb.auth.OAuthAccessTokenResponse;
import de.l3s.interwebj.tomcat.jaxb.auth.OAuthRequestTokenResponse;

@Path("/oauth")
public class OAuth extends Endpoint {
    private static final Logger log = LogManager.getLogger(OAuth.class);

    @GET
    @Path("/OAuthAuthorizeToken")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public XMLResponse authorizeToken(@QueryParam("oauth_token") String requestToken, @QueryParam("oauth_callback") String callbackUrl) {
        log.info("callbackUrl: [" + callbackUrl + "]");
        URI uri = getBaseUri().resolve("../view/authorize_consumer.xhtml");
        UriBuilder builder = UriBuilder.fromUri(uri);
        builder = builder.queryParam("oauth_token", requestToken);
        if (callbackUrl != null) {
            builder = builder.queryParam("oauth_callback", callbackUrl);
        }
        uri = builder.build();
        Response response = Response.seeOther(uri).build();
        throw new WebApplicationException(response);
    }

    @GET
    @Path("/OAuthGetAccessToken")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public XMLResponse getAccessToken() {
        String token = getOAuthParameters().getToken();
        Engine engine = Environment.getInstance().getEngine();
        ExpirableMap<String, Object> expirableMap = engine.getExpirableMap();
        expirableMap.remove("access_token:" + token);
        expirableMap.remove("consumer_token:" + token);
        InterWebPrincipal principal = (InterWebPrincipal) expirableMap.remove("principal:" + token);
        Database database = Environment.getInstance().getDatabase();
        AuthCredentials accessToken = RandomGenerator.getInstance().nextOAuthCredentials();
        principal.setOauthCredentials(accessToken);
        database.updatePrincipal(principal);
        OAuthAccessTokenResponse response = new OAuthAccessTokenResponse(accessToken);
        return response;
    }

    @GET
    @Path("/OAuthGetRequestToken")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public XMLResponse getRequestToken() {
        AuthCredentials authCredentials = RandomGenerator.getInstance().nextOAuthCredentials();
        OAuthRequestTokenResponse response = new OAuthRequestTokenResponse(authCredentials);
        Engine engine = Environment.getInstance().getEngine();
        ExpirableMap<String, Object> expirableMap = engine.getExpirableMap();
        expirableMap.put("request_token:" + authCredentials.getKey(), authCredentials);
        String consumerKey = getOAuthParameters().getConsumerKey();
        expirableMap.put("consumer_token:" + authCredentials.getKey(), consumerKey);
        return response;
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public XMLResponse registerUser(@FormParam("username") String userName, @FormParam("password") String password,
                                    @FormParam("mediator_username") String mediatorUserName, @FormParam("mediator_password") String mediatorPassword) {
        Database database = Environment.getInstance().getDatabase();
        InterWebPrincipal mediator = null;
        if (mediatorUserName != null) {
            mediator = database.authenticate(mediatorUserName, mediatorPassword);
            if (mediator == null) {
                return ErrorResponse.NO_ACCOUNT_FOR_TOKEN;
            }
        }
        InterWebPrincipal principal = InterWebPrincipal.createDefault(userName);
        if (database.hasPrincipal(userName)) {
            throwWebApplicationException(ErrorResponse.USER_EXISTS);
        }
        AuthCredentials accessToken = RandomGenerator.getInstance().nextOAuthCredentials();
        principal.setOauthCredentials(accessToken);
        log.info(principal.toString());
        database.savePrincipal(principal, password);
        if (mediator != null) {
            database.saveMediator(principal.getName(), mediator.getName());
        }
        OAuthAccessTokenResponse response = new OAuthAccessTokenResponse(accessToken);
        return response;
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
