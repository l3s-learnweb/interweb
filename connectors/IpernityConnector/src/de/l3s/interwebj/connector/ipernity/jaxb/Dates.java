package de.l3s.interwebj.connector.ipernity.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Java class for anonymous complex type.
 *
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="posted_at" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="created" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="last_comment_at" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="last_update_at" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
public class Dates {

    @XmlAttribute(name = "posted_at")
    protected Integer postedAt;
    @XmlAttribute(name = "created")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar created;
    @XmlAttribute(name = "last_comment_at")
    protected Integer lastCommentAt;
    @XmlAttribute(name = "last_update_at")
    protected Integer lastUpdateAt;

    /**
     * Gets the value of the postedAt property.
     *
     * @return possible object is
     * {@link Integer }
     */
    public Integer getPostedAt() {
        return postedAt;
    }

    /**
     * Sets the value of the postedAt property.
     *
     * @param value allowed object is
     * {@link Integer }
     */
    public void setPostedAt(Integer value) {
        this.postedAt = value;
    }

    /**
     * Gets the value of the created property.
     *
     * @return possible object is
     * {@link XMLGregorianCalendar }
     */
    public XMLGregorianCalendar getCreated() {
        return created;
    }

    /**
     * Sets the value of the created property.
     *
     * @param value allowed object is
     * {@link XMLGregorianCalendar }
     */
    public void setCreated(XMLGregorianCalendar value) {
        this.created = value;
    }

    /**
     * Gets the value of the lastCommentAt property.
     *
     * @return possible object is
     * {@link Integer }
     */
    public Integer getLastCommentAt() {
        return lastCommentAt;
    }

    /**
     * Sets the value of the lastCommentAt property.
     *
     * @param value allowed object is
     * {@link Integer }
     */
    public void setLastCommentAt(Integer value) {
        this.lastCommentAt = value;
    }

    /**
     * Gets the value of the lastUpdateAt property.
     *
     * @return possible object is
     * {@link Integer }
     */
    public Integer getLastUpdateAt() {
        return lastUpdateAt;
    }

    /**
     * Sets the value of the lastUpdateAt property.
     *
     * @param value allowed object is
     * {@link Integer }
     */
    public void setLastUpdateAt(Integer value) {
        this.lastUpdateAt = value;
    }

}
