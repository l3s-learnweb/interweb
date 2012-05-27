//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.05.13 at 07:49:56 PM CEST 
//


package de.l3s.interwebj.connector.vimeo.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}title"/>
 *         &lt;element ref="{}upload_date"/>
 *         &lt;element ref="{}modified_date"/>
 *         &lt;element ref="{}number_of_likes"/>
 *         &lt;element ref="{}number_of_plays"/>
 *         &lt;element ref="{}number_of_comments"/>
 *         &lt;element ref="{}owner"/>
 *         &lt;element ref="{}thumbnails"/>
 *       &lt;/sequence>
 *       &lt;attribute name="embed_privacy" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="is_hd" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="is_transcoding" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="privacy" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "title",
    "description",
    "uploadDate",
    "modifiedDate",
    "numberOfLikes",
    "numberOfPlays",
    "numberOfComments",
    "thumbnails"
})
@XmlRootElement(name = "video")
public class Video {
	
    @XmlElement(required = true)
    protected String title;
	@XmlElement(name = "description", required = true)
    protected String description;
	@XmlElement(name = "upload_date", required = true)
    protected String uploadDate;
    @XmlElement(name = "modified_date", required = true)
    protected String modifiedDate;
    @XmlElement(name = "number_of_likes", required = true)
    protected int numberOfLikes;
    @XmlElement(name = "number_of_plays", required = true)
    protected int numberOfPlays;
    @XmlElement(name = "number_of_comments", required = true)
    protected int numberOfComments;
    /*
    @XmlElement(required = true)
    protected Owner owner;
    */
    @XmlElement(required = true)
    protected Thumbnails thumbnails;
    @XmlAttribute(name = "embed_privacy", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String embedPrivacy;
    @XmlAttribute(name = "id", required = true)
    protected long id;
    @XmlAttribute(name = "is_hd", required = true)
    protected int isHd;
    @XmlAttribute(name = "is_transcoding", required = true)
    protected int isTranscoding;
    @XmlAttribute(name = "privacy", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String privacy;

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the uploadDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUploadDate() {
        return uploadDate;
    }

    /**
     * Sets the value of the uploadDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUploadDate(String value) {
        this.uploadDate = value;
    }

    /**
     * Gets the value of the modifiedDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModifiedDate() {
        return modifiedDate;
    }

    /**
     * Sets the value of the modifiedDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModifiedDate(String value) {
        this.modifiedDate = value;
    }

    /**
     * Gets the value of the numberOfLikes property.
     * 
     * @return
     *     possible object is
     *     {@link long }
     *     
     */
    public long getNumberOfLikes() {
        return numberOfLikes;
    }

    /**
     * Sets the value of the numberOfLikes property.
     * 
     * @param value
     *     allowed object is
     *     {@link long }
     *     
     */
    public void setNumberOfLikes(int value) {
        this.numberOfLikes = value;
    }

    /**
     * Gets the value of the numberOfPlays property.
     * 
     * @return
     *     possible object is
     *     {@link long }
     *     
     */
    public int getNumberOfPlays() {
        return numberOfPlays;
    }

    /**
     * Sets the value of the numberOfPlays property.
     * 
     * @param value
     *     allowed object is
     *     {@link long }
     *     
     */
    public void setNumberOfPlays(int value) {
        this.numberOfPlays = value;
    }

    /**
     * Gets the value of the numberOfComments property.
     * 
     * @return
     *     possible object is
     *     {@link long }
     *     
     */
    public int getNumberOfComments() {
        return numberOfComments;
    }

    /**
     * Sets the value of the numberOfComments property.
     * 
     * @param value
     *     allowed object is
     *     {@link long }
     *     
     */
    public void setNumberOfComments(int value) {
        this.numberOfComments = value;
    }

    /**
     * Gets the value of the owner property.
     * 
     * @return
     *     possible object is
     *     {@link Owner }
     *     
     * /
    public Owner getOwner() {
        return owner;
    }

    /**
     * Sets the value of the owner property.
     * 
     * @param value
     *     allowed object is
     *     {@link Owner }
     *     
     * /
    public void setOwner(Owner value) {
        this.owner = value;
    }*/

    /**
     * Gets the value of the thumbnails property.
     * 
     * @return
     *     possible object is
     *     {@link Thumbnails }
     *     
     */
    public Thumbnails getThumbnails() {
        return thumbnails;
    }

    /**
     * Sets the value of the thumbnails property.
     * 
     * @param value
     *     allowed object is
     *     {@link Thumbnails }
     *     
     */
    public void setThumbnails(Thumbnails value) {
        this.thumbnails = value;
    }

    /**
     * Gets the value of the embedPrivacy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmbedPrivacy() {
        return embedPrivacy;
    }

    /**
     * Sets the value of the embedPrivacy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmbedPrivacy(String value) {
        this.embedPrivacy = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link long }
     *     
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link long }
     *     
     */
    public void setId(long value) {
        this.id = value;
    }

    /**
     * Gets the value of the isHd property.
     * 
     * @return
     *     possible object is
     *     {@link long }
     *     
     */
    public long getIsHd() {
        return isHd;
    }

    /**
     * Sets the value of the isHd property.
     * 
     * @param value
     *     allowed object is
     *     {@link long }
     *     
     */
    public void setIsHd(int value) {
        this.isHd = value;
    }

    /**
     * Gets the value of the isTranscoding property.
     * 
     * @return
     *     possible object is
     *     {@link long }
     *     
     */
    public long getIsTranscoding() {
        return isTranscoding;
    }

    /**
     * Sets the value of the isTranscoding property.
     * 
     * @param value
     *     allowed object is
     *     {@link long }
     *     
     */
    public void setIsTranscoding(int value) {
        this.isTranscoding = value;
    }

    /**
     * Gets the value of the privacy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrivacy() {
        return privacy;
    }

    /**
     * Sets the value of the privacy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrivacy(String value) {
        this.privacy = value;
    }

    public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
