package de.l3s.interwebj.connector.slideshare;


import java.io.*;
import java.util.*;

import javax.xml.bind.*;
import javax.xml.bind.annotation.*;

import org.apache.commons.lang.text.*;


@XmlRootElement(name = "Slideshows")
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchResponse
{
	
	@XmlElement(name = "Meta")
	protected MetaEntity meta;
	@XmlElement(name = "Slideshow")
	protected List<SearchResultEntity> searchResults;
	

	public MetaEntity getMeta()
	{
		return meta;
	}
	

	public List<SearchResultEntity> getSearchResults()
	{
		return searchResults;
	}
	

	public void setMeta(MetaEntity meta)
	{
		this.meta = meta;
	}
	

	public void setSearchResults(List<SearchResultEntity> searchResults)
	{
		this.searchResults = searchResults;
	}
	

	@Override
	public String toString()
	{
		StrBuilder sb = new StrBuilder();
		try
		{
			JAXBContext context = JAXBContext.newInstance(getClass());
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			m.marshal(this, baos);
			sb.append(baos.toString("UTF8"));
		}
		catch (JAXBException e)
		{
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return sb.toString();
	}
	
}
