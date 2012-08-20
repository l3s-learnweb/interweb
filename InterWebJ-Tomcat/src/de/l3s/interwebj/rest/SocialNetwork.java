package de.l3s.interwebj.rest;


import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import de.l3s.interwebj.InterWebException;
import de.l3s.interwebj.core.Engine;
import de.l3s.interwebj.core.Environment;
import de.l3s.interwebj.core.InterWebPrincipal;
import de.l3s.interwebj.jaxb.ErrorResponse;
import de.l3s.interwebj.jaxb.SearchResponse;
import de.l3s.interwebj.jaxb.SocialNetworkResponse;
import de.l3s.interwebj.jaxb.XMLResponse;
import de.l3s.interwebj.query.Query;
import de.l3s.interwebj.query.Query.SearchScope;
import de.l3s.interwebj.query.Query.SortOrder;
import de.l3s.interwebj.query.QueryFactory;
import de.l3s.interwebj.query.QueryResult;
import de.l3s.interwebj.query.QueryResultCollector;
import de.l3s.interwebj.query.UserSocialNetworkCollector;
import de.l3s.interwebj.query.UserSocialNetworkResult;
import de.l3s.interwebj.util.CoreUtils;
import de.l3s.interwebj.util.ExpirableMap;


@Path("/getsocialnetwork")
public class SocialNetwork
    extends Endpoint
{
	
	@Context
	HttpServletRequest request;
	@Context
	private UriInfo uriInfo;
	

	@GET
	@Produces(MediaType.APPLICATION_XML)
	public XMLResponse getSocialNetworkResult(@QueryParam("userid") String userid)
	                                  
	{
		
		
		try
		{
			Engine engine = Environment.getInstance().getEngine();
			InterWebPrincipal principal = getPrincipal();
			Environment.logger.info("principal: [" + principal + "]");
			
			UserSocialNetworkCollector collector = engine.getSocialNetworkOf(userid, principal, "Flickr");
			UserSocialNetworkResult userSocialNetwork = collector.retrieve();
			
			ExpirableMap<String, Object> expirableMap = engine.getExpirableMap();
			expirableMap.put(userid, userSocialNetwork);
			//create xml objects to wrap like search response 
			SocialNetworkResponse socialnetworkresponse = new SocialNetworkResponse(userSocialNetwork);
			String userName = (principal == null)
			    ? "anonymous" : principal.getName();
			socialnetworkresponse.getSocialNetwork().setUser(userid);
			Environment.logger.info(socialnetworkresponse.getSocialNetwork().getResults().size()
			                        + " results found  "
			                       );
			return socialnetworkresponse;
		}
		catch (InterWebException e)
		{
			e.printStackTrace();
			Environment.logger.severe(e.getMessage());
			return new ErrorResponse(999, e.getMessage());
		}
	}
	

	
	private static ErrorResponse checkServices(Query query, String services)
	{
		Engine engine = Environment.getInstance().getEngine();
		if (services == null || services.trim().length() == 0)
		{
			List<String> connectorNames = engine.getConnectorNames();
			for (String connectorName : connectorNames)
			{
				query.addConnectorName(connectorName);
			}
			return null;
		}
		String[] serviceArray = services.split(",");
		List<String> connectorNames = engine.getConnectorNames();
		for (String service : serviceArray)
		{
			if (connectorNames.contains(service))
			{
				query.addConnectorName(service);
			}
		}
		return null;
	}
}
