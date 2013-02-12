package de.l3s.interwebj.connector.dbpedia;


import static de.l3s.interwebj.util.Assertions.notNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.imageio.stream.FileImageInputStream;

import org.apache.commons.lang.NotImplementedException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

import de.l3s.dbpedia.ArrayOfResult;
import de.l3s.dbpedia.Result;
import de.l3s.interwebj.AuthCredentials;
import de.l3s.interwebj.InterWebException;
import de.l3s.interwebj.Parameters;
import de.l3s.interwebj.config.Configuration;
import de.l3s.interwebj.core.AbstractServiceConnector;
import de.l3s.interwebj.core.ServiceConnector;
import de.l3s.interwebj.query.Query;
import de.l3s.interwebj.query.QueryResult;
import de.l3s.interwebj.query.ResultItem;
import de.l3s.interwebj.query.Thumbnail;
import de.l3s.interwebj.query.UserSocialNetworkResult;
import de.l3s.interwebj.socialsearch.SocialSearchQuery;
import de.l3s.interwebj.socialsearch.SocialSearchResult;


public class DbpediaConnector
    extends AbstractServiceConnector
{
	
	private static final long MAX_RESULT_COUNT = 50L;	
	private static final String APP_ID = "E42FC069A142DB0CE26C4B3C428854F7C49DA01F";
	

	public DbpediaConnector(Configuration configuration)
	{
		this(configuration, null);
	}
	

	public DbpediaConnector(Configuration configuration,
	                       AuthCredentials consumerAuthCredentials)
	{
		super(configuration);
		setAuthCredentials(consumerAuthCredentials);
	}
	

	@Override
	public de.l3s.interwebj.Parameters authenticate(String callbackUrl)
	    throws InterWebException
	{
		// authentication is not supported. do nothing
		return null;
	}
	

	@Override
	public ServiceConnector clone()
	{
		return new DbpediaConnector(getConfiguration(), getAuthCredentials());
	}
	

	@Override
	public AuthCredentials completeAuthentication(Parameters params)
	    throws InterWebException
	{
		// authentication is not supported. do nothing
		return null;
	}
	

	@Override
	public QueryResult get(Query query, AuthCredentials authCredentials)
	    throws InterWebException
	{
		notNull(query, "query");
		
		QueryResult queryResult = new QueryResult(query);
		
		query.getQuery();
		
		Client client=Client.create();
		
		WebResource resource = client.resource("http://lookup.dbpedia.org/api/search.asmx/KeywordSearch").
		queryParam("QueryString",query.getQuery());
		
		ArrayOfResult results = resource.get(ArrayOfResult.class);
		queryResult.setTotalResultCount(results.getResult().size());
		int index=0;
		
		
		for(Result r: results.getResult())
		{
			
			queryResult.addResultItem(convertResult(r, index, results.getResult().size()));
			index++;
		}
		
		
		
		return queryResult;
	}

	private ResultItem convertResult(Result r, int index, int totalResultCount) {
		
		ResultItem resultItem = new ResultItem(getName());
		resultItem.setType(Query.CT_TEXT);
		resultItem.setTitle(r.getLabel());
		resultItem.setUrl(r.getURI()); 
		resultItem.setRank(index);
		resultItem.setTotalResultCount(totalResultCount);
		
		
		
	/*
		String thumb = getThumbnail(r.getURI());
		
		if(thumb!=null){
		resultItem.setThumbnails(createThumbnails(thumb));
		resultItem.setImageUrl(resultItem.getThumbnails().iterator().next().getUrl());
		}
*/
		return resultItem;
	}

	private String getThumbnail(String uri) {
		
		String dataurl = "http://dbpedia.org/data/"+uri.substring(uri.lastIndexOf("/"))+".rdf";
		
		try {
            URL google = new URL(dataurl);
            URLConnection yc = google.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(yc
                    .getInputStream()));
            String inputLine;
            StringBuffer sb=new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
               
 sb.append(inputLine);
            }
            in.close();
            
            String input=sb.toString();
            String token="<foaf:depiction rdf:resource=\"";
            int start=input.indexOf(token);
            if(start<10){return null;}
            start+=token.length();
            int end=input.indexOf("\"",start+1);
            input=input.substring(start,end);
            return input;
        } catch (Exception e) {
            e.printStackTrace();
        }
		return null;
	}

