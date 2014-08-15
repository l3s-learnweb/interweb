package de.l3s.interwebj.connector.bingAzure;


import static de.l3s.interwebj.util.Assertions.notNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringEscapeUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

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


public class BingAzureConnector extends AbstractServiceConnector
{	
	public BingAzureConnector() {
		super();
	}
	public BingAzureConnector(Configuration configuration)
	{
		this(configuration, null);
	}	

	public BingAzureConnector(de.l3s.interwebj.config.Configuration configuration, AuthCredentials consumerAuthCredentials)
	{
		super(configuration);
		setAuthCredentials(consumerAuthCredentials);
	}	

	@Override
	public Parameters authenticate(String callbackUrl)
	    throws InterWebException
	{
		// authentication is not supported. do nothing
		return null;
	}	

	@Override
	public ServiceConnector clone()
	{
		return new BingAzureConnector(getConfiguration(), getAuthCredentials());
	}	

	@Override
	public AuthCredentials completeAuthentication(Parameters params) throws InterWebException
	{
		// authentication is not supported. do nothing
		return null;
	}

	private static String convertHighlighting(String string)
	{
		if(string == null)
			return "";

		return string.replace("<","&lt;").replace(">","&gt;").replace(""+(char)57344, "<b>").replace(""+(char)57345, "</b>");
	}
	

	
	public final static void main(String[] args) throws IOException
	{
		//https://api.datamarket.azure.com/Bing/SearchWeb/v1/Web
		//https://api.datamarket.azure.com/Bing/Search/v1/Image
		AuthCredentials a = new AuthCredentials("ccaaeac1-04d8-4761-8a2c-5d2dfc5f2133", "KE+X3nVEVxvvxM/fZE+1FR4rpl27/nmzQB6VVqGB/2I="); // getAuthCredentials()
		WebResource resource = createWebResource("https://api.datamarket.azure.com/Bing/SearchWeb/v1/Web", a);
		resource = resource.queryParam("Query", "'test'");
		resource = resource.queryParam("$top", "10");
		resource = resource.queryParam("$skip", "5");
		resource = resource.queryParam("Market", "'"+ createMarket("en") +"'");
		resource = resource.queryParam("Options", "'EnableHighlighting'");


		ClientResponse response = resource.get(ClientResponse.class);

		SAXReader reader = new SAXReader();
		Document document;
		
		
        try {
			document = reader.read(response.getEntityInputStream());
			//System.out.println(document.asXML());
			//parse(document, Query.CT_IMAGE);
		} catch (DocumentException e) {
			e.printStackTrace();
			return;
		}
		
	
	}
	
	private static WebResource createWebResource(String apiUrl, AuthCredentials consumerAuthCredentials) 
	{
		Client client = Client.create();
		client.addFilter(new HTTPBasicAuthFilter(consumerAuthCredentials.getKey(), consumerAuthCredentials.getSecret())); 
		WebResource resource = client.resource(apiUrl);		
		return resource;
	}
	
	@Override
	public QueryResult get(Query query, AuthCredentials authCredentials) throws InterWebException
	{
		notNull(query, "query");
		
		authCredentials = getAuthCredentials();
		//authCredentials = new AuthCredentials("ccaaeac1-04d8-4761-8a2c-5d2dfc5f2133", "KE+X3nVEVxvvxM/fZE+1FR4rpl27/nmzQB6VVqGB/2I=");
		
		QueryResult results = new QueryResult(query);
		
		if(query.getContentTypes().contains(Query.CT_IMAGE))
			results.addQueryResult(getImage(query, authCredentials));
		
		if(query.getContentTypes().contains(Query.CT_TEXT))
			results.addQueryResult(getWeb(query, authCredentials));
		
		return results;
	}
	
	private QueryResult getImage(Query query, AuthCredentials authCredentials) throws InterWebException
	{
		WebResource resource = createWebResource("https://api.datamarket.azure.com/Bing/Search/v1/Image", authCredentials);
		resource = resource.queryParam("Query", "'"+ query.getQuery() +"'");
		resource = resource.queryParam("$top", Integer.toString(query.getResultCount()));
		resource = resource.queryParam("$skip", Integer.toString( (query.getPage()-1)*query.getResultCount() ));
		resource = resource.queryParam("Market", "'"+ createMarket(query.getLanguage()) +"'");
		resource = resource.queryParam("Options", "'EnableHighlighting'");
		
		return executeRequest(resource, Query.CT_IMAGE);	
	}	

	private QueryResult getWeb(Query query, AuthCredentials authCredentials) throws InterWebException
	{
		WebResource resource = createWebResource("https://api.datamarket.azure.com/Bing/SearchWeb/v1/Web", authCredentials);
		resource = resource.queryParam("Query", "'"+ query.getQuery() +"'");
		resource = resource.queryParam("$top", Integer.toString(query.getResultCount()));
		resource = resource.queryParam("$skip", Integer.toString( (query.getPage()-1)*query.getResultCount() ));
		resource = resource.queryParam("Market", "'"+ createMarket(query.getLanguage()) +"'");
		resource = resource.queryParam("Options", "'EnableHighlighting'");
		
		return executeRequest(resource, Query.CT_TEXT);
	}
	
