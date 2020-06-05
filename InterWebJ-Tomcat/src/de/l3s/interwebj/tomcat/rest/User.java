package de.l3s.interwebj.tomcat.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.oauth1.signature.OAuth1Parameters;

import de.l3s.interwebj.core.InterWebException;
import de.l3s.interwebj.core.Parameters;
import de.l3s.interwebj.core.connector.ServiceConnector;
import de.l3s.interwebj.core.core.Engine;
import de.l3s.interwebj.core.core.Environment;
import de.l3s.interwebj.core.core.InterWebPrincipal;
import de.l3s.interwebj.core.db.Database;
import de.l3s.interwebj.tomcat.jaxb.services.AuthorizationLinkEntity;
import de.l3s.interwebj.tomcat.jaxb.services.AuthorizationLinkResponse;
import de.l3s.interwebj.tomcat.jaxb.services.ServiceEntity;
import de.l3s.interwebj.tomcat.jaxb.services.ServiceResponse;
import de.l3s.interwebj.tomcat.jaxb.services.ServicesResponse;

@Path("/users/{user}")
public class User extends Endpoint {
    private static final Logger log = LogManager.getLogger(User.class);

    @PathParam("user")
    protected String username;
    private InterWebPrincipal targetPrincipal;

    @POST
    @Path("/services/{service}/auth")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public AuthorizationLinkResponse authenticateOnService(@PathParam("service") String connectorName, @QueryParam("callback") String callback,
        @FormParam("username") String userName, @FormParam("password") String password) {
        Engine engine = Environment.getInstance().getEngine();

        ServiceConnector connector = engine.getConnector(connectorName);
        if (connector == null) {
            throw new WebApplicationException("Service unknown", Response.Status.BAD_REQUEST);
        }

        InterWebPrincipal principal = getPrincipal();
        if (principal == null) {
            throw new WebApplicationException("User does not exist", Response.Status.UNAUTHORIZED);
        }

        InterWebPrincipal targetPrincipal = getTargetPrincipal();
        if (!principal.equals(targetPrincipal)) {
            throw new WebApplicationException("Token is not authorized", Response.Status.UNAUTHORIZED);
        }

        String baseApiUrl = getBaseUri().resolve("..").toASCIIString();
        Parameters parameters = new Parameters();
        parameters.add(Parameters.IWJ_USER_ID, principal.getName());
        parameters.add(Parameters.IWJ_CONNECTOR_ID, connector.getName());
        parameters.add(Parameters.CLIENT_TYPE, "rest");
        String interwebjCallbackUrl = connector.generateCallbackUrl(baseApiUrl, parameters);
        log.info("interwebjCallbackUrl: [" + interwebjCallbackUrl + "]");

        try {
            Parameters params = connector.authenticate(interwebjCallbackUrl, parameters);
            if (userName != null) {
                params.add(Parameters.USER_KEY, userName);
            }
            if (password != null) {
                params.add(Parameters.USER_SECRET, password);
            }
            if (connectorName.equalsIgnoreCase("facebook")) {
                params.add(Parameters.USER_KEY, connector.getAuthCredentials().getKey());
                params.add(Parameters.USER_SECRET, connector.getAuthCredentials().getSecret());
            }
            String authorizationUrl = params.get(Parameters.AUTHORIZATION_URL);
            if (authorizationUrl != null) {
                log.info("redirecting to service authorization url: " + authorizationUrl);
                OAuth1Parameters oauthParameters = getOAuthParameters();
                params.add(Parameters.CONSUMER_KEY, oauthParameters.getConsumerKey());
                if (callback != null) {
                    params.add(Parameters.CALLBACK, callback);
                }
                engine.addPendingAuthorizationConnector(principal, connector, params);
                AuthorizationLinkResponse response = new AuthorizationLinkResponse();
                AuthorizationLinkEntity linkEntity = new AuthorizationLinkEntity("GET", authorizationUrl);
                response.setAuthorizationLinkEntity(linkEntity);
                return response;
            }
        } catch (InterWebException e) {
            log.error(e);
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }

        throw new WebApplicationException("Authentication on service failed", Response.Status.BAD_REQUEST);
    }

    @GET
    @Path("/services/{service}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public ServiceResponse getService(@PathParam("service") String serviceName) {
        ServiceEntity serviceEntity = Services.createServiceEntity(serviceName, getBaseUri().toASCIIString(), getTargetPrincipal());
        ServiceResponse serviceResponse = new ServiceResponse();
        serviceResponse.setServiceEntity(serviceEntity);
        return serviceResponse;
    }

    @GET
    @Path("/services")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public ServicesResponse getServices() {
        List<ServiceEntity> serviceEntities = Services.createServiceEntities(getBaseUri().toASCIIString(), getTargetPrincipal());
        ServicesResponse servicesResponse = new ServicesResponse();
        servicesResponse.setServiceEntities(serviceEntities);
        return servicesResponse;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getUsername() {
        InterWebPrincipal targetPrincipal = getTargetPrincipal();
        return targetPrincipal.getName();
    }

    @POST
    @Path("/mediator")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response registerUser(@FormParam("mediator_token") String mediatorToken) {
        Database database = Environment.getInstance().getDatabase();

        InterWebPrincipal mediator = database.readPrincipalByKey(mediatorToken);
        if (mediator == null) {
            throw new WebApplicationException("No account for this token", Response.Status.BAD_REQUEST);
        }

        InterWebPrincipal principal = getPrincipal();
        if (principal == null || !principal.equals(getTargetPrincipal())) {
            throw new WebApplicationException("API call not authorized for user", Response.Status.FORBIDDEN);
        }

        database.saveMediator(principal.getName(), mediator.getName());
        return Response.status(Response.Status.CREATED).build();
    }

    @DELETE
    @Path("/mediator")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response removeMediator() {
        Database database = Environment.getInstance().getDatabase();

        InterWebPrincipal principal = getPrincipal();
        if (principal == null || !principal.equals(getTargetPrincipal())) {
            throw new WebApplicationException("API call not authorized for user", Response.Status.FORBIDDEN);
        }

        database.deleteMediator(principal.getName());
        return Response.ok().build();
    }

    @DELETE
    @Path("/services/{service}/auth")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public ServiceResponse revokeAuthorizationOnService(@PathParam("service") String serviceName) {
        log.info("revoking user authentication");
        InterWebPrincipal principal = getTargetPrincipal();
        Engine engine = Environment.getInstance().getEngine();

        ServiceConnector connector = engine.getConnector(serviceName);
        if (connector == null) {
            throw new WebApplicationException("Service unknown", Response.Status.BAD_REQUEST);
        }

        engine.setUserAuthCredentials(connector.getName(), principal, null, null);
        try {
            connector.revokeAuthentication();
        } catch (InterWebException e) {
            log.error(e);
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }

        ServiceEntity serviceEntity = Services.createServiceEntity(serviceName, getBaseUri().toASCIIString(), principal);
        serviceEntity.setMessage("Authorization successfully revoked");
        ServiceResponse serviceResponse = new ServiceResponse();
        serviceResponse.setServiceEntity(serviceEntity);
        return serviceResponse;
    }

    private InterWebPrincipal getTargetPrincipal() {
        if (targetPrincipal == null) {
            if (username == null || username.equals("default")) {
                targetPrincipal = getPrincipal();
            } else {
                Database database = Environment.getInstance().getDatabase();
                targetPrincipal = database.readPrincipalByName(username);
            }
            if (targetPrincipal == null) {
                throw new WebApplicationException("User does not exist", Response.Status.BAD_REQUEST);
            }
        }
        return targetPrincipal;
    }
}
