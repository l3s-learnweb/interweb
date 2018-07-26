package de.l3s.bingCacheService;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import de.l3s.bingCacheService.entity.Response;
import de.l3s.bingService.services.BingApiService;

public class Controller extends HttpServlet {

	private static final long serialVersionUID = 4824768584324474328L;
	private final static Logger log = Logger.getLogger(Controller.class);

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Received POST request");
		//Checks if client has the correct key
		String key = request.getHeader("Ocp-Apim-Subscription-Key");
		int clientAllowed = DBManager.instance().checkClientPriveleges(key);

		if(clientAllowed == -1){
			log.error("Subscription key is not found.");
			response.sendError(403, "Subscription key is not found. This is required to use this service.");
			return;
		}

		String q = request.getParameter("q");
		if(q==null){
			log.error("Query incorrect or not found.");
			response.sendError(400, "POST request parameters not found or are incorrectly formed.");
			return;
		}

		String offset = request.getParameter("offset");
		String freshness = request.getParameter("freshness");
		String safeSearch = request.getParameter("safeSearch");
		String mkt = request.getParameter("mkt");
		String lang= request.getParameter("setLang");

		System.out.println("Received query with following params. Query: "+q+". Offset: "+offset+".  Freshness: "+freshness+". Safe search: "+safeSearch+". Market: "+mkt+". Language: "+lang);
		System.out.println("Client key: "+key);

		String queryResponse = getQueryResult(q, mkt, lang, offset, freshness, safeSearch, key, clientAllowed);
		System.out.println("RESPONSE: "+queryResponse);

		if(queryResponse==null){
			if(clientAllowed==1){
				log.error("Querry could not be made or retrieved. ");
				response.sendError(500, "Querry could not be made or retrieved. Please contact the administrators.");
			}
			else{
				log.error("Uncached querry request from client not authorized to make new ones.");
				response.sendError(403, "Selected query not found in cache. To make out-of-cache queries you need special permission.");
			}
			return;
		}

		response.setContentType("application/json");
		response.getWriter().write(queryResponse);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		log.debug("Received GET request. Eh.");
		PrintWriter writer = response.getWriter();
		writer.write("Service is working. Please use a POST request to make the query.");
	}

	/**
	 * Gets query JSON string from the cache.
	 */
	private String getQueryResult(String query, String mkt, String lang, String off, String freshness, String safeSearch, String clientKey, int clientAllowed){
		int offset;

		try{
			offset = Integer.parseInt(off);
			if(offset%50!=0){
				log.error("Incorrect offset specified: "+offset+". Rounding it to nearest multiple of 50.");
				offset = offset-offset%50;
			}
			else{
				offset = offset-offset%50;
			}
		}
		catch(NumberFormatException e){
			offset=0;
		}

		if(lang==null){
			if(mkt==null){
				lang="EN";
			}

			lang=mkt.split("-")[1];
		}

		System.out.println("Trying to process query...");

		Response cachedResponse = DBManager.instance().retrieveCachedResult(query, mkt, lang, offset, freshness, safeSearch, clientAllowed);

		//If there is no cached response, checks if client can make paid requests. If yes, makes bing request
		//If the cached response exists, checks if it is expired AND if client can make paid requests. If both are yes, makes bing request
		//If the cached response exists, but isn't outdated, client cant make bing requests, or the re-issued bing request failed, returs last available response
		String res;
		if(cachedResponse==null){
			System.out.println("Cached response doesnt exist. Checking if client can make requests...");
			res=null;
			if(clientAllowed==1){
				System.out.println("Client allowed to make requests. Attempting...");
				res=makeQueryToServer(query, mkt, lang, offset, freshness, safeSearch, clientKey);
				System.out.print("Received response: "+res);
			}
		}
		else{
			System.out.println("Cached response exists. Checking freshness...");
			if(cachedResponse.isExpired() && clientAllowed==1){
				System.out.println("Expired and client allowed. Attempting...");
				res=makeQueryToServer(query, mkt, lang, offset, freshness, safeSearch, clientKey);
				System.out.print("Received response: "+res);

				if(res==null){
					System.out.print("Received null, returning cached result.");
					res=cachedResponse.getText();
				}
			}
			else{
				System.out.println("Not expired or client lacks priveleges. Returning cached results.");
				res=cachedResponse.getText();
			}
		}


		return res;
	}

	/**
	 * Makes query to the BingClient.
	 */
	private String makeQueryToServer(String query, String mkt, String lang, int offset, String freshness, String safeSearch, String clientKey){
		String bingResponse = BingApiService.getResponseString(query, mkt, lang, offset,freshness, safeSearch, clientKey);
		if(bingResponse != null){
			System.out.println("Request successful, recoring to DB");
			DBManager.instance().addRequest(query, mkt, lang, offset, freshness, safeSearch, clientKey, bingResponse);
		}

		return bingResponse;
	}

}
