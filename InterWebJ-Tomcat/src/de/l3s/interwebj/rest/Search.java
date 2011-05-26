package de.l3s.interwebj.rest;


import java.text.*;
import java.util.*;

import javax.servlet.http.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.jaxb.*;
import de.l3s.interwebj.query.*;
import de.l3s.interwebj.query.Query.SearchScope;
import de.l3s.interwebj.query.Query.SortOrder;
import de.l3s.interwebj.util.*;


@Path("/search")
public class Search
    extends Endpoint
{
	
	@Context
	HttpServletRequest request;
	@Context
	private UriInfo uriInfo;
	

	@GET
	@Produces(MediaType.APPLICATION_XML)
	public XMLResponse getQueryResult(@QueryParam("q") String queryString,
	                                  @QueryParam("search_in") String searchIn,
	                                  @QueryParam("media_types") String mediaTypes,
	                                  @QueryParam("date_from") String dateFrom,
	                                  @QueryParam("date_till") String dateTill,
	                                  @QueryParam("ranking") String ranking,
	                                  @QueryParam("number_of_results") String resultCount,
	                                  @QueryParam("services") String services)
	{
		QueryFactory queryFactory = new QueryFactory();
		ErrorResponse errorResponse;
		errorResponse = checkQueryString(queryString);
		if (errorResponse != null)
		{
			return errorResponse;
		}
		Query query = queryFactory.createQuery(queryString.trim());
		query.setLink(uriInfo.getAbsolutePath() + "/" + query.getId() + ".xml");
		errorResponse = checkSearchIn(query, searchIn);
		if (errorResponse != null)
		{
			return errorResponse;
		}
		errorResponse = checkMediaTypes(query, mediaTypes);
		if (errorResponse != null)
		{
			return errorResponse;
		}
		errorResponse = checkDates(query, dateFrom, dateTill);
		if (errorResponse != null)
		{
			return errorResponse;
		}
		errorResponse = checkRanking(query, ranking);
		if (errorResponse != null)
		{
			return errorResponse;
		}
		errorResponse = checkResultCount(query, resultCount);
		if (errorResponse != null)
		{
			return errorResponse;
		}
		errorResponse = checkServices(query, services);
		if (errorResponse != null)
		{
			return errorResponse;
		}
		try
		{
			Engine engine = Environment.getInstance().getEngine();
			InterWebPrincipal principal = getPrincipal();
			System.out.println(principal);
			QueryResultMerger merger = new DumbQueryResultMerger();
			QueryResultCollector collector = engine.getQueryResultCollector(query,
			                                                                principal,
			                                                                merger);
			QueryResult queryResult = collector.retrieve();
			ExpirableMap<String, Object> expirableMap = engine.getExpirableMap();
			expirableMap.put(queryResult.getQuery().getId(), queryResult);
			SearchResponse searchResponse = new SearchResponse(queryResult);
			String userName = (principal == null)
			    ? "anonymous" : principal.getName();
			searchResponse.getQuery().setUser(userName);
			System.out.println(searchResponse.getQuery().getResults().size()
			                   + " results found in "
			                   + searchResponse.getQuery().getElapsedTime()
			                   + " ms");
			return searchResponse;
		}
		catch (InterWebException e)
		{
			e.printStackTrace();
			Environment.logger.error(e);
			return new ErrorResponse(999, e.getMessage());
		}
	}
	

	@GET
	@Path("/{id}.xml")
	@Produces(MediaType.APPLICATION_XML)
	public XMLResponse getStandingQueryResult(@PathParam(value = "id") String id)
	{
		Engine engine = Environment.getInstance().getEngine();
		ExpirableMap<String, Object> expirableMap = engine.getExpirableMap();
		QueryResult queryResult = (QueryResult) expirableMap.get(id);
		InterWebPrincipal principal = getPrincipal();
		if (queryResult == null
		    || principal == null
		    || principal.getName().equals(queryResult.getQuery().getParam("user")))
		{
			return ErrorResponse.NO_STANDING_QUERY;
		}
		SearchResponse iwSearchResponse = new SearchResponse(queryResult);
		return iwSearchResponse;
	}
	

	private boolean checkDate(String date)
	{
		try
		{
			CoreUtils.parseDate(date);
		}
		catch (ParseException e)
		{
			Environment.logger.error(e);
			return false;
		}
		return true;
	}
	

	private ErrorResponse checkDates(Query query,
	                                 String dateFrom,
	                                 String dateTill)
	{
		if (dateFrom != null)
		{
			if (checkDate(dateFrom))
			{
				query.addParam("date_from", dateFrom);
			}
			else
			{
				return ErrorResponse.INVALID_DATE_FROM;
			}
		}
		if (dateFrom != null)
		{
			if (checkDate(dateTill))
			{
				query.addParam("date_till", dateTill);
			}
			else
			{
				return ErrorResponse.INVALID_DATE_TILL;
			}
		}
		return null;
	}
	

	private ErrorResponse checkMediaTypes(Query query, String mediaTypes)
	{
		if (mediaTypes == null || mediaTypes.trim().length() == 0)
		{
			return ErrorResponse.NO_MEDIA_TYPE;
		}
		String[] mediaTypeArray = mediaTypes.split(",");
		Engine engine = Environment.getInstance().getEngine();
		List<String> contentTypes = engine.getContentTypes();
		for (String mediaType : mediaTypeArray)
		{
			if (!contentTypes.contains(mediaType))
			{
				return ErrorResponse.UNKNOWN_MEDIA_TYPE;
			}
			query.addContentType(mediaType);
		}
		return null;
	}
	

	private ErrorResponse checkQueryString(String queryString)
	{
		if (queryString == null || queryString.trim().length() == 0)
		{
			return ErrorResponse.NO_QUERY_STRING;
		}
		return null;
	}
	

	private ErrorResponse checkRanking(Query query, String ranking)
	{
		if (ranking == null || ranking.trim().length() == 0)
		{
			query.setSortOrder(SortOrder.RELEVANCE);
			return null;
		}
		SortOrder sortOrder = SortOrder.find(ranking);
		if (sortOrder == null)
		{
			query.setSortOrder(SortOrder.RELEVANCE);
			return null;
		}
		query.setSortOrder(sortOrder);
		return null;
	}
	

	private ErrorResponse checkResultCount(Query query, String resultCount)
	{
		if (resultCount == null || resultCount.trim().length() == 0)
		{
			return null;
		}
		try
		{
			int i = Integer.parseInt(resultCount);
			i = Math.max(1, i);
			i = Math.min(100, i);
			query.setResultCount(i);
		}
		catch (NumberFormatException e)
		{
			Environment.logger.error(e);
		}
		return null;
	}
	

	private ErrorResponse checkSearchIn(Query query, String searchIn)
	{
		if (searchIn == null || searchIn.trim().length() == 0)
		{
			query.addSearchScope(SearchScope.TEXT);
			return null;
		}
		String[] scopes = searchIn.split(",");
		for (String scope : scopes)
		{
			SearchScope searchScope = SearchScope.find(scope);
			if (searchScope == null)
			{
				Set<SearchScope> searchScopes = new HashSet<Query.SearchScope>();
				query.setSearchScopes(searchScopes);
				return null;
			}
			query.addSearchScope(searchScope);
		}
		return null;
	}
	

	private ErrorResponse checkServices(Query query, String services)
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
