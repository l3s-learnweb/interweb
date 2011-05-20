package de.l3s.interwebj.jaxb.services;


import javax.xml.bind.annotation.*;


@XmlAccessorType(XmlAccessType.FIELD)
public class TokenAuthorizationEntity
    extends AuthorizationEntity
{
	
	public TokenAuthorizationEntity()
	{
		type = "token";
	}
}
