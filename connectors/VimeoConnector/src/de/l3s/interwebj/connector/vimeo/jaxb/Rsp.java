//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.05.13 at 07:49:56 PM CEST 
//


package de.l3s.interwebj.connector.vimeo.jaxb;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
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
 *       &lt;sequence>
 *         &lt;element ref="{}videos"/>
 *       &lt;/sequence>
 *       &lt;attribute name="generated_in" use="required" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *       &lt;attribute name="stat" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "videos"
})
@XmlRootElement(name = "rsp")
public class Rsp {

    @XmlElement(required = true)
    protected Videos videos;
    @XmlAttribute(name = "generated_in", required = true)
    protected BigDecimal generatedIn;
    @XmlAttribute(name = "stat", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String stat;

    /**
     * Gets the value of the videos property.
     * 
     * @return
     *     possible object is
     *     {@link Videos }
     *     
     */
    public Videos getVideos() {
        return videos;
    }

    /**
     * Sets the value of the videos property.
     * 
     * @param value
     *     allowed object is
     *     {@link Videos }
     *     
     */
    public void setVideos(Videos value) {
        this.videos = value;
    }

    /**
     * Gets the value of the generatedIn property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getGeneratedIn() {
        return generatedIn;
    }

    /**
     * Sets the value of the generatedIn property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setGeneratedIn(BigDecimal value) {
        this.generatedIn = value;
    }

    /**
     * Gets the value of the stat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStat() {
        return stat;
    }

    /**
     * Sets the value of the stat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStat(String value) {
        this.stat = value;
    }

}
