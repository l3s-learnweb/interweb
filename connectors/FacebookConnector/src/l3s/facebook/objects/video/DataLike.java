package l3s.facebook.objects.video;



//
//This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
//See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
//Any modifications to this file will be lost upon recompilation of the source schema. 
//Generated on: 2012.08.17 at 04:49:27 PM CEST 
//




import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
*         &lt;element ref="{}id"/>
*         &lt;choice>
*           &lt;element ref="{}name"/>
*           &lt;sequence>
*             &lt;element ref="{}from"/>
*             &lt;element ref="{}message"/>
*             &lt;element ref="{}can_remove"/>
*             &lt;element ref="{}created_time"/>
*             &lt;element ref="{}like_count"/>
*             &lt;element ref="{}user_likes"/>
*           &lt;/sequence>
*         &lt;/choice>
*       &lt;/sequence>
*     &lt;/restriction>
*   &lt;/complexContent>
* &lt;/complexType>
* </pre>
* 
* 
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
 "id",
 "name"
})
@XmlRootElement(name = "data")
public class DataLike {

 @XmlElement(required = true)
 @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
 @XmlSchemaType(name = "NMTOKEN")
 protected String id;
 protected String name;
 

 /**
  * Gets the value of the id property.
  * 
  * @return
  *     possible object is
  *     {@link String }
  *     
  */
 public String getId() {
     return id;
 }

 /**
  * Sets the value of the id property.
  * 
  * @param value
  *     allowed object is
  *     {@link String }
  *     
  */
 public void setId(String value) {
     this.id = value;
 }

 /**
  * Gets the value of the name property.
  * 
  * @return
  *     possible object is
  *     {@link String }
  *     
  */
 public String getName() {
     return name;
 }

 /**
  * Sets the value of the name property.
  * 
  * @param value
  *     allowed object is
  *     {@link String }
  *     
  */
 public void setName(String value) {
     this.name = value;
 }

 /**
  * Gets the value of the from property.
  * 
  * @return
  *     possible object is
  *     {@link From }
  *     
  */
 
}
