package de.l3s.interwebj.tomcat.rest;

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

import de.l3s.interwebj.core.InterWebException;
import de.l3s.interwebj.core.core.Engine;
import de.l3s.interwebj.core.core.Environment;
import de.l3s.interwebj.core.core.InterWebPrincipal;
import de.l3s.interwebj.tomcat.jaxb.ErrorResponse;
import de.l3s.interwebj.tomcat.jaxb.SearchResponse;
import de.l3s.interwebj.tomcat.jaxb.XMLResponse;
import de.l3s.interwebj.core.query.Query;
import de.l3s.interwebj.core.query.Query.SearchScope;
import de.l3s.interwebj.core.query.Query.SortOrder;
import de.l3s.interwebj.core.query.QueryFactory;
import de.l3s.interwebj.core.query.QueryResult;
import de.l3s.interwebj.core.query.QueryResultCollector;
import de.l3s.interwebj.core.util.CoreUtils;
import de.l3s.interwebj.core.util.ExpirableMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Path("/search")
public class Search extends Endpoint
{
	private static final Logger log = LogManager.getLogger(Search.class);

    @Context
    HttpServletRequest request;
    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public XMLResponse getQueryResult(@QueryParam("q") String queryString, @QueryParam("search_in") String searchIn, @QueryParam("media_types") String mediaTypes, @QueryParam("date_from") String dateFrom, @QueryParam("date_till") String dateTill,
	    @QueryParam("ranking") String ranking, @QueryParam("number_of_results") String resultCount, @QueryParam("services") String services, @QueryParam("page") String page, @QueryParam("language") String language, @QueryParam("privacy") String privacy,
	    @QueryParam("privacy_image_feature") String privacyUseImageFeature, @QueryParam("timeout") String timeout)
    {
	QueryFactory queryFactory = new QueryFactory();
	ErrorResponse errorResponse;
	errorResponse = checkQueryString(queryString);
	if(errorResponse != null)
	{
	    return errorResponse;
	}
	Query query = queryFactory.createQuery(queryString.trim());
	query.setLink(uriInfo.getAbsolutePath() + "/" + query.getId() + ".xml");
	errorResponse = checkSearchIn(query, searchIn);
	if(errorResponse != null)
	{
	    return errorResponse;
	}
	errorResponse = checkMediaTypes(query, mediaTypes);
	if(errorResponse != null)
	{
	    return errorResponse;
	}
	errorResponse = checkDates(query, dateFrom, dateTill);
	if(errorResponse != null)
	{
	    return errorResponse;
	}
	errorResponse = checkRanking(query, ranking);
	if(errorResponse != null)
	{
	    return errorResponse;
	}
	errorResponse = checkResultCount(query, resultCount);
	if(errorResponse != null)
	{
	    return errorResponse;
	}
	errorResponse = checkServices(query, services);
	if(errorResponse != null)
	{
	    return errorResponse;
	}
	checkPage(query, page);

	if(null != privacy)
	    query.setPrivacy(Float.parseFloat(privacy));

	if(null != privacyUseImageFeature && (privacyUseImageFeature.equals("1") || privacyUseImageFeature.equalsIgnoreCase("true")))
	    query.setPrivacyUseImageFeatures(true);

	if(null != language)
	    query.setLanguage(language);

	if(null != timeout)
	    query.setTimeout(Integer.parseInt(timeout));

	try
	{
	    Engine engine = Environment.getInstance().getEngine();
	    InterWebPrincipal principal = getPrincipal();
	    log.info("principal: [" + principal + "]");

	    QueryResultCollector collector = engine.getQueryResultCollector(query, principal);
	    QueryResult queryResult = collector.retrieve();

	    ExpirableMap<String, Object> expirableMap = engine.getExpirableMap();
	    expirableMap.put(queryResult.getQuery().getId(), queryResult);
	    SearchResponse searchResponse = new SearchResponse(queryResult);
	    String userName = (principal == null) ? "anonymous" : principal.getName();
	    searchResponse.getQuery().setUser(userName);
	    log.info(searchResponse.getQuery().getResults().size() + " results found in " + searchResponse.getQuery().getElapsedTime() + " ms");
	    return searchResponse;
	}
	catch(InterWebException e)
	{
	    log.error(e);
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
	if(queryResult == null || principal == null || principal.getName().equals(queryResult.getQuery().getParam("user")))
	{
	    return ErrorResponse.NO_STANDING_QUERY;
	}
	SearchResponse iwSearchResponse = new SearchResponse(queryResult);
	return iwSearchResponse;
    }

    private static boolean checkDate(String date)
    {
	try
	{
	    CoreUtils.parseDate(date);
	}
	catch(ParseException e)
	{
	    log.error(e);
	    return false;
	}
	return true;
    }

    private static ErrorResponse checkDates(Query query, String dateFrom, String dateTill)
    {
	if(dateFrom != null)
	{
	    if(checkDate(dateFrom))
	    {
		query.addParam("date_from", dateFrom);
	    }
	    else
	    {
		return ErrorResponse.INVALID_DATE_FROM;
	    }
	}

	if(dateTill != null)
	{
	    if(checkDate(dateTill))
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

    private static ErrorResponse checkMediaTypes(Query query, String mediaTypes)
    {
	if(mediaTypes == null || mediaTypes.trim().length() == 0)
	{
	    return ErrorResponse.NO_MEDIA_TYPE;
	}
	String[] mediaTypeArray = mediaTypes.split(",");
	Engine engine = Environment.getInstance().getEngine();
	List<String> contentTypes = engine.getContentTypes();
	for(String mediaType : mediaTypeArray)
	{
	    if(!contentTypes.contains(mediaType))
	    {
		return ErrorResponse.UNKNOWN_MEDIA_TYPE;
	    }
	    query.addContentType(mediaType);
	}
	return null;
    }

    private static ErrorResponse checkQueryString(String queryString)
    {
	if(queryString == null || queryString.trim().length() == 0)
	{
	    return ErrorResponse.NO_QUERY_STRING;
	}
	return null;
    }

    private static ErrorResponse checkRanking(Query query, String ranking)
    {
	if(ranking == null || ranking.trim().length() == 0)
	{
	    query.setSortOrder(SortOrder.RELEVANCE);
	    return null;
	}
	SortOrder sortOrder = SortOrder.find(ranking);
	if(sortOrder == null)
	{
	    query.setSortOrder(SortOrder.RELEVANCE);
	    return null;
	}
	query.setSortOrder(sortOrder);
	return null;
    }

    private static ErrorResponse checkResultCount(Query query, String resultCount)
    {
	if(resultCount == null || resultCount.trim().length() == 0)
	{
	    return null;
	}
	try
	{
	    int i = Integer.parseInt(resultCount);
	    i = Math.max(1, i);
	    i = Math.min(500, i);
	    query.setResultCount(i);
	}
	catch(NumberFormatException e)
	{
	    log.error(e);
	}
	return null;
    }

    private static ErrorResponse checkPage(Query query, String page)
    {
	if(page == null || page.trim().length() == 0)
	{
	    return null;
	}
	try
	{
	    int i = Integer.parseInt(page);
	    i = Math.max(1, i);
	    i = Math.min(100, i);
	    query.setPage(i);
	}
	catch(NumberFormatException e)
	{
	    log.error(e);
	}
	return null;
    }

    private static ErrorResponse checkSearchIn(Query query, String searchIn)
    {
	if(searchIn == null || searchIn.trim().length() == 0)
	{
	    query.addSearchScope(SearchScope.TEXT);
	    return null;
	}
	String[] scopes = searchIn.split(",");
	for(String scope : scopes)
	{
	    SearchScope searchScope = SearchScope.find(scope);
	    if(searchScope == null)
	    {
		Set<SearchScope> searchScopes = new HashSet<Query.SearchScope>();
		query.setSearchScopes(searchScopes);
		return null;
	    }
	    query.addSearchScope(searchScope);
	}
	return null;
    }

    private static ErrorResponse checkServices(Query query, String services)
    {
	Engine engine = Environment.getInstance().getEngine();
	if(services == null || services.trim().length() == 0)
	{
	    List<String> connectorNames = engine.getConnectorNames();
	    for(String connectorName : connectorNames)
	    {
		query.addConnectorName(connectorName);
	    }
	    return null;
	}
	String[] serviceArray = services.split(",");
	List<String> connectorNames = engine.getConnectorNames();
	for(String service : serviceArray)
	{
	    service = service.toLowerCase();
	    if(connectorNames.contains(service))
	    {
		query.addConnectorName(service);
	    }
	}
	return null;
    }
}
