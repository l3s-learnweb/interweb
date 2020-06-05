package de.l3s.interwebj.connector.ipernity.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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
 *         &lt;element name="docs">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="doc" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="owner">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;attribute name="user_id" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="thumb">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;attribute name="label" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                                     &lt;attribute name="url" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                     &lt;attribute name="secret" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                     &lt;attribute name="farm" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                                     &lt;attribute name="path" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                     &lt;attribute name="ext" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                     &lt;attribute name="icon" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                     &lt;attribute name="h" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                                     &lt;attribute name="w" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="dates">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;attribute name="posted_at" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                                     &lt;attribute name="created" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *                                     &lt;attribute name="last_comment_at" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                                     &lt;attribute name="last_update_at" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="count">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;attribute name="visits" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                                     &lt;attribute name="faves" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                                     &lt;attribute name="comments" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                                     &lt;attribute name="notes" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="medias">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="media" maxOccurs="unbounded">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;attribute name="label" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                                               &lt;attribute name="format" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                               &lt;attribute name="width" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                                               &lt;attribute name="height" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                                               &lt;attribute name="length" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                                               &lt;attribute name="url" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                     &lt;/sequence>
 *                                     &lt;attribute name="total" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                           &lt;attribute name="doc_id" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                           &lt;attribute name="media" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="license" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                           &lt;attribute name="title" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="rotation" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="total" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                 &lt;attribute name="per_page" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                 &lt;attribute name="page" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                 &lt;attribute name="pages" type="{http://www.w3.org/2001/XMLSchema}int" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="status" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="at" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"docs"})
@XmlRootElement(name = "api")
public class IpernityResponse {

    @XmlElement(required = true)
    protected Docs docs;
    @XmlAttribute(name = "status")
    protected String status;
    @XmlAttribute(name = "at")
    protected Integer at;

    /**
     * Gets the value of the docs property.
     *
     * @return possible object is
     * {@link Docs }
     */
    public Docs getDocs() {
        return docs;
    }

    /**
     * Sets the value of the docs property.
     *
     * @param value allowed object is
     * {@link Docs }
     */
    public void setDocs(Docs value) {
        this.docs = value;
    }

    /**
     * Gets the value of the status property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     *
     * @param value allowed object is
     * {@link String }
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Gets the value of the at property.
     *
     * @return possible object is
     * {@link Integer }
     */
    public Integer getAt() {
        return at;
    }

    /**
     * Sets the value of the at property.
     *
     * @param value allowed object is
     * {@link Integer }
     */
    public void setAt(Integer value) {
        this.at = value;
    }

}
