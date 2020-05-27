package de.l3s.interwebj.client.jaxb;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "result")
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchResultEntity {

    @XmlElement(name = "service")
    protected String service;
    @XmlElement(name = "id_at_service")
    protected String idAtService;
    @XmlElement(name = "type")
    protected String type;
    @XmlElement(name = "title")
    protected String title;
    @XmlElement(name = "description")
    protected String description;
    @XmlElement(name = "url")
    protected String url;
    // TODO: Remove image element. Used only for the InterWeb compatibility
    @XmlElement(name = "image")
    protected String image;
    @XmlElementWrapper(name = "thumbnails")
    @XmlElement(name = "thumbnail")
    protected List<ThumbnailEntity> thumbnailEntities;
    @XmlElement(name = "date")
    protected String date;
    @XmlElement(name = "tags")
    protected String tags;
    @XmlElement(name = "rank_at_service")
    protected int rankAtService;
    @XmlElement(name = "total_results_at_service")
    protected long totalResultsAtService;
    @XmlElement(name = "views")
    protected int numberOfViews;
    @XmlElement(name = "number_of_comments")
    protected int numberOfComments;
    @XmlElement(name = "privacy")
    protected double privacy;
    @XmlElement(name = "privacy_confidence")
    protected double privacyConfidence;
    @XmlElement(name = "embedded_size1")
    private String embeddedSize1;
    @XmlElement(name = "embedded_size2")
    private String embeddedSize2;
    @XmlElement(name = "embedded_size3")
    private String embeddedSize3;
    @XmlElement(name = "embedded_size4")
    private String embeddedSize4;
    @XmlElement(name = "max_image_url")
    private String imageUrl;
    @XmlElement(name = "duration")
    private int duration;
    @XmlElement(name = "snippet")
    private String snippet;

    public SearchResultEntity() {
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getIdAtService() {
        return idAtService;
    }

    public void setIdAtService(String idAtService) {
        this.idAtService = idAtService;
    }

    public String getType() {
        return type.toLowerCase();
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<ThumbnailEntity> getThumbnailEntities() {
        return thumbnailEntities;
    }

    public void setThumbnailEntities(List<ThumbnailEntity> thumbnailEntities) {
        this.thumbnailEntities = thumbnailEntities;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int getRankAtService() {
        return rankAtService;
    }

    public void setRankAtService(int rankAtService) {
        this.rankAtService = rankAtService;
    }

    public long getTotalResultsAtService() {
        return totalResultsAtService;
    }

    public void setTotalResultsAtService(long totalResultsAtService) {
        this.totalResultsAtService = totalResultsAtService;
    }

    public int getNumberOfViews() {
        return numberOfViews;
    }

    public void setNumberOfViews(int numberOfViews) {
        this.numberOfViews = numberOfViews;
    }

    public int getNumberOfComments() {
        return numberOfComments;
    }

    public void setNumberOfComments(int numberOfComments) {
        this.numberOfComments = numberOfComments;
    }

    /**
     * html code, only image or text, max width and max height 100px.
     */
    public String getEmbeddedSize1() {
        return embeddedSize1;
    }

    public void setEmbeddedSize1(String embeddedSize1) {
        this.embeddedSize1 = embeddedSize1;
    }

    /**
     * html code, only image or text, max width and max height 240px.
     */
    public String getEmbeddedSize2() {
        return embeddedSize2;
    }

    public void setEmbeddedSize2(String embeddedSize2) {
        this.embeddedSize2 = embeddedSize2;
    }

    /**
     * html code, could be flash, max width and max height 500px.
     */
    public String getEmbeddedSize3() {
        return embeddedSize3;
    }

    public void setEmbeddedSize3(String embeddedSize3) {
        this.embeddedSize3 = embeddedSize3;
    }

    /**
     * html code, could be flash, max width and max height 100%.
     */
    public String getEmbeddedSize4() {
        return embeddedSize4;
    }

    public void setEmbeddedSize4(String embeddedSize4) {
        this.embeddedSize4 = embeddedSize4;
    }

    /**
     * Url to the best (high resolution) available preview image.
     */
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getSnippet() {
        return snippet;
    }

    @Override
    public String toString() {
        return "SearchResultEntity [service=" + service + ", idAtService=" + idAtService + ", type=" + type + ", title=" + title
            + ", description=" + description + ", url=" + url + ", image=" + image + ", thumbnailEntities=" + thumbnailEntities + ", date=" + date
            + ", tags=" + tags + ", rankAtService=" + rankAtService + ", totalResultsAtService=" + totalResultsAtService + ", numberOfViews=" + numberOfViews
            + ", numberOfComments=" + numberOfComments + ", privacy=" + privacy + ", privacyConfidence=" + privacyConfidence
            + ", embeddedSize1=" + embeddedSize1 + ", embeddedSize2=" + embeddedSize2 + ", embeddedSize3=" + embeddedSize3
            + ", embeddedSize4=" + embeddedSize4 + ", imageUrl=" + imageUrl + ", duration=" + duration + ", snippet=" + snippet + "]";
    }

}
