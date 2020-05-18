package de.l3s.interwebj.connector.ipernity.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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
    protected CheckTokenResponse.Auth auth;
    @XmlAttribute(name = "status")
    protected String status;
    @XmlAttribute(name = "at")
    protected Integer at;

    /**
     * Gets the value of the auth property.
     *
     * @return possible object is
     * {@link CheckTokenResponse.Auth }
     */
    public CheckTokenResponse.Auth getAuth() {
        return auth;
    }

    /**
     * Sets the value of the auth property.
     *
     * @param value allowed object is
     *              {@link CheckTokenResponse.Auth }
     */
    public void setAuth(CheckTokenResponse.Auth value) {
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
     *         &lt;element name="token" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="permissions">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="doc" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="blog" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="profile" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="network" type="{http://www.w3.org/2001/XMLSchema}string" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="user">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="user_id" type="{http://www.w3.org/2001/XMLSchema}int" />
     *                 &lt;attribute name="username" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="realname" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="lg" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="is_pro" type="{http://www.w3.org/2001/XMLSchema}int" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {"token", "permissions", "user"})
    public static class Auth {

        @XmlElement(required = true)
        protected String token;
        @XmlElement(required = true)
        protected CheckTokenResponse.Auth.Permissions permissions;
        @XmlElement(required = true)
        protected CheckTokenResponse.Auth.User user;

        /**
         * Gets the value of the token property.
         *
         * @return possible object is
         * {@link String }
         */
        public String getToken() {
            return token;
        }

        /**
         * Sets the value of the token property.
         *
         * @param value allowed object is
         *              {@link String }
         */
        public void setToken(String value) {
            this.token = value;
        }

        /**
         * Gets the value of the permissions property.
         *
         * @return possible object is
         * {@link CheckTokenResponse.Auth.Permissions }
         */
        public CheckTokenResponse.Auth.Permissions getPermissions() {
            return permissions;
        }

        /**
         * Sets the value of the permissions property.
         *
         * @param value allowed object is
         *              {@link CheckTokenResponse.Auth.Permissions }
         */
        public void setPermissions(CheckTokenResponse.Auth.Permissions value) {
            this.permissions = value;
        }

        /**
         * Gets the value of the user property.
         *
         * @return possible object is
         * {@link CheckTokenResponse.Auth.User }
         */
        public CheckTokenResponse.Auth.User getUser() {
            return user;
        }

        /**
         * Sets the value of the user property.
         *
         * @param value allowed object is
         *              {@link CheckTokenResponse.Auth.User }
         */
        public void setUser(CheckTokenResponse.Auth.User value) {
            this.user = value;
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
        public static class Permissions {

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
             *              {@link String }
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
             *              {@link String }
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
             *              {@link String }
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
             *              {@link String }
             */
            public void setNetwork(String value) {
                this.network = value;
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
        public static class User {

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
             *              {@link Integer }
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
             *              {@link String }
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
             *              {@link String }
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
             *              {@link String }
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
             *              {@link Integer }
             */
            public void setIsPro(Integer value) {
                this.isPro = value;
            }

        }

    }

}
