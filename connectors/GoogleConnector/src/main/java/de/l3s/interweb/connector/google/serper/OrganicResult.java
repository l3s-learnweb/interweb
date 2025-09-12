package de.l3s.interweb.connector.google.serper;

import java.util.List;
import java.util.Map;

public class OrganicResult {
    private String title;
    private String link;
    private String snippet;
    private String date;
    private int position;
    private double rating;
    private int ratingCount;
    private List<Sitelink> sitelinks;
    private Map<String, String> attributes;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public List<Sitelink> getSitelinks() {
        return sitelinks;
    }

    public void setSitelinks(List<Sitelink> sitelinks) {
        this.sitelinks = sitelinks;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }
}
