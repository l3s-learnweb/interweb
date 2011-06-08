package de.l3s.interwebj.rest;


import java.util.*;

import javax.servlet.http.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.apache.commons.lang.*;

import com.sun.jersey.api.client.*;
import com.sun.jersey.core.util.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.jaxb.*;


@Path("/embedded")
public class Embedded
    extends Endpoint
{
	
	@Context
	HttpServletRequest request;
	

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_XML)
	public XMLResponse getEmbedded(@FormParam("url") String url,
	                               @FormParam("max_width") int maxWidth,
	                               @FormParam("max_height") int maxHeight)
	{
		if (StringUtils.isEmpty(url))
		{
			return new ErrorResponse(999, "URL must not by null or empty");
		}
		InterWebPrincipal principal = getPrincipal();
		AuthCredentials authCredentials = (principal == null)
		    ? null : principal.getOauthCredentials();
		Engine engine = Environment.getInstance().getEngine();
		List<ServiceConnector> connectors = engine.getConnectors();
		String embedded = null;
		for (ServiceConnector connector : connectors)
		{
			if (connector.isConnectorRegistered())
			{
				try
				{
					Environment.logger.debug("querying connector: "
					                         + connector.getName());
					embedded = connector.getEmbedded(authCredentials,
					                                 url,
					                                 maxWidth,
					                                 maxHeight);
					break;
				}
				catch (InterWebException e)
				{
					//				e.printStackTrace();
					Environment.logger.warn(e);
				}
			}
		}
		return new EmbeddedResponse(embedded);
	}
	

	public static void main(String[] args)
	{
		AuthCredentials consumerCredentials = new AuthCredentials("***REMOVED***",
		                                                          "***REMOVED***");
		AuthCredentials userCredentials = new AuthCredentials("***REMOVED***",
		                                                      "***REMOVED***");
		//		AuthCredentials userCredentials = new AuthCredentials("***REMOVED***",
		//		                                                      "***REMOVED***");
		WebResource resource = createWebResource("http://localhost:8181/InterWebJ/api/embedded",
		                                         consumerCredentials,
		                                         userCredentials);
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();
		params.add("url", "http://flickr.com/photos/35948364@N00/330342884");
		//		params.add("url",
		//		           "http://www.youtube.com/watch?v=-hRycGcj_AQ&feature=youtube_gdata_player");
		params.add("max_width", "100");
		params.add("max_height", "100");
		System.out.println("querying InterWebJ URL: " + resource.toString());
		ClientResponse response = resource.type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class,
		                                                                                    params);
		EmbeddedResponse embeddedResponse = response.getEntity(EmbeddedResponse.class);
		System.out.println(embeddedResponse);
	}
}
