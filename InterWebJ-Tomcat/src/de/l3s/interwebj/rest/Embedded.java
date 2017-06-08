package de.l3s.interwebj.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.lang.StringUtils;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import de.l3s.interwebj.AuthCredentials;
import de.l3s.interwebj.InterWebException;
import de.l3s.interwebj.core.Engine;
import de.l3s.interwebj.core.Environment;
import de.l3s.interwebj.core.InterWebPrincipal;
import de.l3s.interwebj.core.ServiceConnector;
import de.l3s.interwebj.jaxb.EmbeddedResponse;
import de.l3s.interwebj.jaxb.ErrorResponse;
import de.l3s.interwebj.jaxb.XMLResponse;

@Path("/embedded")
public class Embedded extends Endpoint
{

    @Context
    HttpServletRequest request;

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_XML)
    public XMLResponse getEmbedded(@FormParam("url") String url, @FormParam("max_width") int maxWidth, @FormParam("max_height") int maxHeight)
    {
	if(StringUtils.isEmpty(url))
	{
	    return new ErrorResponse(999, "URL must not by null or empty");
	}
	InterWebPrincipal principal = getPrincipal();
	AuthCredentials authCredentials = (principal == null) ? null : principal.getOauthCredentials();
	Engine engine = Environment.getInstance().getEngine();
	List<ServiceConnector> connectors = engine.getConnectors();
	String embedded = null;
	for(ServiceConnector connector : connectors)
	{
	    if(connector.isRegistered())
	    {
		try
		{
		    Environment.logger.info("querying connector: " + connector.getName());
		    embedded = connector.getEmbedded(authCredentials, url, maxWidth, maxHeight);
		    if(embedded != null)
		    {
			break;
		    }
		}
		catch(InterWebException e)
		{
		    Environment.logger.severe(e.getMessage());
		}
	    }
	}
	return new EmbeddedResponse(embedded);
    }

    public static void main(String[] args)
    {
	AuthCredentials consumerCredentials = new AuthCredentials("***REMOVED***", "***REMOVED***");
	AuthCredentials userCredentials = new AuthCredentials("***REMOVED***", "***REMOVED***");
	//		AuthCredentials userCredentials = new AuthCredentials("***REMOVED***",
	//		                                                      "***REMOVED***");
	WebResource resource = createWebResource("http://localhost:8181/InterWebJ/api/embedded", consumerCredentials, userCredentials);
	MultivaluedMap<String, String> params = new MultivaluedMapImpl();
	params.add("url", "http://flickr.com/photos/35948364@N00/330342884");
	//		params.add("url",
	//		           "http://www.youtube.com/watch?v=-hRycGcj_AQ&feature=youtube_gdata_player");
	params.add("max_width", "100");
	params.add("max_height", "100");
	System.out.println("querying InterWebJ URL: " + resource.toString());
	ClientResponse response = resource.type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, params);
	EmbeddedResponse embeddedResponse = response.getEntity(EmbeddedResponse.class);
	System.out.println(embeddedResponse);
    }
}