	private QueryResult executeRequest(WebResource resource, String contentType)
	{
		ClientResponse response = resource.get(ClientResponse.class);
		SAXReader reader = new SAXReader();

        try {
        	Document document = reader.read(response.getEntityInputStream());
			return parse(document, contentType);
		} catch (DocumentException e) {
			e.printStackTrace();
			return new QueryResult(null);
		}		
	}
	
	private QueryResult parse(Document document, String contentType)
	{
		QueryResult queryResult = new QueryResult(null);
		
		List<Element> entrys = document.getRootElement().elements("entry");
		int index = 1;
		for(Element entry : entrys)
		{
			Element prop = entry.element("content").element("properties");
		
			ResultItem resultItem = new ResultItem(getName());
			resultItem.setType(contentType);
			resultItem.setTitle(convertHighlighting(prop.elementText("Title")));			
			resultItem.setDescription(convertHighlighting(prop.elementText("Description")));
			resultItem.setUrl(prop.elementText("Url"));
			resultItem.setRank(index++);
			
			Set<Thumbnail> thumbnails = new LinkedHashSet<Thumbnail>();
			
			if(contentType == Query.CT_IMAGE) // get image
			{
				String url = null;
				Integer width = null;
				Integer height = null;
				
				try {
					url = prop.elementText("MediaUrl");
					width = Integer.parseInt(prop.elementText("Width"));
					height = Integer.parseInt(prop.elementText("Height"));
				}
				catch(Exception e) {
					//ignore e.printStackTrace();
				}
				
				if(url != null && height != null && width != null)
				{					
					thumbnails.add(new Thumbnail(url, width, height));
				}					
			}

			
			Element thumbnailElement = prop.element("Thumbnail");
			if(thumbnailElement != null)
			{
				String url = null;
				Integer width = null;
				Integer height = null;
				
				try {
					url = thumbnailElement.elementText("MediaUrl");
					width = Integer.parseInt(thumbnailElement.elementText("Width"));
					height = Integer.parseInt(thumbnailElement.elementText("Height"));
				}
				catch(NumberFormatException e) {
					//ignore e.printStackTrace();
				}
				
				if(url != null && height != null && width != null)
				{					
					thumbnails.add(new Thumbnail(url, width, height));
				}			
			}
						
			resultItem.setThumbnails(thumbnails);
			
			queryResult.addResultItem(resultItem);
		}
		return queryResult;
	}
	

	@Override
	public String getEmbedded(AuthCredentials authCredentials,
	                          String url,
	                          int maxWidth,
	                          int maxHeight)
	    throws InterWebException
	{
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
	
	private static String createMarket(String language) 
	{
		if(language.equalsIgnoreCase("ar")) return "ar-XA";
		if(language.equalsIgnoreCase("bg")) return "bg-BG";
		if(language.equalsIgnoreCase("cs")) return "cs-CZ";
		if(language.equalsIgnoreCase("da")) return "da-DK";
		if(language.equalsIgnoreCase("de")) return "de-DE";
		if(language.equalsIgnoreCase("el")) return "el-GR";
		if(language.equalsIgnoreCase("es")) return "es-ES";
		if(language.equalsIgnoreCase("et")) return "et-EE";
		if(language.equalsIgnoreCase("fi")) return "fi-FI";
		if(language.equalsIgnoreCase("fr")) return "fr-FR";
		if(language.equalsIgnoreCase("he")) return "he-IL";
		if(language.equalsIgnoreCase("hr")) return "hr-HR";
		if(language.equalsIgnoreCase("hu")) return "hu-HU";
		if(language.equalsIgnoreCase("it")) return "it-IT";
		if(language.equalsIgnoreCase("ja")) return "ja-JP";
		if(language.equalsIgnoreCase("ko")) return "ko-KR";
		if(language.equalsIgnoreCase("lt")) return "lt-LT";
		if(language.equalsIgnoreCase("lv")) return "lv-LV";
		if(language.equalsIgnoreCase("nb")) return "nb-NO";
		if(language.equalsIgnoreCase("nl")) return "nl-NL";
		if(language.equalsIgnoreCase("pl")) return "pl-PL";
		if(language.equalsIgnoreCase("pt")) return "pt-PT";
		if(language.equalsIgnoreCase("ro")) return "ro-RO";
		if(language.equalsIgnoreCase("ru")) return "ru-RU";
		if(language.equalsIgnoreCase("sk")) return "sk-SK";
		if(language.equalsIgnoreCase("sl")) return "sl-SL";
		if(language.equalsIgnoreCase("sv")) return "sv-SE";
		if(language.equalsIgnoreCase("th")) return "th-TH";
		if(language.equalsIgnoreCase("tr")) return "tr-TR";
		if(language.equalsIgnoreCase("uk")) return "uk-UA";
		if(language.equalsIgnoreCase("zh")) return "zh-CN";		
		return "en-US";				
	}

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SocialSearchResult get(SocialSearchQuery query,
			AuthCredentials authCredentials) {
		// TODO Auto-generated method stub
		return null;
	}
}
