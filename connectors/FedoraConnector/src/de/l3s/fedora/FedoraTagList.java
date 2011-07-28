package de.l3s.fedora;

import java.util.Vector;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
@XmlAccessorType(XmlAccessType.FIELD)
public class FedoraTagList {
	public Vector<FedoraTitledLink> getTag() {
		return tag;
	}

	@XmlElement(name = "tag")
	Vector<FedoraTitledLink> tag;
}
