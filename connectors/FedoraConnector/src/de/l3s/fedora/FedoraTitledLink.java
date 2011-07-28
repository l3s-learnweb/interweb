package de.l3s.fedora;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class FedoraTitledLink {
	//@XmlAttribute(name = "link")
	FedoraLink link;
	public FedoraLink getLink() {
		return link;
	}
	public String getTitle() {
		return title;
	}
	String title;
}
