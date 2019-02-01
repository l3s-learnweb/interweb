package de.l3s.bingService.models.query;

public enum FreshnessParam
{

    DAY("day"),
    WEEK("week"),
    MONTH("month");

    private String value;

    FreshnessParam(String value)
    {
        this.setValue(value);
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value.toLowerCase();
    }

}
