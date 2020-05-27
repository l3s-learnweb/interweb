package de.l3s.interwebj.client.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "thumbnail")
@XmlAccessorType(XmlAccessType.FIELD)
public class ThumbnailEntity {

    @XmlValue
    protected String url;
    @XmlAttribute(name = "width")
    protected int width;
    @XmlAttribute(name = "height")
    protected int height;

    public ThumbnailEntity() {
    }

    public ThumbnailEntity(String url, int width, int height) {
        this.url = url;
        this.width = width;
        this.height = height;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "ThumbnailEntity [width=" + width + ", height=" + height + ", url=" + url + "]";
    }

}
