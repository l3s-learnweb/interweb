package de.l3s.interwebj.tomcat.jaxb.auth;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
