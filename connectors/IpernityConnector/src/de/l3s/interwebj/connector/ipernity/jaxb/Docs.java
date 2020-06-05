package de.l3s.interwebj.connector.ipernity.jaxb;

import java.util.ArrayList;
import java.util.List;

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
 *         &lt;element name="doc" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="owner">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="user_id" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="thumb">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="label" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                           &lt;attribute name="url" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="secret" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="farm" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                           &lt;attribute name="path" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="ext" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="icon" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="h" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                           &lt;attribute name="w" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="dates">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="posted_at" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                           &lt;attribute name="created" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *                           &lt;attribute name="last_comment_at" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                           &lt;attribute name="last_update_at" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="count">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="visits" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                           &lt;attribute name="faves" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                           &lt;attribute name="comments" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                           &lt;attribute name="notes" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="medias">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="media" maxOccurs="unbounded">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;attribute name="label" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                                     &lt;attribute name="format" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                     &lt;attribute name="width" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                                     &lt;attribute name="height" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                                     &lt;attribute name="length" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                                     &lt;attribute name="url" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                           &lt;attribute name="total" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="doc_id" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                 &lt;attribute name="media" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="license" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                 &lt;attribute name="title" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="rotation" type="{http://www.w3.org/2001/XMLSchema}int" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="total" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="per_page" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="page" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="pages" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"doc"})
public class Docs {

    @XmlElement(required = true)
    protected List<Doc> doc;
    @XmlAttribute(name = "total")
    protected Integer total;
    @XmlAttribute(name = "per_page")
    protected Integer perPage;
    @XmlAttribute(name = "page")
    protected Integer page;
    @XmlAttribute(name = "pages")
    protected Integer pages;

    /**
     * Gets the value of the doc property.
     *
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the doc property.
     *
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getDoc().add(newItem);
     * </pre>
     *
     *
     * Objects of the following type(s) are allowed in the list {@link Doc }
     */
    public List<Doc> getDoc() {
        if (doc == null) {
            doc = new ArrayList<>();
        }
        return this.doc;
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

    /**
     * Gets the value of the perPage property.
     *
     * @return possible object is
     * {@link Integer }
     */
    public Integer getPerPage() {
        return perPage;
    }

    /**
     * Sets the value of the perPage property.
     *
     * @param value allowed object is
     * {@link Integer }
     */
    public void setPerPage(Integer value) {
        this.perPage = value;
    }

    /**
     * Gets the value of the page property.
     *
     * @return possible object is
     * {@link Integer }
     */
    public Integer getPage() {
        return page;
    }

    /**
     * Sets the value of the page property.
     *
     * @param value allowed object is
     * {@link Integer }
     */
    public void setPage(Integer value) {
        this.page = value;
    }

    /**
     * Gets the value of the pages property.
     *
     * @return possible object is
     * {@link Integer }
     */
    public Integer getPages() {
        return pages;
    }

    /**
     * Sets the value of the pages property.
     *
     * @param value allowed object is
     * {@link Integer }
     */
    public void setPages(Integer value) {
        this.pages = value;
    }

}
