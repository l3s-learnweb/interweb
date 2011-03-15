package de.l3s.interwebj.core;


import java.util.*;


public class IWPrincipal
    implements java.security.Principal
{
	
	private String name;
	private String email;
	private Set<String> roles;
	

	public IWPrincipal(String name, String email)
	{
		this(name, email, new HashSet<String>());
	}
	

	public IWPrincipal(String name, String email, Set<String> roles)
	{
		if (name == null)
		{
			throw new NullPointerException("Argument [name] can not be null");
		}
		if (roles == null)
		{
			throw new NullPointerException("Argument [roles] can not be null");
		}
		this.name = name;
		this.email = email;
		this.roles = roles;
	}
	

	public void addRole(String role)
	{
		if (role == null)
		{
			throw new NullPointerException("Argument [role] can not be null");
		}
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
	

	public Set<String> getRoles()
	{
		return roles;
	}
	

	public boolean hasRole(String role)
	{
		Environment.logger.debug("user roles: " + roles);
		return roles.contains(role.toLowerCase());
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
		}
		builder.append("]");
		return builder.toString();
	}
	
}
