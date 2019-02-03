package de.l3s.bingService.models.query;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import de.l3s.bingService.models.Entity;

public class BingQuery extends Entity
{

    private String query;
    private String market;
    private int count = 50;
    private int offset = 0;
    private String language;
    private SafesearchParam safeSearch;
    private FreshnessParam freshness;

    private ResponseFilterParam responseFilter;

    /*
    public BingQuery()
    {
        super();
    }
    */

    public BingQuery(String query, String mkt, String lang, String offset, String freshness, String safeSearch)
    {
        setQuery(query);
        setMarket(mkt);
        setLanguage(lang);
        setOffset(offset);
        setFreshness(freshness);
        setSafesearch(safeSearch);
    }

    public BingQuery(String query, String mkt, String lang, int offset, FreshnessParam freshness, SafesearchParam safeSearch)
    {
        setQuery(query);
        setMarket(mkt);
        setLanguage(lang);
        setOffset(offset);
        setFreshness(freshness);
        setSafesearch(safeSearch);
    }

    public String getQuery()
    {
        return query;
    }

    public void setQuery(String query)
    {
        Validate.notEmpty(query, "query");

        //Max length of request allowed in bing
        if(query.length() > 15)
        {
            query = query.substring(0, 1499);
        }
        /*
        else
            this.query = query;
        */
        this.query = query.replaceAll("_", "+");
    }

    public boolean hasMarket()
    {
        return market != null;
    }

    public int getCount()
    {
        return count;
    }

    public void setCount(int count)
    {
        this.count = count;
    }

    public int getOffset()
    {
        return offset;
    }

    /**
     * convenience method to set the offset by string
     *
     * @param offset
     */
    public void setOffset(String offsetStr)
    {
        int offset = 0;

        if(!StringUtils.isEmpty(offsetStr))
        {
            try
            {
                offset = Integer.parseInt(offsetStr);
                if(offset % 50 != 0)
                {
                    throw new IllegalArgumentException("Invalid offset parameter, has to be a multiple of 50: " + offsetStr);
                }
            }
            catch(NumberFormatException e)
            {
                throw new IllegalArgumentException("Invalid offset parameter: " + offsetStr);
            }
        }
    }

    public void setOffset(int offset)
    {
        if(offset > 0)
        {
            this.offset = offset;
        }
    }

    public String getMarket()
    {
        return market;
    }

    public void setMarket(String mkt)
    {
        if(StringUtils.isBlank(mkt))
        {
            market = "en-US";
        }
        else if(mkt.length() == 5)
        {
            String[] parts = mkt.split("-");
            if(parts.length != 2)
                throw new IllegalArgumentException("Invalid Market: " + mkt);

            setLanguage(parts[0]);

        }
        else
            throw new IllegalArgumentException("Invalid Market: " + mkt);

        market = mkt;
    }

    public boolean hasSafesearch()
    {
        return safeSearch != null;
    }

    public SafesearchParam getSafesearch()
    {
        return safeSearch;
    }

    public void setSafesearch(String safeSearchStr)
    {
        SafesearchParam safeSearch = null;

        if(safeSearchStr != null)
        {
            safeSearch = SafesearchParam.valueOf(safeSearchStr);
        }

        setSafesearch(safeSearch);
    }

    public void setSafesearch(SafesearchParam safeSearch)
    {
        this.safeSearch = safeSearch != null ? safeSearch : SafesearchParam.MODERATE;
    }

    public boolean hasFreshness()
    {
        return freshness != null;
    }

    public FreshnessParam getFreshness()
    {
        return freshness;
    }

    public void setFreshness(String freshnessStr)
    {
        FreshnessParam freshness = null;
        if(freshnessStr != null)
        {
            freshness = FreshnessParam.valueOf(freshnessStr);
        }
        setFreshness(freshness);
    }

    public void setFreshness(FreshnessParam freshness)
    {
        this.freshness = freshness;
    }

    public ResponseFilterParam getResponseFilter()
    {
        return responseFilter;
    }

    public boolean hasResponseFilter()
    {
        return responseFilter != null;
    }

    public void setResponseFilter(ResponseFilterParam responseFilter)
    {
        this.responseFilter = responseFilter;
    }

    public boolean hasLanguage()
    {
        return language != null;
    }

    public String getLanguage()
    {
        return language;
    }

    public void setLanguage(String language)
    {
        this.language = language;
    }

}
