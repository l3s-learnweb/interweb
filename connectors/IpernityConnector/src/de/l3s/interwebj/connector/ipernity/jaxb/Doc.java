package de.l3s.interwebj.connector.ipernity.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Java class for anonymous complex type.
 *
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="owner">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="user_id" type="{http://www.w3.org/2001/XMLSchema}int" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="thumb">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="label" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                 &lt;attribute name="url" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="secret" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="farm" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                 &lt;attribute name="path" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="ext" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="icon" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="h" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                 &lt;attribute name="w" type="{http://www.w3.org/2001/XMLSchema}int" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="dates">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="posted_at" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                 &lt;attribute name="created" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *                 &lt;attribute name="last_comment_at" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                 &lt;attribute name="last_update_at" type="{http://www.w3.org/2001/XMLSchema}int" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="count">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="visits" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                 &lt;attribute name="faves" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                 &lt;attribute name="comments" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                 &lt;attribute name="notes" type="{http://www.w3.org/2001/XMLSchema}int" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="medias">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="media" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="label" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                           &lt;attribute name="format" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="width" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                           &lt;attribute name="height" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                           &lt;attribute name="length" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                           &lt;attribute name="url" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="total" type="{http://www.w3.org/2001/XMLSchema}int" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="doc_id" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="media" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="license" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="title" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="rotation" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"owner", "thumb", "dates", "count", "medias", "original"})
public class Doc {

    @XmlElement(required = true)
    protected Owner owner;
    @XmlElement(required = true)
    protected Thumb thumb;
    @XmlElement()
    protected Dates dates;
    @XmlElement()
    protected Count count;
    @XmlElement()
    protected Medias medias;
    @XmlElement()
    protected Original original;
    @XmlAttribute(name = "doc_id")
    protected Integer docId;
    @XmlAttribute(name = "media")
    protected String media;
    @XmlAttribute(name = "license")
    protected Integer license;
    @XmlAttribute(name = "title")
    protected String title;
    @XmlAttribute(name = "rotation")
    protected Integer rotation;

    /**
     * Gets the value of the owner property.
     *
     * @return possible object is
     * {@link Owner }
     */
    public Owner getOwner() {
        return owner;
    }

    /**
     * Sets the value of the owner property.
     *
     * @param value allowed object is
     * {@link Owner }
     */
    public void setOwner(Owner value) {
        this.owner = value;
    }

    /**
     * Gets the value of the thumb property.
     *
     * @return possible object is
     * {@link Thumb }
     */
    public Thumb getThumb() {
        return thumb;
    }

    /**
     * Sets the value of the thumb property.
     *
     * @param value allowed object is
     * {@link Thumb }
     */
    public void setThumb(Thumb value) {
        this.thumb = value;
    }

    /**
     * Gets the value of the dates property.
     *
     * @return possible object is
     * {@link Dates }
     */
    public Dates getDates() {
        return dates;
    }

    /**
     * Sets the value of the dates property.
     *
     * @param value allowed object is
     * {@link Dates }
     */
    public void setDates(Dates value) {
        this.dates = value;
    }

    /**
     * Gets the value of the count property.
     *
     * @return possible object is
     * {@link Count }
     */
    public Count getCount() {
        return count;
    }

    /**
     * Sets the value of the count property.
     *
     * @param value allowed object is
     * {@link Count }
     */
    public void setCount(Count value) {
        this.count = value;
    }

    /**
     * Gets the value of the medias property.
     *
     * @return possible object is
     * {@link Medias }
     */
    public Medias getMedias() {
        return medias;
    }

    /**
     * Sets the value of the medias property.
     *
     * @param value allowed object is
     * {@link Medias }
     */
    public void setMedias(Medias value) {
        this.medias = value;
    }

    public Original getOriginal() {
        return original;
    }

    public void setOriginal(final Original original) {
        this.original = original;
    }

    /**
     * Gets the value of the docId property.
     *
     * @return possible object is
     * {@link Integer }
     */
    public Integer getDocId() {
        return docId;
    }

    /**
     * Sets the value of the docId property.
     *
     * @param value allowed object is
     * {@link Integer }
     */
    public void setDocId(Integer value) {
        this.docId = value;
    }

    /**
     * Gets the value of the media property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getMedia() {
        return media;
    }

    /**
     * Sets the value of the media property.
     *
     * @param value allowed object is
     * {@link String }
     */
    public void setMedia(String value) {
        this.media = value;
    }

    /**
     * Gets the value of the license property.
     *
     * @return possible object is
     * {@link Integer }
     */
    public Integer getLicense() {
        return license;
    }

    /**
     * Sets the value of the license property.
     *
     * @param value allowed object is
     * {@link Integer }
     */
    public void setLicense(Integer value) {
        this.license = value;
    }

    /**
     * Gets the value of the title property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     *
     * @param value allowed object is
     * {@link String }
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the rotation property.
     *
     * @return possible object is
     * {@link Integer }
     */
    public Integer getRotation() {
        return rotation;
    }

    /**
     * Sets the value of the rotation property.
     *
     * @param value allowed object is
     * {@link Integer }
     */
    public void setRotation(Integer value) {
        this.rotation = value;
    }

}
