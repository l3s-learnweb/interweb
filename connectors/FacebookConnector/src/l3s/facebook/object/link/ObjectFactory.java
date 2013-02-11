//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.12.26 at 01:45:50 PM CET 
//


package l3s.facebook.object.link;

import java.math.BigInteger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the l3s.facebook.object.link package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Message_QNAME = new QName("", "message");
    private final static QName _Id_QNAME = new QName("", "id");
    private final static QName _UserLikes_QNAME = new QName("", "user_likes");
    private final static QName _Icon_QNAME = new QName("", "icon");
    private final static QName _LikeCount_QNAME = new QName("", "like_count");
    private final static QName _Description_QNAME = new QName("", "description");
    private final static QName _Name_QNAME = new QName("", "name");
    private final static QName _Link_QNAME = new QName("", "link");
    private final static QName _Next_QNAME = new QName("", "next");
    private final static QName _Value_QNAME = new QName("", "value");
    private final static QName _CanRemove_QNAME = new QName("", "can_remove");
    private final static QName _CreatedTime_QNAME = new QName("", "created_time");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: l3s.facebook.object.link
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Networks }
     * 
     */
    public Networks createNetworks() {
        return new Networks();
    }

    /**
     * Create an instance of {@link Allow }
     * 
     */
    public Allow createAllow() {
        return new Allow();
    }

    /**
     * Create an instance of {@link Privacy }
     * 
     */
    public Privacy createPrivacy() {
        return new Privacy();
    }

    /**
     * Create an instance of {@link Deny }
     * 
     */
    public Deny createDeny() {
        return new Deny();
    }

    /**
     * Create an instance of {@link Friends }
     * 
     */
    public Friends createFriends() {
        return new Friends();
    }

    /**
     * Create an instance of {@link Data }
     * 
     */
    public Data createData() {
        return new Data();
    }

    /**
     * Create an instance of {@link From }
     * 
     */
    public From createFrom() {
        return new From();
    }

    /**
     * Create an instance of {@link Sharedlink }
     * 
     */
    public Sharedlink createSharedlink() {
        return new Sharedlink();
    }

    /**
     * Create an instance of {@link Comments }
     * 
     */
    public Comments createComments() {
        return new Comments();
    }

    /**
     * Create an instance of {@link Paging }
     * 
     */
    public Paging createPaging() {
        return new Paging();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "message")
    public JAXBElement<String> createMessage(String value) {
        return new JAXBElement<String>(_Message_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createId(String value) {
        return new JAXBElement<String>(_Id_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "user_likes")
    public JAXBElement<Boolean> createUserLikes(Boolean value) {
        return new JAXBElement<Boolean>(_UserLikes_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "icon")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createIcon(String value) {
        return new JAXBElement<String>(_Icon_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "like_count")
    public JAXBElement<BigInteger> createLikeCount(BigInteger value) {
        return new JAXBElement<BigInteger>(_LikeCount_QNAME, BigInteger.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "description")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createDescription(String value) {
        return new JAXBElement<String>(_Description_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "name")
    public JAXBElement<String> createName(String value) {
        return new JAXBElement<String>(_Name_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "link")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createLink(String value) {
        return new JAXBElement<String>(_Link_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "next")
    public JAXBElement<String> createNext(String value) {
        return new JAXBElement<String>(_Next_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "value")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createValue(String value) {
        return new JAXBElement<String>(_Value_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "can_remove")
    public JAXBElement<Boolean> createCanRemove(Boolean value) {
        return new JAXBElement<Boolean>(_CanRemove_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "created_time")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createCreatedTime(String value) {
        return new JAXBElement<String>(_CreatedTime_QNAME, String.class, null, value);
    }

}