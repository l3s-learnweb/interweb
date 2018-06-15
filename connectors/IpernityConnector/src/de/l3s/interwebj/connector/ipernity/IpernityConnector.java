package de.l3s.interwebj.connector.ipernity;

import static de.l3s.interwebj.util.Assertions.notNull;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.oauth.client.OAuthClientFilter;
import com.sun.jersey.oauth.signature.HMAC_SHA1;
import com.sun.jersey.oauth.signature.OAuthParameters;
import com.sun.jersey.oauth.signature.OAuthSecrets;

import de.l3s.interwebj.AuthCredentials;
import de.l3s.interwebj.InterWebException;
import de.l3s.interwebj.Parameters;
import de.l3s.interwebj.config.Configuration;
import de.l3s.interwebj.connector.ipernity.jaxb.CheckTokenResponse;
import de.l3s.interwebj.connector.ipernity.jaxb.SearchResponse;
import de.l3s.interwebj.connector.ipernity.jaxb.SearchResponse.Docs.Doc;
import de.l3s.interwebj.core.AbstractServiceConnector;
import de.l3s.interwebj.core.Environment;
import de.l3s.interwebj.core.ServiceConnector;
import de.l3s.interwebj.query.Query;
import de.l3s.interwebj.query.Query.SortOrder;
import de.l3s.interwebj.query.QueryResult;
import de.l3s.interwebj.query.ResultItem;
import de.l3s.interwebj.query.Thumbnail;
import de.l3s.interwebj.util.CoreUtils;

public class IpernityConnector extends AbstractServiceConnector
{
    private static final String REQUEST_TOKEN_PATH = "http://www.ipernity.com/apps/oauth/request";
    private static final String AUTHORIZATION_PATH = "http://www.ipernity.com/apps/oauth/authorize";
    private static final String ACCESS_TOKEN_PATH = "http://www.ipernity.com/apps/oauth/access";
    private static final String IPERNITY_BASE = "http://api.ipernity.com/api/";

    public IpernityConnector(Configuration configuration)
    {
	this(configuration, null);
    }

    public IpernityConnector(Configuration configuration, AuthCredentials consumerAuthCredentials)
    {
	super(configuration);
	setAuthCredentials(consumerAuthCredentials);
    }

    @Override
    public ServiceConnector clone()
    {
	return new IpernityConnector(getConfiguration(), getAuthCredentials());
    }

    @Override
    public Set<String> getTags(String username, int maxCount) throws IllegalArgumentException, IOException
    {
	throw new NotImplementedException();
    }

    @Override
    public Set<String> getUsers(Set<String> tags, int maxCount) throws IOException, InterWebException
    {
	throw new NotImplementedException();
    }

    private static String createSortOrder(SortOrder sortOrder)
    {
	/*
	 * Method to sort by: relevant, newest, oldest, most_played, most_commented, or most_liked.
	 */
	switch(sortOrder)
	{
	case RELEVANCE:
	    return "relevance";
	case DATE:
	    return "posted-desc";
	case INTERESTINGNESS:
	    return "popular";
	default:
	    return "relevance";
	}
    }

