package de.l3s.interwebj.connector.interweb;


import javax.xml.bind.annotation.*;


@XmlRootElement(name = "rsp")
@XmlAccessorType(XmlAccessType.FIELD)
public class IWSearchResponse
    extends IWXMLResponse
{
	
	@XmlElement(name = "query")
	protected IWSearchQueryEntity query;
	

	public IWSearchQueryEntity getQuery()
	{
		return query;
	}
	

	public void setQuery(IWSearchQueryEntity query)
	{
		this.query = query;
	}
}
