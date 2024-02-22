package de.l3s.interweb.connector.bing.entity;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class WebPage {

    private String id;
    private String name;
    private String url;
    private Boolean isFamilyFriendly;
    private String displayUrl;
    private String snippet;
    private String urlPingSuffix;
    private List<WebPage> deepLinks;
    private String dateLastCrawled;
    private String language;
    private Boolean isNavigational;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public Boolean getFamilyFriendly() {
        return isFamilyFriendly;
    }

    public void setFamilyFriendly(final Boolean familyFriendly) {
        isFamilyFriendly = familyFriendly;
    }

    public String getDisplayUrl() {
        return displayUrl;
    }

    public void setDisplayUrl(final String displayUrl) {
        this.displayUrl = displayUrl;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(final String snippet) {
        this.snippet = snippet;
    }

    public String getUrlPingSuffix() {
        return urlPingSuffix;
    }

    public void setUrlPingSuffix(final String urlPingSuffix) {
        this.urlPingSuffix = urlPingSuffix;
    }

    public List<WebPage> getDeepLinks() {
        return deepLinks;
    }

    public void setDeepLinks(final List<WebPage> deepLinks) {
        this.deepLinks = deepLinks;
    }

    public String getDateLastCrawled() {
        return dateLastCrawled;
    }

    public void setDateLastCrawled(final String dateLastCrawled) {
        this.dateLastCrawled = dateLastCrawled;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(final String language) {
        this.language = language;
    }

    public Boolean getNavigational() {
        return isNavigational;
    }

    public void setNavigational(final Boolean navigational) {
        isNavigational = navigational;
    }
}
