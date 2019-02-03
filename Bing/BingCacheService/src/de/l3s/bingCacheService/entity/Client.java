package de.l3s.bingCacheService.entity;

public class Client
{
    private final int id;
    private final String cacheApiKey; // the key for this service
    private final String bingApiKey; // the bing key. If null user can only retrieve cache results    

    public Client(int id, String cacheApiKey, String bingApiKey)
    {
        super();
        this.id = id;
        this.cacheApiKey = cacheApiKey;
        this.bingApiKey = bingApiKey;
    }

    public int getId()
    {
        return id;
    }

    public String getCacheApiKey()
    {
        return cacheApiKey;
    }

    public String getBingApiKey()
    {
        return bingApiKey;
    }
}
