package de.l3s.interwebj.core.query;

import java.io.Serializable;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlRootElement(name = "thumbnail")
@XmlAccessorType(XmlAccessType.FIELD)
public class Thumbnail implements Serializable {
    private static final long serialVersionUID = -792701713759619246L;

    @XmlValue
    private String url;
    @XmlAttribute(name = "width")
    private int width;
    @XmlAttribute(name = "height")
    private int height;

    public Thumbnail() {
    }

    public Thumbnail(String url, int width, int height) {
        this.url = url;
        this.width = width;
        this.height = height;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(final int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(final int height) {
        this.height = height;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Thumbnail thumbnail = (Thumbnail) o;
        return width == thumbnail.width && height == thumbnail.height && Objects.equals(url, thumbnail.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, width, height);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append("url", url)
            .append("width", width)
            .append("height", height)
            .toString();
    }
}
