package de.l3s.bingService.models;

//import org.springframework.data.annotation.Id;

public abstract class Entity
{

    // @Id
    private String id;

    public String getId()
    {
	return id;
    }

    public void setId(String id)
    {
	this.id = id;
    }
}
