package de.l3s.interwebj.tomcat.rest;

import java.text.ParseException;
import java.util.List;

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

import org.apache.commons.lang3.StringUtils;
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
import de.l3s.interwebj.core.query.SearchExtra;
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
    public SearchResults getQueryResult(@QueryParam("q") String queryString,
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
            Engine engine = Environment.getInstance().getEngine();
            InterWebPrincipal principal = getPrincipal();
            log.info("principal: [{}]", principal);

            QueryResultCollector collector = engine.getQueryResultCollector(query, principal);
            SearchResults searchResults = collector.retrieve();

            engine.getGeneralCache().put(searchResults.getQuery().getId(), searchResults);
            log.info("{} results found in {} ms", searchResults.getResultItems().size(), searchResults.getElapsedTime());
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
            return true;
        } catch (ParseException e) {
            log.error(e);
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

    private static void checkMediaTypes(Query query, String mediaTypes) {
        if (StringUtils.isBlank(mediaTypes)) {
            throw new WebApplicationException("Media type missing. You have to specify 'type' query param.", Response.Status.BAD_REQUEST);
        }

        String[] types = mediaTypes.split(",");
        List<ContentType> contentTypes = Environment.getInstance().getEngine().getContentTypes();
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
            log.error(e);
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
            log.error(e);
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

    private static void checkServices(Query query, String servicesStr) {
        List<String> connectorNames = Environment.getInstance().getEngine().getConnectorNames();

        if (StringUtils.isBlank(servicesStr)) {
            query.setConnectorNames(connectorNames);
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
