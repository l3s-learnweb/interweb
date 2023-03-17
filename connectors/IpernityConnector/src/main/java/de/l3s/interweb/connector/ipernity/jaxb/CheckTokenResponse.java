package de.l3s.interweb.connector.ipernity.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
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
 *         &lt;element name="auth">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="token" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="permissions">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="doc" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="blog" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="profile" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="network" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="user">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="user_id" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                           &lt;attribute name="username" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="realname" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="lg" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="is_pro" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
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
@XmlType(name = "", propOrder = {"auth"})
@XmlRootElement(name = "api")
public class CheckTokenResponse {

    @XmlElement(required = true)
    protected Auth auth;
    @XmlAttribute(name = "status")
    protected String status;
    @XmlAttribute(name = "at")
    protected Integer at;

    /**
     * Gets the value of the auth property.
     *
     * @return possible object is
     * {@link Auth }
     */
    public Auth getAuth() {
        return auth;
    }

    /**
     * Sets the value of the auth property.
     *
     * @param value allowed object is
     * {@link Auth }
     */
    public void setAuth(Auth value) {
        this.auth = value;
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
