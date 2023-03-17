package de.l3s.interweb.connector.ipernity.jaxb;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
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
 *       &lt;sequence>
 *         &lt;element name="media" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="label" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                 &lt;attribute name="format" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="width" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                 &lt;attribute name="height" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                 &lt;attribute name="length" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                 &lt;attribute name="url" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="total" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"media"})
public class Medias {

    @XmlElement(required = true)
    protected List<Media> media;
    @XmlAttribute(name = "total")
    protected Integer total;

    /**
     * Gets the value of the media property.
     *
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the media property.
     *
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getMedia().add(newItem);
     * </pre>
     *
     *
     * Objects of the following type(s) are allowed in the list
     * {@link Media }
     */
    public List<Media> getMedia() {
        if (media == null) {
            media = new ArrayList<>();
        }
        return this.media;
    }

    /**
     * Gets the value of the total property.
     *
     * @return possible object is
     * {@link Integer }
     */
    public Integer getTotal() {
        return total;
    }

    /**
     * Sets the value of the total property.
     *
     * @param value allowed object is
     * {@link Integer }
     */
    public void setTotal(Integer value) {
        this.total = value;
    }

}
