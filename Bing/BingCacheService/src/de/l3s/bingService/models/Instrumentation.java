package de.l3s.bingService.models;

public class Instrumentation
{

    private String pingUrlBase;

    private String pageLoadPingUrl;

    public String getPingUrlBase()
    {
        return pingUrlBase;
    }

    public void setPingUrlBase(String pingUrlBase)
    {
        this.pingUrlBase = pingUrlBase;
    }

    public String getPageLoadPingUrl()
    {
        return pageLoadPingUrl;
    }

    public void setPageLoadPingUrl(String pageLoadPingUrl)
    {
        this.pageLoadPingUrl = pageLoadPingUrl;
    }

}
