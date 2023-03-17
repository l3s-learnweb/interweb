package de.l3s.interweb.connector.ipernity.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Java class for anonymous complex type.
 *
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="visits" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="faves" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="comments" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="notes" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
public class Count {

    @XmlAttribute(name = "visits")
    protected Long visits;
    @XmlAttribute(name = "faves")
    protected Long faves;
    @XmlAttribute(name = "comments")
    protected Long comments;
    @XmlAttribute(name = "notes")
    protected Long notes;

    /**
     * Gets the value of the visits property.
     *
     * @return possible object is
     * {@link Integer }
     */
    public Long getVisits() {
        return visits;
    }

    /**
     * Sets the value of the visits property.
     *
     * @param value allowed object is
     * {@link Integer }
     */
    public void setVisits(Long value) {
        this.visits = value;
    }

    /**
     * Gets the value of the faves property.
     *
     * @return possible object is
     * {@link Integer }
     */
    public Long getFaves() {
        return faves;
    }

    /**
     * Sets the value of the faves property.
     *
     * @param value allowed object is
     * {@link Integer }
     */
    public void setFaves(Long value) {
        this.faves = value;
    }

    /**
     * Gets the value of the comments property.
     *
     * @return possible object is
     * {@link Integer }
     */
    public Long getComments() {
        return comments;
    }

    /**
     * Sets the value of the comments property.
     *
     * @param value allowed object is
     * {@link Integer }
     */
    public void setComments(Long value) {
        this.comments = value;
    }

    /**
     * Gets the value of the notes property.
     *
     * @return possible object is
     * {@link Integer }
     */
    public Long getNotes() {
        return notes;
    }

    /**
     * Sets the value of the notes property.
     *
     * @param value allowed object is
     * {@link Integer }
     */
    public void setNotes(Long value) {
        this.notes = value;
    }

}
