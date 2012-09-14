package de.l3s.interwebj.rest;


import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
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
import de.l3s.interwebj.jaxb.SocialSearchResponse;
import de.l3s.interwebj.jaxb.XMLResponse;
import de.l3s.interwebj.socialsearch.SocialSearchQuery;
import de.l3s.interwebj.socialsearch.SocialSearchResult;
import de.l3s.interwebj.socialsearch.SocialSearchResultCollector;
import de.l3s.interwebj.util.ExpirableMap;


@Path("/socialsearch")
public class SocialSearch
    extends Endpoint
{
	
	@Context
	HttpServletRequest request;
	@Context
	private UriInfo uriInfo;
	

	@GET
	@Produces(MediaType.APPLICATION_XML)
	public XMLResponse getSocialNetworkResult(@QueryParam("userid") String userid,
			@QueryParam("q") String queryString,
            @QueryParam("date_from") String dateFrom,
            @QueryParam("date_till") String dateTill,
            @QueryParam("ranking") String ranking,
            @QueryParam("number_of_results") String resultCount,
            @QueryParam("services") String services,
            @QueryParam("page") String page,
            @QueryParam("timeout") String timeout)
	                                  
	{
		
		
		try
		{

			Engine engine = Environment.getInstance().getEngine();
			InterWebPrincipal principal = getPrincipal();
			Environment.logger.info("principal: [" + principal + "]");
			SocialSearchQuery query= new SocialSearchQuery(queryString);
			SocialSearchResultCollector collector = engine.getSocialSearchResultsOf(query.getQuery(), principal);
			SocialSearchResult queryResult = collector.retrieve();
			
			ExpirableMap<String, Object> expirableMap = engine.getExpirableMap();
			expirableMap.put(queryResult.getQuery().getQuery(), queryResult);
			SocialSearchResponse searchResponse= new SocialSearchResponse(queryResult);
			//SearchResponse searchResponse = new SearchResponse(queryResult);
			String userName = (principal == null)
			    ? "anonymous" : principal.getName();
			searchResponse.getQuery().setUserId(userName);
			Environment.logger.info(searchResponse.getQuery().getResults().size()
			                        + " results found in "
			                       );
			return searchResponse;
		}
		catch (InterWebException e)
		{
			e.printStackTrace();
			Environment.logger.severe(e.getMessage());
			return new ErrorResponse(999, e.getMessage());
		}
	}
	

	
}