    @Override
    public QueryResult get(Query query, AuthCredentials authCredentials) throws InterWebException
    {
	notNull(query, "query");
	if(!isRegistered())
	{
	    throw new InterWebException("Service is not yet registered");
	}
	QueryResult queryResult = new QueryResult(query);

	if(!query.getContentTypes().contains(Query.CT_IMAGE) && !query.getContentTypes().contains(Query.CT_VIDEO) && !query.getContentTypes().contains(Query.CT_AUDIO))
	    return queryResult;

	if(query.getQuery().startsWith("user::"))
	{
	    //String username = query.getQuery().substring(6).trim();

	    return queryResult;
	}

	WebResource resource = createWebResource(IPERNITY_BASE + "doc.search/xml", getAuthCredentials(), null);

	resource = resource.queryParam("text", query.getQuery());
	resource = resource.queryParam("media", "photo"); // TODO media values are : photo, audio, video  query.getContentTypes()  Query.CT_AUDIO
	resource = resource.queryParam("page", Integer.toString(query.getPage()));
	resource = resource.queryParam("per_page", Integer.toString(query.getResultCount()));
	resource = resource.queryParam("sort", createSortOrder(query.getSortOrder()));
	resource = resource.queryParam("thumbsize", "560");
	resource = resource.queryParam("extra", "medias,count"); //,dates,original

	if(query.getParam("date_from") != null)
	{
	    try
	    {
		resource = resource.queryParam("created_min", query.getParam("date_from"));
	    }
	    catch(Exception e)
	    {
		e.printStackTrace();
	    }
	}

	if(query.getParam("date_till") != null)
	{
	    try
	    {
		resource = resource.queryParam("created_max", query.getParam("date_till"));
	    }
	    catch(Exception e)
	    {
		e.printStackTrace();
	    }
	}

	/*
	ClientResponse response = resource.get(ClientResponse.class);
	try {
		String responseContent = CoreUtils.getClientResponseContent(response);
		System.out.println("search response;:"+ responseContent);
	} catch (IOException e) {
		e.printStackTrace();
	}
	*/

	SearchResponse sr = resource.get(SearchResponse.class);

	long totalResultCount = sr.getDocs().getTotal();
	queryResult.setTotalResultCount(totalResultCount);
	int count = (sr.getDocs().getPage() - 1) * sr.getDocs().getPerPage();

	List<Doc> docs = sr.getDocs().getDoc();
	for(Doc doc : docs)
	{
	    ResultItem resultItem = new ResultItem(getName());
	    resultItem.setType(Query.CT_IMAGE);
	    resultItem.setId(Integer.toString(doc.getDocId()));
	    resultItem.setTitle(doc.getTitle());
	    //resultItem.setDescription(video.getDescription());
	    resultItem.setUrl("http://ipernity.com/doc/" + doc.getOwner().getUserId() + "/" + doc.getDocId());
	    //resultItem.setDate(CoreUtils.formatDate(new Date(video.getDates().getCreated().getMillisecond())));
	    resultItem.setRank(count++);
	    resultItem.setTotalResultCount(totalResultCount);
	    resultItem.setCommentCount(doc.getCount().getComments());
	    resultItem.setViewCount(doc.getCount().getVisits());

	    String url = doc.getThumb().getUrl();
	    int height = doc.getThumb().getH();
	    int width = doc.getThumb().getW();

	    int width240, width100, height240, height100;
	    double aspect = (double) width / (double) height;
	    if(width > height)
	    {
		width240 = 240;
		width100 = 100;
		height240 = (int) Math.ceil(240.0 / aspect);
		height100 = (int) Math.ceil(100.0 / aspect);
	    }
	    else
	    {
		width240 = (int) Math.ceil(240.0 * aspect);
		width100 = (int) Math.ceil(100.0 * aspect);
		height240 = 240;
		height100 = 100;
	    }
	    resultItem.setImageUrl(url); // todo parse original-tag and use it when available
	    resultItem.setEmbeddedSize3("<img src=\"" + url + "\" width=\"" + width + "\" height=\"" + height + "\" />");
	    resultItem.setEmbeddedSize2(CoreUtils.createImageCode(url.replace(".560.", ".240."), width, height, 240, 240));
	    resultItem.setEmbeddedSize1(CoreUtils.createImageCode(url.replace(".560.", ".100."), width, height, 100, 100));

	    Set<Thumbnail> thumbnails = new LinkedHashSet<Thumbnail>();
	    thumbnails.add(new Thumbnail(url.replace(".560.", ".75x."), 75, 75));
	    thumbnails.add(new Thumbnail(url.replace(".560.", ".100."), width100, height100));
	    thumbnails.add(new Thumbnail(url.replace(".560.", ".240."), width240, height240));
	    thumbnails.add(new Thumbnail(url, width, height));
	    resultItem.setThumbnails(thumbnails);

	    queryResult.addResultItem(resultItem);
	}

	if(query.getPrivacy() != -1f)
	    queryResult = Environment.getInstance().getPrivacyClassifier().classify(queryResult, query);

	return queryResult;
    }

