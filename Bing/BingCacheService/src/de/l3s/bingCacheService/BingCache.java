package de.l3s.bingCacheService;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import de.l3s.bingCacheService.entity.Response;
import de.l3s.bingService.models.query.BingQuery;

public class BingCache
{

    private final static Logger log = LogManager.getLogger(BingCache.class);
    private static BingCache instance;

    private final LoadingCache<BingQuery, Response> cache; // latest response of a BingQuery
    private final DBManager dbManager;

    public static synchronized BingCache getInstance()
    {
        if(instance == null)
            instance = new BingCache();
        return instance;
    }

    private BingCache()
    {
        dbManager = DBManager.getInstance();

        cache = CacheBuilder.newBuilder().maximumSize(3000000).build(new CacheLoader<BingQuery, Response>()
        {
            @Override
            public Response load(BingQuery query) throws SQLException, ResponseNotFoundException
            {
                log.debug("try to load query from db: " + query);

                int queryId = dbManager.getQueryIdByQuery(query);

                if(queryId < 0) // query not seen before
                {
                    queryId = dbManager.addQuery(query);
                }

                query.setQueryId(queryId);

                Response response = dbManager.getLatestResponseByQueryId(queryId);

                if(null == response)
                    throw new ResponseNotFoundException();
                return response;

            }
        });
    }

    private class ResponseNotFoundException extends Exception
    {
        private static final long serialVersionUID = -8891583601122853274L;
    }

    /**
     * Returns the latest response for the given query. Or null if no cached results exists.
     * Will assign a unique id to the Bingquery object
     *
     * @param query
     * @return
     */
    public Response getLatestResponseByQuery(BingQuery query)
    {
        try
        {
            return cache.get(query);
        }
        catch(ExecutionException e)
        {
            if(e.getCause().getClass() == ResponseNotFoundException.class)
                log.debug("Response not found");
            else
                log.error("Can't load entry from cache for query = " + query, e);
        }
        return null;
    }

    public void put(BingQuery query, Response response)
    {
        cache.put(query, response);
    }
}
