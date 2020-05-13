package de.l3s.interwebj.connector.vimeo;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import de.l3s.interwebj.core.AuthCredentials;
import de.l3s.interwebj.core.InterWebException;
import de.l3s.interwebj.core.Parameters;
import de.l3s.interwebj.core.core.AbstractServiceConnector;
import de.l3s.interwebj.core.core.ServiceConnector;
import de.l3s.interwebj.core.query.Query;
import de.l3s.interwebj.core.query.QueryResult;
import de.l3s.interwebj.core.query.ResultItem;
import de.l3s.interwebj.core.query.Thumbnail;
import de.l3s.interwebj.core.util.CoreUtils;

public class VimeoConnector extends AbstractServiceConnector
{
	private static final Logger log = LogManager.getLogger(VimeoConnector.class);

    public VimeoConnector()
    {
		super("Vimeo", "http://www.vimeo.com", new TreeSet<>(Arrays.asList("video")));
    }

    public VimeoConnector(AuthCredentials consumerAuthCredentials)
    {
    	this();
		setAuthCredentials(consumerAuthCredentials);
    }

    @Override
    public ServiceConnector clone()
    {
	return new VimeoConnector(getAuthCredentials());
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
    /*
    private static String createSortOrder(SortOrder sortOrder)
    {
    	switch(sortOrder)
    	{
    	case RELEVANCE:
    	    return "relevant";
    	case DATE:
    	    return "date";
    	case INTERESTINGNESS:
    	    return "plays";
    	default:
    	    return "relevant";
    	}
    }*/

    private static Date parseDate(String dateString) throws InterWebException
    {
	if(dateString == null)
	    return null;

	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
	try
	{
	    return dateFormat.parse(dateString);
	}
	catch(ParseException e)
	{
	    log.error(e);
	    throw new InterWebException("dateString: [" + dateString + "] " + e.getMessage());
	}
    }

    @Override
    public QueryResult get(Query query, AuthCredentials authCredentials) throws InterWebException
    {
	if(!isRegistered())
	{
	    throw new InterWebException("Service is not yet registered");
	}
	QueryResult queryResult = new QueryResult(query);

	if(!query.getContentTypes().contains(Query.CT_VIDEO))
	    return queryResult;

	if(query.getQuery().startsWith("user::"))
	{
	    return queryResult;
	}

	Vimeo vimeo = new Vimeo();
	try
	{
	    JSONObject response = vimeo.searchVideos(query.getQuery(), query.getPage() + "", "" + query.getResultCount());

	    //System.out.println(response.getJson());
	    int count = (query.getPage() - 1) * query.getResultCount();
	    long totalResultCount = response.getLong("total");
	    queryResult.setTotalResultCount(totalResultCount);

	    JSONArray data = response.getJSONArray("data");
	    for(int i = 0; i < data.length(); i++)
	    {
		try
		{
		    JSONObject video = data.getJSONObject(i);
		    String name = video.getString("name");
		    String description = video.isNull("description") ? "" : video.getString("description");
		    int duration = video.getInt("duration");
		    String link = video.getString("link");

		    ResultItem resultItem = new ResultItem(getName());
		    resultItem.setType(Query.CT_VIDEO);
		    resultItem.setId(link.substring(1 + link.lastIndexOf('/')));
		    resultItem.setTitle(name);
		    resultItem.setDescription(description);
		    resultItem.setUrl(link);
		    resultItem.setDate(CoreUtils.formatDate(parseDate(video.getString("created_time"))));
		    resultItem.setRank(count++);
		    resultItem.setTotalResultCount(totalResultCount);
		    //resultItem.setCommentCount(video.getNumberOfComments());
		    // resultItem.setViewCount(video.getJSONObject("stats").getInt("plays")); plays can be null TODO need to handle it
		    resultItem.setDuration(duration);

		    JSONArray pictures = video.getJSONObject("pictures").getJSONArray("sizes");
		    Set<Thumbnail> thumbnails = new LinkedHashSet<Thumbnail>();
		    for(int j = 0; j < pictures.length(); j++)
		    {
			JSONObject picture = pictures.getJSONObject(j);
			int width = picture.getInt("width");
			int height = picture.getInt("height");
			String pictureURL = picture.getString("link");

			thumbnails.add(new Thumbnail(pictureURL, width, height));

			resultItem.setImageUrl(pictureURL); // thumbnails are orderd by size. so the last assigned image is the largest

			if(width <= 100)
			    resultItem.setEmbeddedSize1(CoreUtils.createImageCode(pictureURL, width, height, 100, 100));

		    }
		    resultItem.setThumbnails(thumbnails);

		    queryResult.addResultItem(resultItem);
		}
		catch(Throwable e)
		{
		    log.warn("Can't parse entry: " + e.getMessage());
		}
	    }
	}
	catch(Throwable e)
	{
	    log.warn(e.getMessage());
	}
	return queryResult;
    }

    @Override
    public Parameters authenticate(String callbackUrl) throws InterWebException
    {
	if(!isRegistered())
	{
	    throw new InterWebException("Service is not yet registered");
	}
	throw new NotImplementedException();

    }

    @Override
    public AuthCredentials completeAuthentication(Parameters params) throws InterWebException
    {

	throw new NotImplementedException();

    }

    @Override
    public String getEmbedded(AuthCredentials authCredentials, String url, int maxWidth, int maxHeight) throws InterWebException
    {

	throw new InterWebException("URL: [" + url + "] doesn't belong to connector [" + getName() + "]");

    }

    @Override
    public String getUserId(AuthCredentials authCredentials) throws InterWebException
    {

	throw new NotImplementedException();

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

	throw new NotImplementedException();

    }

    @Override
    public void revokeAuthentication() throws InterWebException
    {
    }
}
