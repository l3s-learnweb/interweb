package de.l3s.interweb.core.search;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import de.l3s.interweb.core.util.StringUtils;
import de.l3s.interweb.core.util.ToStringBuilder;

@JsonPropertyOrder({"rank", "id", "type", "url", "title", "description", "author", "author_url"})
@RegisterForReflection
public class SearchItem implements Serializable {
    @Serial
    private static final long serialVersionUID = 8890673440048432524L;

    private static final int MAX_TITLE_LENGTH = 256;
    private static final int MAX_DESCRIPTION_LENGTH = 1024;

    public static final int THUMBNAIL_SMALL_MAX_HEIGHT = 180;
    public static final int THUMBNAIL_MEDIUM_MAX_HEIGHT = 440;
    public static final int THUMBNAIL_LARGE_MAX_HEIGHT = 920;

    // base
    private String id;
    private Integer rank;

    // general
    private ContentType type;
    private String title;
    private String description;
    private String url;
    private Instant date;

    // media
    /**
     * Duration in seconds.
     */
    private Long duration;
    private Integer width;
    private Integer height;
    private Set<String> tags = new HashSet<>();

    // author
    private String author;
    @JsonProperty("author_url")
    private String authorUrl;

    @JsonProperty("views_count")
    private Long viewsCount;
    @JsonProperty("comments_count")
    private Long commentsCount;

    @JsonProperty("embed_url")
    private String embedUrl;
    /**
     * Usually an image with HEIGHT between 100 and 180 px.
     */
    @JsonProperty("thumbnail_small")
    private Thumbnail thumbnailSmall;
    /**
     * Usually an image with HEIGHT between 200 and 440 px.
     */
    @JsonProperty("thumbnail_medium")
    private Thumbnail thumbnailMedium;
    /**
     * Usually an image with HEIGHT between 600 and 920 px.
     */
    @JsonProperty("thumbnail_large")
    private Thumbnail thumbnailLarge;
    /**
     * Image in max available quality, if bigger than large.
     */
    @JsonProperty("thumbnail_original")
    private Thumbnail thumbnailOriginal;

    public SearchItem() {
    }

    public SearchItem(final Integer rank) {
        this.rank = rank;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(final Integer rank) {
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
        this.title = StringUtils.shorten(title, MAX_TITLE_LENGTH);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = StringUtils.shorten(description, MAX_DESCRIPTION_LENGTH);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(final Instant date) {
        this.date = date;
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

    public void addTag(final String tag) {
        this.tags.add(tag);
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

    public Long getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(final Long viewsCount) {
        this.viewsCount = viewsCount;
    }

    public Long getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(final Long commentsCount) {
        this.commentsCount = commentsCount;
    }

    public String getEmbedUrl() {
        return embedUrl;
    }

    public void setEmbedUrl(final String embedUrl) {
        this.embedUrl = embedUrl;
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

    /**
     * @deprecated Use {@link #getLargestThumbnail()} instead.
     */
    @Deprecated(forRemoval = true)
    @JsonIgnore
    public Thumbnail getBiggestThumbnail() {
        return getLargestThumbnail();
    }

    @JsonIgnore
    public Thumbnail getLargestThumbnail() {
        if (thumbnailOriginal != null) {
            return thumbnailOriginal;
        }
        if (thumbnailLarge != null) {
            return thumbnailLarge;
        }
        if (thumbnailMedium != null) {
            return thumbnailMedium;
        }
        return thumbnailSmall;
    }

    public void setThumbnail(Thumbnail thumbnail) {
        if (thumbnail.getHeight() <= THUMBNAIL_SMALL_MAX_HEIGHT && (thumbnailSmall == null || thumbnailSmall.getHeight() < thumbnail.getHeight())) {
            thumbnailSmall = thumbnail;
        } else if (thumbnail.getHeight() <= THUMBNAIL_MEDIUM_MAX_HEIGHT && (thumbnailMedium == null || thumbnailMedium.getHeight() < thumbnail.getHeight())) {
            thumbnailMedium = thumbnail;
        } else if (thumbnail.getHeight() <= THUMBNAIL_LARGE_MAX_HEIGHT && (thumbnailLarge == null || thumbnailLarge.getHeight() < thumbnail.getHeight())) {
            thumbnailLarge = thumbnail;
        } else if (thumbnail.getHeight() > THUMBNAIL_LARGE_MAX_HEIGHT && (thumbnailOriginal == null || thumbnailOriginal.getHeight() < thumbnail.getHeight())) {
            thumbnailOriginal = thumbnail;
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, false)
            .append("id", id)
            .append("rank", rank)
            .append("type", type)
            .append("title", title)
            .append("description", description)
            .append("url", url)
            .append("date", date)
            .append("duration", duration)
            .append("width", width)
            .append("height", height)
            .append("tags", tags)
            .append("author", author)
            .append("authorUrl", authorUrl)
            .append("viewsCount", viewsCount)
            .append("commentsCount", commentsCount)
            .append("embedUrl", embedUrl)
            .append("thumbnailSmall", thumbnailSmall)
            .append("thumbnailMedium", thumbnailMedium)
            .append("thumbnailLarge", thumbnailLarge)
            .append("thumbnailOriginal", thumbnailOriginal)
            .build();
    }
}
