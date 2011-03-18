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
import de.l3s.interwebj.query.*;


public class FlickrConnector
    extends ServiceConnector
{
	
	private static final String MEDIA_ALL = "all";
	private static final String MEDIA_PHOTOS = "photos";
	private static final String MEDIA_VIDEOS = "videos";
	

	public FlickrConnector(AuthCredentials consumerAuthCredentials)
	    throws InterWebException
	{
		super("flickr", "http://www.flickr.com");
		setConsumerAuthCredentials(consumerAuthCredentials);
		init();
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
			Flickr flickr = getFlickrInstance();
			AuthInterface authInterface = flickr.getAuthInterface();
			String authId = authInterface.getFrob();
			Permission permission = Permission.fromString(permissionLevel.getName());
			URL requestTokenUrl = authInterface.buildAuthenticationUrl(permission,
			                                                           authId);
			params.add("oauth_authorization_url",
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
			Flickr flickr = getFlickrInstance();
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
	public void put(byte[] data,
	                Parameters params,
	                AuthCredentials authCredentials)
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
		if (authCredentials == null)
		{
			throw new InterWebException("Upload is forbidden for non-authorized users");
		}
		try
		{
			RequestContext requestContext = RequestContext.getRequestContext();
			Auth auth = new Auth();
			requestContext.setAuth(auth);
			auth.setToken(authCredentials.getKey());
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
	

	@Override
	public boolean requestRegistrationData()
	{
		return true;
	}
	

	private boolean supportContentTypes(List<String> contentTypes)
	{
		return contentTypes.contains("image") || contentTypes.contains("video");
	}
	

	public static void main(String[] args)
	    throws InterWebException
	{
		try
		{
			URL configUrl = new File("./config/config.xml").toURI().toURL();
			Environment environment = Environment.getInstance(configUrl);
			Database database = environment.getDatabase();
			AuthCredentials consumerAuthCredentials = database.readConsumerAuthCredentials("flickr",
			                                                                               "interwebj");
			FlickrConnector connector = new FlickrConnector(consumerAuthCredentials);
			AuthCredentials userAuthCredentials = database.readUserAuthCredentials("flickr",
			                                                                       "olex");
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
			Parameters params = new Parameters();
			params.add("title", "L3S Logo");
			params.add("description", "The L3S Logo");
			connector.put(data, params, userAuthCredentials);
			Environment.logger.info("file sent successfully");
		}
		catch (Exception e)
		{
			throw new InterWebException(e);
		}
	}
}
