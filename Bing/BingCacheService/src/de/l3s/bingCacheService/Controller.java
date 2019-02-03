package de.l3s.bingCacheService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.l3s.bingCacheService.entity.Response;
import de.l3s.bingService.models.query.BingQuery;
import de.l3s.bingService.services.BingApiService;

public class Controller extends HttpServlet
{
    private static final long serialVersionUID = 4824768584324474328L;
    private final static Logger log = LogManager.getLogger(Controller.class);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        //Checks if client has the correct key
        String key = request.getHeader("Ocp-Apim-Subscription-Key");
        int clientAllowed = DBManager.instance().checkClientPriveleges(key);

        if(clientAllowed == -1)
        {
            log.error("Subscription key is not found.");
            response.sendError(403, "Subscription key is not found. This is required to use this service.");
            return;
        }

        String query = "irgendwas langes mit vielen zeichen";
        //request.getParameter("q")
        log.debug(query);
        BingQuery bingQuery = null;
        try
        {
            bingQuery = new BingQuery(query, request.getParameter("mkt"), request.getParameter("setLang"), request.getParameter("offset"), request.getParameter("freshness"), request.getParameter("safeSearch"));
        }
        catch(IllegalArgumentException e)
        {
            log.error("Invalid parameter: ", e);
            response.sendError(400, "Invalid parameter: " + e.getMessage());
            return;
        }
        log.debug(query);

        // validate "offset" parameter

        log.debug("Received query with following params. ");

        String queryResponse = getQueryResult(bingQuery, clientAllowed, response);

        if(queryResponse == null)
        {
            if(clientAllowed == 1)
            {
                log.error("Querry could not be made or retrieved. ");
                response.sendError(500, "Querry could not be made or retrieved. Please contact the administrators.");
            }
            else
            {
                log.error("Uncached querry request from client not authorized to make new ones.");
                response.sendError(403, "Selected query not found in cache. To make out-of-cache queries you need special permission.");
            }
            return;
        }

        response.setContentType("application/json");
        response.getWriter().write(queryResponse);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    /**
     * Gets query JSON string from the cache.
     *
     * @param response
     * @throws IOException
     */
    private String getQueryResult(BingQuery bingQuery, int clientAllowed, HttpServletResponse response) throws IOException
    {

        Response cachedResponse = DBManager.instance().retrieveCachedResult(bingQuery, clientAllowed);

        //If there is no cached response, checks if client can make paid requests. If yes, makes bing request
        //If the cached response exists, checks if it is expired AND if client can make paid requests. If both are yes, makes bing request
        //If the cached response exists, but isn't outdated, client can't make bing requests, or the re-issued bing request failed, returs last available response
        String res;
        if(cachedResponse == null)
        {
            res = null;
            if(clientAllowed == 1)
            {
                HttpResponse bingResponse = BingApiService.getResponseString(bingQuery, clientKey);
                res = readJsonResponseAsString(bingResponse);
                headersToServletHeaders(response, bingResponse);
                log.debug("Query not in cache; response requested and recorded.");
            }
            else
            {
                log.debug("Query not in cache; client lacks priveleges to request one.");
            }
        }
        else
        {
            if(cachedResponse.isExpired() && clientAllowed == 1)
            {
                HttpResponse bingResponse = BingApiService.getResponseString(bingQuery, clientKey);
                res = readJsonResponseAsString(bingResponse);
                headersToServletHeaders(response, bingResponse);

                if(res == null)
                {
                    log.debug("Query in cache, but expired; request for a fresher response failed.");
                    res = cachedResponse.getText();
                }
                else
                {
                    log.debug("Query in cache, but expired; fresher response requested and recorded.");
                }
            }
            else
            {
                log.debug("Query in cache. Returning.");
                res = cachedResponse.getText();
            }
        }

        return res;
    }

    private static void headersToServletHeaders(HttpServletResponse sres, HttpResponse hres)
    {
        Header[] headers = hres.getAllHeaders();
        for(Header h : headers)
        {
            sres.addHeader(h.getName(), h.getValue());
        }
    }

    private static String readJsonResponseAsString(HttpResponse res) throws IOException
    {
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent(), "UTF-8"));
        StringBuilder result = new StringBuilder();
        String line;
        while((line = rd.readLine()) != null)
        {
            result.append(line);
        }
        return result.toString();
    }

}
