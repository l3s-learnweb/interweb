package de.l3s.interwebj.connector.flickr;


import static de.l3s.interwebj.util.Assertions.*;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.xml.parsers.*;

import org.xml.sax.*;

import com.aetrion.flickr.*;
import com.aetrion.flickr.auth.*;
import com.aetrion.flickr.people.*;
import com.aetrion.flickr.photos.*;
import com.aetrion.flickr.tags.*;
import com.aetrion.flickr.uploader.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.config.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.query.*;
import de.l3s.interwebj.query.Query.SearchScope;
import de.l3s.interwebj.query.Query.SortOrder;
import de.l3s.interwebj.util.*;


public class FlickrConnector
    extends AbstractServiceConnector
    implements Serializable
{
	
	private static final long serialVersionUID = 7937357305875510798L;
	
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
	public Parameters authenticate(PermissionLevel permissionLevel,
	                               String callbackUrl)
	    throws InterWebException
	{
		notNull(permissionLevel, "permissionLevel");
		if (!isRegistered())
		{
			throw new InterWebException("Service is not yet registered");
		}
		Parameters params = new Parameters();
		try
		{
			AuthCredentials consumerAuthCredentials = getAuthCredentials();
			AuthInterface authInterface = new ExtraAuthInterface(consumerAuthCredentials.getKey(),
			                                                     consumerAuthCredentials.getSecret(),
			                                                     new REST(),
			                                                     callbackUrl);
			String authId = authInterface.getFrob();
			System.out.println("authId: [" + authId + "]");
			Permission permission = Permission.fromString(permissionLevel.getName());
			URL requestTokenUrl = authInterface.buildAuthenticationUrl(permission,
			                                                           authId);
			System.out.println("callbackUrl: [" + callbackUrl + "]");
			System.out.println("requestTokenUrl: ["
			                   + requestTokenUrl.toExternalForm() + "]");
			params.add(Parameters.OAUTH_AUTHORIZATION_URL,
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
			System.out.println("request token frob: " + frob);
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
			                            + "] doesn't belong to connector");
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
	public boolean isRegistrationRequired()
	{
		return true;
	}
	

	@Override
	public void put(byte[] data,
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
				System.out.println(flickr.getPhotosInterface().getPhoto(id).getSmallUrl());
				System.out.println("data successfully uploaded");
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
	

	@SuppressWarnings({"rawtypes", "null"})
	private String createEmbeddedCode(Collection sizes,
	                                  int maxWidth,
	                                  int maxHeight)
	{
		Size size = null;
		for (Object obj : sizes)
		{
			Size s = (Size) obj;
			if (size == null
			    || (s.getWidth() > size.getWidth() && s.getHeight() > size.getHeight())
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
	    throws FlickrException
	{
		ResultItem resultItem = new ImageResultItem(getName());
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
	

	private Set<Thumbnail> createThumbnails(Photo photo)
	    throws FlickrException
	{
		SortedSet<Thumbnail> thumbnails = new TreeSet<Thumbnail>();
		thumbnails.add(new Thumbnail(photo.getSmallSquareUrl(), 75, 75));
		thumbnails.add(new Thumbnail(photo.getThumbnailUrl(), 100, 100));
		thumbnails.add(new Thumbnail(photo.getSmallUrl(), 240, 240));
		thumbnails.add(new Thumbnail(photo.getMediumUrl(), 500, 500));
		return thumbnails;
	}
	

	private Set<String> getExtras()
	{
		Set<String> extras = new HashSet<String>();
		extras.add("description");
		extras.add("tags");
		extras.add("date_upload");
		extras.add("views");
		extras.add("media");
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
				SearchParameters params = new SearchParameters();
				params.setExtras(getExtras());
				if (query.getSearchScopes().contains(SearchScope.TEXT))
				{
					params.setText(query.getQuery());
				}
				if (query.getSearchScopes().contains(SearchScope.TAGS))
				{
					String[] tags = createTags(query.getQuery());
					params.setTags(tags);
				}
				params.setMedia(getMediaType(query));
				params.setMinUploadDate(null);
				params.setMaxUploadDate(null);
				params.setSort(getSortOrder(query.getSortOrder()));
				PhotoList photoList = pi.search(params,
				                                query.getResultCount(),
				                                1);
				System.out.println("Total " + photoList.getTotal()
				                   + " result(s) found");
				int count = 0;
				for (Object o : photoList)
				{
					if (o instanceof Photo)
					{
						Photo photo = (Photo) o;
						ResultItem resultItem = createResultItem(photo,
						                                         count,
						                                         photoList.getTotal());
						queryResult.addResultItem(resultItem);
						count++;
					}
				}
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
}
