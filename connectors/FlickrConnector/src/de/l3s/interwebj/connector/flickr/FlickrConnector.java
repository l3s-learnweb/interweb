package de.l3s.interwebj.connector.flickr;


import static de.l3s.interwebj.util.Assertions.notNull;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.NotImplementedException;
import org.xml.sax.SAXException;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.REST;
import com.aetrion.flickr.RequestContext;
import com.aetrion.flickr.auth.Auth;
import com.aetrion.flickr.auth.AuthInterface;
import com.aetrion.flickr.auth.Permission;
import com.aetrion.flickr.contacts.Contact;
import com.aetrion.flickr.people.User;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.PhotoList;
import com.aetrion.flickr.photos.PhotosInterface;
import com.aetrion.flickr.photos.SearchParameters;
import com.aetrion.flickr.photos.Size;
import com.aetrion.flickr.tags.Tag;
import com.aetrion.flickr.uploader.UploadMetaData;
import com.aetrion.flickr.uploader.Uploader;

import de.l3s.interwebj.AuthCredentials;
import de.l3s.interwebj.InterWebException;
import de.l3s.interwebj.Parameters;
import de.l3s.interwebj.config.Configuration;
import de.l3s.interwebj.core.AbstractServiceConnector;
import de.l3s.interwebj.core.Environment;
import de.l3s.interwebj.core.ServiceConnector;
import de.l3s.interwebj.query.ContactFromSocialNetwork;
import de.l3s.interwebj.query.Query;
import de.l3s.interwebj.query.Query.SearchScope;
import de.l3s.interwebj.query.Query.SortOrder;
import de.l3s.interwebj.query.QueryResult;
import de.l3s.interwebj.query.ResultItem;
import de.l3s.interwebj.query.Thumbnail;
import de.l3s.interwebj.query.UserSocialNetworkResult;
import de.l3s.interwebj.socialsearch.SocialSearchQuery;
import de.l3s.interwebj.socialsearch.SocialSearchResult;
import de.l3s.interwebj.util.CoreUtils;


public class FlickrConnector extends AbstractServiceConnector
{	
	private static final String MEDIA_ALL = "all";
	private static final String MEDIA_PHOTOS = "photos";
	private static final String MEDIA_VIDEOS = "videos";

	public FlickrConnector(Configuration configuration)
	{
		this(configuration, null);
	}
	

	public FlickrConnector(Configuration configuration,
	                       AuthCredentials consumerAuthCredentials)
	{
		super(configuration);
		setAuthCredentials(consumerAuthCredentials);
	}
	

	@Override
	public Parameters authenticate(String callbackUrl)
	    throws InterWebException
	{
		if (!isRegistered())
		{
			throw new InterWebException("Service is not yet registered");
		}
		Parameters params = new Parameters();
		try
		{
			String callbackQueryParams = URI.create(callbackUrl).getQuery();
			AuthCredentials consumerAuthCredentials = getAuthCredentials();
			AuthInterface authInterface = new ExtraAuthInterface(consumerAuthCredentials.getKey(),
			                                                     consumerAuthCredentials.getSecret(),
			                                                     new REST(),
			                                                     callbackQueryParams);
			String authId = authInterface.getFrob();
			System.out.println("authId: [" + authId + "]");
			Permission permission = Permission.DELETE;
			URL requestTokenUrl = authInterface.buildAuthenticationUrl(permission,
			                                                           authId);
			System.out.println("callbackUrl: [" + callbackUrl + "]");
			System.out.println("requestTokenUrl: ["
			                   + requestTokenUrl.toExternalForm() + "]");
			params.add(Parameters.AUTHORIZATION_URL,
			           requestTokenUrl.toExternalForm());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new InterWebException(e);
		}
		return params;
	}
	

	@Override
	public ServiceConnector clone()
	{
		return new FlickrConnector(getConfiguration(), getAuthCredentials());
	}
	

	@Override
	public AuthCredentials completeAuthentication(Parameters params)
	    throws InterWebException
	{
		notNull(params, "params");
		AuthCredentials authCredentials = null;
		if (!isRegistered())
		{
			throw new InterWebException("Service is not yet registered");
		}
		try
		{
			String frob = params.get("frob");
			Flickr flickr = createFlickrInstance();
			Environment.logger.info("request token frob: " + frob);
			AuthInterface authInterface = flickr.getAuthInterface();
			Auth auth = authInterface.getToken(frob);
			authCredentials = new AuthCredentials(auth.getToken());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new InterWebException(e);
		}
		return authCredentials;
	}
	

