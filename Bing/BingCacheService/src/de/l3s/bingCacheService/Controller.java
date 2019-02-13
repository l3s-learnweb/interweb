package de.l3s.bingCacheService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.l3s.bingCacheService.entity.Client;
import de.l3s.bingCacheService.entity.Response;
import de.l3s.bingService.models.query.BingQuery;
import de.l3s.bingService.services.BingApiService;

public class Controller extends HttpServlet
{
    private static final long serialVersionUID = 4824768584324474328L;
    private final static Logger log = LogManager.getLogger(Controller.class);

    private final BingCache cache;
    private final DBManager dbManager;

    public Controller()
    {
        log.debug("Init Controller servlet");
        cache = BingCache.getInstance();
        dbManager = DBManager.getInstance();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        try
        {
            //Checks if client has the correct key
            String key = request.getHeader("Ocp-Apim-Subscription-Key");
            Client client = DBManager.getInstance().getClient(key);

            if(null == client)
            {
                log.error("Subscription key is not found.");
                response.sendError(403, "Subscription key is not found. This is required to use this service.");
                return;
            }

            BingQuery bingQuery = null;
            try
            {
                bingQuery = new BingQuery(request.getParameter("q"), request.getParameter("mkt"), request.getParameter("setLang"), request.getParameter("offset"), request.getParameter("freshness"), request.getParameter("safeSearch"));
            }
            catch(IllegalArgumentException e)
            {
                log.error("Invalid parameter: " + e.getMessage());
                response.sendError(400, "Invalid parameter: " + e.getMessage());
                return;
            }
            log.debug("Received query with following params: " + bingQuery);

            Response queryResponse = cache.getLatestResponseByQuery(bingQuery);

            if(queryResponse == null && !client.canMakePaidRequests())
            {
                log.error("Uncached query request from client not authorized to make new ones.");
                response.sendError(403, "Selected query not found in cache. To make out-of-cache queries you need special permission.");
                return;
            }

            //if no cached result or result is older than 30 days, issue new request
            if(queryResponse == null || Duration.between(queryResponse.getTimestamp(), LocalDateTime.now()).toDays() > 30)
            {
                queryResponse = getFreshResponseFromBing(bingQuery, client);
            }

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(queryResponse.getResponse());

        }
        catch(Exception e1)
        {
            log.fatal("Internal error", e1);
            response.sendError(500, "Internal error.");
        }
    }

    private Response getFreshResponseFromBing(BingQuery bingQuery, Client client) throws SQLException, IOException
    {
        String rawBingResponse = readJsonResponseAsString(BingApiService.getResponseString(bingQuery, client.getBingApiKey()));

        if(rawBingResponse.startsWith("\"_type\": \"ErrorResponse\""))
        {
            log.error("Received error; Query: {}; Response: {}", bingQuery, rawBingResponse);
        }
        // TODO handle bing errors and  retry header
        log.debug("response: " + rawBingResponse);

        Response queryResponse = new Response(rawBingResponse, bingQuery.getQueryId(), client.getId());
        dbManager.addResponse(queryResponse);
        cache.put(bingQuery, queryResponse);

        return queryResponse;
    }

    /*
     *
         DBManager dbManager = DBManager.instance();
        Response cachedResponse = dbManager.getLatestResponseByQueryId(bingQuery);
    
        //If there is no cached response, checks if client can make paid requests. If yes, makes bing request
        //If the cached response exists, checks if it is expired AND if client can make paid requests. If both are yes, makes bing request
        //If the cached response exists, but isn't outdated, client can't make bing requests, or the re-issued bing request failed, returns last available response
        String res;
        if(!client.canMakePaidRequests())
        {
            if(cachedResponse == null)
            {
                log.debug("Query not in cache; client lacks priveleges to request one.");
                return null;
            }
        }
        else
        {
            if(cachedResponse == null) // || cachedResponse.isExpired() && clientAllowed == 1)
            {
                HttpResponse bingResponse = BingApiService.getResponseString(bingQuery, client.getBingApiKey());
                String newResponse = readJsonResponseAsString(bingResponse);
    
                if(newResponse != null)
                {
                    log.debug("Query in cache, but expired; request for a fresher response failed.");
                    res = cachedResponse.getResponse();
                }
                else
                    return null;
            }
        }
    
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
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
