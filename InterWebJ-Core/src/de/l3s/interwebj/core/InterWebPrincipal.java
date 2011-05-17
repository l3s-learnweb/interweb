package de.l3s.interwebj.core;


import static de.l3s.interwebj.util.Assertions.*;

import java.util.*;

import de.l3s.interwebj.*;


public class InterWebPrincipal
    implements java.security.Principal
{
	
	private String name;
	private String email;
	private Set<String> roles;
	private AuthCredentials oauthCredentials;
	

	public InterWebPrincipal(String name, String email)
	{
		this(name, email, new HashSet<String>());
	}
	

	public InterWebPrincipal(String name, String email, Set<String> roles)
	{
		notNull(name, "name");
		notNull(roles, "roles");
		this.name = name;
		this.email = email;
		this.roles = roles;
	}
	

	public void addRole(String role)
	{
		notNull(role, "role");
		roles.add(role.toLowerCase());
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
	

	public boolean hasRole(String role)
	{
		//		Environment.logger.debug("user roles: " + roles);
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
		builder.append("IWPrincipal [");
		if (name != null)
		{
			builder.append("name=");
			builder.append(name);
			builder.append(", ");
		}
		if (roles != null)
		{
			builder.append("roles=");
			builder.append(roles);
		}
		builder.append("]");
		return builder.toString();
	}
	
}
