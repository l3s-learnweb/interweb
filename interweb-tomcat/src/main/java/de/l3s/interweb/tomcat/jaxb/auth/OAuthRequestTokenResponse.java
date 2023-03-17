package de.l3s.interweb.tomcat.jaxb.auth;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import de.l3s.interweb.core.AuthCredentials;

@XmlRootElement(name = "rsp")
@XmlAccessorType(XmlAccessType.FIELD)
public class OAuthRequestTokenResponse {

    @XmlElement(name = "request_token")
    protected OAuthRequestTokenEntity requestToken;

    public OAuthRequestTokenResponse() {
    }

    public OAuthRequestTokenResponse(AuthCredentials requestToken) {
        this(new OAuthRequestTokenEntity(requestToken));
    }

    public OAuthRequestTokenResponse(OAuthRequestTokenEntity requestToken) {
        this.requestToken = requestToken;
    }

    public OAuthRequestTokenEntity getRequestToken() {
        return requestToken;
    }

    public void setRequestToken(OAuthRequestTokenEntity requestToken) {
        this.requestToken = requestToken;
    }
}
