package de.l3s.interwebj.connector;


import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

import javax.xml.parsers.*;

import org.xml.sax.*;

import com.aetrion.flickr.*;
import com.aetrion.flickr.auth.*;
import com.aetrion.flickr.photos.*;
import com.aetrion.flickr.uploader.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.db.*;
import de.l3s.interwebj.oauth.*;
import de.l3s.interwebj.query.*;


public class FlickrConnector
    extends ServiceConnector
{
	
	private static final String MEDIA_ALL = "all";
	private static final String MEDIA_PHOTOS = "photos";
	private static final String MEDIA_VIDEOS = "videos";
	

	public FlickrConnector(AuthData consumerAuthData)
	    throws InterWebException
	{
		super("flickr", "http://www.flickr.com");
		setConsumerAuthData(consumerAuthData);
		init();
	}
	

	@Override
	public OAuthParams authenticate(PermissionLevel permissionLevel,
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
		OAuthParams oAuthParams = new OAuthParams();
		try
		{
			Flickr flickr = getFlickrInstance();
			AuthInterface authInterface = flickr.getAuthInterface();
			String authId = authInterface.getFrob();
			Permission permission = Permission.fromString(permissionLevel.getName());
			URL requestTokenUrl = authInterface.buildAuthenticationUrl(permission,
			                                                           authId);
			oAuthParams.setRequestUrl(requestTokenUrl);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new InterWebException(e);
		}
		return oAuthParams;
	}
	

	@Override
	public AuthData completeAuthentication(Map<String, String[]> params)
	    throws InterWebException
	{
		if (params == null)
		{
			throw new NullPointerException("Argument [params] can not be null");
		}
		AuthData authData = null;
		if (!isRegistered())
		{
			throw new InterWebException("Service is not yet registered");
		}
		try
		{
			String[] values = params.get("frob");
			if (values == null || values.length == 0)
			{
				return null;
			}
			if (values.length > 1)
			{
				Environment.logger.warn("More than one parameter values for \"frob\" key found");
			}
			String frob = values[0];
			Flickr flickr = getFlickrInstance();
			Environment.logger.info("request token frob: " + frob);
			AuthInterface authInterface = flickr.getAuthInterface();
			Auth auth = authInterface.getToken(frob);
			authData = new AuthData(auth.getToken());
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
		return authData;
	}
	

	@Override
	public QueryResult get(Query query, AuthData authData)
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
				Flickr flickr = getFlickrInstance();
				PhotosInterface pi = flickr.getPhotosInterface();
				SearchParameters params = new SearchParameters();
				params.setText(query.getQuery());
				params.setMedia(getMedia(query));
				PhotoList photoList = pi.search(params, 100, 0);
				Environment.logger.debug("Total " + photoList.getTotal()
				                         + " result(s) found for query ["
				                         + query.getQuery() + "]");
				for (Object o : photoList)
				{
					if (o instanceof Photo)
					{
						Photo photo = (Photo) o;
						Environment.logger.debug(photo.getMedia() + " "
						                         + photo.getMedia() + " ["
						                         + photo.getId() + "]: "
						                         + photo.getUrl());
						ResultItem resultItem = new ImageResultItem(getName());
						resultItem.setTitle(photo.getTitle());
						resultItem.setUrl(photo.getUrl());
						resultItem.setPreviewUrl(photo.getSmallUrl());
						resultItem.setDescription(photo.getDescription());
						if (photo.getDateAdded() != null)
						{
							Date date = photo.getDateAdded();
							resultItem.setDate("(added)"
							                   + DateFormat.getDateInstance(DateFormat.MEDIUM).format(date));
						}
						else if (photo.getDatePosted() != null)
						{
							Date date = photo.getDatePosted();
							resultItem.setDate("(posted)"
							                   + DateFormat.getDateInstance(DateFormat.MEDIUM).format(date));
						}
						else if (photo.getDateTaken() != null)
						{
							Date date = photo.getDateTaken();
							resultItem.setDate("(taken)"
							                   + DateFormat.getDateInstance(DateFormat.MEDIUM).format(date));
						}
						queryResult.addResultItem(resultItem);
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
	

	private Flickr getFlickrInstance()
	    throws InterWebException
	{
		if (!isRegistered())
		{
			throw new InterWebException("Unable to create Flickr instance. Service is not registered");
		}
		try
		{
			AuthData consumerAuthData = getConsumerAuthData();
			URL baseUrl = new URL(getBaseUrl());
			return new Flickr(consumerAuthData.getKey(),
			                  consumerAuthData.getSecret(),
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
	

	private String getMedia(Query query)
	{
		if (query == null)
		{
			throw new NullPointerException("Argument [query] can not be null");
		}
		if (query.getContentTypes().contains(MEDIA_PHOTOS)
		    && query.getContentTypes().contains(MEDIA_VIDEOS))
		{
			return MEDIA_ALL;
		}
		if (query.getContentTypes().contains(MEDIA_PHOTOS))
		{
			return MEDIA_PHOTOS;
		}
		return MEDIA_VIDEOS;
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
	public void put(byte[] data, Map<String, String> params, AuthData authData)
	    throws InterWebException
	{
		if (data == null)
		{
			throw new NullPointerException("Argument [data] can not be null");
		}
		if (params == null)
		{
			throw new NullPointerException("Argument [params] can not be null");
		}
		if (!isRegistered())
		{
			throw new InterWebException("Service is not yet registered");
		}
		if (authData == null)
		{
			throw new InterWebException("Upload is forbidden for non-authorized users");
		}
		try
		{
			RequestContext requestContext = RequestContext.getRequestContext();
			Auth auth = new Auth();
			requestContext.setAuth(auth);
			auth.setToken(authData.getKey());
			auth.setPermission(Permission.WRITE);
			Flickr flickr = getFlickrInstance();
			Uploader uploader = flickr.getUploader();
			UploadMetaData metaData = new UploadMetaData();
			if (params.containsKey("title"))
			{
				metaData.setDescription(params.get("title"));
			}
			if (params.containsKey("description"))
			{
				metaData.setDescription(params.get("description"));
			}
			uploader.upload(data, metaData);
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
	

	private boolean supportContentTypes(List<String> contentTypes)
	{
		return contentTypes.contains("image") || contentTypes.contains("video");
	}
	

	@Override
	public boolean supportOAuth()
	{
		return true;
	}
	

	public static void main(String[] args)
	    throws InterWebException
	{
		try
		{
			URL configUrl = new File("./config/config.xml").toURI().toURL();
			Environment environment = Environment.getInstance(configUrl);
			Database database = environment.getDatabase();
			AuthData consumerAuthData = database.readConsumerAuthData("flickr",
			                                                          "interwebj");
			FlickrConnector connector = new FlickrConnector(consumerAuthData);
			AuthData userAuthData = database.readUserAuthData("flickr", "olex");
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int i;
			byte[] buffer = new byte[1024];
			InputStream is = new FileInputStream("/home/olex/temp/L3S_header_logo.png");
			while ((i = is.read(buffer)) != -1)
			{
				out.write(buffer, 0, i);
				out.flush();
			}
			byte data[] = out.toByteArray();
			out.close();
			is.close();
			Map<String, String> params = new HashMap<String, String>();
			params.put("title", "L3S Logo");
			params.put("description", "The L3S Logo");
			connector.put(data, params, userAuthData);
			Environment.logger.info("file sent successfully");
		}
		catch (Exception e)
		{
			throw new InterWebException(e);
		}
	}
}
