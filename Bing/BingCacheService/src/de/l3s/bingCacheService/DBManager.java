package de.l3s.bingCacheService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.l3s.bingCacheService.entity.Client;
import de.l3s.bingCacheService.entity.Response;
import de.l3s.bingService.models.query.BingQuery;

public class DBManager
{
    private static DBManager instance = null;
    private final static Logger log = LogManager.getLogger(DBManager.class);
    private Connection conn;

    //private static final String connectionString = "jdbc:mysql://learnweb.l3s.uni-hannover.de/bing?useLegacyDatetimeCode=false&serverTimezone=UTC";
    private static final String connectionString = "jdbc:mysql://localhost/bing?useLegacyDatetimeCode=false&serverTimezone=UTC";
    private static final String username = "bing";
    private static final String pass = "QvkF0hDbKLnTOwgJ";

    public synchronized static DBManager getInstance()
    {
        if(instance == null)
        {
            instance = new DBManager();
        }
        return instance;
    }

    private DBManager()
    {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(connectionString, username, pass);
        }
        catch(Exception e)
        {
            log.fatal("Can't create connection", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds a given query to the cache and also logs it into database.
     *
     * @return ID of the inserted query.
     * @throws SQLException
     */
    public int addQuery(BingQuery query) throws SQLException
    {
        try(PreparedStatement insert = conn.prepareStatement("INSERT INTO b_query (query, market, lang, offset, freshness, safeSearch) VALUES (?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS))
        {
            insert.setString(1, query.getQuery());
            insert.setString(2, query.getMarket());
            insert.setString(3, query.getLanguage());
            insert.setInt(4, query.getOffset());
            insert.setString(5, query.getFreshness().name());
            insert.setString(6, query.getSafesearch().name());
            insert.execute();

            ResultSet rs = insert.getGeneratedKeys();

            //Returns ID of inserted row OR -1 if failed
            if(!rs.next())
                throw new SQLException("database error: no id generated");

            int id = rs.getInt(1);
            query.setQueryId(id);
            return id;
        }

    }

    /**
     * Returns queryid of the given query or -1 if the query is not yet stored
     *
     * @param query
     * @return
     * @throws SQLException
     */
    public int getQueryIdByQuery(BingQuery query) throws SQLException
    {
        try(PreparedStatement select = conn.prepareStatement("SELECT query_id FROM b_query WHERE (query=?) AND ( market=?) AND ( lang=?) AND ( offset=?) AND ( freshness=?) AND ( safeSearch=?)"))
        {
            select.setString(1, query.getQuery());
            select.setString(2, query.getMarket());
            select.setString(3, query.getLanguage());
            select.setInt(4, query.getOffset());
            select.setString(5, query.getFreshness().name());
            select.setString(6, query.getSafesearch().name());
            log.debug(select);
            ResultSet rs = select.executeQuery();

            //Returns ID of inserted row OR -1 if failed
            if(rs.next())
                return rs.getInt(1);

            return -1;
        }
    }

    /**
     * Retrieves cached results for the given query.
     *
     * @param query Query string
     * @param mrkt String specifying the market. Can be null
     * @param lang String specifying the language. Can be null
     * @param offset Query result offset. Default is 0.
     * @param freshness How fresh are results. Allowed values: 'Day','Week','Month'
     * @param safeSearch Whether safe search is on. Allowed values: 'Off','Moderate','Strict'
     * @param clientAllowed Whether the given client is allowed to make paid requests
     * @return Response object containing the json of the results and whether it needs to be updated. Returns null if the result isn't found.
     * @throws SQLException
     */
    public Response getLatestResponseByQueryId(int queryId) throws SQLException
    {
        try(PreparedStatement select = conn.prepareStatement("SELECT response_id, client_id, response, timestamp FROM b_response WHERE query_id = ? ORDER BY timestamp DESC LIMIT 1"))
        {
            select.setInt(1, queryId);

            ResultSet rs = select.executeQuery();

            if(rs.next())
            {
                return new Response(rs.getLong(1), queryId, rs.getInt(2), rs.getString(3), rs.getObject(4, LocalDateTime.class));
            }
        }

        return null;
    }

    /**
     *
     *
     * @param cacheKey Client's key
     * @return null if key is invalid
     * @throws SQLException
     */
    public Client getClient(String cacheKey) throws SQLException
    {
        if(StringUtils.isBlank(cacheKey))
            return null;

        try(PreparedStatement select = conn.prepareStatement("SELECT client_id, bing_api_key, description FROM b_client WHERE `cache_api_key`=?"))
        {
            select.setString(1, cacheKey);
            ResultSet rs = select.executeQuery();

            if(rs.next())
            {
                return new Client(rs.getInt(1), cacheKey, rs.getString(2));
            }
        }

        return null;
    }

    /**
     * Adds the given response to DB
     *
     * @throws SQLException
     */
    public void addResponse(Response response) throws SQLException
    {
        try(PreparedStatement insert = conn.prepareStatement("INSERT INTO b_response (query_id, client_id, response, timestamp) VALUES(?,?,?,?)", Statement.RETURN_GENERATED_KEYS))
        {
            insert.setInt(1, response.getQueryId());
            insert.setInt(2, response.getClientId());
            insert.setString(3, response.getResponse());
            insert.setObject(4, response.getTimestamp());
            insert.execute();

            ResultSet rs = insert.getGeneratedKeys();

            //Returns ID of inserted row OR -1 if failed
            if(!rs.next())
                throw new SQLException("database error: no id generated");

            response.setResponseId(rs.getInt(1));
        }
    }
}
