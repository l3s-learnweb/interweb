package de.l3s.interwebj.core.query;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.json.bind.annotation.JsonbProperty;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import de.l3s.interwebj.core.util.CoreUtils;

@XmlRootElement(name = "result")
@XmlAccessorType(XmlAccessType.FIELD)
public class ResultItem implements Serializable {
    private static final long serialVersionUID = 8890673440048432524L;

    private static final int MAX_TITLE_LENGTH = 256;
    private static final int MAX_DESCRIPTION_LENGTH = 1024;

    public static final int THUMBNAIL_SMALL_MAX_HEIGHT = 180;
    public static final int THUMBNAIL_MEDIUM_MAX_HEIGHT = 440;
    public static final int THUMBNAIL_LARGE_MAX_HEIGHT = 920;

    // base
    @JsonbProperty("service")
    @XmlElement(name = "service")
    private String serviceName;
    @JsonbProperty("rank_at_service")
    @XmlElement(name = "rank_at_service")
    private int rank;
    @JsonbProperty("id_at_service")
    @XmlElement(name = "id_at_service")
    private String id;

    // general
    @XmlElement(name = "type")
    private ContentType type;
    @XmlElement(name = "title")
    private String title;
    @XmlElement(name = "description")
    private String description;
    @XmlElement(name = "url")
    private String url;
    @XmlElement(name = "date")
    private String date;
    @XmlElement(name = "snippet")
    private String snippet;
    @XmlElement(name = "duration")
    private Long duration;
    @XmlElement(name = "width")
    private Integer width;
    @XmlElement(name = "height")
    private Integer height;
    @JsonbProperty("tags")
    @XmlElementWrapper(name = "tags")
    @XmlElement(name = "tag")
    private Set<String> tags = new HashSet<>();

    // author
    @XmlElement(name = "author")
    private String author;
    @JsonbProperty("author_url")
    @XmlElement(name = "author_url")
    private String authorUrl;

    // statistic
    @JsonbProperty("number_of_views")
    @XmlElement(name = "number_of_views")
    private Long viewCount;
    @JsonbProperty("number_of_comments")
    @XmlElement(name = "number_of_comments")
    private Long commentCount;

    // media
    @JsonbProperty("embedded_code")
    @XmlElement(name = "embedded_code")
    private String embeddedCode;
    /**
     * Usually an image with HEIGHT between 100 and 180 px.
     */
    @JsonbProperty("thumbnail_small")
    @XmlElement(name = "thumbnail_small")
    private Thumbnail thumbnailSmall;
    /**
     * Usually an image with HEIGHT between 200 and 440 px.
     */
    @JsonbProperty("thumbnail_medium")
    @XmlElement(name = "thumbnail_medium")
    private Thumbnail thumbnailMedium;
    /**
     * Usually an image with HEIGHT between 600 and 920 px.
     */
    @JsonbProperty("thumbnail_large")
    @XmlElement(name = "thumbnail_large")
    private Thumbnail thumbnailLarge;
    /**
     * Image in max available quality, if bigger than large.
     */
    @JsonbProperty("thumbnail_original")
    @XmlElement(name = "thumbnail_original")
    private Thumbnail thumbnailOriginal;

    public ResultItem() {
    }

    public ResultItem(final String serviceName) {
        this.serviceName = serviceName;
    }

    public ResultItem(final String serviceName, final int rank) {
        this.serviceName = serviceName;
        this.rank = rank;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(final String serviceName) {
        this.serviceName = serviceName;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(final int rank) {
        this.rank = rank;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public ContentType getType() {
        return type;
    }

    public void setType(final ContentType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = CoreUtils.shortnString(title, MAX_TITLE_LENGTH);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = CoreUtils.shortnString(description, MAX_DESCRIPTION_LENGTH);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getDate() {
        return date;
    }

    public void setDate(final String date) {
        this.date = date;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(final String snippet) {
        this.snippet = snippet;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(final Long duration) {
        this.duration = duration;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(final Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(final Integer height) {
        this.height = height;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(final Set<String> tags) {
        this.tags = tags;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(final String author) {
        this.author = author;
    }

    public String getAuthorUrl() {
        return authorUrl;
    }

    public void setAuthorUrl(final String authorUrl) {
        this.authorUrl = authorUrl;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(final Long viewCount) {
        this.viewCount = viewCount;
    }

    public Long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(final Long commentCount) {
        this.commentCount = commentCount;
    }

    public String getEmbeddedCode() {
        return embeddedCode;
    }

    public void setEmbeddedCode(final String embeddedCode) {
        this.embeddedCode = embeddedCode;
    }

    public Thumbnail getThumbnailSmall() {
        return thumbnailSmall;
    }

    public void setThumbnailSmall(final Thumbnail thumbnailSmall) {
        this.thumbnailSmall = thumbnailSmall;
    }

    public Thumbnail getThumbnailMedium() {
        return thumbnailMedium;
    }

    public void setThumbnailMedium(final Thumbnail thumbnailMedium) {
        this.thumbnailMedium = thumbnailMedium;
    }

    public Thumbnail getThumbnailLarge() {
        return thumbnailLarge;
    }

    public void setThumbnailLarge(final Thumbnail thumbnailLarge) {
        this.thumbnailLarge = thumbnailLarge;
    }

    public Thumbnail getThumbnailOriginal() {
        return thumbnailOriginal;
    }

    public void setThumbnailOriginal(final Thumbnail thumbnailOriginal) {
        this.thumbnailOriginal = thumbnailOriginal;
    }

    public void setThumbnail(Thumbnail thumbnail) {
        if (thumbnail.getHeight() < THUMBNAIL_SMALL_MAX_HEIGHT) {
            if (thumbnailSmall == null || thumbnailSmall.getHeight() < thumbnail.getHeight()) {
                thumbnailSmall = thumbnail;
            }
        } else if (thumbnail.getHeight() < THUMBNAIL_MEDIUM_MAX_HEIGHT) {
            if (thumbnailMedium == null || thumbnailMedium.getHeight() < thumbnail.getHeight()) {
                thumbnailMedium = thumbnail;
            }
        } else if (thumbnail.getHeight() < THUMBNAIL_LARGE_MAX_HEIGHT) {
            if (thumbnailLarge == null || thumbnailLarge.getHeight() < thumbnail.getHeight()) {
                thumbnailLarge = thumbnail;
            }
        } else {
            if (thumbnailOriginal == null || thumbnailOriginal.getHeight() < thumbnail.getHeight()) {
                thumbnailOriginal = thumbnail;
            }
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append("serviceName", serviceName)
            .append("rank", rank)
            .append("id", id)
            .append("title", title)
            .append("type", type)
            .append("description", description)
            .append("url", url)
            .append("date", date)
            .append("snippet", snippet)
            .append("duration", duration)
            .append("width", width)
            .append("height", height)
            .append("tags", tags)
            .append("author", author)
            .append("authorUrl", authorUrl)
            .append("viewCount", viewCount)
            .append("commentCount", commentCount)
            .append("embeddedCode", embeddedCode)
            .append("thumbnailSmall", thumbnailSmall)
            .append("thumbnailMedium", thumbnailMedium)
            .append("thumbnailLarge", thumbnailLarge)
            .append("thumbnailOriginal", thumbnailOriginal)
            .toString();
    }
}
