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
public class Thumb {

    @XmlAttribute(name = "label")
    protected String label;
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
    public String getLabel() {
        return label;
    }

    /**
     * Sets the value of the label property.
     *
     * @param value allowed object is
     * {@link Integer }
     */
    public void setLabel(String value) {
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
     * {@link String }
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
     * {@link String }
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
     * {@link Integer }
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
     * {@link String }
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
     * {@link String }
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
     * {@link String }
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
     * {@link Integer }
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
     * {@link Integer }
     */
    public void setW(Integer value) {
        this.w = value;
    }

}
