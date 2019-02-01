package de.l3s.bingService.models;

public class About
{

    private String name;

    private String readLink;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getReadLink()
    {
        return readLink;
    }

    public void setReadLink(String readLink)
    {
        this.readLink = readLink;
    }

    @Override
    public String toString()
    {
        return "About [name=" + name + ", readLink=" + readLink + "]";
    }

}
