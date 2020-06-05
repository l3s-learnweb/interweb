package de.l3s.interwebj.tomcat.jaxb.auth;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.l3s.interwebj.core.AuthCredentials;

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
