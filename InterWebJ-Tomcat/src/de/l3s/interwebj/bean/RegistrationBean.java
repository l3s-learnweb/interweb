package de.l3s.interwebj.bean;

import java.util.*;
import java.util.regex.*;

import de.l3s.interwebj.db.*;

public class RegistrationBean
{

    private static Pattern emailPattern = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

    private String username;
    private String password;
    private String password2;
    private String email;
    private Map<String, String> errors;

    public RegistrationBean()
    {
	username = "";
	password = "";
	password2 = "";
	email = "";
	errors = new HashMap<String, String>();
    }

    public String getEmail()
    {
	return email;
    }

    public String getErrorMessage(String key)
    {
	return errors.containsKey(key) ? errors.get(key) : "";
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

    public boolean validate(Database iwDatabase)
    {
	boolean valid = validateUserName(iwDatabase);
	valid = validatePassword() && valid;
	valid = validatePassword2() && valid;
	valid = validateEmail() && valid;
	return valid;
    }

    private boolean validateEmail()
    {
	if(email == null || email.equals(""))
	{
	    errors.put("email", "Please type in your e-mail");
	}
	else if(!emailPattern.matcher(email).matches())
	{
	    errors.put("email", "Sorry. Entered e-mail is incorrect");
	}
	else
	{
	    return true;
	}
	return false;
    }

    private boolean validatePassword()
    {
	if(password == null || password.equals(""))
	{
	    errors.put("password", "Please type in your password");
	}
	else if(password.length() < 4 || password.length() > 32)
	{
	    errors.put("password", "The length of password must be in bounds [4, 32]");
	}
	else
	{
	    return true;
	}
	return false;
    }

    private boolean validatePassword2()
    {
	if(password2 == null || password2.equals(""))
	{
	    errors.put("password2", "Please type in your password confirmation");
	}
	else if(password2.length() < 4 || password2.length() > 32)
	{
	    errors.put("password2", "The length of password must be in bounds [4, 32]");
	}
	else if(!password2.equals(password))
	{
	    errors.put("password2", "Passwords are not equal");
	}
	else
	{
	    return true;
	}
	return false;
    }

    private boolean validateUserName(Database iwDatabase)
    {
	if(username == null || username.equals(""))
	{
	    errors.put("userName", "Please type in the user name");
	}
	else if(username.length() < 4 || username.length() > 20)
	{
	    errors.put("userName", "User name length must be in bounds [4, 20]");
	}
	else if(iwDatabase.hasPrincipal(username))
	{
	    errors.put("userName", "User already exists");
	}
	else
	{
	    return true;
	}
	return false;
    }

}