	@Override
	public QueryResult get(Query query, AuthCredentials authCredentials)
	    throws InterWebException
	{
		notNull(query, "query");
		if (!isRegistered())
		{
			throw new InterWebException("Service is not yet registered");
		}
		QueryResult queryResult = new QueryResult(query);
		//		queryResult.addQueryResult(getFriends(query, authCredentials));
		queryResult.addQueryResult(getMedia(query, authCredentials));
		
		if(query.getPrivacy() != -1f)
			queryResult = Environment.getInstance().getPrivacyClassifier().classify(queryResult, query);
		
		return queryResult;
	}
	

	@SuppressWarnings("rawtypes")
	@Override
	public String getEmbedded(AuthCredentials authCredentials,
	                          String url,
	                          int maxWidth,
	                          int maxHeight)
	    throws InterWebException
	{
		notNull(url, "url");
		URI uri = URI.create(url);
		URI baseUri = URI.create(getBaseUrl());
		if (!baseUri.getHost().endsWith(uri.getHost()))
		{
			throw new InterWebException("URL: [" + url
			                            + "] doesn't belong to connector ["
			                            + getName() + "]");
		}
		String path = uri.getPath();
		String id = path.substring(path.lastIndexOf('/') + 1);
		RequestContext requestContext = RequestContext.getRequestContext();
		if (authCredentials != null)
		{
			Auth auth = new Auth();
			requestContext.setAuth(auth);
			auth.setToken(authCredentials.getKey());
			auth.setPermission(Permission.READ);
		}
		try
		{
			Flickr flickr = createFlickrInstance();
			PhotosInterface pi = flickr.getPhotosInterface();
			Collection sizes = pi.getSizes(id);
			notNull(sizes, "sizes");
			if (sizes.size() == 0)
			{
				throw new InterWebException("There are no thumbnails available for an image with URL: ["
				                            + url + "]");
			}
			return createEmbeddedCode(sizes, maxWidth, maxHeight);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new InterWebException(e);
		}
		catch (SAXException e)
		{
			e.printStackTrace();
			throw new InterWebException(e);
		}
		catch (FlickrException e)
		{
			e.printStackTrace();
			throw new InterWebException(e);
		}
	}
	

	@Override
	public Parameters getRefinedCallbackParameters(Parameters parameters)
	{
		Parameters refinedParameters = new Parameters();
		for (String parameterName : parameters.keySet())
		{
			if (parameterName.equals("extra"))
			{
				String query = parameters.get(parameterName);
				refinedParameters.addQueryParameters(query);
			}
			else
			{
				refinedParameters.add(parameterName,
				                      parameters.get(parameterName));
			}
		}
		return refinedParameters;
	}
	

