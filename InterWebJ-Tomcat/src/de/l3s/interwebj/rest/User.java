package de.l3s.interwebj.rest;


import static de.l3s.interwebj.webutil.RestUtils.*;

import java.util.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.sun.jersey.api.client.*;
import com.sun.jersey.api.core.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.db.*;
import de.l3s.interwebj.jaxb.*;
import de.l3s.interwebj.jaxb.services.*;


@Path("/users/{user}")
public class User
    extends Endpoint
{
	
	@PathParam("user")
	protected String userName;
	private InterWebPrincipal targetPrincipal;
	

	@GET
	@Path("/services/{service}")
	@Produces(MediaType.APPLICATION_XML)
	public XMLResponse getService(@PathParam("service") String serviceName)
	{
		HttpContext httpContext = getHttpContext();
		ServiceEntity serviceEntity = Services.createServiceEntity(serviceName,
		                                                           httpContext,
		                                                           getTargetPrincipal());
		ServiceResponse serviceResponse = new ServiceResponse();
		serviceResponse.setServiceEntity(serviceEntity);
		return serviceResponse;
	}
	

	@GET
	@Path("/services")
	@Produces(MediaType.APPLICATION_XML)
	public XMLResponse getServices()
	{
		HttpContext httpContext = getHttpContext();
		List<ServiceEntity> serviceEntities = Services.createServiceEntities(httpContext,
		                                                                     getTargetPrincipal());
		ServicesResponse servicesResponse = new ServicesResponse();
		servicesResponse.setServiceEntities(serviceEntities);
		return servicesResponse;
	}
	

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getUserName()
	{
		InterWebPrincipal targetPrincipal = getTargetPrincipal();
		if (targetPrincipal != null)
		{
			return targetPrincipal.getName();
		}
		throwWebApplicationException(ErrorResponse.NO_USER);
		return null;
	}
	

	private InterWebPrincipal getTargetPrincipal()
	{
		if (targetPrincipal == null)
		{
			if (userName == null || userName.equals("default"))
			{
				targetPrincipal = getPrincipal();
			}
			else
			{
				Database database = Environment.getInstance().getDatabase();
				targetPrincipal = database.readPrincipalByName(userName);
			}
			if (targetPrincipal == null)
			{
				throwWebApplicationException(ErrorResponse.NO_USER);
			}
		}
		return targetPrincipal;
	}
	

	public static void main(String[] args)
	    throws Exception
	{
		testUserServices();
		testUserService();
		testUserInfo();
	}
	

	private static void testUserInfo()
	    throws Exception
	{
		AuthCredentials consumerCredentials = new AuthCredentials("***REMOVED***",
		                                                          "***REMOVED***");
		AuthCredentials userCredentials = new AuthCredentials("***REMOVED***",
		                                                      "***REMOVED***");
		WebResource resource = createWebResource("http://localhost:8181/InterWebJ/api/users/default",
		                                         consumerCredentials,
		                                         userCredentials);
		System.out.println("querying InterWebJ URL: " + resource.toString());
		ClientResponse response = resource.get(ClientResponse.class);
		String responseContent = response.getEntity(String.class);
		System.out.println(responseContent);
	}
	

	private static void testUserService()
	    throws Exception
	{
		AuthCredentials consumerCredentials = new AuthCredentials("***REMOVED***",
		                                                          "***REMOVED***");
		AuthCredentials userCredentials = new AuthCredentials("***REMOVED***",
		                                                      "***REMOVED***");
		WebResource resource = createWebResource("http://localhost:8181/InterWebJ/api/users/olex/services/flickr",
		                                         consumerCredentials,
		                                         userCredentials);
		System.out.println("querying InterWebJ URL: " + resource.toString());
		ClientResponse response = resource.get(ClientResponse.class);
		ServiceResponse serviceResponse = response.getEntity(ServiceResponse.class);
		System.out.println(serviceResponse);
	}
	

	private static void testUserServices()
	    throws Exception
	{
		AuthCredentials consumerCredentials = new AuthCredentials("***REMOVED***",
		                                                          "***REMOVED***");
		AuthCredentials userCredentials = new AuthCredentials("***REMOVED***",
		                                                      "***REMOVED***");
		WebResource resource = createWebResource("http://localhost:8181/InterWebJ/api/users/olex/services",
		                                         consumerCredentials,
		                                         userCredentials);
		System.out.println("querying InterWebJ URL: " + resource.toString());
		ClientResponse response = resource.get(ClientResponse.class);
		ServicesResponse servicesResponse = response.getEntity(ServicesResponse.class);
		System.out.println(servicesResponse);
	}
	
}
