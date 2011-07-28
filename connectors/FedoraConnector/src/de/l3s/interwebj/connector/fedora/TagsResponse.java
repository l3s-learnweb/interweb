package de.l3s.interwebj.connector.fedora;


import java.io.*;
import java.util.*;

import javax.xml.bind.*;
import javax.xml.bind.annotation.*;

import org.apache.commons.lang.text.*;


@XmlRootElement(name = "Tags")
@XmlAccessorType(XmlAccessType.FIELD)
public class TagsResponse
{
	
	@XmlElement(name = "Tag")
	protected List<TagEntity> tags;
	

	public List<TagEntity> getTags()
	{
		return tags;
	}
	

	public void setTags(List<TagEntity> tags)
	{
		this.tags = tags;
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
