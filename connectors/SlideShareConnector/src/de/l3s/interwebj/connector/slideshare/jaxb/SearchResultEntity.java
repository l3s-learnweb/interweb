package de.l3s.interwebj.connector.slideshare.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Slideshow")
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchResultEntity {

    @XmlElement(name = "ID")
    protected int id;
    @XmlElement(name = "Title")
    protected String title;
    @XmlElement(name = "Description")
    protected String description;
    @XmlElement(name = "Status")
    protected int status;
    @XmlElement(name = "Username")
    protected String userName;
    @XmlElement(name = "URL")
    protected String url;

    @XmlElement(name = "ThumbnailURL")
    protected String thumbnailURL;
    @XmlElement(name = "ThumbnailSize")
    protected String thumbnailSize;
    @XmlElement(name = "ThumbnailSmallURL")
    protected String thumbnailSmallURL;
    @XmlElement(name = "Embed")
    protected String embed;
    @XmlElement(name = "Created")
    protected String created;
    @XmlElement(name = "Updated")
    protected String updated;
    @XmlElement(name = "Language")
    protected String language;
    @XmlElement(name = "Format")
    protected String format;
    @XmlElement(name = "Download")
    protected int download;
    @XmlElement(name = "DownloadUrl")
    protected String downloadUrl;
    @XmlElement(name = "SlideshowType")
    protected int slideshowType;
    @XmlElement(name = "InContest")
    protected int inContest;

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDownload() {
        return download;
    }

    public void setDownload(int download) {
        this.download = download;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getEmbed() {
        return embed;
    }

    public void setEmbed(String embed) {
        this.embed = embed;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInContest() {
        return inContest;
    }

    public void setInContest(int inContest) {
        this.inContest = inContest;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getSlideshowType() {
        return slideshowType;
    }

    public void setSlideshowType(int slideshowType) {
        this.slideshowType = slideshowType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getThumbnailSmallURL() {
        return thumbnailSmallURL;
    }

    public void setThumbnailSmallURL(String thumbnailSmallURL) {
        this.thumbnailSmallURL = thumbnailSmallURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getThumbnailSize() {
        return thumbnailSize;
    }

    public void setThumbnailSize(String thumbnailSize) {
        this.thumbnailSize = thumbnailSize;
    }

}