    public static WebResource createWebResource(String apiUrl, AuthCredentials consumerAuthCredentials, AuthCredentials userAuthCredentials)
    {
	Client client = Client.create();
	WebResource resource = client.resource(apiUrl);
	OAuthParameters oauthParams = new OAuthParameters();
	oauthParams.consumerKey(consumerAuthCredentials.getKey());
	if(userAuthCredentials != null)
	{
	    oauthParams.token(userAuthCredentials.getKey());
	}
	oauthParams.signatureMethod(HMAC_SHA1.NAME);
	oauthParams.timestamp();
	oauthParams.nonce();
	oauthParams.version();
	OAuthSecrets oauthSecrets = new OAuthSecrets();
	oauthSecrets.consumerSecret(consumerAuthCredentials.getSecret());
	if(userAuthCredentials != null && userAuthCredentials.getSecret() != null)
	{
	    oauthSecrets.tokenSecret(userAuthCredentials.getSecret());
	}
	OAuthClientFilter filter = new OAuthClientFilter(client.getProviders(), oauthParams, oauthSecrets);
	resource.addFilter(filter);

	return resource;
    }

    @Override
    public Parameters authenticate(String callbackUrl) throws InterWebException
    {
	if(!isRegistered())
	{
	    throw new InterWebException("Service is not yet registered");
	}
	Parameters params = new Parameters();
	Client client = Client.create();
	WebResource resource = client.resource(REQUEST_TOKEN_PATH);
	AuthCredentials authCredentials = getAuthCredentials();
	System.out.println("auth cred" + getAuthCredentials());
	com.sun.jersey.oauth.signature.OAuthParameters oauthParams = new com.sun.jersey.oauth.signature.OAuthParameters();
	oauthParams.consumerKey(authCredentials.getKey());
	oauthParams.signatureMethod(HMAC_SHA1.NAME);
	oauthParams.timestamp();
	oauthParams.nonce();
	oauthParams.callback(callbackUrl);
	oauthParams.version();
	OAuthSecrets oauthSecrets = new OAuthSecrets();
	oauthSecrets.consumerSecret(authCredentials.getSecret());
	OAuthClientFilter filter = new OAuthClientFilter(client.getProviders(), oauthParams, oauthSecrets);
	resource.addFilter(filter);
	Environment.logger.info("querying ipernity request token: " + resource.toString());
	try
	{
	    //resource = resource.queryParam("scope", "http://gdata.youtube.com");
	    ClientResponse response = resource.get(ClientResponse.class);
	    String responseContent = CoreUtils.getClientResponseContent(response);
	    Environment.logger.info("Content: " + responseContent);
	    params.addQueryParameters(responseContent);
	    String authUrl = AUTHORIZATION_PATH + "?oauth_token=" + params.get(Parameters.OAUTH_TOKEN);
	    Environment.logger.info("requesting url: " + authUrl);
	    params.add(Parameters.AUTHORIZATION_URL, authUrl);
	}
	catch(UniformInterfaceException e)
	{
	    e.printStackTrace();
	    throw new InterWebException(e);
	}
	catch(IOException e)
	{
	    e.printStackTrace();
	    throw new InterWebException(e);
	}
	return params;
    }

