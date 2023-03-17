package de.l3s.bingService.models;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Video extends BaseMedia {

    private String description;
    private List<Publisher> publisher;
    private Boolean isAccessibleForFree;
    private String duration;
    private String motionThumbnailUrl;
    private String embedHtml;
    private Boolean allowHttpsEmbed;
    private Long viewCount;
    private Boolean allowMobileEmbed;
    private Boolean isSuperfresh;

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public List<Publisher> getPublisher() {
        return publisher;
    }

    public void setPublisher(final List<Publisher> publisher) {
        this.publisher = publisher;
    }

    public Boolean getAccessibleForFree() {
        return isAccessibleForFree;
    }

    public void setAccessibleForFree(final Boolean accessibleForFree) {
        isAccessibleForFree = accessibleForFree;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(final String duration) {
        this.duration = duration;
    }

    public String getMotionThumbnailUrl() {
        return motionThumbnailUrl;
    }

    public void setMotionThumbnailUrl(final String motionThumbnailUrl) {
        this.motionThumbnailUrl = motionThumbnailUrl;
    }

    public String getEmbedHtml() {
        return embedHtml;
    }

    public void setEmbedHtml(final String embedHtml) {
        this.embedHtml = embedHtml;
    }

    public Boolean getAllowHttpsEmbed() {
        return allowHttpsEmbed;
    }

    public void setAllowHttpsEmbed(final Boolean allowHttpsEmbed) {
        this.allowHttpsEmbed = allowHttpsEmbed;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(final Long viewCount) {
        this.viewCount = viewCount;
    }

    public Boolean getAllowMobileEmbed() {
        return allowMobileEmbed;
    }

    public void setAllowMobileEmbed(final Boolean allowMobileEmbed) {
        this.allowMobileEmbed = allowMobileEmbed;
    }

    public Boolean getSuperfresh() {
        return isSuperfresh;
    }

    public void setSuperfresh(final Boolean superfresh) {
        isSuperfresh = superfresh;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("webSearchUrl", webSearchUrl)
            .append("name", name)
            .append("thumbnailUrl", thumbnailUrl)
            .append("datePublished", datePublished)
            .append("contentUrl", contentUrl)
            .append("hostPageUrl", hostPageUrl)
            .append("encodingFormat", encodingFormat)
            .append("hostPageDisplayUrl", hostPageDisplayUrl)
            .append("width", width)
            .append("height", height)
            .append("thumbnail", thumbnail)
            .append("description", description)
            .append("publisher", publisher)
            .append("isAccessibleForFree", isAccessibleForFree)
            .append("duration", duration)
            .append("motionThumbnailUrl", motionThumbnailUrl)
            .append("embedHtml", embedHtml)
            .append("allowHttpsEmbed", allowHttpsEmbed)
            .append("viewCount", viewCount)
            .append("allowMobileEmbed", allowMobileEmbed)
            .append("isSuperfresh", isSuperfresh)
            .toString();
    }
}
