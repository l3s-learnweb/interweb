package de.l3s.interwebj.connector.vimeo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

public class Vimeo
{
    private static final String token = "***REMOVED***";

    public static void main(String[] args) throws Exception
    {
	String query = "angela merkel";
	String pageNumber = "1";
	String itemsPerPage = "10";

	System.out.println();
	//System.setProperty("https.protocols", "TLSv1.1");
	System.exit(0);

    }

    private static String HttpRequest(String url) throws Exception
    {
	URL obj = new URL(url);

	//create connection
	HttpURLConnection con = (HttpURLConnection) obj.openConnection();

	//add request header
	//con.setRequestProperty("User-Agent", "PortalsUpdates/0.1 +do not have any website yet, email address: ...);
	con.addRequestProperty("Accept", "application/vnd.vimeo.*+json; version=3.2");
	con.addRequestProperty("Authorization", "bearer " + token);

	StringBuilder response;

	//read response
	try(BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream())))
	{
	    String inputLine;
	    response = new StringBuilder();
	    while((inputLine = in.readLine()) != null)
	    {
		response.append(inputLine);
	    }
	}
	return response.toString();
    }

    public JSONObject searchVideos(String query, String pageNumber, String itemsPerPage) throws JSONException, UnsupportedEncodingException, Exception
    {

	return new JSONObject(HttpRequest("https://api.vimeo.com/videos?page=" + pageNumber + "&per_page=" + itemsPerPage + "&query=" + URLEncoder.encode(query, "UTF-8")));
    }

}
