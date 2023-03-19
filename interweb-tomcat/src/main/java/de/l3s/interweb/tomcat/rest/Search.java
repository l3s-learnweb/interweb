package de.l3s.interweb.tomcat.rest;

import java.time.format.DateTimeParseException;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.l3s.interweb.core.InterWebException;
import de.l3s.interweb.core.query.*;
import de.l3s.interweb.core.search.SearchResponse;
import de.l3s.interweb.core.util.DateUtils;
import de.l3s.interweb.tomcat.app.Engine;
import de.l3s.interweb.tomcat.app.InterWebPrincipal;
import de.l3s.interweb.tomcat.app.QueryResultCollector;

@Path("/search")
public class Search extends Endpoint {
    private static final Logger log = LogManager.getLogger(Search.class);

    @Context
    HttpServletRequest request;
    @Context
    private UriInfo uriInfo;

    @Inject
    private Engine engine;

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public SearchResponse getQueryResult(@QueryParam("q") String queryString,
                                         @QueryParam("services") String services, @QueryParam("media_types") String mediaTypes, @QueryParam("search_in") String searchIn,
                                         @QueryParam("date_from") String dateFrom, @QueryParam("date_till") String dateTill, @QueryParam("ranking") String ranking,
                                         @QueryParam("page") String page, @QueryParam("per_page") String perPage, @QueryParam("language") String language,
                                         @QueryParam("extras") String extras, @QueryParam("timeout") String timeout) {

        checkQueryString(queryString);

        Query query = QueryFactory.createQuery(queryString.trim());
        query.setLink(uriInfo.getAbsolutePath() + "/" + query.getId() + ".xml");

        checkServices(query, services);
        checkMediaTypes(query, mediaTypes);
        checkSearchIn(query, searchIn);
        checkDates(query, dateFrom, dateTill);
        checkRanking(query, ranking);
        checkPage(query, page);
        checkPerPage(query, perPage);
        checkExtras(query, extras);

        if (null != language) {
            query.setLanguage(language);
        }

        if (null != timeout) {
            query.setTimeout(Integer.parseInt(timeout));
        }

        try {
            InterWebPrincipal principal = getPrincipal();
            log.info("principal: [{}]", principal);

            QueryResultCollector collector = engine.getQueryResultCollector(query, principal);
            SearchResponse searchResponse = collector.retrieve();

            engine.getGeneralCache().put(searchResponse.getQuery().getId(), searchResponse);
            log.info("{} results found in {} ms", searchResponse.getResults().size(), searchResponse.getElapsedTime());
            return searchResponse;
        } catch (InterWebException e) {
            log.catching(e);
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
    }

    @GET
    @Path("/{id}.xml")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public SearchResponse getStandingQueryResult(@PathParam(value = "id") String id) {
        SearchResponse searchResponse = (SearchResponse) engine.getGeneralCache().getIfPresent(id);
        InterWebPrincipal principal = getPrincipal();
        if (searchResponse == null || principal == null) {
            throw new WebApplicationException("Standing query does not exist", Response.Status.BAD_REQUEST);
        }

        return searchResponse;
    }

    private static boolean checkDate(String date) {
        try {
            DateUtils.parse(date);
            return true;
        } catch (DateTimeParseException e) {
            log.catching(e);
            return false;
        }
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

    private void checkMediaTypes(Query query, String mediaTypes) {
        if (StringUtils.isBlank(mediaTypes)) {
            throw new WebApplicationException("Media type missing. You have to specify 'type' query param.", Response.Status.BAD_REQUEST);
        }

        String[] types = mediaTypes.split(",");
        List<ContentType> contentTypes = engine.getContentTypes();
        for (String type : types) {
            ContentType contentType = ContentType.find(type);
            if (contentType == null || !contentTypes.contains(contentType)) {
                throw new WebApplicationException("Unknown media type: " + type, Response.Status.BAD_REQUEST);
            } else {
                query.addContentType(contentType);
            }
        }
    }

    private static void checkQueryString(String queryString) {
        if (StringUtils.isBlank(queryString)) {
            throw new WebApplicationException("Query not set. You have to specify 'q' query param.", Response.Status.BAD_REQUEST);
        }
    }

    private static void checkRanking(Query query, String ranking) {
        if (StringUtils.isBlank(ranking)) {
            return;
        }

        SearchRanking searchRanking = SearchRanking.find(ranking);
        if (searchRanking != null) {
            query.setRanking(searchRanking);
        }
    }

    private static void checkPage(Query query, String page) {
        if (StringUtils.isBlank(page)) {
            return;
        }

        try {
            int pageNumber = Integer.parseInt(page);
            query.setPage(Math.min(100, Math.max(1, pageNumber)));
        } catch (NumberFormatException e) {
            log.catching(e);
        }
    }

    private static void checkPerPage(Query query, String perPageStr) {
        if (StringUtils.isBlank(perPageStr)) {
            return;
        }

        try {
            int perPage = Integer.parseInt(perPageStr);
            query.setPerPage(Math.min(500, Math.max(1, perPage)));
        } catch (NumberFormatException e) {
            log.catching(e);
        }
    }

    private static void checkSearchIn(Query query, String scopesStr) {
        if (StringUtils.isBlank(scopesStr)) {
            return;
        }

        SearchScope searchScope = SearchScope.find(scopesStr);
        if (searchScope != null) {
            query.setSearchScope(searchScope);
        }
    }

    private static void checkExtras(Query query, String extraStr) {
        if (StringUtils.isBlank(extraStr)) {
            return;
        }

        String[] extras = extraStr.split(",");
        for (String extra : extras) {
            SearchExtra searchExtra = SearchExtra.find(extra);
            if (searchExtra != null) {
                query.addSearchExtra(searchExtra);
            }
        }
    }

    private void checkServices(Query query, String servicesStr) {
        List<String> connectorNames = engine.getSearchServiceNames();

        if (StringUtils.isBlank(servicesStr)) {
            query.setServices(connectorNames);
            return;
        }

        String[] services = servicesStr.toLowerCase().split(",");
        for (String service : services) {
            if (connectorNames.contains(service)) {
                query.addConnectorName(service);
            }
        }
    }
}
