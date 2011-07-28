package de.l3s.fedora;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "dc")
@XmlAccessorType(XmlAccessType.FIELD)
public class DCMetadata {
public String getTitle() {
		return title;
	}
	public String getDescription() {
		return description;
	}
	public String getIdentifier() {
		return identifier;
	}
	public String getCreator() {
		return creator;
	}
	public String getFormat() {
		return format;
	}
	public String getSubject() {
		return subject;
	}
	public String getType() {
		return type;
	}
	public String getSource() {
		return source;
	}
	public String getCoverage() {
		return coverage;
	}
	public String getPublisher() {
		return publisher;
	}
	public String getContributor() {
		return contributor;
	}
	public String getRights() {
		return rights;
	}
	public String getRelation() {
		return relation;
	}
	public String getDate() {
		return date;
	}
	public String getLanguage() {
		return language;
	}
String title;
String description;
String identifier;
String creator;
String format;
String subject;
String type;
String source;
String coverage;
String publisher;
String contributor;
String rights;
String relation;
String date;
String language;
}
