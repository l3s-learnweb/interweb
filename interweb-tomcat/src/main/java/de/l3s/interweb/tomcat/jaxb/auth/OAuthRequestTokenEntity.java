package de.l3s.interweb.tomcat.jaxb.auth;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import de.l3s.interweb.core.AuthCredentials;

@XmlRootElement(name = "request_token")
@XmlAccessorType(XmlAccessType.FIELD)
public class OAuthRequestTokenEntity {

    @XmlElement(name = "oauth_token")
    protected String oauthToken;
    @XmlElement(name = "oauth_token_secret")
    protected String oauthTokenSecret;

    public OAuthRequestTokenEntity() {
    }

    public OAuthRequestTokenEntity(AuthCredentials authCredentials) {
        this(authCredentials.getKey(), authCredentials.getSecret());
    }

    public OAuthRequestTokenEntity(String oauthToken, String oauthTokenSecret) {
        this.oauthToken = oauthToken;
        this.oauthTokenSecret = oauthTokenSecret;
    }

    public String getOauthToken() {
        return oauthToken;
    }

    public void setOauthToken(String oauthToken) {
        this.oauthToken = oauthToken;
    }

    public String getOauthTokenSecret() {
        return oauthTokenSecret;
    }

    public void setOauthTokenSecret(String oauthTokenSecret) {
        this.oauthTokenSecret = oauthTokenSecret;
    }
}
