package de.l3s.bingService.models;

import java.util.List;

public class NewsHolder
{

    private String id;

    private String readLink;

    private List<New> value;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getReadLink()
    {
        return readLink;
    }

    public void setReadLink(String readLink)
    {
        this.readLink = readLink;
    }

    public List<New> getValue()
    {
        return value;
    }

    public void setValue(List<New> value)
    {
        this.value = value;
    }

}
