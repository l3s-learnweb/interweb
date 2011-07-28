package de.l3s.fedora;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "link")
@XmlAccessorType(XmlAccessType.FIELD)
public class FedoraLink {
	@XmlAttribute(name = "href")
	protected String href;
	@XmlAttribute(name = "source")
	protected String source;
	@XmlAttribute(name = "type")
	protected String type;
	@XmlAttribute(name = "asd")
	protected String asd;
	public String getHref() {
		return href;
	}
	public String getSource() {
		return source;
	}
	public String getType() {
		return type;
	}
	public String getAsd() {
		return asd;
	}
}
