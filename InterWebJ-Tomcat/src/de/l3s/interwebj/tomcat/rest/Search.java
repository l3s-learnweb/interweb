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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.l3s.interwebj.core.InterWebException;
import de.l3s.interwebj.core.connector.QueryResultCollector;
import de.l3s.interwebj.core.core.Engine;
import de.l3s.interwebj.core.core.Environment;
import de.l3s.interwebj.core.core.InterWebPrincipal;
import de.l3s.interwebj.core.query.ContentType;
import de.l3s.interwebj.core.query.Query;
import de.l3s.interwebj.core.query.QueryFactory;
import de.l3s.interwebj.core.query.SearchRanking;
import de.l3s.interwebj.core.query.SearchResults;
import de.l3s.interwebj.core.query.SearchScope;
import de.l3s.interwebj.core.util.CoreUtils;

@Path("/search")
public class Search extends Endpoint {
    private static final Logger log = LogManager.getLogger(Search.class);

    @Context
    HttpServletRequest request;
    @Context
    private UriInfo uriInfo;

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public SearchResults getQueryResult(@QueryParam("q") String queryString, @QueryParam("services") String services,
        @QueryParam("search_in") String searchIn, @QueryParam("types") String mediaTypes,
        @QueryParam("date_from") String dateFrom, @QueryParam("date_till") String dateTill,
        @QueryParam("page") String page, @QueryParam("per_page") String resultCount, @QueryParam("ranking") String ranking,
        @QueryParam("language") String language, @QueryParam("timeout") String timeout) {

        QueryFactory queryFactory = new QueryFactory();
        checkQueryString(queryString);

        Query query = queryFactory.createQuery(queryString.trim());
        query.setLink(uriInfo.getAbsolutePath() + "/" + query.getId() + ".xml");

        checkSearchIn(query, searchIn);
        checkMediaTypes(query, mediaTypes);
        checkDates(query, dateFrom, dateTill);
        checkRanking(query, ranking);
        checkResultCount(query, resultCount);
        checkServices(query, services);
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
            SearchResults searchResults = collector.retrieve();

            engine.getGeneralCache().put(searchResults.getQuery().getId(), searchResults);
            log.info(searchResults.getResultItems().size() + " results found in " + searchResults.getElapsedTime() + " ms");
            return searchResults;
        } catch (InterWebException e) {
            log.error(e);
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
    }

    @GET
    @Path("/{id}.xml")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public SearchResults getStandingQueryResult(@PathParam(value = "id") String id) {
        Engine engine = Environment.getInstance().getEngine();

        SearchResults searchResults = (SearchResults) engine.getGeneralCache().getIfPresent(id);
        InterWebPrincipal principal = getPrincipal();
        if (searchResults == null || principal == null) {
            throw new WebApplicationException("Standing query does not exist", Response.Status.BAD_REQUEST);
        }

        return searchResults;
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

    private static void checkDates(Query query, String dateFrom, String dateTill) {
        if (dateFrom != null) {
            if (checkDate(dateFrom)) {
                query.setDateFrom(dateFrom);
            } else {
                throw new WebApplicationException("Invalid format of date_from", Response.Status.BAD_REQUEST);
            }
        }

        if (dateTill != null) {
            if (checkDate(dateTill)) {
                query.setDateTill(dateTill);
            } else {
                throw new WebApplicationException("Invalid format of date_till", Response.Status.BAD_REQUEST);
            }
        }
    }

    private static void checkMediaTypes(Query query, String mediaTypes) {
        if (mediaTypes == null || mediaTypes.trim().length() == 0) {
            throw new WebApplicationException("Media type missing. You have to specify 'type' query param.", Response.Status.BAD_REQUEST);
        }

        String[] mediaTypeArray = mediaTypes.split(",");
        Engine engine = Environment.getInstance().getEngine();

        List<ContentType> contentTypes = engine.getContentTypes();
        for (String mediaType : mediaTypeArray) {
            ContentType contentType = ContentType.find(mediaType);
            if (contentType == null || !contentTypes.contains(contentType)) {
                throw new WebApplicationException("Unknown media type", Response.Status.BAD_REQUEST);
            }
            query.addContentType(contentType);
        }
    }

    private static void checkQueryString(String queryString) {
        if (queryString == null || queryString.trim().length() == 0) {
            throw new WebApplicationException("Query not set. You have to specify 'q' query param.", Response.Status.BAD_REQUEST);
        }
    }

    private static void checkRanking(Query query, String ranking) {
        if (ranking == null || ranking.trim().length() == 0) {
            query.setRanking(SearchRanking.relevance);
            return;
        }

        SearchRanking searchRanking = SearchRanking.find(ranking);
        if (searchRanking == null) {
            query.setRanking(SearchRanking.relevance);
            return;
        }

        query.setRanking(searchRanking);
    }

    private static void checkResultCount(Query query, String resultCount) {
        if (resultCount == null || resultCount.trim().length() == 0) {
            return;
        }

        try {
            int i = Integer.parseInt(resultCount);
            i = Math.max(1, i);
            i = Math.min(500, i);
            query.setPerPage(i);
        } catch (NumberFormatException e) {
            log.error(e);
        }
    }

    private static void checkPage(Query query, String page) {
        if (page == null || page.trim().length() == 0) {
            return;
        }

        try {
            int i = Integer.parseInt(page);
            i = Math.max(1, i);
            i = Math.min(100, i);
            query.setPage(i);
        } catch (NumberFormatException e) {
            log.error(e);
        }
    }

    private static void checkSearchIn(Query query, String searchIn) {
        if (searchIn == null || searchIn.trim().length() == 0) {
            query.addSearchScope(SearchScope.text);
            return;
        }

        String[] scopes = searchIn.split(",");
        for (String scope : scopes) {
            SearchScope searchScope = SearchScope.find(scope);
            if (searchScope == null) {
                Set<SearchScope> searchScopes = new HashSet<>();
                query.setSearchScopes(searchScopes);
                return;
            }
            query.addSearchScope(searchScope);
        }
    }

    private static void checkServices(Query query, String services) {
        Engine engine = Environment.getInstance().getEngine();
        if (services == null || services.trim().length() == 0) {
            List<String> connectorNames = engine.getConnectorNames();
            for (String connectorName : connectorNames) {
                query.addConnectorName(connectorName);
            }
            return;
        }

        String[] serviceArray = services.split(",");
        List<String> connectorNames = engine.getConnectorNames();
        for (String service : serviceArray) {
            service = service.toLowerCase();
            if (connectorNames.contains(service)) {
                query.addConnectorName(service);
            }
        }
    }
}
