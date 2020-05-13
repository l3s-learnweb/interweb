package de.l3s.interwebj.tomcat.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import com.sun.istack.NotNull;

@ManagedBean
@RequestScoped
public class LoginBean
{

    @NotNull
    private String username;
    @NotNull
    private String password;

    public String getPassword()
    {
	return password;
    }

    public String getUsername()
    {
	return username;
    }

    public void setPassword(String password)
    {
	this.password = password;
    }

    public void setUsername(String userName)
    {
	username = userName;
    }
}
