package de.l3s.bingService.models;

import de.l3s.bingService.utils.UriUtils;

public class RelatedSearch
{

    private String text;

    private String displayText;

    private String webSearchUrl;

    private String webSearchUrlPingSuffix;

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getDisplayText()
    {
        return displayText;
    }

    public void setDisplayText(String displayText)
    {
        this.displayText = displayText;
    }

    public String getWebSearchUrl()
    {
        return webSearchUrl;
    }

    public void setWebSearchUrl(String webSearchUrl)
    {
        this.webSearchUrl = UriUtils.splitQuery(webSearchUrl);
    }

    public String getWebSearchUrlPingSuffix()
    {
        return webSearchUrlPingSuffix;
    }

    public void setWebSearchUrlPingSuffix(String webSearchUrlPingSuffix)
    {
        this.webSearchUrlPingSuffix = webSearchUrlPingSuffix;
    }

}
