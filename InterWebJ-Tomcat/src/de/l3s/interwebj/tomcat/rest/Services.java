package de.l3s.interwebj.tomcat.rest;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import de.l3s.interwebj.core.connector.ServiceConnector;
import de.l3s.interwebj.core.core.Engine;
import de.l3s.interwebj.core.core.Environment;
import de.l3s.interwebj.core.db.Database;
import de.l3s.interwebj.tomcat.jaxb.services.AuthorizationEntity;
import de.l3s.interwebj.tomcat.jaxb.services.AuthorizationLinkEntity;
import de.l3s.interwebj.tomcat.jaxb.services.ServiceEntity;
import de.l3s.interwebj.tomcat.jaxb.services.ServicesResponse;

@Path("/services")
public class Services extends Endpoint {

    public static List<ServiceEntity> createServiceEntities(String baseUri, Principal principal) {
        List<ServiceEntity> serviceEntities = new ArrayList<>();
        Engine engine = Environment.getInstance().getEngine();
        List<ServiceConnector> connectors = engine.getConnectors();
        for (ServiceConnector connector : connectors) {
            if (connector.isRegistered()) {
                ServiceEntity serviceEntity = createServiceEntity(baseUri, principal, connector);
                serviceEntities.add(serviceEntity);
            }
        }
        return serviceEntities;
    }

    public static ServiceEntity createServiceEntity(String baseUri, Principal principal, ServiceConnector connector) {
        Engine engine = Environment.getInstance().getEngine();
        boolean authenticated = engine.isUserAuthenticated(connector, principal);
        AuthorizationEntity authorizationEntity = createAuthorizationEntity(baseUri, connector, authenticated);
        ServiceEntity serviceEntity = new ServiceEntity(authorizationEntity, authenticated);
        serviceEntity.setId(connector.getName());
        serviceEntity.setTitle(connector.getName());
        serviceEntity.setMediaTypes(StringUtils.join(connector.getContentTypes(), ','));
        Database database = Environment.getInstance().getDatabase();
        if (principal != null) {
            String userId = database.readConnectorUserId(connector.getName(), principal.getName());
            serviceEntity.setServiceUserId(userId);
        }
        return serviceEntity;
    }

    public static ServiceEntity createServiceEntity(String serviceName, String baseUri, Principal principal) {
        Engine engine = Environment.getInstance().getEngine();
        ServiceConnector connector = engine.getConnector(serviceName);
        if (connector == null) {
            throw new WebApplicationException("Service unknown", Response.Status.BAD_REQUEST);
        }
        return createServiceEntity(baseUri, principal, connector);
    }

    private static AuthorizationEntity createAuthorizationEntity(String baseUri, ServiceConnector connector, boolean isAuthenticated) {
        AuthorizationEntity authorizationEntity = new AuthorizationEntity();
        if (connector.isUserRegistrationDataRequired()) {
            authorizationEntity.setType("login");
            authorizationEntity.addParameter("text", "username");
            authorizationEntity.addParameter("password", "password");
        } else {
            authorizationEntity.setType("token");
        }
        authorizationEntity.setAuthorizationLinkEntity(createAuthorizationLinkEntity(baseUri, connector, isAuthenticated));
        return authorizationEntity;
    }

    private static AuthorizationLinkEntity createAuthorizationLinkEntity(String baseUri, ServiceConnector connector, boolean isAuthenticated) {
        String link = baseUri + "users/default/services/" + connector.getName() + "/auth";
        String method = isAuthenticated ? "DELETE" : "POST";
        return new AuthorizationLinkEntity(method, link);
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public ServicesResponse getServices() {
        List<ServiceEntity> serviceEntities = createServiceEntities(getBaseUri().toASCIIString(), getPrincipal());
        ServicesResponse servicesResponse = new ServicesResponse();
        servicesResponse.setServiceEntities(serviceEntities);
        return servicesResponse;
    }
}
