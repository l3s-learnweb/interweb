package de.l3s.bingService.models;

import java.util.List;

public class RelatedSearchesHolder
{

    private String id;

    private List<RelatedSearch> value;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public List<RelatedSearch> getValue()
    {
        return value;
    }

    public void setValue(List<RelatedSearch> value)
    {
        this.value = value;
    }

}
