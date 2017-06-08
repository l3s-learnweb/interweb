package de.l3s.interwebj.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.api.client.ClientResponse;

import de.l3s.interwebj.core.Environment;
import de.l3s.interwebj.query.Thumbnail;

public class CoreUtils
{

    private static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static List<String> convertToUniqueList(String s)
    {
	Set<String> list = new HashSet<String>();
	String[] tokens = s.split("[,\\s]");
	for(String token : tokens)
	{
	    if(token.length() > 0)
	    {
		list.add(token);
	    }
	}
	return new ArrayList<String>(list);
    }

    public static String formatDate(DateFormat df, Date date)
    {
	return (date == null) ? null : df.format(date);
    }

    public static String formatDate(Date date)
    {
	return formatDate(DEFAULT_DATE_FORMAT, date);
    }

    public static String formatDate(DateFormat df, long millis)
    {
	return df.format(new Date(millis));
    }

    public static String formatDate(long millis)
    {
	return formatDate(DEFAULT_DATE_FORMAT, millis);
    }

    public static String getClientResponseContent(ClientResponse response) throws IOException
    {
	StringBuilder sb = new StringBuilder();
	InputStream is = response.getEntityInputStream();
	BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	int c;
	while((c = br.read()) != -1)
	{
	    sb.append((char) c);
	}
	br.close();
	return sb.toString();
    }

    public static long parseDate(DateFormat df, String dateString) throws ParseException
    {
	return df.parse(dateString).getTime();
    }

    public static long parseDate(String dateString) throws ParseException
    {
	return parseDate(DEFAULT_DATE_FORMAT, dateString);
    }

    public static void printClientResponse(ClientResponse response)
    {
	Environment.logger.info("Status: [" + response.getStatus() + "]");
	Environment.logger.info("Headers:");
	MultivaluedMap<String, String> headers = response.getHeaders();
	for(String header : headers.keySet())
	{
	    Environment.logger.info("    " + header + ": " + headers.get(header));
	}
    }

    public static String createImageCode(Thumbnail tn, int maxWidth, int maxHeight)
    {
	return createImageCode(tn.getUrl(), tn.getWidth(), tn.getHeight(), maxWidth, maxHeight);
    }

    public static String ulrToHttps(String url)
    {
	if(url.startsWith("http://"))
	    return url.replace("http://", "https://");
	return url;
    }
    
    public static String createImageCode(String url, int imageWidth, int imageHeight, int maxWidth, int maxHeight)
    {
	if(null == url || url.length() < 7 || imageWidth < 2 || imageHeight < 2)
	    return null;

	int width = imageWidth;
	int height = imageHeight;

	if(width > maxWidth)
	{
	    double ratio = (double) maxWidth / (double) width;
	    height = (int) Math.ceil(height * ratio);
	    width = maxWidth;
	}

	if(height > maxHeight)
	{
	    double ratio = (double) maxHeight / (double) height;
	    width = (int) (width * ratio);
	    height = maxHeight;
	}

	

	return "<img src=\"" + ulrToHttps(url) + "\" width=\"" + width + "\" height=\"" + height + "\" alt=\"\" />";
    }
}
