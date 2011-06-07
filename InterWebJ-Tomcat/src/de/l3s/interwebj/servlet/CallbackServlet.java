package de.l3s.interwebj.servlet;


import java.io.*;
import java.net.*;
import java.nio.charset.*;

import javax.faces.application.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.ws.rs.core.*;

import com.sun.jersey.api.client.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.db.*;
import de.l3s.interwebj.jaxb.services.*;
import de.l3s.interwebj.rest.*;
import de.l3s.interwebj.util.*;
import de.l3s.interwebj.webutil.*;


/**
 * Servlet implementation class Logout
 */
public class CallbackServlet
    extends HttpServlet
{
	
	private static final long serialVersionUID = 6534209215912582685L;
	

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CallbackServlet()
	{
		super();
	}
	

	public void process(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException
	{
		Environment.logger.debug("query string: [" + request.getQueryString()
		                         + "]");
		Parameters parameters = new Parameters();
		parameters.addMultivaluedParams(request.getParameterMap());
		refineParameters(parameters);
		try
		{
			InterWebPrincipal principal = getPrincipal(parameters);
			ServiceConnector connector = getConnector(parameters);
			Engine engine = Environment.getInstance().getEngine();
			parameters = engine.processAuthenticationCallback(principal,
			                                                  connector,
			                                                  parameters);
			if (parameters != null
			    && parameters.hasParameter(Parameters.CLIENT_TYPE)
			    && parameters.get(Parameters.CLIENT_TYPE).equals("REST"))
			{
				String callback = parameters.get(Parameters.CALLBACK);
				if (callback != null)
				{
					response.sendRedirect(callback);
					return;
				}
				String userToken = parameters.get(Parameters.TOKEN);
				String consumerKey = parameters.get(Parameters.CONSUMER_KEY);
				Database database = Environment.getInstance().getDatabase();
				AuthCredentials consumerAuthCredentials = database.readConsumerByKey(consumerKey).getAuthCredentials();
				AuthCredentials userAuthCredentials = database.readPrincipalByKey(userToken).getOauthCredentials();
				URI baseUri = URI.create(request.getRequestURL().toString()).resolve(".");
				String serviceApiPath = baseUri.toASCIIString()
				                        + "api/users/default/services/"
				                        + connector.getName();
				WebResource webResource = Endpoint.createWebResource(serviceApiPath,
				                                                     consumerAuthCredentials,
				                                                     userAuthCredentials);
				ClientResponse clientResponse = webResource.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
				CoreUtils.printClientResponse(clientResponse);
				ServiceResponse serviceResponse = clientResponse.getEntity(ServiceResponse.class);
				byte[] content = serviceResponse.toString().getBytes(Charset.forName("UTF8"));
				OutputStream os = response.getOutputStream();
				os.write(content);
				os.close();
				response.setContentType(MediaType.APPLICATION_XML);
				return;
			}
			response.sendRedirect(request.getContextPath()
			                      + "/view/services.xhtml");
		}
		catch (InterWebException e)
		{
			e.printStackTrace();
			Environment.logger.error(e);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Environment.logger.error(e);
		}
	}
	

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request,
	                     HttpServletResponse response)
	    throws ServletException, IOException
	{
		process(request, response);
	}
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
	                      HttpServletResponse response)
	    throws ServletException, IOException
	{
		process(request, response);
	}
	

	private String fetchParam(Parameters parameters, String paramName)
	{
		for (String parameter : parameters.keySet())
		{
			if (parameter.equals(paramName))
			{
				return parameters.get(parameter);
			}
		}
		return null;
	}
	

	private ServiceConnector getConnector(Parameters parameters)
	    throws InterWebException
	{
		String connectorName = fetchParam(parameters,
		                                  Parameters.IWJ_CONNECTOR_ID);
		if (connectorName == null)
		{
			throw new InterWebException("Unable to fetch connector name from the callback URL");
		}
		Engine engine = Environment.getInstance().getEngine();
		ServiceConnector connector = engine.getConnector(connectorName);
		if (connector == null)
		{
			throw new InterWebException("Connector [" + connectorName
			                            + "] not found");
		}
		return connector;
	}
	

	private InterWebPrincipal getPrincipal(Parameters parameters)
	    throws InterWebException
	{
		String userName = fetchParam(parameters, Parameters.IWJ_USER_ID);
		if (userName == null)
		{
			throw new InterWebException("Unable to fetch user name from the callback URL");
		}
		Database database = Environment.getInstance().getDatabase();
		InterWebPrincipal principal = database.readPrincipalByName(userName);
		if (principal == null)
		{
			throw new InterWebException("User [" + userName + "] not found");
		}
		return principal;
	}
	

	private void refineParameters(Parameters parameters)
	{
		Engine engine = Environment.getInstance().getEngine();
		for (ServiceConnector connector : engine.getConnectors())
		{
			Parameters refinedParameters = connector.getRefinedCallbackParameters(parameters);
			parameters.add(refinedParameters);
		}
	}
}
