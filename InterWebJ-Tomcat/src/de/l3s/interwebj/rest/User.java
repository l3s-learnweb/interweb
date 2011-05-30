package de.l3s.interwebj.rest;


import static de.l3s.interwebj.webutil.RestUtils.*;

import java.awt.*;
import java.net.*;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.sun.jersey.api.client.*;
import com.sun.jersey.api.core.*;
import com.sun.jersey.core.util.*;
import com.sun.jersey.oauth.signature.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.core.ServiceConnector.PermissionLevel;
import de.l3s.interwebj.db.*;
import de.l3s.interwebj.jaxb.*;
import de.l3s.interwebj.jaxb.services.*;
import de.l3s.interwebj.util.*;


@Path("/users/{user}")
public class User
    extends Endpoint
{
	
	@PathParam("user")
	protected String userName;
	private InterWebPrincipal targetPrincipal;
	

	@POST
	@Path("/services/{service}/auth")
	@Produces(MediaType.APPLICATION_XML)
	public XMLResponse authenticateOnService(@PathParam("service") String serviceName,
	                                         @QueryParam("callback") String callback)
	{
		Engine engine = Environment.getInstance().getEngine();
		ServiceConnector connector = engine.getConnector(serviceName);
		if (connector == null)
		{
			return ErrorResponse.UNKNOWN_SERVICE;
		}
		HttpContext httpContext = getHttpContext();
		String baseApiUrl = httpContext.getUriInfo().getBaseUri().resolve("..").toASCIIString();
		try
		{
			Parameters params = connector.authenticate(PermissionLevel.DELETE,
			                                           baseApiUrl + "callback");
			String oauthAuthorizationUrl = params.get(Parameters.OAUTH_AUTHORIZATION_URL);
			if (oauthAuthorizationUrl != null)
			{
				Environment.logger.debug("redirecting to service authorization url: "
				                         + oauthAuthorizationUrl);
				params.add(Parameters.CLIENT_TYPE, "REST");
				OAuthParameters oauthParameters = getOAuthParameters();
				params.add(Parameters.CONSUMER_KEY,
				           oauthParameters.getConsumerKey());
				params.add(Parameters.TOKEN, oauthParameters.getToken());
				if (callback != null)
				{
					params.add(Parameters.CALLBACK, callback);
				}
				
				engine.addPendingAuthorizationConnector(connector, params);
				AuthorizationLinkResponse response = new AuthorizationLinkResponse();
				AuthorizationLinkEntity linkEntity = new AuthorizationLinkEntity("GET",
				                                                                 oauthAuthorizationUrl);
				response.setAuthorizationLinkEntity(linkEntity);
				return response;
			}
		}
		catch (InterWebException e)
		{
			e.printStackTrace();
			Environment.logger.error(e);
			return new ErrorResponse(999, e.getMessage());
		}
		return ErrorResponse.AUTHENTICATION_FAILED;
	}
	

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
		System.out.println(serviceResponse);
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
	

	@POST
	@Path("/mediator")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_XML)
	public XMLResponse registerUser(@FormParam("mediator_token") String token)
	{
		System.out.println("set");
		Database database = Environment.getInstance().getDatabase();
		InterWebPrincipal mediator = database.readPrincipalByKey(token);
		if (mediator == null)
		{
			return ErrorResponse.NO_ACCOUNT_FOR_TOKEN;
		}
		InterWebPrincipal principal = getPrincipal();
		if (principal == null || !principal.equals(getTargetPrincipal()))
		{
			return ErrorResponse.NOT_AUTHORIZED;
		}
		database.saveMediator(principal.getName(), mediator.getName());
		return new OkResponse();
	}
	

	@DELETE
	@Path("/mediator")
	@Produces(MediaType.APPLICATION_XML)
	public XMLResponse removeMediator()
	{
		System.out.println("remove");
		Database database = Environment.getInstance().getDatabase();
		InterWebPrincipal principal = getPrincipal();
		if (principal == null || !principal.equals(getTargetPrincipal()))
		{
			return ErrorResponse.NOT_AUTHORIZED;
		}
		database.deleteMediator(principal.getName());
		return new OkResponse();
	}
	

	@DELETE
	@Path("/services/{service}/auth")
	@Produces(MediaType.APPLICATION_XML)
	public XMLResponse revokeAuthorizationOnService(@PathParam("service") String serviceName)
	{
		Environment.logger.debug("revoking user authentication");
		InterWebPrincipal principal = getTargetPrincipal();
		if (principal == null)
		{
			return ErrorResponse.NO_TOKEN_GIVEN;
		}
		Engine engine = Environment.getInstance().getEngine();
		ServiceConnector connector = engine.getConnector(serviceName);
		if (connector == null)
		{
			return ErrorResponse.UNKNOWN_SERVICE;
		}
		engine.setUserAuthCredentials(connector.getName(),
		                              principal,
		                              null,
		                              null);
		try
		{
			connector.revokeAuthentication();
		}
		catch (InterWebException e)
		{
			e.printStackTrace();
			Environment.logger.error(e);
			return new ErrorResponse(999, e.getMessage());
		}
		HttpContext httpContext = getHttpContext();
		ServiceEntity serviceEntity = Services.createServiceEntity(serviceName,
		                                                           httpContext,
		                                                           principal);
		serviceEntity.setMessage("Authorization successfully revoked");
		ServiceResponse serviceResponse = new ServiceResponse();
		serviceResponse.setServiceEntity(serviceEntity);
		return serviceResponse;
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
		//				testUserService("flickr");
		//		testRevokeService("youtube");
		//		testAuthService("interweb");
		//		testRemoveMediator();
		//		testSetMediator();
		//		testUserService("flickr");
		//		testUserInfo();
	}
	

	@SuppressWarnings("all")
	private static void testAuthService(String connectorName)
	    throws Exception
	{
		AuthCredentials consumerCredentials = new AuthCredentials("***REMOVED***",
		                                                          "***REMOVED***");
		AuthCredentials userCredentials = new AuthCredentials("***REMOVED***",
		                                                      "***REMOVED***");
		WebResource resource = createWebResource("http://localhost:8181/InterWebJ/api/users/default/services/"
		                                             + connectorName + "/auth",
		                                         consumerCredentials,
		                                         userCredentials);
		System.out.println("querying InterWebJ URL: " + resource.toString());
		ClientResponse response = resource.post(ClientResponse.class);
		CoreUtils.printClientResponse(response);
		AuthorizationLinkResponse authorizationLinkResponse = response.getEntity(AuthorizationLinkResponse.class);
		String location = authorizationLinkResponse.getAuthorizationLinkEntity().getLink();
		System.out.println("redirecting to: [" + location + "]");
		Desktop.getDesktop().browse(URI.create(location));
		System.out.println(authorizationLinkResponse);
	}
	

	@SuppressWarnings("all")
	private static void testRemoveMediator()
	    throws Exception
	{
		AuthCredentials consumerCredentials = new AuthCredentials("***REMOVED***",
		                                                          "***REMOVED***");
		AuthCredentials mediatorCredentials = new AuthCredentials("***REMOVED***",
		                                                          "***REMOVED***");
		AuthCredentials userCredentials = new AuthCredentials("***REMOVED***",
		                                                      "***REMOVED***");
		WebResource resource = createWebResource("http://localhost:8181/InterWebJ/api/users/default/mediator",
		                                         consumerCredentials,
		                                         userCredentials);
		System.out.println("querying InterWebJ URL: " + resource.toString());
		ClientResponse response = resource.delete(ClientResponse.class);
		OkResponse servicesResponse = response.getEntity(OkResponse.class);
		System.out.println(servicesResponse);
	}
	

	@SuppressWarnings("all")
	private static void testRevokeService(String connectorName)
	    throws Exception
	{
		AuthCredentials consumerCredentials = new AuthCredentials("***REMOVED***",
		                                                          "***REMOVED***");
		AuthCredentials userCredentials = new AuthCredentials("***REMOVED***",
		                                                      "***REMOVED***");
		WebResource resource = createWebResource("http://localhost:8181/InterWebJ/api/users/default/services/"
		                                             + connectorName + "/auth",
		                                         consumerCredentials,
		                                         userCredentials);
		System.out.println("querying InterWebJ URL: " + resource.toString());
		ClientResponse response = resource.delete(ClientResponse.class);
		ServiceResponse serviceResponse = response.getEntity(ServiceResponse.class);
		System.out.println(serviceResponse);
	}
	

	@SuppressWarnings("all")
	private static void testSetMediator()
	    throws Exception
	{
		AuthCredentials consumerCredentials = new AuthCredentials("***REMOVED***",
		                                                          "***REMOVED***");
		AuthCredentials mediatorCredentials = new AuthCredentials("***REMOVED***",
		                                                          "***REMOVED***");
		AuthCredentials userCredentials = new AuthCredentials("***REMOVED***",
		                                                      "***REMOVED***");
		WebResource resource = createWebResource("http://localhost:8181/InterWebJ/api/users/default/mediator",
		                                         consumerCredentials,
		                                         userCredentials);
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();
		params.add("mediator_token", mediatorCredentials.getKey());
		System.out.println("querying InterWebJ URL: " + resource.toString());
		ClientResponse response = resource.type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class,
		                                                                                    params);
		OkResponse servicesResponse = response.getEntity(OkResponse.class);
		System.out.println(servicesResponse);
	}
	

	@SuppressWarnings("all")
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
	

	@SuppressWarnings("all")
	private static void testUserService(String connectorName)
	    throws Exception
	{
		AuthCredentials consumerCredentials = new AuthCredentials("***REMOVED***",
		                                                          "***REMOVED***");
		AuthCredentials userCredentials = new AuthCredentials("***REMOVED***",
		                                                      "***REMOVED***");
		WebResource resource = createWebResource("http://localhost:8181/InterWebJ/api/users/default/services/"
		                                             + connectorName,
		                                         consumerCredentials,
		                                         userCredentials);
		System.out.println("querying InterWebJ URL: " + resource.toString());
		ClientResponse response = resource.get(ClientResponse.class);
		ServiceResponse serviceResponse = response.getEntity(ServiceResponse.class);
		System.out.println(serviceResponse);
	}
	

	@SuppressWarnings("all")
	private static void testUserServices()
	    throws Exception
	{
		AuthCredentials consumerCredentials = new AuthCredentials("***REMOVED***",
		                                                          "***REMOVED***");
		//		AuthCredentials userCredentials = new AuthCredentials("***REMOVED***",
		//		                                                      "***REMOVED***");
		AuthCredentials userCredentials = new AuthCredentials("***REMOVED***",
		                                                      "***REMOVED***");
		WebResource resource = createWebResource("http://localhost:8181/InterWebJ/api/users/default/services",
		                                         consumerCredentials,
		                                         userCredentials);
		System.out.println("querying InterWebJ URL: " + resource.toString());
		ClientResponse response = resource.get(ClientResponse.class);
		ServicesResponse servicesResponse = response.getEntity(ServicesResponse.class);
		System.out.println(servicesResponse);
	}
	
}
