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
 *       &lt;attribute name="doc" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="blog" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="profile" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="network" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
public class Permissions {

    @XmlAttribute(name = "doc")
    protected String doc;
    @XmlAttribute(name = "blog")
    protected String blog;
    @XmlAttribute(name = "profile")
    protected String profile;
    @XmlAttribute(name = "network")
    protected String network;

    /**
     * Gets the value of the doc property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getDoc() {
        return doc;
    }

    /**
     * Sets the value of the doc property.
     *
     * @param value allowed object is
     * {@link String }
     */
    public void setDoc(String value) {
        this.doc = value;
    }

    /**
     * Gets the value of the blog property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getBlog() {
        return blog;
    }

    /**
     * Sets the value of the blog property.
     *
     * @param value allowed object is
     * {@link String }
     */
    public void setBlog(String value) {
        this.blog = value;
    }

    /**
     * Gets the value of the profile property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getProfile() {
        return profile;
    }

    /**
     * Sets the value of the profile property.
     *
     * @param value allowed object is
     * {@link String }
     */
    public void setProfile(String value) {
        this.profile = value;
    }

    /**
     * Gets the value of the network property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getNetwork() {
        return network;
    }

    /**
     * Sets the value of the network property.
     *
     * @param value allowed object is
     * {@link String }
     */
    public void setNetwork(String value) {
        this.network = value;
    }

}
