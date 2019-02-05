package de.l3s.bingService.models;

public abstract class Entity
{
    private String id;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
        System.out.println("set id" + id);
    }
}
