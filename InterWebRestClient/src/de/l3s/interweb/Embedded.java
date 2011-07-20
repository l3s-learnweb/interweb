package de.l3s.interweb;

import org.dom4j.Element;

public class Embedded {

	private String embedded;

	
	public Embedded(String embedded) 
	{
		this.embedded = embedded;
	}


	public Embedded(Element root) 
	{
		System.out.println(root.asXML());
		embedded = root.elementText("embedded");
	}
	
	


	public String getEmbedded() {
		return embedded;
	}
}
