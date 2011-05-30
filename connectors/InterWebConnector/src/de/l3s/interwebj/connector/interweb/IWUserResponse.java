package de.l3s.interwebj.connector.interweb;


import javax.xml.bind.annotation.*;


@XmlRootElement(name = "rsp")
@XmlAccessorType(XmlAccessType.FIELD)
public class IWUserResponse
    extends IWXMLResponse
{
	
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class UserEntity
	{
		
		@XmlValue
		protected String userName;
		

		public String getUserName()
		{
			return userName;
		}
		

		public void setUserName(String userName)
		{
			this.userName = userName;
		}
	}
	

	@XmlElement(name = "user")
	protected UserEntity user;
	

	public UserEntity getUser()
	{
		return user;
	}
	

	public void setUser(UserEntity user)
	{
		this.user = user;
	}
	
}
