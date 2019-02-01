package de.l3s.bingService.models;

import java.util.List;

public class WebPage extends WebPageBase
{

    private List<About> about;

    private String displayUrl;

    private String snippet;

    private List<WebPage> deepLinks;

    private String dateLastCrawled;

    public List<About> getAbout()
    {
        return about;
    }

    public void setAbout(List<About> about)
    {
        this.about = about;
    }

    public String getDisplayUrl()
    {
        return displayUrl;
    }

    public void setDisplayUrl(String displayUrl)
    {
        this.displayUrl = displayUrl;
    }

    public String getSnippet()
    {
        return snippet;
    }

    public void setSnippet(String snippet)
    {
        this.snippet = snippet;
    }

    public List<WebPage> getDeepLinks()
    {
        return deepLinks;
    }

    public void setDeepLinks(List<WebPage> deepLinks)
    {
        this.deepLinks = deepLinks;
    }

    public String getDateLastCrawled()
    {
        return dateLastCrawled;
    }

    public void setDateLastCrawled(String dateLastCrawled)
    {
        this.dateLastCrawled = dateLastCrawled;
    }

    @Override
    public String toString()
    {
        return "WebPage [about=" + about + ", displayUrl=" + displayUrl + ", snippet=" + snippet + ", deepLinks=" + deepLinks + ", dateLastCrawled=" + dateLastCrawled + "]";
    }

}
