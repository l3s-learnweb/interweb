package de.l3s.interweb;


import java.io.*;
import java.net.*;
import java.util.*;

import org.dom4j.*;
import org.dom4j.io.*;
import org.xml.sax.*;


public class SearchQuery
    implements Serializable
{
	
	private static final long serialVersionUID = 681547180910687848L;
	protected final List<SearchResult> results = new LinkedList<SearchResult>();
	protected String elapsedTime;
	

	/** Do nothing constructor */
	public SearchQuery()
	{
		
	}
	

	public String getElapsedTime()
	{
		return elapsedTime;
	}
	

	public List<SearchResult> getResults()
	{
		return results;
	}
	

	/**
	 * replaces all fields of this object with the values from the xml @param
	 * inputStream
	 * 
	 * @param inputStream stream of an interweb search query
	 * @param date
	 * @throws IOException
	 * @throws SAXException
	 * @throws IllegalResponseException
	 */
	
	protected void parse(InputStream inputStream)
	    throws IllegalResponseException
	{
		try
		{
			Element root = new SAXReader().read(inputStream).getRootElement();
			
			if (!root.attributeValue("stat").equals("ok"))
			{
				throw new IllegalResponseException(root.asXML());
			}
			System.out.println(root.asXML());
			Element queryelement = root.element("query");
			elapsedTime = queryelement.elementText("elapsed_time");
			
			List<Element> resultElements = queryelement.element("results").elements("result");
			
			for (Element resultElement : resultElements)
			{
				
				SearchResult currentResult = new SearchResult();
				results.add(currentResult);
				
				currentResult.type = resultElement.elementText("type");
				currentResult.title = resultElement.elementText("title");
				currentResult.description = resultElement.elementText("description");
				currentResult.serviceName = resultElement.elementText("service");
				currentResult.tags = resultElement.elementText("tags");
				/*
				String views = resultElement.elementText("views");
				if(null != views && views.length())
					Integer.parseInt(views)
				
				if(body.equals("BibSonomy"))
					currentResult.service = Interweb.BIBSONOMY;
				else if(body.equals("Blogger"))
					currentResult.service = Interweb.BLOGGER;
				else if(body.equals("Delicious"))
					currentResult.service = Interweb.DELICIOUS;
				else if(body.equals("Facebook"))
					currentResult.service = Interweb.FACEBOOK;
				else if(body.equals("Flickr"))
					currentResult.service = Interweb.FLICKR;
				else if(body.equals("GroupMe!"))
					currentResult.service = Interweb.GROUPME;
				else if(body.equals("Ipernity"))
					currentResult.service = Interweb.IPERNITY;
				else if(body.equals("last.fm"))
					currentResult.service = Interweb.LASTFM;
				else if(body.equals("SlideShare"))
					currentResult.service = Interweb.SLIDESHARE;
				else if(body.equals("Vimeo"))
					currentResult.service = Interweb.VIMEO;
				else if(body.equals("YouTube"))
					currentResult.service = Interweb.YOUTUBE;
				else 
					exception = new SAXException("unknown service <service>"+body+"</service>");
				 */

				String body = resultElement.elementText("url");
				try
				{
					currentResult.url = new URL(body);
				}
				catch (MalformedURLException e)
				{
					throw new IllegalResponseException("malformed URL in <url>"
					                                   + body + "</url>");
				}
				
				body = resultElement.elementText("image");
				if (body != null && body.length() > 10)
				{
					try
					{
						currentResult.imageURL = new URL(body);
					}
					catch (MalformedURLException e)
					{
						throw new IllegalResponseException("malformed URL in <image>"
						                                   + body + "</image>");
					}
				}
			}
			
		}
		catch (DocumentException e1)
		{
			throw new IllegalResponseException(e1.getMessage());
		}
	}
}
