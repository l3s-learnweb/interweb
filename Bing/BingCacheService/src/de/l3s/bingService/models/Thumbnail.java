package de.l3s.bingService.models;

public class Thumbnail
{

    private String width;

    private String height;

    public String getWidth()
    {
        return width;
    }

    public void setWidth(String width)
    {
        this.width = width;
    }

    public String getHeight()
    {
        return height;
    }

    public void setHeight(String height)
    {
        this.height = height;
    }

    @Override
    public String toString()
    {
        return "Thumbnail [width=" + width + ", height=" + height + "]";
    }

}
