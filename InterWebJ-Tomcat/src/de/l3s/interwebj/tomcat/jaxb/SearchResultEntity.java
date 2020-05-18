package de.l3s.interwebj.tomcat.jaxb;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.l3s.interwebj.core.query.ResultItem;
import de.l3s.interwebj.core.query.Thumbnail;

@XmlRootElement(name = "result")
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchResultEntity {
    private static final Logger log = LogManager.getLogger(SearchResultEntity.class);

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

    public SearchResultEntity(ResultItem resultItem) {
        this();

        if (resultItem == null) {
            log.error("Result is null ");
            return;
        }
        setService(resultItem.getServiceName());
        setIdAtService(resultItem.getId());
        setType(resultItem.getType());
        setTitle(resultItem.getTitle());
        String description = resultItem.getDescription();
        if (description == null) {
            description = "no desc";
        }
        setDescription(resultItem.getDescription());

        setUrl(resultItem.getUrl());
        Set<Thumbnail> thumbnails = resultItem.getThumbnails();
        List<ThumbnailEntity> thumbnailEntities = new ArrayList<ThumbnailEntity>();
        if (thumbnails != null) {
            int i = 0;
            for (Thumbnail thumbnail : thumbnails) {
                if (i == 1) {
                    setImage(thumbnail.getUrl());
                }
                i++;
                thumbnailEntities.add(new ThumbnailEntity(thumbnail));
            }
        } else {
            log.error("No thumbnails found for " + resultItem.getId() + " in " + resultItem.getConnectorName());
        }
        setThumbnailEntities(thumbnailEntities);
        //setEmbedded(resultItem.getEmbedded());
        setDate(resultItem.getDate());
        setTags(resultItem.getTags());
        setRankAtService(resultItem.getRank());
        setTotalResultsAtService(resultItem.getTotalResultCount());
        setViews(resultItem.getViewCount());
        setNumberOfComments(resultItem.getCommentCount());
        setNumberOfViews(resultItem.getViewCount());
        setEmbeddedSize1(resultItem.getEmbeddedSize1());
        setEmbeddedSize2(resultItem.getEmbeddedSize2());
        setEmbeddedSize3(resultItem.getEmbeddedSize3());
        setEmbeddedSize4(resultItem.getEmbeddedSize4());
        setImageUrl(resultItem.getImageUrl());
        this.snippet = resultItem.getSnippet();
        this.privacy = resultItem.getPrivacy();
        this.privacyConfidence = resultItem.getPrivacyConfidence();
        this.duration = resultItem.getDuration();
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    //public String getEmbedded() {
    //    return embedded;
    //}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIdAtService() {
        return idAtService;
    }

    public void setIdAtService(String idAtService) {
        this.idAtService = idAtService;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getNumberOfComments() {
        return numberOfComments;
    }

    public void setNumberOfComments(int numberOfComments) {
        this.numberOfComments = numberOfComments;
    }

    public int getNumberOfViews() {
        return numberOfViews;
    }

    public void setNumberOfViews(int numberOfViews) {
        this.numberOfViews = numberOfViews;
    }

    public int getRankAtService() {
        return rankAtService;
    }

    public void setRankAtService(int rankAtService) {
        this.rankAtService = rankAtService;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    //public void setEmbedded(String embedded) {
    //    this.embedded = embedded;
    //}

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public List<ThumbnailEntity> getThumbnailEntities() {
        return thumbnailEntities;
    }

    public void setThumbnailEntities(List<ThumbnailEntity> thumbnailEntities) {
        this.thumbnailEntities = thumbnailEntities;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTotalResultsAtService() {
        return totalResultsAtService;
    }

    public void setTotalResultsAtService(long totalResultsAtService) {
        this.totalResultsAtService = totalResultsAtService;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setViews(int numberOfViews) {
        this.numberOfViews = numberOfViews;
    }

    /**
     * html code, could be flash, max width and max height 500px.
     */
    public String getEmbeddedSize3() {
        return embeddedSize3;
    }

    /**
     * html code, could be flash, max width and max height 500px.
     */
    public void setEmbeddedSize3(String embedded) {
        this.embeddedSize3 = embedded;
    }

    /**
     * html code, only image or text, max width and max height 100px.
     */
    public String getEmbeddedSize1() {
        return embeddedSize1;
    }

    /**
     * html code, only image or text, max width and max height 100px.
     */
    public void setEmbeddedSize1(String embeddedSize1) {
        this.embeddedSize1 = embeddedSize1;
    }

    /**
     * html code, only image or text, max width and max height 240px.
     */
    public String getEmbeddedSize2() {
        return embeddedSize2;
    }

    /**
     * html code, only image or text, max width and max height 240px.
     */
    public void setEmbeddedSize2(String embeddedSize2) {
        this.embeddedSize2 = embeddedSize2;
    }

    /**
     * html code, could be flash, max width and max height 100%.
     */
    public String getEmbeddedSize4() {
        return embeddedSize4;
    }

    /**
     * html code, could be flash, max width and max height 100%.
     */
    public void setEmbeddedSize4(String embeddedSize4) {
        this.embeddedSize4 = embeddedSize4;
    }

    /**
     * Url to the best (high resolution) available preview image.
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Url to the best (high resolution) available preview image.
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSnippet() {
        return snippet;
    }
}
