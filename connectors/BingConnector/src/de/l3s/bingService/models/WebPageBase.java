package de.l3s.bingService.models;

import de.l3s.bingService.utils.UriUtils;

public class WebPageBase
{

    private String name;

    private String url;

    private String urlPingSuffix;

    public String getName()
    {
	return name;
    }

    public void setName(String name)
    {
	this.name = name;
    }

    public String getUrl()
    {
	return url;
    }

    public void setUrl(String url)
    {
	this.url = UriUtils.splitQuery(url);
    }

    public String getUrlPingSuffix()
    {
	return urlPingSuffix;
    }

    public void setUrlPingSuffix(String urlPingSuffix)
    {
	this.urlPingSuffix = urlPingSuffix;
    }

}
