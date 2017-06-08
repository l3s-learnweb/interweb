package de.l3s.interwebj.bean;

import javax.faces.bean.*;

@ManagedBean
@RequestScoped
public class ChangePasswordBean
{

    private String oldPassword;
    private String newPassword;
    private String newPassword2;

    public String changePassword()
    {
	return null;
    }

    public String getNewPassword()
    {
	return newPassword;
    }

    public String getNewPassword2()
    {
	return newPassword2;
    }

    public String getOldPassword()
    {
	return oldPassword;
    }

    public void setNewPassword(String newPassword)
    {
	this.newPassword = newPassword;
    }

    public void setNewPassword2(String newPassword2)
    {
	this.newPassword2 = newPassword2;
    }

    public void setOldPassword(String oldPassword)
    {
	this.oldPassword = oldPassword;
    }

}
