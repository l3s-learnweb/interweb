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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.l3s.interwebj.core.InterWebException;
import de.l3s.interwebj.core.core.Engine;
import de.l3s.interwebj.core.core.Environment;
import de.l3s.interwebj.core.core.InterWebPrincipal;
import de.l3s.interwebj.core.query.ContentType;
import de.l3s.interwebj.core.query.Query;
import de.l3s.interwebj.core.query.QueryFactory;
import de.l3s.interwebj.core.query.QueryResultCollector;
import de.l3s.interwebj.core.query.QueryResults;
import de.l3s.interwebj.core.query.SearchRanking;
import de.l3s.interwebj.core.query.SearchScope;
import de.l3s.interwebj.core.util.CoreUtils;
import de.l3s.interwebj.core.util.ExpirableMap;
import de.l3s.interwebj.core.xml.ErrorResponse;
import de.l3s.interwebj.core.xml.XmlResponse;

@Path("/search")
public class Search extends Endpoint {
    private static final Logger log = LogManager.getLogger(Search.class);

    @Context
    HttpServletRequest request;
    @Context
    private UriInfo uriInfo;

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public XmlResponse getQueryResult(@QueryParam("q") String queryString, @QueryParam("services") String services,
        @QueryParam("search_in") String searchIn, @QueryParam("types") String mediaTypes,
        @QueryParam("date_from") String dateFrom, @QueryParam("date_till") String dateTill,
        @QueryParam("page") String page, @QueryParam("per_page") String resultCount, @QueryParam("ranking") String ranking,
        @QueryParam("language") String language, @QueryParam("timeout") String timeout) {

        QueryFactory queryFactory = new QueryFactory();
        ErrorResponse errorResponse;
        errorResponse = checkQueryString(queryString);
        if (errorResponse != null) {
            return errorResponse;
        }
        Query query = queryFactory.createQuery(queryString.trim());
        query.setLink(uriInfo.getAbsolutePath() + "/" + query.getId() + ".xml");
        errorResponse = checkSearchIn(query, searchIn);
        if (errorResponse != null) {
            return errorResponse;
        }
        errorResponse = checkMediaTypes(query, mediaTypes);
        if (errorResponse != null) {
            return errorResponse;
        }
        errorResponse = checkDates(query, dateFrom, dateTill);
        if (errorResponse != null) {
            return errorResponse;
        }
        errorResponse = checkRanking(query, ranking);
        if (errorResponse != null) {
            return errorResponse;
        }
        errorResponse = checkResultCount(query, resultCount);
        if (errorResponse != null) {
            return errorResponse;
        }
        errorResponse = checkServices(query, services);
        if (errorResponse != null) {
            return errorResponse;
        }
        checkPage(query, page);

        if (null != language) {
            query.setLanguage(language);
        }

        if (null != timeout) {
            query.setTimeout(Integer.parseInt(timeout));
        }

        try {
            Engine engine = Environment.getInstance().getEngine();
            InterWebPrincipal principal = getPrincipal();
            log.info("principal: [" + principal + "]");

            QueryResultCollector collector = engine.getQueryResultCollector(query, principal);
            QueryResults queryResults = collector.retrieve();

            ExpirableMap<String, Object> expirableMap = engine.getExpirableMap();
            expirableMap.put(queryResults.getQuery().getId(), queryResults);
            log.info(queryResults.getResultItems().size() + " results found in " + queryResults.getElapsedTime() + " ms");
            return queryResults;
        } catch (InterWebException e) {
            log.error(e);
            return new ErrorResponse(999, e.getMessage());
        }
    }

    @GET
    @Path("/{id}.xml")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public XmlResponse getStandingQueryResult(@PathParam(value = "id") String id) {
        Engine engine = Environment.getInstance().getEngine();
        ExpirableMap<String, Object> expirableMap = engine.getExpirableMap();
        QueryResults queryResults = (QueryResults) expirableMap.get(id);
        InterWebPrincipal principal = getPrincipal();
        if (queryResults == null || principal == null) {
            return ErrorResponse.NO_STANDING_QUERY;
        }
        return queryResults;
    }

