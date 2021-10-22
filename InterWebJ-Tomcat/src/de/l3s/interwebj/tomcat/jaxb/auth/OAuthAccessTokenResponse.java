package de.l3s.interwebj.tomcat.jaxb.auth;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import de.l3s.interwebj.core.AuthCredentials;

@XmlRootElement(name = "rsp")
@XmlAccessorType(XmlAccessType.FIELD)
public class OAuthAccessTokenResponse {

    @XmlElement(name = "access_token")
    protected OAuthAccessTokenEntity accessToken;

    public OAuthAccessTokenResponse() {
    }

    public OAuthAccessTokenResponse(AuthCredentials accessToken) {
        this(new OAuthAccessTokenEntity(accessToken));
    }

    public OAuthAccessTokenResponse(OAuthAccessTokenEntity accessToken) {
        this.accessToken = accessToken;
    }

    public OAuthAccessTokenEntity getAccessToken() {
        return accessToken;
    }

    public void setRequestToken(OAuthAccessTokenEntity accessToken) {
        this.accessToken = accessToken;
    }
}
