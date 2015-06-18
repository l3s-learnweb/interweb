package de.l3s.interwebj.jaxb;


import javax.xml.bind.annotation.*;


@XmlRootElement(name = "rsp")
@XmlAccessorType(XmlAccessType.FIELD)
public class EmbeddedResponse
    extends XMLResponse
{
	
	@XmlElement(name = "embedded")
	protected String embedded;
	

	public EmbeddedResponse()
	{
	}
	

	public EmbeddedResponse(String embedded)
	{
		this.embedded = embedded;
	}
	

	public String getEmbedded()
	{
		return embedded;
	}
	

	public void setEmbedded(String embedded)
	{
		this.embedded = embedded;
	}
	
}