    @Override
    public AuthCredentials completeAuthentication(Parameters params) throws InterWebException
    {
	notNull(params, "params");
	AuthCredentials authCredentials = null;
	if(!isRegistered())
	{
	    throw new InterWebException("Service is not yet registered");
	}
	try
	{
	    String oauthToken = params.get(Parameters.OAUTH_TOKEN);
	    Environment.logger.info("oauth_token: " + oauthToken);
	    String oauthTokenSecret = params.get(Parameters.OAUTH_TOKEN_SECRET);
	    Environment.logger.info("oauth_token_secret: " + oauthTokenSecret);
	    String oauthVerifier = params.get(Parameters.OAUTH_VERIFIER);
	    Environment.logger.info("oauth_verifier: " + oauthVerifier);
	    Client client = Client.create();
	    WebResource resource = client.resource(ACCESS_TOKEN_PATH);
	    AuthCredentials consumerAuthCredentials = getAuthCredentials();
	    com.sun.jersey.oauth.signature.OAuthParameters oauthParams = new com.sun.jersey.oauth.signature.OAuthParameters();
	    oauthParams.version();
	    oauthParams.nonce();
	    oauthParams.timestamp();
	    oauthParams.consumerKey(consumerAuthCredentials.getKey());
	    if(oauthVerifier != null)
	    {
		oauthParams.verifier(oauthVerifier);
	    }
	    oauthParams.token(oauthToken);
	    oauthParams.signatureMethod(HMAC_SHA1.NAME);
	    OAuthSecrets oauthSecrets = new OAuthSecrets();
	    oauthSecrets.consumerSecret(consumerAuthCredentials.getSecret());
	    oauthSecrets.tokenSecret(oauthTokenSecret);
	    OAuthClientFilter filter = new OAuthClientFilter(client.getProviders(), oauthParams, oauthSecrets);
	    resource.addFilter(filter);
	    Environment.logger.info("getting ipernity access token: " + resource.toString());
	    ClientResponse response = resource.get(ClientResponse.class);
	    String content = CoreUtils.getClientResponseContent(response);
	    Environment.logger.info("ipernity response: " + content);
	    params.addQueryParameters(content);
	    String key = params.get(Parameters.OAUTH_TOKEN);
	    String secret = params.get(Parameters.OAUTH_TOKEN_SECRET);
	    authCredentials = new AuthCredentials(key, secret);
	}
	catch(UniformInterfaceException e)
	{
	    e.printStackTrace();
	    throw new InterWebException(e);
	}
	catch(IOException e)
	{
	    e.printStackTrace();
	    throw new InterWebException(e);
	}
	return authCredentials;
    }

    // alles hier drunter muss noch Ã¼berarbeitet werden

    /*
    private ResultItem createResultItem(VideoEntry ve,
        int rank,
        int totalResultCount)
    {
    	ResultItem resultItem = new ResultItem(getName());
    	resultItem.setType(Query.CT_VIDEO);
    	resultItem.setId(ve.getId());
    	resultItem.setTitle(ve.getTitle().getPlainText());
    	MediaGroup mg = ve.getMediaGroup();
    	resultItem.setDescription(mg.getDescription().getPlainTextContent());
    	resultItem.setUrl(mg.getPlayer().getUrl());
    	resultItem.setDate(CoreUtils.formatDate(ve.getPublished().getValue()));
    	resultItem.setTags(StringUtils.join(mg.getKeywords().getKeywords(), ','));
    	resultItem.setRank(rank++);
    	resultItem.setTotalResultCount(totalResultCount);
    	resultItem.setViewCount(getViewCount(ve));
    	resultItem.setCommentCount(getCommentCount(ve));
    
    	// load thumbnails
    	Set<Thumbnail> thumbnails = new TreeSet<Thumbnail>();
    	List<MediaThumbnail> mediaThumbnails = mg.getThumbnails();
    	for (MediaThumbnail mt : mediaThumbnails)
    	{
    		Thumbnail thumbnail = new Thumbnail(mt.getUrl(), mt.getWidth(),  mt.getHeight());
    		thumbnails.add(thumbnail);
    		
    		if(thumbnail.getUrl().contains("/default.jpg"))
    			resultItem.setEmbeddedSize1(CoreUtils.createImageCode(thumbnail, 100, 100));
    		else if(thumbnail.getUrl().contains("/hqdefault.jpg"))
    		{
    			resultItem.setEmbeddedSize2(CoreUtils.createImageCode(thumbnail, 240, 240));
    			resultItem.setImageUrl(thumbnail.getUrl());
    		}
    	}
    	resultItem.setThumbnails(thumbnails);
    	
    	Pattern pattern = Pattern.compile("v[/=]([^&]+)"); 
    	Matcher matcher = pattern.matcher(mg.getPlayer().getUrl()); 
    	
        if(matcher.find()) 
        {
            String id = matcher.group(1);
            
    		//create embedded flash video player
    		String embeddedCode = "<embed pluginspage=\"http://www.adobe.com/go/getflashplayer\" src=\"http://www.youtube.com/v/"+
    								id +"\" type=\"application/x-shockwave-flash\" width=\"500\" height=\"400\"></embed>";
    		resultItem.setEmbeddedSize3(embeddedCode);				
        }
        return resultItem;
    }
    */

