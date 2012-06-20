//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.05.13 at 07:49:56 PM CEST 
//


package de.l3s.interwebj.connector.vimeo.jaxb;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
 *       &lt;attribute name="display_name" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="is_plus" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="is_pro" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="is_staff" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="profileurl" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="realname" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="username" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="videosurl" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "owner")
public class Owner {

    @XmlAttribute(name = "display_name", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String displayName;
    @XmlAttribute(name = "id", required = true)
    protected BigInteger id;
    @XmlAttribute(name = "is_plus", required = true)
    protected BigInteger isPlus;
    @XmlAttribute(name = "is_pro", required = true)
    protected BigInteger isPro;
    @XmlAttribute(name = "is_staff", required = true)
    protected BigInteger isStaff;
    @XmlAttribute(name = "profileurl", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String profileurl;
    @XmlAttribute(name = "realname", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String realname;
    @XmlAttribute(name = "username", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String username;
    @XmlAttribute(name = "videosurl", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String videosurl;

    /**
     * Gets the value of the displayName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the value of the displayName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisplayName(String value) {
        this.displayName = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setId(BigInteger value) {
        this.id = value;
    }

    /**
     * Gets the value of the isPlus property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getIsPlus() {
        return isPlus;
    }

    /**
     * Sets the value of the isPlus property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setIsPlus(BigInteger value) {
        this.isPlus = value;
    }

    /**
     * Gets the value of the isPro property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getIsPro() {
        return isPro;
    }

    /**
     * Sets the value of the isPro property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setIsPro(BigInteger value) {
        this.isPro = value;
    }

    /**
     * Gets the value of the isStaff property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getIsStaff() {
        return isStaff;
    }

    /**
     * Sets the value of the isStaff property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setIsStaff(BigInteger value) {
        this.isStaff = value;
    }

    /**
     * Gets the value of the profileurl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProfileurl() {
        return profileurl;
    }

    /**
     * Sets the value of the profileurl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProfileurl(String value) {
        this.profileurl = value;
    }

    /**
     * Gets the value of the realname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRealname() {
        return realname;
    }

    /**
     * Sets the value of the realname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRealname(String value) {
        this.realname = value;
    }

    /**
     * Gets the value of the username property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the value of the username property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsername(String value) {
        this.username = value;
    }

    /**
     * Gets the value of the videosurl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVideosurl() {
        return videosurl;
    }

    /**
     * Sets the value of the videosurl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVideosurl(String value) {
        this.videosurl = value;
    }

}