package de.l3s.interweb.connector.bing.client.entity;

public abstract class BaseHolder {

    String id;
    String readLink;
    String webSearchUrl;
    Boolean isFamilyFriendly;
    Long totalEstimatedMatches;
    Integer currentOffset;
    Integer nextOffset;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReadLink() {
        return readLink;
    }

    public void setReadLink(String readLink) {
        this.readLink = readLink;
    }

    public String getWebSearchUrl() {
        return webSearchUrl;
    }

    public void setWebSearchUrl(String webSearchUrl) {
        this.webSearchUrl = webSearchUrl;
    }

    public Boolean isFamilyFriendly() {
        return isFamilyFriendly;
    }

    public void setFamilyFriendly(Boolean familyFriendly) {
        isFamilyFriendly = familyFriendly;
    }

    public Long getTotalEstimatedMatches() {
        return totalEstimatedMatches;
    }

    public void setTotalEstimatedMatches(final Long totalEstimatedMatches) {
        this.totalEstimatedMatches = totalEstimatedMatches;
    }

    public Boolean getFamilyFriendly() {
        return isFamilyFriendly;
    }

    public Integer getCurrentOffset() {
        return currentOffset;
    }

    public void setCurrentOffset(final Integer currentOffset) {
        this.currentOffset = currentOffset;
    }

    public Integer getNextOffset() {
        return nextOffset;
    }

    public void setNextOffset(final Integer nextOffset) {
        this.nextOffset = nextOffset;
    }
}