	@Override
	public String getUserId(AuthCredentials authCredentials)
	    throws InterWebException
	{
		try
		{
			Flickr flickr = createFlickrInstance();
			AuthInterface authInterface = flickr.getAuthInterface();
			Auth auth = authInterface.checkToken(authCredentials.getKey());
			User user = auth.getUser();
			return user.getId();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new InterWebException(e);
		}
		catch (SAXException e)
		{
			e.printStackTrace();
			throw new InterWebException(e);
		}
		catch (FlickrException e)
		{
			e.printStackTrace();
			throw new InterWebException(e);
		}
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
	public ResultItem put(byte[] data,
	                String contentType,
	                Parameters params,
	                AuthCredentials authCredentials)
	    throws InterWebException
	{
		notNull(data, "data");
		notNull(contentType, "contentType");
		notNull(params, "params");
		if (!isRegistered())
		{
			throw new InterWebException("Service is not yet registered");
		}
		if (authCredentials == null)
		{
			throw new InterWebException("Upload is forbidden for non-authorized users");
		}
		if (contentType.equals(Query.CT_IMAGE))
		{
			try
			{
				RequestContext requestContext = RequestContext.getRequestContext();
				Auth auth = new Auth();
				requestContext.setAuth(auth);
				auth.setToken(authCredentials.getKey());
				auth.setPermission(Permission.WRITE);
				Flickr flickr = createFlickrInstance();
				Uploader uploader = flickr.getUploader();
				UploadMetaData metaData = new UploadMetaData();
				metaData.setTitle(params.get(Parameters.TITLE, "No Title"));
				metaData.setDescription(params.get(Parameters.DESCRIPTION,
				                                   "No Description"));
				String tags = params.get(Parameters.TAGS, "");
				metaData.setTags(CoreUtils.convertToUniqueList(tags));
				int privacy = Integer.parseInt(params.get(Parameters.PRIVACY,
				                                          "0"));
				metaData.setPublicFlag(privacy == 0);
				String id = uploader.upload(data, metaData);
				Photo photo = flickr.getPhotosInterface().getPhoto(id);
				System.out.println(photo.getSmallUrl());
				System.out.println("data successfully uploaded");
				
				return createResultItem(photo, 0, 0);
			}
			catch (FlickrException e)
			{
				e.printStackTrace();
				throw new InterWebException(e);
			}
			catch (IOException e)
			{
				e.printStackTrace();
				throw new InterWebException(e);
			}
			catch (SAXException e)
			{
				e.printStackTrace();
				throw new InterWebException(e);
			}
		}
		return null;
	}
	

	@Override
	public void revokeAuthentication()
	    throws InterWebException
	{
		// Flickr doesn't provide api for token revokation
	}
	

	private String createContentType(String media)
	{
		if ("photo".equals(media))
		{
			return Query.CT_IMAGE;
		}
		return media;
	}
	

	@SuppressWarnings({"rawtypes"})
	private String createEmbeddedCode(Collection sizes,
	                                  int maxWidth,
	                                  int maxHeight)
	{
		Size size = null;
		for (Object obj : sizes)
		{
			Size s = (Size) obj;
			if (size == null
			    || (s.getWidth() >= size.getWidth() && s.getHeight() >= size.getHeight())
			    && (s.getWidth() <= maxWidth && s.getHeight() <= maxHeight))
			{
				size = s;
			}
		}
		String embeddedCode = "<img src=\"" + size.getSource() + "\" width=\""
		                      + size.getWidth() + "\" height=\""
		                      + size.getHeight() + "\" />";
		return embeddedCode;
	}
	

	private Flickr createFlickrInstance()
	    throws InterWebException
	{
		if (!isRegistered())
		{
			throw new InterWebException("Unable to create Flickr instance. Service is not registered");
		}
		try
		{
			AuthCredentials consumerAuthCredentials = getAuthCredentials();
			return new Flickr(consumerAuthCredentials.getKey(),
			                  consumerAuthCredentials.getSecret(),
			                  new REST());
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
			throw new InterWebException(e);
		}
	}
	

	private ResultItem createResultItem(Photo photo,
	                                    int rank,
	                                    int totalResultCount)
	    throws FlickrException, InterWebException
	{
		
		ResultItem resultItem = new ResultItem(getName());
		resultItem.setId(photo.getId());
		resultItem.setType(createContentType(photo.getMedia()));
		resultItem.setTitle(photo.getTitle());
		resultItem.setDescription(photo.getDescription());
		resultItem.setTags(createTags(photo.getTags()));
		resultItem.setUrl(photo.getUrl());
		resultItem.setThumbnails(createThumbnails(photo));
		Date date = photo.getDatePosted();
		if (date != null)
		{
			resultItem.setDate(CoreUtils.formatDate(date.getTime()));
		}
		resultItem.setRank(rank);
		resultItem.setTotalResultCount(totalResultCount);
		resultItem.setCommentCount(photo.getComments());
		
		Size thumbnail = photo.getThumbnailSize();
		resultItem.setEmbeddedSize1("<img src=\""+ thumbnail.getSource() +"\" height=\""+ thumbnail.getHeight() +"\" width=\""+ thumbnail.getWidth() +"\"/>");
		thumbnail = photo.getSmallSize();
		resultItem.setEmbeddedSize2("<img src=\""+ thumbnail.getSource() +"\" height=\""+ thumbnail.getHeight() +"\" width=\""+ thumbnail.getWidth() +"\"/>");
		thumbnail = photo.getMediumSize();
		if(thumbnail != null)
			resultItem.setEmbeddedSize3("<img src=\""+ thumbnail.getSource() +"\" height=\""+ thumbnail.getHeight() +"\" width=\""+ thumbnail.getWidth() +"\"/>");
		thumbnail = photo.getLargeSize();
		if(thumbnail != null)
			resultItem.setEmbeddedSize4("<img src=\""+ thumbnail.getSource() +"\" height=\""+ thumbnail.getHeight() +"\" width=\""+ thumbnail.getWidth() +"\"/>");
		
		if(photo.getLargeUrl() != null && photo.getLargeUrl().length() > 7)
			resultItem.setImageUrl(photo.getLargeUrl());
		else
			resultItem.setImageUrl(photo.getMediumUrl());
		
		return resultItem;
	}
	

	@SuppressWarnings("rawtypes")
	private String createTags(Collection tags)
	{
		StringBuilder sb = new StringBuilder();
		for (Iterator i = tags.iterator(); i.hasNext();)
		{
			Tag tag = (Tag) i.next();
			sb.append(tag.getValue());
			if (i.hasNext())
			{
				sb.append(',');
			}
		}
		return sb.toString();
	}
	

	private String[] createTags(String query)
	{
		return query.split("[\\W]+");
	}
	

	private Set<Thumbnail> createThumbnails(Photo photo) throws FlickrException
	{		
		SortedSet<Thumbnail> thumbnails = new TreeSet<Thumbnail>();
		Size thumbnail = photo.getThumbnailSize();
		if(thumbnail != null)
			thumbnails.add(new Thumbnail(thumbnail.getSource(), thumbnail.getWidth(), thumbnail.getHeight()));
		thumbnail = photo.getSmallSize();
		if(thumbnail != null)
			thumbnails.add(new Thumbnail(thumbnail.getSource(), thumbnail.getWidth(), thumbnail.getHeight()));
		thumbnail = photo.getMediumSize();
		if(thumbnail != null)
			thumbnails.add(new Thumbnail(thumbnail.getSource(), thumbnail.getWidth(), thumbnail.getHeight()));
		thumbnail = photo.getLargeSize();
		if(thumbnail != null)
			thumbnails.add(new Thumbnail(thumbnail.getSource(), thumbnail.getWidth(), thumbnail.getHeight()));
		
		/*
		thumbnails.add(new Thumbnail(photo.getSmallSquareUrl(), 75, 75)); // das sind nur die maximalen breiten/höhen und somit fast nutzlos
		thumbnails.add(new Thumbnail(photo.getThumbnailUrl(), 100, 100));
		thumbnails.add(new Thumbnail(photo.getSmallUrl(), 240, 240));
		thumbnails.add(new Thumbnail(photo.getMediumUrl(), 500, 500));
		*/
		return thumbnails;
	}
	

	private static Set<String> getExtras()
	{
		Set<String> extras = new HashSet<String>();
		extras.add("description");
		extras.add("tags");
		extras.add("date_upload");
		extras.add("views");
		extras.add("media");
		extras.add("url_t");
		extras.add("url_s");
		extras.add("url_m");
		extras.add("url_l");
		
		return extras;
	}
	

	private QueryResult getMedia(Query query, AuthCredentials authCredentials)
	    throws InterWebException
	{
		QueryResult queryResult = new QueryResult(query);
		if (supportContentTypes(query.getContentTypes()))
		{
			try
			{
				RequestContext requestContext = RequestContext.getRequestContext();
				if (authCredentials != null)
				{
					Auth auth = new Auth();
					requestContext.setAuth(auth);
					auth.setToken(authCredentials.getKey());
					auth.setPermission(Permission.READ);
				}
				Flickr flickr = createFlickrInstance();
				PhotosInterface pi = flickr.getPhotosInterface();
				// 
				
				SearchParameters params = new SearchParameters();
				params.setExtras(getExtras());
				params.setMedia(getMediaType(query));
				
				if (query.getParam("date_from") != null) {
					try {
						Date dateFrom = new Date(CoreUtils.parseDate(query.getParam("date_from")));
						params.setMinUploadDate(dateFrom);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				if (query.getParam("date_till") != null) {
					try {
						Date dateTill = new Date(CoreUtils.parseDate(query.getParam("date_till")));
						params.setMaxUploadDate(dateTill);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				params.setSort(getSortOrder(query.getSortOrder()));
				
				PhotoList photoList = null;
				
				if(query.getQuery().startsWith("user::"))
				{
					String username = query.getQuery().substring(6).trim();					
					User user = flickr.getPeopleInterface().findByUsername(username);
					
					params.setUserId(user.getId());
				}
				else if(query.getQuery().startsWith("recent::"))
				{
					photoList = pi.getRecent(getExtras(), query.getResultCount(), query.getPage());
				}
				else 
				{
					if (query.getSearchScopes().contains(SearchScope.TEXT))
					{
						params.setText(query.getQuery());
						
					}
					if (query.getSearchScopes().contains(SearchScope.TAGS))
					{
						String[] tags = createTags(query.getQuery());
						params.setTags(tags);
					}
				}

				if(null == photoList)
					photoList = pi.search(params, query.getResultCount(), query.getPage());
				int rank = query.getResultCount() * (query.getPage()-1);
				int totalResultCount = photoList.getTotal();
				queryResult.setTotalResultCount(totalResultCount);
				
				for (Object o : photoList)
				{
					if (o instanceof Photo)
					{
						Photo photo = (Photo) o;
						ResultItem resultItem = createResultItem(photo,
						                                         rank,
						                                         totalResultCount);
						queryResult.addResultItem(resultItem);
						rank++;
					}
				}
			}
			catch (FlickrException e)
			{
				if(e.getErrorMessage().equals("User not found"))
					System.err.println("Unknown user");
				else 
				{
					e.printStackTrace();					
					throw new InterWebException(e);
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
				throw new InterWebException(e);
			}
			catch (SAXException e)
			{
				e.printStackTrace();
				throw new InterWebException(e);
			}
		}
		return queryResult;
	}
	

	private String getMediaType(Query query)
	{
		notNull(query, "query");
		String media = null;
		if (query.getContentTypes().contains(Query.CT_IMAGE)
		    && query.getContentTypes().contains(Query.CT_VIDEO))
		{
			media = MEDIA_ALL;
		}
		else if (query.getContentTypes().contains(Query.CT_IMAGE))
		{
			media = MEDIA_PHOTOS;
		}
		else
		{
			media = MEDIA_VIDEOS;
		}
		return media;
	}
	

	private int getSortOrder(SortOrder sortOrder)
	{
		switch (sortOrder)
		{
			case RELEVANCE:
				return SearchParameters.RELEVANCE;
			case DATE:
				return SearchParameters.DATE_POSTED_DESC;
			case INTERESTINGNESS:
				return SearchParameters.INTERESTINGNESS_DESC;
		}
		return SearchParameters.RELEVANCE;
	}
	

	private boolean supportContentTypes(List<String> contentTypes)
	{
		return contentTypes.contains(Query.CT_IMAGE)
		       || contentTypes.contains(Query.CT_VIDEO);
	}
	

	public static void main(String[] args)
	{
		String urlString = "http://flickr.com/photos/46648241@N00/4056831952";
		URI uri = URI.create(urlString);
		String path = uri.getPath();
		String id = path.substring(path.lastIndexOf('/') + 1);
		System.out.println(id);
	}


	@Override
	public Set<String> getTags(String username, int maxCount) throws IllegalArgumentException, IOException {
		// TODO Auto-generated method stub
		throw new NotImplementedException();

		
	}


	@Override
	public Set<String> getUsers(Set<String> tags, int maxCount) throws IOException, InterWebException 
	{
		SearchParameters params = new SearchParameters();
		{
			HashSet<String> temp = new HashSet<String>();
			temp.add("owner_name");		
			params.setExtras(temp);
		}
		HashSet<String> users = new HashSet<String>();
		
		int errorCounter = 0;
		for(int page=1; page < 8 && errorCounter < 80; page++)
		{
			for(String tag : tags)
			{		
				if(tag.trim().length() < 3) // don't use very short tags
					continue;
				
				String[] temp = {tag};
				params.setTags(temp);
	
				Flickr flickr = createFlickrInstance();
				PhotoList result;
				try {
					result = flickr.getPhotosInterface().search(params, 500, page);
				}
				catch (SAXException e) {
					throw new RuntimeException(e);
				}
				catch (FlickrException e) {
					throw new RuntimeException(e);
				}
				
				if(result.size() == 0)
					errorCounter++;
				
				@SuppressWarnings("unchecked")
				Iterator<Photo> iterator = result.iterator();
				
				while(iterator.hasNext())
				{
					Photo f = iterator.next();
					users.add(f.getOwner().getUsername());

					if(users.size() == maxCount)
						return users;
				}	
			}
		}
		
		return users;
	}


	@Override
	public UserSocialNetworkResult getUserSocialNetwork(String userid,
			AuthCredentials authCredentials) throws InterWebException {
		
		Flickr flickr = createFlickrInstance();
		
		UserSocialNetworkResult socialnetwork= new UserSocialNetworkResult(userid);
		try {
			if(authCredentials==null)
			flickr.getContactsInterface().getPublicList(userid);
			else
			{
				RequestContext requestContext = RequestContext.getRequestContext();
				Auth auth = new Auth();
				requestContext.setAuth(auth);
				auth.setToken(authCredentials.getKey());
				auth.setPermission(Permission.READ);
				flickr = createFlickrInstance();
				ArrayList<Contact> contactlist= new ArrayList<Contact>(flickr.getContactsInterface().getList());
				for (Contact contact : contactlist) {
					ContactFromSocialNetwork person = new ContactFromSocialNetwork(contact.getUsername(),contact.getId(),1,"Flickr");
					socialnetwork.getResultItems().put(contact.getId(), person );
				}
				
			}
			 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FlickrException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
		
		
		
		return socialnetwork;
	}


	@Override
	public SocialSearchResult get(SocialSearchQuery query,
			AuthCredentials authCredentials) {
		// TODO Auto-generated method stub
		return null;
	}
}
