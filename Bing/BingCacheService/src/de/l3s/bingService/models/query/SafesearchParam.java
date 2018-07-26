package de.l3s.bingService.models.query;

public enum SafesearchParam
{

    OFF("off"),
    MODERATE("moderate"),
    STRICT("strict");

    private String value;

    SafesearchParam(String value)
    {
    	this.value = value;
    }

    public String getValue()
    {
    	return value;
    }

    public void setValue(String value)
    {
    	this.value = value;
    }

}
