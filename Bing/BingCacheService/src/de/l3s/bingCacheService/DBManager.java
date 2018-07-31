package de.l3s.bingCacheService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import de.l3s.bingCacheService.entity.Response;

public class DBManager {

	private static DBManager instance = null;

	private Logger log = Logger.getLogger(DBManager.class);
	private Connection conn;

	private static final String connectionString="jdbc:mysql://learnweb.l3s.uni-hannover.de/bing?useLegacyDatetimeCode=false&serverTimezone=UTC";
	private static final String username="bing";
	private static final String pass="QvkF0hDbKLnTOwgJ";
	private static final int timeoutDays=30;

	//TODO: This is a debug method. Run this to do actions on the databse, like look at requests, queries etc
	public static void main(String[] args) throws SQLException{
		System.out.println("Connection starting:");
		Connection conn2 = DriverManager.getConnection(connectionString,username,pass);

		PreparedStatement select = conn2.prepareStatement("SELECT * FROM b_request");
		ResultSet rs = select.executeQuery();

		ResultSetMetaData metadata = rs.getMetaData();
		int columnCount = metadata.getColumnCount();

		while (rs.next()) {
			String row = "";
			for (int i = 1; i <= columnCount; i++) {
				row += rs.getString(i) + ", ";
			}
			System.out.println(row);
		}

	}

	public synchronized static DBManager instance(){
		if (instance == null) {
			instance = new DBManager();
		}
		return instance;
	}

	private DBManager(){
		try{
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(connectionString, username, pass);
		}
		catch(Exception e){
			System.out.println(e);
			log.fatal("DBManager initialization failed. Error: ",e);
		}
	}

	/**
	 * Adds a client to the client lists
	 * @param key Client key
	 * @param description Client description
	 * @param allowPaidSearch Whether the client is allowed to make new paid bing searches. Clients that are not allowed to use paid search can still use cached searches.
	 */
	public void addClient(String key, String description, int allowPaidSearch){
		if(conn==null){
			log.error("Initial server connection error. Serviced must be restarted.");
			return;
		}

		try(PreparedStatement insert = conn.prepareStatement("INSERT INTO b_client(`key`,description,allow_paid_requests)  VALUES(?,?,?)")){
			insert.setString(1, key);
			insert.setString(2, description);
			insert.setString(3, ""+allowPaidSearch);

			insert.execute();
		} catch (SQLException e) {
			log.error("Adding client failed. SQL error: ", e);
		}
	}

