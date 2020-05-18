package de.l3s.interwebj.connector.ipernity.jaxb;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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
public class SearchResponse {

    @XmlElement(required = true)
    protected SearchResponse.Docs docs;
    @XmlAttribute(name = "status")
    protected String status;
    @XmlAttribute(name = "at")
    protected Integer at;

    /**
     * Gets the value of the docs property.
     *
     * @return possible object is
     * {@link SearchResponse.Docs }
     */
    public SearchResponse.Docs getDocs() {
        return docs;
    }

    /**
     * Sets the value of the docs property.
     *
     * @param value allowed object is
     *              {@link SearchResponse.Docs }
     */
    public void setDocs(SearchResponse.Docs value) {
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
     *              {@link String }
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
     *              {@link Integer }
     */
    public void setAt(Integer value) {
        this.at = value;
    }

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
    public static class Docs {

        @XmlElement(required = true)
        protected List<SearchResponse.Docs.Doc> doc;
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
         * Objects of the following type(s) are allowed in the list {@link SearchResponse.Docs.Doc }
         */
        public List<SearchResponse.Docs.Doc> getDoc() {
            if (doc == null) {
                doc = new ArrayList<SearchResponse.Docs.Doc>();
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
         *              {@link Integer }
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
         *              {@link Integer }
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
         *              {@link Integer }
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
         *              {@link Integer }
         */
        public void setPages(Integer value) {
            this.pages = value;
        }

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
        @XmlType(name = "", propOrder = {"owner", "thumb", "dates", "count", "medias"})
        public static class Doc {

            @XmlElement(required = true)
            protected SearchResponse.Docs.Doc.Owner owner;
            @XmlElement(required = true)
            protected SearchResponse.Docs.Doc.Thumb thumb;
            @XmlElement()
            protected SearchResponse.Docs.Doc.Dates dates;
            @XmlElement()
            protected SearchResponse.Docs.Doc.Count count;
            @XmlElement()
            protected SearchResponse.Docs.Doc.Medias medias;
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
             * {@link SearchResponse.Docs.Doc.Owner }
             */
            public SearchResponse.Docs.Doc.Owner getOwner() {
                return owner;
            }

            /**
             * Sets the value of the owner property.
             *
             * @param value allowed object is
             *              {@link SearchResponse.Docs.Doc.Owner }
             */
            public void setOwner(SearchResponse.Docs.Doc.Owner value) {
                this.owner = value;
            }

            /**
             * Gets the value of the thumb property.
             *
             * @return possible object is
             * {@link SearchResponse.Docs.Doc.Thumb }
             */
            public SearchResponse.Docs.Doc.Thumb getThumb() {
                return thumb;
            }

            /**
             * Sets the value of the thumb property.
             *
             * @param value allowed object is
             *              {@link SearchResponse.Docs.Doc.Thumb }
             */
            public void setThumb(SearchResponse.Docs.Doc.Thumb value) {
                this.thumb = value;
            }

            /**
             * Gets the value of the dates property.
             *
             * @return possible object is
             * {@link SearchResponse.Docs.Doc.Dates }
             */
            public SearchResponse.Docs.Doc.Dates getDates() {
                return dates;
            }

            /**
             * Sets the value of the dates property.
             *
             * @param value allowed object is
             *              {@link SearchResponse.Docs.Doc.Dates }
             */
            public void setDates(SearchResponse.Docs.Doc.Dates value) {
                this.dates = value;
            }

            /**
             * Gets the value of the count property.
             *
             * @return possible object is
             * {@link SearchResponse.Docs.Doc.Count }
             */
            public SearchResponse.Docs.Doc.Count getCount() {
                return count;
            }

            /**
             * Sets the value of the count property.
             *
             * @param value allowed object is
             *              {@link SearchResponse.Docs.Doc.Count }
             */
            public void setCount(SearchResponse.Docs.Doc.Count value) {
                this.count = value;
            }

            /**
             * Gets the value of the medias property.
             *
             * @return possible object is
             * {@link SearchResponse.Docs.Doc.Medias }
             */
            public SearchResponse.Docs.Doc.Medias getMedias() {
                return medias;
            }

            /**
             * Sets the value of the medias property.
             *
             * @param value allowed object is
             *              {@link SearchResponse.Docs.Doc.Medias }
             */
            public void setMedias(SearchResponse.Docs.Doc.Medias value) {
                this.medias = value;
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
             *              {@link Integer }
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
             *              {@link String }
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
             *              {@link Integer }
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
             *              {@link String }
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
             *              {@link Integer }
             */
            public void setRotation(Integer value) {
                this.rotation = value;
            }

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
            public static class Count {

                @XmlAttribute(name = "visits")
                protected Integer visits;
                @XmlAttribute(name = "faves")
                protected Integer faves;
                @XmlAttribute(name = "comments")
                protected Integer comments;
                @XmlAttribute(name = "notes")
                protected Integer notes;

                /**
                 * Gets the value of the visits property.
                 *
                 * @return possible object is
                 * {@link Integer }
                 */
                public Integer getVisits() {
                    return visits;
                }

                /**
                 * Sets the value of the visits property.
                 *
                 * @param value allowed object is
                 *              {@link Integer }
                 */
                public void setVisits(Integer value) {
                    this.visits = value;
                }

                /**
                 * Gets the value of the faves property.
                 *
                 * @return possible object is
                 * {@link Integer }
                 */
                public Integer getFaves() {
                    return faves;
                }

                /**
                 * Sets the value of the faves property.
                 *
                 * @param value allowed object is
                 *              {@link Integer }
                 */
                public void setFaves(Integer value) {
                    this.faves = value;
                }

                /**
                 * Gets the value of the comments property.
                 *
                 * @return possible object is
                 * {@link Integer }
                 */
                public Integer getComments() {
                    return comments;
                }

                /**
                 * Sets the value of the comments property.
                 *
                 * @param value allowed object is
                 *              {@link Integer }
                 */
                public void setComments(Integer value) {
                    this.comments = value;
                }

                /**
                 * Gets the value of the notes property.
                 *
                 * @return possible object is
                 * {@link Integer }
                 */
                public Integer getNotes() {
                    return notes;
                }

                /**
                 * Sets the value of the notes property.
                 *
                 * @param value allowed object is
                 *              {@link Integer }
                 */
                public void setNotes(Integer value) {
                    this.notes = value;
                }

            }

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
            public static class Dates {

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
                 *              {@link Integer }
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
                 *              {@link XMLGregorianCalendar }
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
                 *              {@link Integer }
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
                 *              {@link Integer }
                 */
                public void setLastUpdateAt(Integer value) {
                    this.lastUpdateAt = value;
                }

            }

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
            public static class Medias {

                @XmlElement(required = true)
                protected List<SearchResponse.Docs.Doc.Medias.Media> media;
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
                 * {@link SearchResponse.Docs.Doc.Medias.Media }
                 */
                public List<SearchResponse.Docs.Doc.Medias.Media> getMedia() {
                    if (media == null) {
                        media = new ArrayList<SearchResponse.Docs.Doc.Medias.Media>();
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
                 *              {@link Integer }
                 */
                public void setTotal(Integer value) {
                    this.total = value;
                }

                /**
                 * Java class for anonymous complex type.
                 *
                 * The following schema fragment specifies the expected content contained within this class.
                 *
                 * <pre>
                 * &lt;complexType>
                 *   &lt;complexContent>
                 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *       &lt;attribute name="label" type="{http://www.w3.org/2001/XMLSchema}int" />
                 *       &lt;attribute name="format" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *       &lt;attribute name="width" type="{http://www.w3.org/2001/XMLSchema}int" />
                 *       &lt;attribute name="height" type="{http://www.w3.org/2001/XMLSchema}int" />
                 *       &lt;attribute name="length" type="{http://www.w3.org/2001/XMLSchema}int" />
                 *       &lt;attribute name="url" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *     &lt;/restriction>
                 *   &lt;/complexContent>
                 * &lt;/complexType>
                 * </pre>
                 */
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "")
                public static class Media {

                    @XmlAttribute(name = "label")
                    protected Integer label;
                    @XmlAttribute(name = "format")
                    protected String format;
                    @XmlAttribute(name = "width")
                    protected Integer width;
                    @XmlAttribute(name = "height")
                    protected Integer height;
                    @XmlAttribute(name = "length")
                    protected Integer length;
                    @XmlAttribute(name = "url")
                    protected String url;

                    /**
                     * Gets the value of the label property.
                     *
                     * @return possible object is
                     * {@link Integer }
                     */
                    public Integer getLabel() {
                        return label;
                    }

                    /**
                     * Sets the value of the label property.
                     *
                     * @param value allowed object is
                     *              {@link Integer }
                     */
                    public void setLabel(Integer value) {
                        this.label = value;
                    }

                    /**
                     * Gets the value of the format property.
                     *
                     * @return possible object is
                     * {@link String }
                     */
                    public String getFormat() {
                        return format;
                    }

                    /**
                     * Sets the value of the format property.
                     *
                     * @param value allowed object is
                     *              {@link String }
                     */
                    public void setFormat(String value) {
                        this.format = value;
                    }

                    /**
                     * Gets the value of the width property.
                     *
                     * @return possible object is
                     * {@link Integer }
                     */
                    public Integer getWidth() {
                        return width;
                    }

                    /**
                     * Sets the value of the width property.
                     *
                     * @param value allowed object is
                     *              {@link Integer }
                     */
                    public void setWidth(Integer value) {
                        this.width = value;
                    }

                    /**
                     * Gets the value of the height property.
                     *
                     * @return possible object is
                     * {@link Integer }
                     */
                    public Integer getHeight() {
                        return height;
                    }

                    /**
                     * Sets the value of the height property.
                     *
                     * @param value allowed object is
                     *              {@link Integer }
                     */
                    public void setHeight(Integer value) {
                        this.height = value;
                    }

                    /**
                     * Gets the value of the length property.
                     *
                     * @return possible object is
                     * {@link Integer }
                     */
                    public Integer getLength() {
                        return length;
                    }

                    /**
                     * Sets the value of the length property.
                     *
                     * @param value allowed object is
                     *              {@link Integer }
                     */
                    public void setLength(Integer value) {
                        this.length = value;
                    }

                    /**
                     * Gets the value of the url property.
                     *
                     * @return possible object is
                     * {@link String }
                     */
                    public String getUrl() {
                        return url;
                    }

                    /**
                     * Sets the value of the url property.
                     *
                     * @param value allowed object is
                     *              {@link String }
                     */
                    public void setUrl(String value) {
                        this.url = value;
                    }

                }

            }

            /**
             * Java class for anonymous complex type.
             *
             * The following schema fragment specifies the expected content contained within this class.
             *
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *       &lt;attribute name="user_id" type="{http://www.w3.org/2001/XMLSchema}int" />
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "")
            public static class Owner {

                @XmlAttribute(name = "user_id")
                protected Integer userId;

                /**
                 * Gets the value of the userId property.
                 *
                 * @return possible object is
                 * {@link Integer }
                 */
                public Integer getUserId() {
                    return userId;
                }

                /**
                 * Sets the value of the userId property.
                 *
                 * @param value allowed object is
                 *              {@link Integer }
                 */
                public void setUserId(Integer value) {
                    this.userId = value;
                }

            }

            /**
             * Java class for anonymous complex type.
             *
             * The following schema fragment specifies the expected content contained within this class.
             *
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *       &lt;attribute name="label" type="{http://www.w3.org/2001/XMLSchema}int" />
             *       &lt;attribute name="url" type="{http://www.w3.org/2001/XMLSchema}string" />
             *       &lt;attribute name="secret" type="{http://www.w3.org/2001/XMLSchema}string" />
             *       &lt;attribute name="farm" type="{http://www.w3.org/2001/XMLSchema}int" />
             *       &lt;attribute name="path" type="{http://www.w3.org/2001/XMLSchema}string" />
             *       &lt;attribute name="ext" type="{http://www.w3.org/2001/XMLSchema}string" />
             *       &lt;attribute name="icon" type="{http://www.w3.org/2001/XMLSchema}string" />
             *       &lt;attribute name="h" type="{http://www.w3.org/2001/XMLSchema}int" />
             *       &lt;attribute name="w" type="{http://www.w3.org/2001/XMLSchema}int" />
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "")
            public static class Thumb {

                @XmlAttribute(name = "label")
                protected Integer label;
                @XmlAttribute(name = "url")
                protected String url;
                @XmlAttribute(name = "secret")
                protected String secret;
                @XmlAttribute(name = "farm")
                protected Integer farm;
                @XmlAttribute(name = "path")
                protected String path;
                @XmlAttribute(name = "ext")
                protected String ext;
                @XmlAttribute(name = "icon")
                protected String icon;
                @XmlAttribute(name = "h")
                protected Integer h;
                @XmlAttribute(name = "w")
                protected Integer w;

                /**
                 * Gets the value of the label property.
                 *
                 * @return possible object is
                 * {@link Integer }
                 */
                public Integer getLabel() {
                    return label;
                }

                /**
                 * Sets the value of the label property.
                 *
                 * @param value allowed object is
                 *              {@link Integer }
                 */
                public void setLabel(Integer value) {
                    this.label = value;
                }

                /**
                 * Gets the value of the url property.
                 *
                 * @return possible object is
                 * {@link String }
                 */
                public String getUrl() {
                    return url;
                }

                /**
                 * Sets the value of the url property.
                 *
                 * @param value allowed object is
                 *              {@link String }
                 */
                public void setUrl(String value) {
                    this.url = value;
                }

                /**
                 * Gets the value of the secret property.
                 *
                 * @return possible object is
                 * {@link String }
                 */
                public String getSecret() {
                    return secret;
                }

                /**
                 * Sets the value of the secret property.
                 *
                 * @param value allowed object is
                 *              {@link String }
                 */
                public void setSecret(String value) {
                    this.secret = value;
                }

                /**
                 * Gets the value of the farm property.
                 *
                 * @return possible object is
                 * {@link Integer }
                 */
                public Integer getFarm() {
                    return farm;
                }

                /**
                 * Sets the value of the farm property.
                 *
                 * @param value allowed object is
                 *              {@link Integer }
                 */
                public void setFarm(Integer value) {
                    this.farm = value;
                }

                /**
                 * Gets the value of the path property.
                 *
                 * @return possible object is
                 * {@link String }
                 */
                public String getPath() {
                    return path;
                }

                /**
                 * Sets the value of the path property.
                 *
                 * @param value allowed object is
                 *              {@link String }
                 */
                public void setPath(String value) {
                    this.path = value;
                }

                /**
                 * Gets the value of the ext property.
                 *
                 * @return possible object is
                 * {@link String }
                 */
                public String getExt() {
                    return ext;
                }

                /**
                 * Sets the value of the ext property.
                 *
                 * @param value allowed object is
                 *              {@link String }
                 */
                public void setExt(String value) {
                    this.ext = value;
                }

                /**
                 * Gets the value of the icon property.
                 *
                 * @return possible object is
                 * {@link String }
                 */
                public String getIcon() {
                    return icon;
                }

                /**
                 * Sets the value of the icon property.
                 *
                 * @param value allowed object is
                 *              {@link String }
                 */
                public void setIcon(String value) {
                    this.icon = value;
                }

                /**
                 * Gets the value of the h property.
                 *
                 * @return possible object is
                 * {@link Integer }
                 */
                public Integer getH() {
                    return h;
                }

                /**
                 * Sets the value of the h property.
                 *
                 * @param value allowed object is
                 *              {@link Integer }
                 */
                public void setH(Integer value) {
                    this.h = value;
                }

                /**
                 * Gets the value of the w property.
                 *
                 * @return possible object is
                 * {@link Integer }
                 */
                public Integer getW() {
                    return w;
                }

                /**
                 * Sets the value of the w property.
                 *
                 * @param value allowed object is
                 *              {@link Integer }
                 */
                public void setW(Integer value) {
                    this.w = value;
                }

            }

        }

    }

}
