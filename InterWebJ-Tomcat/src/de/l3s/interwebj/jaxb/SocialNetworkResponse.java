package de.l3s.interwebj.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.l3s.interwebj.query.ContactFromSocialNetwork;
import de.l3s.interwebj.query.UserSocialNetworkResult;

@XmlRootElement(name = "socialnetwork")
@XmlAccessorType(XmlAccessType.FIELD)
public class SocialNetworkResponse extends XMLResponse
{

    @XmlElement(name = "socialnetwork")
    protected SocialNetworkEntity socialnetwork;

    public SocialNetworkResponse()
    {
    }

    public SocialNetworkResponse(UserSocialNetworkResult result)
    {
	SocialNetworkEntity entity = new SocialNetworkEntity(result.getUserid());

	for(ContactFromSocialNetwork resultItem : result.getResultItems().values())
	{
	    SocialNetworkMember member = new SocialNetworkMember(resultItem);
	    entity.getResults().add(member);
	}
	socialnetwork = entity;
    }

    public SocialNetworkEntity getSocialNetwork()
    {
	return socialnetwork;
    }

    public void setSocialNetwork(SocialNetworkEntity socialnetwork)
    {
	this.socialnetwork = socialnetwork;
    }
}
