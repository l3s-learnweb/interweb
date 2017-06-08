package de.l3s.interwebj.core;

import static de.l3s.interwebj.util.Assertions.notEmpty;
import static de.l3s.interwebj.util.Assertions.notNull;

import java.util.HashSet;
import java.util.Set;

import de.l3s.interwebj.AuthCredentials;

public class InterWebPrincipal implements java.security.Principal
{

    public static final String DEFAULT_ROLE = "default";
    public static final String MANAGER_ROLE = "manager";

    private String name;
    private String email;
    private Set<String> roles;
    private AuthCredentials oauthCredentials;

    public InterWebPrincipal(String name)
    {
	this(name, null);
    }

    public InterWebPrincipal(String name, String email)
    {
	this(name, email, new HashSet<String>());
    }

    public InterWebPrincipal(String name, String email, Set<String> roles)
    {
	notEmpty(name, "name");
	notNull(roles, "roles");
	this.name = name;
	this.email = email;
	this.roles = new HashSet<String>();
	for(String role : roles)
	{
	    addRole(role);
	}
    }

    public void addRole(String role)
    {
	notEmpty(role, "role");
	roles.add(role.toLowerCase());
    }

    @Override
    public boolean equals(Object obj)
    {
	if(this == obj)
	{
	    return true;
	}
	if(obj == null)
	{
	    return false;
	}
	if(getClass() != obj.getClass())
	{
	    return false;
	}
	InterWebPrincipal other = (InterWebPrincipal) obj;
	if(name == null)
	{
	    if(other.name != null)
	    {
		return false;
	    }
	}
	else if(!name.equals(other.name))
	{
	    return false;
	}
	return true;
    }

    public String getEmail()
    {
	return email;
    }

    @Override
    public String getName()
    {
	return name;
    }

    public AuthCredentials getOauthCredentials()
    {
	return oauthCredentials;
    }

    public Set<String> getRoles()
    {
	return roles;
    }

    @Override
    public int hashCode()
    {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((name == null) ? 0 : name.hashCode());
	return result;
    }

    public boolean hasRole(String role)
    {
	//		Environment.logger.debug("user roles: " + roles);
	if(role == null)
	{
	    return false;
	}
	return roles.contains(role.toLowerCase());
    }

    public void setOauthCredentials(AuthCredentials oauthCredentials)
    {
	this.oauthCredentials = oauthCredentials;
    }

    @Override
    public String toString()
    {
	StringBuilder builder = new StringBuilder();
	builder.append("InterWebPrincipal [");
	if(name != null)
	{
	    builder.append("name=");
	    builder.append(name);
	    builder.append(", ");
	}
	if(email != null)
	{
	    builder.append("email=");
	    builder.append(email);
	    builder.append(", ");
	}
	if(roles != null)
	{
	    builder.append("roles=");
	    builder.append(roles);
	    builder.append(", ");
	}
	if(oauthCredentials != null)
	{
	    builder.append("oauthCredentials=");
	    builder.append(oauthCredentials);
	}
	builder.append("]");
	return builder.toString();
    }

    public static InterWebPrincipal createDefault(String name)
    {
	return createDefault(name, null);
    }

    public static InterWebPrincipal createDefault(String name, String email)
    {
	InterWebPrincipal principal = new InterWebPrincipal(name, email);
	principal.addRole(DEFAULT_ROLE);
	return principal;
    }
}
