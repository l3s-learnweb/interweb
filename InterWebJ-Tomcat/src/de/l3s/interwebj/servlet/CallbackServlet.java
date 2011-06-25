package de.l3s.interwebj.servlet;


import java.io.*;
import java.net.*;
import java.nio.charset.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.ws.rs.core.*;

import org.apache.commons.lang.*;

import com.sun.jersey.api.client.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.db.*;
import de.l3s.interwebj.jaxb.*;
import de.l3s.interwebj.jaxb.services.*;
import de.l3s.interwebj.rest.*;
import de.l3s.interwebj.util.*;


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
	    throws ServletException, IOException
	{
		Environment.logger.info("query string: [" + request.getQueryString()
		                        + "]");
		Parameters parameters = new Parameters();
		parameters.addMultivaluedParams(request.getParameterMap());
		refineParameters(parameters);
		InterWebPrincipal principal = null;
		ServiceConnector connector = null;
		String clientType = null;
		try
		{
			principal = getPrincipal(parameters);
			connector = getConnector(parameters);
			clientType = getClientType(parameters);
			Engine engine = Environment.getInstance().getEngine();
			engine.processAuthenticationCallback(principal,
			                                     connector,
			                                     parameters);
		}
		catch (InterWebException e)
		{
			e.printStackTrace();
			Environment.logger.severe(e.getMessage());
			parameters.add(Parameters.ERROR, e.getMessage());
		}
		if ("rest".equals(clientType))
		{
			processRestRequest(request,
			                   response,
			                   principal,
			                   connector,
			                   parameters);
		}
		else if ("servlet".equals(clientType))
		{
			processServletRequest(request, response, parameters);
		}
		else
		{
			throw new ServletException("No valid client type parameter found in URL ["
			                           + clientType + "]");
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
	

	private String getClientType(Parameters parameters)
	    throws InterWebException
	{
		String clientType = fetchParam(parameters, Parameters.CLIENT_TYPE);
		if (clientType == null)
		{
			throw new InterWebException("Unable to fetch client type from the callback URL");
		}
		return clientType;
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
	

	private void processRestRequest(HttpServletRequest request,
	                                HttpServletResponse response,
	                                InterWebPrincipal principal,
	                                ServiceConnector connector,
	                                Parameters parameters)
	    throws ServletException, IOException
	{
		String callback = parameters.get(Parameters.CALLBACK);
		Environment.logger.info("callback: [" + callback + "]");
		if (StringUtils.isNotEmpty(callback))
		{
			response.sendRedirect(callback);
			return;
		}
		if (parameters.hasParameter(Parameters.ERROR))
		{
			ErrorResponse errorResponse = new ErrorResponse(999,
			                                                parameters.get(Parameters.ERROR));
			writeIntoServletResponse(response, errorResponse);
			return;
		}
		String consumerKey = parameters.get(Parameters.CONSUMER_KEY);
		Database database = Environment.getInstance().getDatabase();
		AuthCredentials consumerAuthCredentials = database.readConsumerByKey(consumerKey).getAuthCredentials();
		AuthCredentials userAuthCredentials = principal.getOauthCredentials();
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
		writeIntoServletResponse(response, serviceResponse);
		return;
	}
	

	private void processServletRequest(HttpServletRequest request,
	                                   HttpServletResponse response,
	                                   Parameters parameters)
	    throws ServletException, IOException
	{
		String redirectUrl = request.getContextPath() + "/view/services.xhtml";
		if (parameters.hasParameter(Parameters.ERROR))
		{
			String error = parameters.get(Parameters.ERROR);
			redirectUrl += "?error=" + error;
		}
		response.sendRedirect(redirectUrl);
	}
	

	private void refineParameters(Parameters parameters)
	{
		Engine engine = Environment.getInstance().getEngine();
		for (ServiceConnector connector : engine.getConnectors())
		{
			Parameters refinedParameters = connector.getRefinedCallbackParameters(parameters);
			parameters.add(refinedParameters, true);
		}
	}
	

	private void writeIntoServletResponse(HttpServletResponse servletResponse,
	                                      XMLResponse xmlResponse)
	    throws IOException
	{
		byte[] content = xmlResponse.toString().getBytes(Charset.forName("UTF8"));
		OutputStream os = servletResponse.getOutputStream();
		os.write(content);
		os.close();
		servletResponse.setContentType(MediaType.APPLICATION_XML);
	}
}
