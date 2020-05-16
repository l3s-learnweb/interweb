package de.l3s.interwebj.core.query;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

public class Thumbnail implements Comparable<Thumbnail>, Serializable
{

    private static final long serialVersionUID = -792701713759619246L;

    private final int width;
    private final int height;
    private final String url;

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
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Thumbnail thumbnail = (Thumbnail) o;
		return width == thumbnail.width && height == thumbnail.height && Objects.equals(url, thumbnail.url);
	}

	@Override
	public int hashCode() {
		return Objects.hash(width, height, url);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("width", width)
				.append("height", height)
				.append("url", url)
				.toString();
	}
}
