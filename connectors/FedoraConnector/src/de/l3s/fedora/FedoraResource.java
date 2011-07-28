package de.l3s.fedora;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
@XmlAccessorType(XmlAccessType.FIELD)
public class FedoraResource {
	public FedoraCanonical getCanonical() {
		return canonical;
	}

	@XmlElement(name = "canonical")
FedoraCanonical canonical;
}
