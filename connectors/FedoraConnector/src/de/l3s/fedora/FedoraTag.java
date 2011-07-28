package de.l3s.fedora;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
@XmlAccessorType(XmlAccessType.FIELD)
public class FedoraTag {
	public FedoraTitledLink getTag() {
		return tag;
	}

	@XmlElement(name = "tag")
	FedoraTitledLink tag;
}