    @Override
    public String getEmbedded(AuthCredentials authCredentials, String url, int maxWidth, int maxHeight) throws InterWebException
    {
	/*
	}
	
	if(url.toLowerCase().contains("youtube.com"))
	{
		Pattern pattern = Pattern.compile("v[/=]([^&]+)"); 
		Matcher matcher = pattern.matcher(url); 
		
	    if(matcher.find()) 
	    {
	        String id = matcher.group(1);
	        
			if (id == null)
			{
				throw new InterWebException("No id found in URL: [" + url + "]");
			}
			String embeddedCode = "<embed pluginspage=\"http://www.adobe.com/go/getflashplayer\" src=\"http://www.youtube.com/v/"
			                      + id
			                      + "\" type=\"application/x-shockwave-flash\" width=\""
			                      + maxWidth
			                      + "\" height=\""
			                      + maxHeight
			                      + "\"></embed>";
			return embeddedCode;
	    }
	} */
	throw new InterWebException("URL: [" + url + "] doesn't belong to connector [" + getName() + "]");

    }

    @Override
    public String getUserId(AuthCredentials authCredentials) throws InterWebException
    {
	WebResource resource = createWebResource(IPERNITY_BASE + "auth.checkToken/xml", getAuthCredentials(), authCredentials);

	CheckTokenResponse response = resource.get(CheckTokenResponse.class);

	return response.getAuth().getUser().getUsername();
    }

    @Override
    public boolean isConnectorRegistrationDataRequired()
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
	return true;
    }

    @Override
    public ResultItem put(byte[] data, String contentType, Parameters params, AuthCredentials authCredentials) throws InterWebException
    {
	notNull(data, "data");
	notNull(contentType, "contentType");
	notNull(params, "params");
	if(!isRegistered())
	{
	    throw new InterWebException("Service is not yet registered");
	}
	if(authCredentials == null)
	{
	    throw new InterWebException("Upload is forbidden for non-authorized users");
	}
	throw new NotImplementedException();
	/*
	VideoEntry ve = new VideoEntry();
	YouTubeMediaGroup mg = ve.getOrCreateMediaGroup();
	String category = params.get("category", "Film");
	mg.addCategory(new MediaCategory(YouTubeNamespace.CATEGORY_SCHEME,
	                                 category));
	mg.setTitle(new MediaTitle());
	String title = params.get(Parameters.TITLE, "No Title");
	mg.getTitle().setPlainTextContent(title);
	mg.setKeywords(new MediaKeywords());
	String tags = params.get(Parameters.TAGS, "");
	List<String> keywords = CoreUtils.convertToUniqueList(tags);
	mg.getKeywords().addKeywords(keywords);
	mg.setDescription(new MediaDescription());
	String description = params.get(Parameters.DESCRIPTION,
	                                "No Description");
	mg.getDescription().setPlainTextContent(description);
	int privacy = Integer.parseInt(params.get(Parameters.PRIVACY, "0"));
	mg.setPrivate(privacy > 0);
	MediaSource ms = new MediaByteArraySource(data, "video/*");
	ve.setMediaSource(ms);
	ResultItem resultItem = null;
	try
	{
		YouTubeService service = createYouTubeService(authCredentials);
		service.getRequestFactory().setHeader("Slug", "no_name.mp4");
		ve = service.insert(new URL(UPLOAD_VIDEO_PATH), ve);
		
		resultItem = createResultItem(ve, 0, 0);
	}
	catch (ServiceException e)
	{
		e.printStackTrace();
		throw new InterWebException(e);
	}
	catch (OAuthException e)
	{
		e.printStackTrace();
		throw new InterWebException(e);
	}
	catch (MalformedURLException e)
	{
		e.printStackTrace();
		throw new InterWebException(e);
	}
	catch (IOException e)
	{
		e.printStackTrace();
		throw new InterWebException(e);
	}
	
	return resultItem;*/
    }

    @Override
    public void revokeAuthentication() throws InterWebException
    {
	// keine möglichkeit gefunden bei ipernity
    }
}