    private static boolean checkDate(String date) {
        try {
            CoreUtils.parseDate(date);
        } catch (ParseException e) {
            log.error(e);
            return false;
        }
        return true;
    }

    private static ErrorResponse checkDates(Query query, String dateFrom, String dateTill) {
        if (dateFrom != null) {
            if (checkDate(dateFrom)) {
                query.setDateFrom(dateFrom);
            } else {
                return ErrorResponse.INVALID_DATE_FROM;
            }
        }

        if (dateTill != null) {
            if (checkDate(dateTill)) {
                query.setDateTill(dateTill);
            } else {
                return ErrorResponse.INVALID_DATE_TILL;
            }
        }

        return null;
    }

    private static ErrorResponse checkMediaTypes(Query query, String mediaTypes) {
        if (mediaTypes == null || mediaTypes.trim().length() == 0) {
            return ErrorResponse.NO_MEDIA_TYPE;
        }
        String[] mediaTypeArray = mediaTypes.split(",");
        Engine engine = Environment.getInstance().getEngine();
        List<ContentType> contentTypes = engine.getContentTypes();
        for (String mediaType : mediaTypeArray) {
            ContentType contentType = ContentType.find(mediaType);
            if (contentType == null || !contentTypes.contains(contentType)) {
                return ErrorResponse.UNKNOWN_MEDIA_TYPE;
            }
            query.addContentType(contentType);
        }
        return null;
    }

    private static ErrorResponse checkQueryString(String queryString) {
        if (queryString == null || queryString.trim().length() == 0) {
            return ErrorResponse.NO_QUERY_STRING;
        }
        return null;
    }

    private static ErrorResponse checkRanking(Query query, String ranking) {
        if (ranking == null || ranking.trim().length() == 0) {
            query.setRanking(SearchRanking.relevance);
            return null;
        }
        SearchRanking searchRanking = SearchRanking.find(ranking);
        if (searchRanking == null) {
            query.setRanking(SearchRanking.relevance);
            return null;
        }
        query.setRanking(searchRanking);
        return null;
    }

    private static ErrorResponse checkResultCount(Query query, String resultCount) {
        if (resultCount == null || resultCount.trim().length() == 0) {
            return null;
        }
        try {
            int i = Integer.parseInt(resultCount);
            i = Math.max(1, i);
            i = Math.min(500, i);
            query.setPerPage(i);
        } catch (NumberFormatException e) {
            log.error(e);
        }
        return null;
    }

    private static ErrorResponse checkPage(Query query, String page) {
        if (page == null || page.trim().length() == 0) {
            return null;
        }
        try {
            int i = Integer.parseInt(page);
            i = Math.max(1, i);
            i = Math.min(100, i);
            query.setPage(i);
        } catch (NumberFormatException e) {
            log.error(e);
        }
        return null;
    }

    private static ErrorResponse checkSearchIn(Query query, String searchIn) {
        if (searchIn == null || searchIn.trim().length() == 0) {
            query.addSearchScope(SearchScope.text);
            return null;
        }
        String[] scopes = searchIn.split(",");
        for (String scope : scopes) {
            SearchScope searchScope = SearchScope.find(scope);
            if (searchScope == null) {
                Set<SearchScope> searchScopes = new HashSet<>();
                query.setSearchScopes(searchScopes);
                return null;
            }
            query.addSearchScope(searchScope);
        }
        return null;
    }

    private static ErrorResponse checkServices(Query query, String services) {
        Engine engine = Environment.getInstance().getEngine();
        if (services == null || services.trim().length() == 0) {
            List<String> connectorNames = engine.getConnectorNames();
            for (String connectorName : connectorNames) {
                query.addConnectorName(connectorName);
            }
            return null;
        }
        String[] serviceArray = services.split(",");
        List<String> connectorNames = engine.getConnectorNames();
        for (String service : serviceArray) {
            service = service.toLowerCase();
            if (connectorNames.contains(service)) {
                query.addConnectorName(service);
            }
        }
        return null;
    }
}
