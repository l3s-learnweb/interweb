package de.l3s.interwebj.connector.ipernity.jaxb;

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
 *       &lt;attribute name="user_id" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="username" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="realname" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="lg" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="is_pro" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
public class User {

    @XmlAttribute(name = "user_id")
    protected Integer userId;
    @XmlAttribute(name = "username")
    protected String username;
    @XmlAttribute(name = "realname")
    protected String realname;
    @XmlAttribute(name = "lg")
    protected String lg;
    @XmlAttribute(name = "is_pro")
    protected Integer isPro;

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
     * {@link Integer }
     */
    public void setUserId(Integer value) {
        this.userId = value;
    }

    /**
     * Gets the value of the username property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the value of the username property.
     *
     * @param value allowed object is
     * {@link String }
     */
    public void setUsername(String value) {
        this.username = value;
    }

    /**
     * Gets the value of the realname property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getRealname() {
        return realname;
    }

    /**
     * Sets the value of the realname property.
     *
     * @param value allowed object is
     * {@link String }
     */
    public void setRealname(String value) {
        this.realname = value;
    }

    /**
     * Gets the value of the lg property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getLg() {
        return lg;
    }

    /**
     * Sets the value of the lg property.
     *
     * @param value allowed object is
     * {@link String }
     */
    public void setLg(String value) {
        this.lg = value;
    }

    /**
     * Gets the value of the isPro property.
     *
     * @return possible object is
     * {@link Integer }
     */
    public Integer getIsPro() {
        return isPro;
    }

    /**
     * Sets the value of the isPro property.
     *
     * @param value allowed object is
     * {@link Integer }
     */
    public void setIsPro(Integer value) {
        this.isPro = value;
    }

}