	/**
	 * Adds a given query to the cache and also logs it into database.
	 * @param query Query string
	 * @param mrkt String specifying the market. Can be null
	 * @param lang String specifying the language. Can be null
	 * @param offset Query result offset. Default is 0.
	 * @param freshness How fresh are results. Allowed values: 'Day','Week','Month'
	 * @param safeSearch Whether safe search is on. Allowed values: 'Off','Moderate','Strict'
	 * @return ID of the inserted query. Returns -1 if an error happens and insertion fails
	 */
	public int addQuery(String query, String mrkt, String lang, int offset, String freshness, String safeSearch){
		if(conn==null){
			log.error("Initial server connection error. Serviced must be restarted.");
			return -1;
		}

		try(PreparedStatement insert = conn.prepareStatement("INSERT INTO b_query (query, market, lang, offset, freshness, safeSearch) VALUES (?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)){
			if(lang==null){
				lang="EN";
			}

			if(mrkt==null){
				mrkt="None";
			}

			if(freshness==null){
				freshness="Day";
			}

			if(safeSearch==null){
				safeSearch="Moderate";
			}

			insert.setString(1, query);
			insert.setString(2, mrkt);
			insert.setString(3, lang);
			insert.setInt(4, offset);
			insert.setString(5, freshness);
			insert.setString(6, safeSearch);

			insert.execute();

			ResultSet rs = insert.getGeneratedKeys();

			//Returns ID of inserted row OR -1 if failed
			if(rs.next()){
				int id = rs.getInt(1);
				if(id==0){
					return -1;
				}
				else{
					return id;
				}
			}
			else{
				return -1;
			}


		} catch (SQLException e) {
			log.error("Adding query failed. SQL error: ", e);
			return -1;
		}


	}

	/**
	 * Adds a given query to the cache and also logs it into database.
	 * @param query Query string
	 * @param mrkt String specifying the market. Can be null
	 * @param lang String specifying the language. Can be null
	 * @param offset Query result offset. Default is 0.
	 * @param freshness How fresh are results. Allowed values: 'Day','Week','Month'
	 * @param safeSearch Whether safe search is on. Allowed values: 'Off','Moderate','Strict'
	 * @return ID of the selected query. Returns -1 if no query has been found
	 */
	public int getQueryID(String query, String mrkt, String lang, int offset, String freshness, String safeSearch){
		if(conn==null){
			log.error("Initial server connection error. Serviced must be restarted.");
			return -1;
		}

		try(PreparedStatement select = conn.prepareStatement("SELECT query_id FROM b_query WHERE (query=?) AND ( market=?) AND ( lang=?) AND ( offset=?) AND ( freshness=?) AND ( safeSearch=?)")){
			if(lang==null){
				lang="EN";
			}

			if(mrkt==null){
				mrkt="None";
			}

			if(freshness==null){
				freshness="Day";
			}

			if(safeSearch==null){
				safeSearch="Moderate";
			}

			select.setString(1, query);
			select.setString(2, mrkt);
			select.setString(3, lang);
			select.setInt(4, offset);
			select.setString(5, freshness);
			select.setString(6, safeSearch);

			ResultSet rs = select.executeQuery();

			//Returns ID of inserted row OR -1 if failed
			if(rs.next()){
				int id = rs.getInt(1);
				if(id==0){
					return -1;
				}
				else{
					return id;
				}
			}


		} catch (SQLException e) {
			log.error("Adding query failed. SQL error: ", e);
		}

		return -1;

	}

	/**
	 * Adds the specified request to the cache.
	 * @param query Query string
	 * @param mrkt String specifying the market. Can be null
	 * @param lang String specifying the language. Can be null
	 * @param offset Query result offset. Default is 0.
	 * @param freshness How fresh are results. Allowed values: 'Day','Week','Month'
	 * @param safeSearch Whether safe search is on. Allowed values: 'Off','Moderate','Strict'
	 * @param clientKey Client's unique key
	 */
	public void addRequest(String query, String mkt, String lang, int off, String freshness, String safeSearch, String clientKey, String response){
		if(conn==null){
			log.error("Initial server connection error. Serviced must be restarted.");
			return;
		}

		int clientID = getClientID(clientKey);
		int queryID = addQuery(query, mkt, lang, off, freshness, safeSearch);

		if (clientID==-1 || queryID==-1){
			log.error("Adding request failed due to failure of retrieving IDs. Client ID: "+clientID+" Query ID: "+queryID);
		}
		try(PreparedStatement insert = conn.prepareStatement("INSERT INTO b_request (query_id, client_id, response, timestamp) VALUES (?,?,?,?)")){
			insert.setInt(1, queryID);
			insert.setInt(2, clientID);
			insert.setString(3, response);
			insert.setTimestamp(4, new java.sql.Timestamp(new Date().getTime()));

			insert.execute();
		} catch (SQLException e) {
			log.error("Adding request failed. SQL error: ", e);
		}
	}

	/**
	 * Retrieves cached results by specific arameters.
	 * @param query Query string
	 * @param mrkt String specifying the market. Can be null
	 * @param lang String specifying the language. Can be null
	 * @param offset Query result offset. Default is 0.
	 * @param freshness How fresh are results. Allowed values: 'Day','Week','Month'
	 * @param safeSearch Whether safe search is on. Allowed values: 'Off','Moderate','Strict'
	 * @param clientAllowed Whether the given client is allowed to make paid requests
	 * @return Response object containing the json of the results and whether it needs to be updated. Returns null if the result isn't found.
	 */
	public Response retrieveCachedResult(String query, String mrkt, String lang, int offset, String freshness, String safeSearch, int clientAllowed){
		if(conn==null){
			log.error("Initial server connection error. Serviced must be restarted.");
			return null;
		}

		try(PreparedStatement select = conn.prepareStatement("SELECT response, timestamp FROM b_query JOIN b_request ON b_query.query_id = b_request.query_id WHERE (query=?) AND ( market=?) AND ( lang=?) AND ( offset=?) AND ( freshness=?) AND ( safeSearch=?)")){
			if(lang==null){
				lang="EN";
			}

			if(mrkt==null){
				mrkt="None";
			}

			if(freshness==null){
				freshness="Day";
			}

			if(safeSearch==null){
				safeSearch="Moderate";
			}

			select.setString(1, query);
			select.setString(2, mrkt);
			select.setString(3, lang);
			select.setInt(4, offset);
			select.setString(5, freshness);
			select.setString(6, safeSearch);

			ResultSet rs = select.executeQuery();

			if(rs.next()){
				//Checks if client is allowed to make new requests. If yes, then returns special response to requery.
				if(clientAllowed==1){
					Calendar cal = Calendar.getInstance();
			        cal.setTime(new Date());
			        cal.add(Calendar.DAY_OF_MONTH, -timeoutDays);
			        Date threshold = cal.getTime();

			        Date timestamp = rs.getTimestamp(2);

			        if(timestamp.before(threshold)){
			        	return new Response(rs.getString(1), true);
			        }
				}

				return new Response(rs.getString(1),false);
			}
		} catch (SQLException e) {
			log.error("Response fetching failed. SQL error: ", e);
		}

		return null;
	}

	/**
	 * Checks by key whether a given client has paid request priveleges.
	 * @param key Client's key
	 * @return 0 if not allowed, 1 if allowed and -1 if client isn't in the database.
	 */
	public int checkClientPriveleges(String key){
		if(conn==null){
			log.error("Initial server connection error. Serviced must be restarted.");
			return -1;
		}

		if(key==null){
			return -1;
		}
		try(PreparedStatement select = conn.prepareStatement("SELECT allow_paid_requests FROM b_client WHERE `key`=?")){
			select.setString(1, key);
			ResultSet rs = select.executeQuery();

			if(rs.next()){
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			log.error("Checking client failed. SQL error: ", e);
		}

		return -1;
	}

	/**
	 * Gets the id of the client by given search key
	 * @param key Client's key
	 * @return ID. Returns -1 if ID is not found.
	 */
	public int getClientID(String key){
		if(conn==null){
			log.error("Initial server connection error. Serviced must be restarted.");
			return -1;
		}

		try(PreparedStatement select = conn.prepareStatement("SELECT client_id FROM b_client WHERE `key`=?")){
			select.setString(1, key);
			ResultSet rs = select.executeQuery();

			if(rs.next()){
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			log.error("Checking client failed. SQL error: ", e);
		}

		return -1;
	}

	/**
	 * Updates a given request with a fresh search on a new date
	 * @param query_id
	 * @param client_id
	 * @param response
	 */
	public void updateRequest(String query, String mkt, String lang, int off, String freshness, String safeSearch, String clientKey, String response){
		if(conn==null){
			log.error("Initial server connection error. Serviced must be restarted.");
			return;
		}

		int clientID = getClientID(clientKey);
		int queryID = getQueryID(query, mkt, lang, off, freshness, safeSearch);

		if (clientID==-1 || queryID==-1){
			log.error("Updating request failed due to failure of retrieving IDs. Client ID: "+clientID+" Query ID: "+queryID);
		}

		try(PreparedStatement insert = conn.prepareStatement("UPDATE b_request SET response=?, timestamp=?, client_id=? WHERE query_id=?")){
			insert.setString(1, response);
			insert.setTimestamp(2, new java.sql.Timestamp(new Date().getTime()));
			insert.setInt(3, clientID);
			insert.setInt(4, queryID);

			insert.execute();
		} catch (SQLException e) {
			log.error("Adding request failed. SQL error: ", e);
		}
	}
}
