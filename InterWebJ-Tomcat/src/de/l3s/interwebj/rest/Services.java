package de.l3s.interwebj.rest;

import static de.l3s.interwebj.webutil.RestUtils.throwWebApplicationException;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;

import com.sun.jersey.api.core.HttpContext;

import de.l3s.interwebj.core.Engine;
import de.l3s.interwebj.core.Environment;
import de.l3s.interwebj.core.InterWebPrincipal;
import de.l3s.interwebj.core.ServiceConnector;
import de.l3s.interwebj.db.Database;
import de.l3s.interwebj.jaxb.ErrorResponse;
import de.l3s.interwebj.jaxb.XMLResponse;
import de.l3s.interwebj.jaxb.services.AuthorizationEntity;
import de.l3s.interwebj.jaxb.services.AuthorizationLinkEntity;
import de.l3s.interwebj.jaxb.services.ServiceEntity;
import de.l3s.interwebj.jaxb.services.ServicesResponse;

@Path("/services")
public class Services extends Endpoint
{

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public XMLResponse getServices()
    {
	HttpContext httpContext = getHttpContext();
	List<ServiceEntity> serviceEntities = createServiceEntities(httpContext, getPrincipal());
	ServicesResponse servicesResponse = new ServicesResponse();
	servicesResponse.setServiceEntities(serviceEntities);
	return servicesResponse;
    }

    public static List<ServiceEntity> createServiceEntities(HttpContext httpContext, InterWebPrincipal principal)
    {
	List<ServiceEntity> serviceEntities = new ArrayList<ServiceEntity>();
	Engine engine = Environment.getInstance().getEngine();
	List<ServiceConnector> connectors = engine.getConnectors();
	for(ServiceConnector connector : connectors)
	{
	    if(connector.isRegistered())
	    {
		ServiceEntity serviceEntity = createServiceEntity(httpContext, principal, connector);
		serviceEntities.add(serviceEntity);
	    }
	}
	return serviceEntities;

    }

    public static ServiceEntity createServiceEntity(HttpContext httpContext, InterWebPrincipal principal, ServiceConnector connector)
    {
	Engine engine = Environment.getInstance().getEngine();
	boolean authenticated = engine.isUserAuthenticated(connector, principal);
	AuthorizationEntity authorizationEntity = createAuthorizationEntity(httpContext, connector, authenticated);
	ServiceEntity serviceEntity = new ServiceEntity(authorizationEntity, authenticated);
	serviceEntity.setId(connector.getName());
	serviceEntity.setTitle(connector.getName());
	serviceEntity.setMediaTypes(StringUtils.join(connector.getContentTypes(), ','));
	Database database = Environment.getInstance().getDatabase();
	if(principal != null)
	{
	    String userId = database.readConnectorUserId(connector.getName(), principal.getName());
	    serviceEntity.setServiceUserId(userId);
	}
	return serviceEntity;

    }

    public static ServiceEntity createServiceEntity(String serviceName, HttpContext httpContext, InterWebPrincipal principal)
    {
	List<ServiceEntity> serviceEntities = new ArrayList<ServiceEntity>();
	Engine engine = Environment.getInstance().getEngine();
	ServiceConnector connector = engine.getConnector(serviceName);
	if(connector == null)
	{
	    throwWebApplicationException(ErrorResponse.UNKNOWN_SERVICE);
	}
	ServiceEntity serviceEntity = createServiceEntity(httpContext, principal, connector);
	serviceEntities.add(serviceEntity);
	return serviceEntity;
    }

    private static AuthorizationEntity createAuthorizationEntity(HttpContext httpContext, ServiceConnector connector, boolean isAuthenticated)
    {
	AuthorizationEntity authorizationEntity = new AuthorizationEntity();
	if(connector.isUserRegistrationDataRequired())
	{
	    authorizationEntity.setType("login");
	    authorizationEntity.addParameter("text", "username");
	    authorizationEntity.addParameter("password", "password");
	}
	else
	{
	    authorizationEntity.setType("token");
	}
	authorizationEntity.setAuthorizationLinkEntity(createAuthorizationLinkEntity(httpContext, connector, isAuthenticated));
	return authorizationEntity;
    }

    private static AuthorizationLinkEntity createAuthorizationLinkEntity(HttpContext httpContext, ServiceConnector connector, boolean isAuthenticated)
    {
	String baseUri = httpContext.getRequest().getBaseUri().toASCIIString();
	String link = baseUri + "users/default/services/" + connector.getName() + "/auth";
	String method = isAuthenticated ? "DELETE" : "POST";
	return new AuthorizationLinkEntity(method, link);
    }
}