public static void main(String[] args) {
	DbpediaConnector c;
	try {
		c = new DbpediaConnector(new Configuration(new FileInputStream(new File("connector-config.xml"))));
		System.out.println(c.getThumbnail("http://dbpedia.org/resource/Wolfgang_Borchert"));
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}
	private Set<Thumbnail> createThumbnails(String imageurl) 
	{		
	
		
		SortedSet<Thumbnail> thumbnails = new TreeSet<Thumbnail>();
		thumbnails.add(new Thumbnail(imageurl, 75, 75));
		thumbnails.add(new Thumbnail(imageurl, 100, 100));
		thumbnails.add(new Thumbnail(imageurl, 240, 240));
		thumbnails.add(new Thumbnail(imageurl, 500, 500));

		return thumbnails;
	}
	@Override
	public String getEmbedded(AuthCredentials authCredentials,
	                          String url,
	                          int maxWidth,
	                          int maxHeight)
	    throws InterWebException
	{
		// TODO: generate somehow site thumbnails
		return null;
	}
	

	@Override
	public String getUserId(AuthCredentials authCredentials)
	    throws InterWebException
	{
		// not supported. do nothing
		return null;
	}
	

	@Override
	public boolean isConnectorRegistrationDataRequired()
	{
		return false;
	}
	

	@Override
	public boolean isRegistered()
	{
		return true;
	}
	

	@Override
	public boolean isUserRegistrationDataRequired()
	{
		return false;
	}
	

	@Override
	public boolean isUserRegistrationRequired()
	{
		return false;
	}
	

	@Override
	public ResultItem put(byte[] data,
	                String contentType,
	                Parameters params,
	                AuthCredentials authCredentials)
	    throws InterWebException
	{
		// not supported. do nothing
		return null;
	}
	

	@Override
	public void revokeAuthentication()
	    throws InterWebException
	{
		// not supported. do nothing
	}

	/*
	private ResultItem convertWebResult(WebResult webResult,
            int index,
            long totalResultCount)
	{
		ResultItem resultItem = new ResultItem(getName());
		resultItem.setType(Query.CT_TEXT);
		if(webResult.getTitle() != null)
		resultItem.setTitle(webResult.getTitle().replace(""+(char)57344, "<b>").replace(""+(char)57345, "</b>"));
		if(webResult.getDescription() != null)
			resultItem.setDescription(webResult.getDescription().replace(""+(char)57344, "<b>").replace(""+(char)57345, "</b>"));
		resultItem.setUrl(webResult.getUrl());
		resultItem.setRank(index);
		resultItem.setTotalResultCount(totalResultCount);
		return resultItem;
	}
	
	private ResultItem convertImageResult(ImageResult imageResult,
            int index,
            long totalResultCount)
	{
		ResultItem resultItem = new ResultItem(getName());
		resultItem.setType(Query.CT_IMAGE);
		resultItem.setTitle(imageResult.getTitle());
		resultItem.setUrl(imageResult.getUrl());
		resultItem.setRank(index);
		resultItem.setTotalResultCount(totalResultCount);
		resultItem.setThumbnails(createThumbnails(imageResult.getThumbnail()));

		return resultItem;
	}
	
	private ResultItem convertVideoResult(VideoResult videoResult,
            int index,
            long totalResultCount)
	{
		ResultItem resultItem = new ResultItem(getName());
		resultItem.setType(Query.CT_VIDEO);
		resultItem.setTitle(videoResult.getTitle());
		resultItem.setUrl(videoResult.getPlayUrl());
		resultItem.setRank(index);
		resultItem.setTotalResultCount(totalResultCount);
		resultItem.setThumbnails(createThumbnails(videoResult.getStaticThumbnail()));

		return resultItem;
	}
	
*/
	
	



	@Override
	public Set<String> getTags(String username, int maxCount) throws IllegalArgumentException, IOException {
		throw new NotImplementedException();
	}


	@Override
	public Set<String> getUsers(Set<String> tags, int maxCount) throws IOException, InterWebException {
		throw new NotImplementedException();
	}


	@Override
	public UserSocialNetworkResult getUserSocialNetwork(String userid,
			AuthCredentials authCredentials) throws InterWebException {
		throw new NotImplementedException();
	}


	@Override
	public SocialSearchResult get(SocialSearchQuery query,
			AuthCredentials authCredentials) {
		throw new NotImplementedException();
	}
}
