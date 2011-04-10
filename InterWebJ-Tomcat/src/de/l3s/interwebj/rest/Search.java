package de.l3s.interwebj.rest;


import java.text.*;
import java.util.*;

import javax.servlet.http.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import de.l3s.interwebj.InterWebException;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.jaxb.*;
import de.l3s.interwebj.query.*;
import de.l3s.interwebj.query.Query.SearchScope;
import de.l3s.interwebj.query.Query.SortOrder;
import de.l3s.interwebj.util.*;


@Path("/search")
public class Search
{
	
	@Context
	HttpServletRequest request;
	@Context
	private UriInfo uriInfo;
	

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
	

	private IWError checkDates(Query query, String dateFrom, String dateTill)
	{
		if (dateFrom != null)
		{
			if (checkDate(dateFrom))
			{
				query.addParam("date_from", dateFrom);
			}
			else
			{
				return new IWError(204, "Invalid format of date_from");
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
				return new IWError(205, "Invalid format of date_till");
			}
		}
		return null;
	}
	

	private IWError checkMediaTypes(Query query, String mediaTypes)
	{
		if (mediaTypes == null || mediaTypes.trim().length() == 0)
		{
			return new IWError(202, "No media type chosen");
		}
		String[] mediaTypeArray = mediaTypes.split(",");
		Engine engine = Environment.getInstance().getEngine();
		List<String> contentTypes = engine.getContentTypes();
		for (String mediaType : mediaTypeArray)
		{
			if (!contentTypes.contains(mediaType))
			{
				return new IWError(203, "Unknown media type: " + mediaType);
			}
			query.addContentType(mediaType);
		}
		return null;
	}
	

	private IWError checkQueryString(String queryString)
	{
		if (queryString == null || queryString.trim().length() == 0)
		{
			return new IWError(201, "Query string not set");
		}
		return null;
	}
	

	private IWError checkRanking(Query query, String ranking)
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
	

	private IWError checkResultCount(Query query, String resultCount)
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
	

	private IWError checkSearchIn(Query query, String searchIn)
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
	

	private IWError checkServices(Query query, String services)
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
	

	@GET
	@Produces(MediaType.APPLICATION_XML)
	public IWSearchResponse getQueryResult(@QueryParam("q") String queryString,
	                                       @QueryParam("search_in") String searchIn,
	                                       @QueryParam("media_types") String mediaTypes,
	                                       @QueryParam("date_from") String dateFrom,
	                                       @QueryParam("date_till") String dateTill,
	                                       @QueryParam("ranking") String ranking,
	                                       @QueryParam("number_of_results") String resultCount,
	                                       @QueryParam("services") String services)
	{
		QueryFactory queryFactory = new QueryFactory();
		IWError iwError;
		iwError = checkQueryString(queryString);
		if (iwError != null)
		{
			return new IWSearchResponse(iwError);
		}
		Query query = queryFactory.createQuery(queryString.trim());
		query.setLink(uriInfo.getAbsolutePath() + "/" + query.getId() + ".xml");
		iwError = checkSearchIn(query, searchIn);
		if (iwError != null)
		{
			return new IWSearchResponse(iwError);
		}
		iwError = checkMediaTypes(query, mediaTypes);
		if (iwError != null)
		{
			return new IWSearchResponse(iwError);
		}
		iwError = checkDates(query, dateFrom, dateTill);
		if (iwError != null)
		{
			return new IWSearchResponse(iwError);
		}
		iwError = checkRanking(query, ranking);
		if (iwError != null)
		{
			return new IWSearchResponse(iwError);
		}
		iwError = checkResultCount(query, resultCount);
		if (iwError != null)
		{
			return new IWSearchResponse(iwError);
		}
		iwError = checkServices(query, services);
		if (iwError != null)
		{
			return new IWSearchResponse(iwError);
		}
		Environment.logger.debug(query);
		try
		{
			Engine engine = Environment.getInstance().getEngine();
			// TODO: Stub. Implement OAuth Filter, which authenticate user against OAuth key/secret pair, 
			//       read principal from the database and store it in RequestWrapper. 
			//       Get Principal from the request.
			//		 IWPrincipal principal = request.getUserPrincipal();
			IWPrincipal principal = new IWPrincipal("olex", "");
			query.addParam("user", principal.getName());
			QueryResultCollector collector = engine.getQueryResultCollector(query,
			                                                                principal);
			QueryResult queryResult = collector.retrieve();
			engine.getStandingQueryResultPool().add(queryResult);
			IWSearchResponse iwSearchResponse = new IWSearchResponse(queryResult);
			iwSearchResponse.getQuery().setUser(principal.getName());
			return iwSearchResponse;
		}
		catch (InterWebException e)
		{
			e.printStackTrace();
			Environment.logger.error(e);
			return new IWSearchResponse(new IWError(999, e.getMessage()));
		}
	}
	

	@GET
	@Path("/{id}.xml")
	@Produces(MediaType.APPLICATION_XML)
	public IWSearchResponse getStandingQueryResult(@PathParam(value = "id") String id)
	{
		Engine engine = Environment.getInstance().getEngine();
		StandingQueryResultPool queryResultPool = engine.getStandingQueryResultPool();
		QueryResult queryResult = queryResultPool.get(id);
		if (queryResult == null)
		{
			return new IWSearchResponse(new IWError(206,
			                                        "Standing query does not exist"));
		}
		IWSearchResponse iwSearchResponse = new IWSearchResponse(queryResult);
		return iwSearchResponse;
	}
}