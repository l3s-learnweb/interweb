package de.l3s.interwebj.jaxb;

import java.util.ArrayList;

import javax.xml.bind.annotation.*;

import de.l3s.interwebj.query.*;

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
