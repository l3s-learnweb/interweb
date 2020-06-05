package de.l3s.interwebj.connector.ipernity.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
public class Original {

    @XmlAttribute(name = "filename")
    protected String filename;
    @XmlElement(name = "ext")
    protected String ext;
    @XmlAttribute(name = "url")
    protected String url;
    @XmlAttribute(name = "bytes")
    protected Integer size;
    @XmlAttribute(name = "w")
    protected Integer w;
    @XmlAttribute(name = "h")
    protected Integer h;

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(final String ext) {
        this.ext = ext;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(final String filename) {
        this.filename = filename;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(final Integer size) {
        this.size = size;
    }

    public Integer getW() {
        return w;
    }

    public void setW(final Integer w) {
        this.w = w;
    }

    public Integer getH() {
        return h;
    }

    public void setH(final Integer h) {
        this.h = h;
    }

}
