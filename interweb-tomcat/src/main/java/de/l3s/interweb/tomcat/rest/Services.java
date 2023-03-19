package de.l3s.interweb.tomcat.rest;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import de.l3s.interweb.core.search.SearchProvider;
import de.l3s.interweb.tomcat.app.Engine;
import de.l3s.interweb.tomcat.db.Database;
import de.l3s.interweb.tomcat.jaxb.services.AuthorizationEntity;
import de.l3s.interweb.tomcat.jaxb.services.AuthorizationLinkEntity;
import de.l3s.interweb.tomcat.jaxb.services.ServiceEntity;
import de.l3s.interweb.tomcat.jaxb.services.ServicesResponse;

@Path("/services")
public class Services extends Endpoint {

    @Inject
    private Engine engine;

    @Inject
    private Database database;

    public static List<ServiceEntity> createServiceEntities(Engine engine, Database database, String baseUri, Principal principal) {
        List<ServiceEntity> serviceEntities = new ArrayList<>();
        List<SearchProvider> connectors = engine.getSearchProviders();
        for (SearchProvider connector : connectors) {
            if (connector.isRegistered()) {
                ServiceEntity serviceEntity = createServiceEntity(engine, database, baseUri, principal, connector);
                serviceEntities.add(serviceEntity);
            }
        }
        return serviceEntities;
    }

    public static ServiceEntity createServiceEntity(Engine engine, Database database, String baseUri, Principal principal, SearchProvider connector) {
        boolean authenticated = engine.isUserAuthenticated(connector, principal);
        AuthorizationEntity authorizationEntity = createAuthorizationEntity(baseUri, connector, authenticated);
        ServiceEntity serviceEntity = new ServiceEntity(authorizationEntity, authenticated);
        serviceEntity.setId(connector.getName());
        serviceEntity.setTitle(connector.getName());
        serviceEntity.setMediaTypes(StringUtils.join(connector.getContentTypes(), ','));
        if (principal != null) {
            String userId = database.readConnectorUserId(connector.getName(), principal.getName());
            serviceEntity.setServiceUserId(userId);
        }
        return serviceEntity;
    }

    public static ServiceEntity createServiceEntity(Engine engine, Database database, String serviceName, String baseUri, Principal principal) {
        SearchProvider connector = engine.getConnector(serviceName);
        if (connector == null) {
            throw new WebApplicationException("Service unknown", Response.Status.BAD_REQUEST);
        }
        return createServiceEntity(engine, database, baseUri, principal, connector);
    }

    private static AuthorizationEntity createAuthorizationEntity(String baseUri, SearchProvider connector, boolean isAuthenticated) {
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

    private static AuthorizationLinkEntity createAuthorizationLinkEntity(String baseUri, SearchProvider connector, boolean isAuthenticated) {
        String link = baseUri + "users/default/services/" + connector.getName() + "/auth";
        String method = isAuthenticated ? "DELETE" : "POST";
        return new AuthorizationLinkEntity(method, link);
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public ServicesResponse getServices() {
        List<ServiceEntity> serviceEntities = createServiceEntities(engine, database, getBaseUri().toASCIIString(), getPrincipal());
        ServicesResponse servicesResponse = new ServicesResponse();
        servicesResponse.setServiceEntities(serviceEntities);
        return servicesResponse;
    }
}
