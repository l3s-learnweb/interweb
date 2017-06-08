package de.l3s.interwebj.bean;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import de.l3s.interwebj.InterWebException;
import de.l3s.interwebj.core.Environment;
import de.l3s.interwebj.core.InterWebPrincipal;
import de.l3s.interwebj.db.Database;
import de.l3s.interwebj.webutil.FacesUtils;

@ManagedBean
@RequestScoped
public class UserDataBean
{

    private String username;
    private String password;
    private String password2;
    private String email;

    public String getEmail()
    {
	return email;
    }

    public String getPassword()
    {
	return password;
    }

    public String getPassword2()
    {
	return password2;
    }

    public String getUsername()
    {
	return username;
    }

    public String register() throws InterWebException
    {
	Environment environment = Environment.getInstance();
	Database database = environment.getDatabase();
	if(validate(database))
	{
	    InterWebPrincipal principal = InterWebPrincipal.createDefault(username, email);
	    database.savePrincipal(principal, password);
	    FacesUtils.getSessionBean().setPrincipal(principal);
	    return "success";
	}
	return "failed";
    }

    public void setEmail(String email)
    {
	this.email = email;
    }

    public void setPassword(String password)
    {
	this.password = password;
    }

    public void setPassword2(String password2)
    {
	this.password2 = password2;
    }

    public void setUsername(String userName)
    {
	username = userName;
    }

    private boolean validate(Database database)
    {
	if(database.hasPrincipal(username))
	{
	    FacesUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Sorry, such user name already exists", "register_form:username");
	    return false;
	}
	if(!password.equals(password2))
	{
	    FacesUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Passwords are not equal", "register_form:password2");
	    return false;
	}

	return true;
    }
}
