package de.l3s.interwebj.rest;


import static de.l3s.interwebj.webutil.RestUtils.*;

import java.util.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.apache.commons.lang.*;

import com.sun.jersey.api.client.*;
import com.sun.jersey.api.core.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.db.*;
import de.l3s.interwebj.jaxb.*;
import de.l3s.interwebj.jaxb.services.*;


@Path("/services")
public class Services
    extends Endpoint
{
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public XMLResponse getServices()
	{
		HttpContext httpContext = getHttpContext();
		List<ServiceEntity> serviceEntities = createServiceEntities(httpContext,
		                                                            getPrincipal());
		ServicesResponse servicesResponse = new ServicesResponse();
		servicesResponse.setServiceEntities(serviceEntities);
		System.out.println(servicesResponse);
		return servicesResponse;
	}
	

	public static List<ServiceEntity> createServiceEntities(HttpContext httpContext,
	                                                        InterWebPrincipal principal)
	{
		List<ServiceEntity> serviceEntities = new ArrayList<ServiceEntity>();
		Engine engine = Environment.getInstance().getEngine();
		List<ServiceConnector> connectors = engine.getConnectors();
		for (ServiceConnector connector : connectors)
		{
			if (connector.isConnectorRegistered())
			{
				ServiceEntity serviceEntity = createServiceEntity(httpContext,
				                                                  principal,
				                                                  connector);
				serviceEntities.add(serviceEntity);
			}
		}
		return serviceEntities;
		
	}
	

	public static ServiceEntity createServiceEntity(HttpContext httpContext,
	                                                InterWebPrincipal principal,
	                                                ServiceConnector connector)
	{
		Engine engine = Environment.getInstance().getEngine();
		boolean authenticated = engine.isUserAuthenticated(connector, principal);
		AuthorizationEntity authorizationEntity = createAuthorizationEntity(httpContext,
		                                                                    connector,
		                                                                    authenticated);
		ServiceEntity serviceEntity = new ServiceEntity(authorizationEntity,
		                                                authenticated);
		serviceEntity.setId(connector.getName());
		serviceEntity.setTitle(connector.getName());
		serviceEntity.setMediaTypes(StringUtils.join(connector.getContentTypes(),
		                                             ','));
		Database database = Environment.getInstance().getDatabase();
		if (principal != null)
		{
			String userId = database.readConnectorUserId(connector.getName(),
			                                             principal.getName());
			serviceEntity.setServiceUserId(userId);
		}
		return serviceEntity;
		
	}
	

	public static ServiceEntity createServiceEntity(String serviceName,
	                                                HttpContext httpContext,
	                                                InterWebPrincipal principal)
	{
		List<ServiceEntity> serviceEntities = new ArrayList<ServiceEntity>();
		Engine engine = Environment.getInstance().getEngine();
		ServiceConnector connector = engine.getConnector(serviceName);
		if (connector == null)
		{
			throwWebApplicationException(ErrorResponse.UNKNOWN_SERVICE);
		}
		ServiceEntity serviceEntity = createServiceEntity(httpContext,
		                                                  principal,
		                                                  connector);
		serviceEntities.add(serviceEntity);
		return serviceEntity;
	}
	

	public static void main(String[] args)
	    throws Exception
	{
		testServices();
	}
	

	private static AuthorizationEntity createAuthorizationEntity(HttpContext httpContext,
	                                                             ServiceConnector connector,
	                                                             boolean isAuthenticated)
	{
		AuthorizationEntity authorizationEntity;
		if (connector.isConnectorRegistrationDataRequired())
		{
			authorizationEntity = new TokenAuthorizationEntity();
		}
		else
		{
			LoginAuthorizationEntity loginAuthorizationEntity = new LoginAuthorizationEntity();
			loginAuthorizationEntity.addParameter("text", "username");
			loginAuthorizationEntity.addParameter("password", "password");
			authorizationEntity = loginAuthorizationEntity;
		}
		authorizationEntity.setAuthorizationLinkEntity(createAuthorizationLinkEntity(httpContext,
		                                                                             connector,
		                                                                             isAuthenticated));
		return authorizationEntity;
	}
	

	private static AuthorizationLinkEntity createAuthorizationLinkEntity(HttpContext httpContext,
	                                                                     ServiceConnector connector,
	                                                                     boolean isAuthenticated)
	{
		String baseUri = httpContext.getRequest().getBaseUri().toASCIIString();
		String link = baseUri + "users/default/services/" + connector.getName()
		              + "/auth";
		String method = isAuthenticated
		    ? "DELETE" : "POST";
		return new AuthorizationLinkEntity(method, link);
	}
	

	private static void testServices()
	    throws Exception
	{
		AuthCredentials consumerCredentials = new AuthCredentials("***REMOVED***",
		                                                          "***REMOVED***");
		//		AuthCredentials consumerCredentials = new AuthCredentials("***REMOVED***",
		//		"***REMOVED***");
		AuthCredentials userCredentials = null;
		//		userCredentials = new AuthCredentials("***REMOVED***",
		//		                                      "***REMOVED***");
		WebResource resource = createWebResource("http://localhost:8181/InterWebJ/api/services",
		                                         consumerCredentials,
		                                         userCredentials);
		System.out.println("querying InterWebJ URL: " + resource.toString());
		ClientResponse response = resource.get(ClientResponse.class);
		ServicesResponse servicesResponse = response.getEntity(ServicesResponse.class);
		System.out.println(servicesResponse);
	}
	
}
