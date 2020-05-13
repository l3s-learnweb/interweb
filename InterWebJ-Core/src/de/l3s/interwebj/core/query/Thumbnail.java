package de.l3s.interwebj.core.query;

import java.io.Serializable;

public class Thumbnail implements Comparable<Thumbnail>, Serializable
{

    private static final long serialVersionUID = -792701713759619246L;

    private int width;
    private int height;
    private String url;

    public Thumbnail(String url, int width, int height)
    {
	this.url = url; // disabled because of ipernity CoreUtils.ulrToHttps(url);
	this.width = width;
	this.height = height;
    }

    @Override
    public int compareTo(Thumbnail t)
    {
	if(width < t.width)
	{
	    return -1;
	}
	if(width > t.width)
	{
	    return 1;
	}
	if(height < t.height)
	{
	    return -1;
	}
	if(height > t.height)
	{
	    return 1;
	}
	return url.compareTo(t.url);
    }

    public int getHeight()
    {
	return height;
    }

    public String getUrl()
    {
	return url;
    }

    public int getWidth()
    {
	return width;
    }

    @Override
    public String toString()
    {
	StringBuilder builder = new StringBuilder();
	builder.append("Thumbnail [");
	if(url != null)
	{
	    builder.append("url=");
	    builder.append(url);
	    builder.append(", ");
	}
	builder.append("width=");
	builder.append(width);
	builder.append(", height=");
	builder.append(height);
	builder.append("]");
	return builder.toString();
    }
}
