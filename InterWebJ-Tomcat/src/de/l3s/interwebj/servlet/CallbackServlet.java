package de.l3s.interwebj.servlet;


import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.ws.rs.core.*;

import com.sun.jersey.api.client.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.bean.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.db.*;
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
	    throws ServletException, InterWebException
	{
		Map<String, String[]> params = request.getParameterMap();
		Environment.logger.debug("query string: [" + request.getQueryString()
		                         + "]");
		Engine engine = Environment.getInstance().getEngine();
		SessionBean sessionBean = (SessionBean) request.getSession().getAttribute("sessionBean");
		InterWebPrincipal principal = sessionBean.getPrincipal();
		if (principal == null)
		{
			
		}
		Parameters parameters = engine.processAuthenticationCallback(principal,
		                                                             params);
		try
		{
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
				String connectorName = parameters.get(Parameters.CONNECTOR_NAME);
				String userToken = parameters.get(Parameters.TOKEN);
				String consumerKey = parameters.get(Parameters.CONSUMER_KEY);
				Database database = Environment.getInstance().getDatabase();
				AuthCredentials consumerAuthCredentials = database.readConsumerByKey(consumerKey).getAuthCredentials();
				AuthCredentials userAuthCredentials = database.readPrincipalByKey(userToken).getOauthCredentials();
				URI baseUri = URI.create(request.getRequestURL().toString()).resolve(".");
				String serviceApiPath = baseUri.toASCIIString()
				                        + "api/users/default/services/"
				                        + connectorName;
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
		catch (IOException e)
		{
			throw new InterWebException(e);
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
		try
		{
			process(request, response);
		}
		catch (InterWebException e)
		{
			Environment.logger.error(e);
		}
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
		try
		{
			process(request, response);
		}
		catch (InterWebException e)
		{
		}
	}
	
}
