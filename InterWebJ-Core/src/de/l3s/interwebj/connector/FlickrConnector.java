package de.l3s.interwebj.connector;


import java.io.*;
import java.net.*;
import java.util.*;

import javax.xml.parsers.*;

import org.xml.sax.*;

import com.aetrion.flickr.*;
import com.aetrion.flickr.auth.*;
import com.aetrion.flickr.photos.*;
import com.aetrion.flickr.tags.*;
import com.aetrion.flickr.uploader.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.query.*;
import de.l3s.interwebj.query.Query.SearchScope;
import de.l3s.interwebj.query.Query.SortOrder;
import de.l3s.interwebj.util.*;


public class FlickrConnector
    extends ServiceConnector
{
	
	private static final String MEDIA_ALL = "all";
	private static final String MEDIA_PHOTOS = "photos";
	private static final String MEDIA_VIDEOS = "videos";
	

	public FlickrConnector(AuthCredentials consumerAuthCredentials)
	{
		super("flickr", "http://www.flickr.com");
		setConsumerAuthCredentials(consumerAuthCredentials);
	}
	

	@Override
	public Parameters authenticate(PermissionLevel permissionLevel,
	                               String callbackUrl)
	    throws InterWebException
	{
		if (permissionLevel == null)
		{
			throw new NullPointerException("Argument [permissionLevel] can not be null");
		}
		if (!isRegistered())
		{
			throw new InterWebException("Service is not yet registered");
		}
		Parameters params = new Parameters();
		try
		{
			Flickr flickr = createFlickrInstance();
			AuthInterface authInterface = flickr.getAuthInterface();
			String authId = authInterface.getFrob();
			Permission permission = Permission.fromString(permissionLevel.getName());
			URL requestTokenUrl = authInterface.buildAuthenticationUrl(permission,
			                                                           authId);
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
		return new FlickrConnector(getConsumerAuthCredentials());
	}
	

	@Override
	public AuthCredentials completeAuthentication(Parameters params)
	    throws InterWebException
	{
		if (params == null)
		{
			throw new NullPointerException("Argument [params] can not be null");
		}
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
			Environment.logger.debug("Username: "
			                         + auth.getUser().getUsername());
			Environment.logger.debug("Realname: "
			                         + auth.getUser().getRealName());
			Environment.logger.debug("Token: " + auth.getToken());
			Environment.logger.debug("Permission: "
			                         + PermissionLevel.values()[auth.getPermission().getType()]);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new InterWebException(e);
		}
		return authCredentials;
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
			AuthCredentials consumerAuthCredentials = getConsumerAuthCredentials();
			URL baseUrl = new URL(getBaseUrl());
			return new Flickr(consumerAuthCredentials.getKey(),
			                  consumerAuthCredentials.getSecret(),
			                  new REST(baseUrl.getHost()));
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
			throw new InterWebException(e);
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
			throw new InterWebException(e);
		}
	}
	

	private ResultItem createPhoto(Photo photo, int rank, int totalResultCount)
	{
		ResultItem resultItem = new ImageResultItem(getName());
		resultItem.setServiceName(getName());
		resultItem.setId(photo.getId());
		resultItem.setType(getContentType(photo.getMedia()));
		resultItem.setTitle(photo.getTitle());
		resultItem.setDescription(photo.getDescription());
		resultItem.setTags(getTagString(photo.getTags()));
		resultItem.setUrl(photo.getUrl());
		resultItem.setImageUrl(photo.getSmallUrl());
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
	

	private String[] createTags(String query)
	{
		return query.split("[\\W]+");
	}
	

	@Override
	public QueryResult get(Query query, AuthCredentials authCredentials)
	    throws InterWebException
	{
		if (query == null)
		{
			throw new NullPointerException("Argument [query] can not be null");
		}
		if (!isRegistered())
		{
			throw new InterWebException("Service is not yet registered");
		}
		QueryResult queryResult = new QueryResult(query);
		if (supportContentTypes(query.getContentTypes()))
		{
			try
			{
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
				params.setMedia(getMedia(query));
				params.setMinUploadDate(null);
				params.setMaxUploadDate(null);
				params.setSort(getSortOrder(query.getSortOrder()));
				PhotoList photoList = pi.search(params,
				                                query.getResultCount(),
				                                1);
				Environment.logger.debug("Total " + photoList.getTotal()
				                         + " result(s) found");
				int count = 0;
				for (Object o : photoList)
				{
					if (o instanceof Photo)
					{
						Photo photo = (Photo) o;
						ResultItem resultItem = createPhoto(photo,
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
	

	private String getContentType(String media)
	{
		if ("photo".equals(media))
		{
			return Query.CT_IMAGE;
		}
		return media;
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
	

	private String getMedia(Query query)
	{
		if (query == null)
		{
			throw new NullPointerException("Argument [query] can not be null");
		}
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
	

	@SuppressWarnings("rawtypes")
	private String getTagString(Collection tags)
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
	

	@Override
	protected void init()
	{
		// TODO: Stub. Read from configuration file
		TreeSet<String> contentTypes = new TreeSet<String>();
		contentTypes.add("image");
		contentTypes.add("video");
		setContentTypes(contentTypes);
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
		if (data == null)
		{
			throw new NullPointerException("Argument [data] can not be null");
		}
		if (contentType == null)
		{
			throw new NullPointerException("Argument [contentType] can not be null");
		}
		if (params == null)
		{
			throw new NullPointerException("Argument [params] can not be null");
		}
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
				if (params.containsKey(Parameters.TITLE))
				{
					metaData.setTitle(params.get(Parameters.TITLE, "No title"));
				}
				if (params.containsKey(Parameters.DESCRIPTION))
				{
					metaData.setDescription(params.get(Parameters.DESCRIPTION,
					                                   "No description"));
				}
				if (params.containsKey(Parameters.TAGS))
				{
					String tags = params.get(Parameters.TAGS);
					metaData.setTags(CoreUtils.convertToUniqueList(tags));
				}
				if (params.containsKey(Parameters.PRIVACY))
				{
					int privacy = Integer.parseInt(params.get(Parameters.PRIVACY,
					                                          "0"));
					metaData.setPublicFlag(privacy == 0);
				}
				uploader.upload(data, metaData);
				Environment.logger.debug("data successfully uploaded");
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
	

	private boolean supportContentTypes(List<String> contentTypes)
	{
		return contentTypes.contains(Query.CT_IMAGE)
		       || contentTypes.contains(Query.CT_VIDEO);
	}
}
