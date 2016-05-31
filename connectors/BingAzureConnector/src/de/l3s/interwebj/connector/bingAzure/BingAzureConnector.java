package de.l3s.interwebj.connector.bingAzure;


import static de.l3s.interwebj.util.Assertions.notNull;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;
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
import de.l3s.interwebj.core.Environment;
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
		WebResource resource = createWebResource("https://api.datamarket.azure.com/Bing/Search/v1/Composite", a);
		resource = resource.queryParam("Sources", "'web'");
		resource = resource.queryParam("Query", "'test'");
		resource = resource.queryParam("$top", "10");
		resource = resource.queryParam("$skip", "5");
		resource = resource.queryParam("Market", "'"+ createMarket("en") +"'");
		resource = resource.queryParam("Options", "'EnableHighlighting'");

		ClientResponse response = resource.get(ClientResponse.class);
		Environment.logger.info("Resonse code: " + response.getStatus());

		SAXReader reader = new SAXReader();
		Document document;
		
		
        try {
			document = reader.read(response.getEntityInputStream());
			System.out.println(document.asXML());
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
		
		if (!query.getContentTypes().contains(Query.CT_TEXT) || (query.getContentTypes().contains(Query.CT_TEXT) && query.getContentTypes().size() != 1)) {
			query.addParam("requestType", "Composite");
			results.addQueryResult(getComposite(query, authCredentials));
		} else if (query.getPage() == 1) {
			query.addParam("requestType", "CompositeFirst");
			results.addQueryResult(getComposite(query, authCredentials));
		} else {
			query.addParam("requestType", "WebOnly");
			results.addQueryResult(getWeb(query, authCredentials));
		}
		
		return results;
	}

	private QueryResult getWeb(Query query, AuthCredentials authCredentials) throws InterWebException
	{
		WebResource resource = createWebResource("https://api.datamarket.azure.com/Bing/SearchWeb/v1/Web", authCredentials);
		resource = resource.queryParam("Query", "'"+ query.getQuery() +"'");
		resource = resource.queryParam("$top", Integer.toString(query.getResultCount()));
		resource = resource.queryParam("$skip", Integer.toString( (query.getPage()-1)*query.getResultCount() ));
		resource = resource.queryParam("Market", "'"+ createMarket(query.getLanguage()) +"'");
		resource = resource.queryParam("Options", "'EnableHighlighting'");
	
		return executeRequest(resource, query, authCredentials);
	}
	
	private QueryResult getComposite(Query query, AuthCredentials authCredentials) throws InterWebException
	{
		String sources = "";
		
		for (String s : query.getContentTypes()) {
			if (s.equals(Query.CT_TEXT)) {
				sources += (sources.length() > 0 ? "+" : "") + "web";
			} else if (s.equals(Query.CT_IMAGE) || s.equals(Query.CT_VIDEO)) {
				sources += (sources.length() > 0 ? "+" : "") + s;
			}
		}
		
		WebResource resource = createWebResource("https://api.datamarket.azure.com/Bing/Search/v1/Composite", authCredentials);
		resource = resource.queryParam("Sources", "'"+ sources +"'");
		resource = resource.queryParam("Query", "'"+ query.getQuery() +"'");
		resource = resource.queryParam("$top", Integer.toString(query.getResultCount()));
		resource = resource.queryParam("$skip", Integer.toString( (query.getPage()-1)*query.getResultCount() ));
		resource = resource.queryParam("Market", "'"+ createMarket(query.getLanguage()) +"'");
		resource = resource.queryParam("Options", "'EnableHighlighting'");
	
		return executeRequest(resource, query, authCredentials);
	}
	
	private QueryResult executeRequest(WebResource resource, Query query, AuthCredentials authCredentials) throws InterWebException
	{
		ClientResponse response = resource.get(ClientResponse.class);
		SAXReader reader = new SAXReader();

        try {
        	Document document = reader.read(response.getEntityInputStream());
			return parse(document);
		} catch (DocumentException e) {
			if (response.getStatus() == 503 && query.getParam("requestType").equals("CompositeFirst")) {
				query.addParam("requestType", "WebOnly");
				return getWeb(query, authCredentials);
			}
			e.printStackTrace();
			return new QueryResult(null);
		}
	}
	
	private QueryResult parse(Document document)
	{
		QueryResult queryResult = new QueryResult(null);
		
		Element rootElement = document.getRootElement();
		
		String title = rootElement.elementText("subtitle");
		
		if (title.equals("Bing Web Search")) {
			List<Element> entrys = rootElement.elements("entry");
			int index = 1;
			for(Element entry : entrys)
			{
				Element prop = entry.element("content").element("properties");
			
				ResultItem resultItem = new ResultItem(getName());
				resultItem.setType(Query.CT_TEXT);
				resultItem.setTitle(convertHighlighting(prop.elementText("Title")));			
				resultItem.setDescription(convertHighlighting(prop.elementText("Description")));
				resultItem.setUrl(prop.elementText("Url"));
				resultItem.setRank(index++);
										
				queryResult.addResultItem(resultItem);
			}
			return queryResult;
		}
		
		Element rootProp = rootElement.element("entry").element("content").element("properties");
		long webTotal = !rootProp.elementText("WebTotal").isEmpty() ? Long.parseLong(rootProp.elementText("WebTotal")) : 0;
		long imageTotal = !rootProp.elementText("ImageTotal").isEmpty() ? Long.parseLong(rootProp.elementText("ImageTotal")) : 0;
		long videoTotal = !rootProp.elementText("VideoTotal").isEmpty() ? Long.parseLong(rootProp.elementText("VideoTotal")) : 0;
		queryResult.setTotalResultCount(webTotal + imageTotal + videoTotal);
		
		int index = 1;
		List<Element> resources = rootElement.element("entry").elements("link");
		
		for (Element res : resources) {
			String type = getContentTypeFromTitle(res.attributeValue("title"));
			
			if (type == null) {
				continue;
			} else if (type.equals(Query.CT_TEXT)) {
				List<Element> entrys = res.element("inline").element("feed").elements("entry");
				
				for(Element entry : entrys)
				{
					Element prop = entry.element("content").element("properties");
					ResultItem resultItem = new ResultItem(getName());
					resultItem.setType(Query.CT_TEXT);
					resultItem.setTitle(convertHighlighting(prop.elementText("Title")));			
					resultItem.setDescription(convertHighlighting(prop.elementText("Description")));
					resultItem.setUrl(prop.elementText("Url"));
					resultItem.setRank(index++);
					resultItem.setTotalResultCount(queryResult.getTotalResultCount());
							
					queryResult.addResultItem(resultItem);
				}
			} else if (type == Query.CT_IMAGE) {
				List<Element> entrys = res.element("inline").element("feed").elements("entry");
								
				for(Element entry : entrys)
				{
					Element prop = entry.element("content").element("properties");
				
					ResultItem resultItem = new ResultItem(getName());
					resultItem.setType(Query.CT_IMAGE); //getContentTypeFromTitle(entry.attributeValue("title"))
					resultItem.setTitle(convertHighlighting(prop.elementText("Title")));			
					resultItem.setDescription(convertHighlighting(prop.elementText("Description")));
					resultItem.setUrl(prop.elementText("Url"));
					resultItem.setRank(index++);
					resultItem.setTotalResultCount(queryResult.getTotalResultCount());
					
					Set<Thumbnail> thumbnails = new LinkedHashSet<Thumbnail>();
					
					Element sourceUrl = prop.element("SourceUrl");
					if (sourceUrl != null) {
						resultItem.setUrl(prop.elementText("SourceUrl"));
					
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
			}
		}
		
		return queryResult;
	}
	
	
	private String getContentTypeFromTitle(String title) {
		if (title.equalsIgnoreCase("WebResult") || title.equalsIgnoreCase("Web")) {
			return Query.CT_TEXT;
		} else if (title.equalsIgnoreCase("ImageResult") || title.equalsIgnoreCase("Image")) {
			return Query.CT_IMAGE;
		} else if (title.equalsIgnoreCase("VideoResult") || title.equalsIgnoreCase("Video")) {
			return Query.CT_VIDEO;
		}
		return null;
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
