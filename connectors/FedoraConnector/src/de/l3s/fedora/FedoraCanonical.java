package de.l3s.fedora;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement(name = "canonical")
@XmlAccessorType(XmlAccessType.FIELD)
public class FedoraCanonical {
	public FedoraLink getLink() {
		return link;
	}
	public DCMetadata getDc() {
		return dc;
	}
	public FedoraTitledLink getCategory() {
		return category;
	}
	public String getTitle() {
		return title;
	}
	public FedoraTitledLink getOwner() {
		return owner;
	}
	public FedoraTagList getTaglist() {
		return taglist;
	}
	public FedoraRating getRating() {
		return rating;
	}
	public Double getScore() {
		return score;
	}
	public FedoraComments getComments() {
		return comments;
	}
	@XmlElement(name = "link")
FedoraLink link;
	@XmlElement(name = "dc")
	DCMetadata dc;
	@XmlElement(name = "category")
	FedoraTitledLink category;
	String title;
	@XmlElement(name = "owner")
	FedoraTitledLink owner;
	@XmlElement(name = "tag-list")
	FedoraTagList taglist;
	@XmlElement(name = "rating")
	FedoraRating rating;
	Double score;
	@XmlElement(name = "comments")
	FedoraComments comments;
	
}
