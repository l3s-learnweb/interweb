package de.l3s.interwebj.core;


import java.util.*;


public class IWAccessControll
{
	
	private static IWAccessControll singleton;
	
	private List<ResourceConstraint> resourceConstraints;
	

	public IWAccessControll()
	{
		initConstraints();
	}
	

	private ResourceConstraint getResourceConstraint(String resource)
	{
		ResourceConstraint resourceConstraint = null;
		for (ResourceConstraint constraint : resourceConstraints)
		{
			if (resourceConstraint == null && constraint.matches(resource)
			    || resourceConstraint != null && constraint.matches(resource)
			    && constraint.getWeight() > resourceConstraint.getWeight())
			{
				resourceConstraint = constraint;
			}
		}
		return resourceConstraint;
	}
	

	private void initConstraints()
	{
		// TODO: find easy and flexible way to store/read constraints
		resourceConstraints = new LinkedList<ResourceConstraint>();
		ResourceConstraint constraint;
		constraint = new ResourceConstraint("(/)?", 20);
		resourceConstraints.add(constraint);
		constraint = new ResourceConstraint("/index\\.jsp", 20);
		resourceConstraints.add(constraint);
		constraint = new ResourceConstraint("/css/.*", 20);
		resourceConstraints.add(constraint);
		constraint = new ResourceConstraint("/img/.*", 20);
		resourceConstraints.add(constraint);
		constraint = new ResourceConstraint("/register\\.jsp", 20);
		resourceConstraints.add(constraint);
		constraint = new ResourceConstraint("/register", 20);
		resourceConstraints.add(constraint);
		constraint = new ResourceConstraint("/login\\.jsp", 20);
		resourceConstraints.add(constraint);
		constraint = new ResourceConstraint("/error\\.jsp", 20);
		resourceConstraints.add(constraint);
		constraint = new ResourceConstraint("/logout", 20);
		resourceConstraints.add(constraint);
		constraint = new ResourceConstraint("/faces/.*", 20);
		resourceConstraints.add(constraint);
		constraint = new ResourceConstraint("/.*", "user", 10);
		resourceConstraints.add(constraint);
		constraint = new ResourceConstraint("/admin(/.*)?", "admin", 20);
		resourceConstraints.add(constraint);
	}
	

	public boolean isAuthorized(IWPrincipal principal,
	                            String resource,
	                            String action)
	{
		ResourceConstraint resourceConstraint = getResourceConstraint(resource);
		if (resourceConstraint == null)
		{
			return false;
		}
		if (resourceConstraint.getRole() == null)
		{
			return true;
		}
		if (principal == null)
		{
			return false;
		}
		String role = resourceConstraint.getRole();
		return principal.hasRole(role);
	}
	

	public static IWAccessControll getInstance()
	{
		if (singleton == null)
		{
			singleton = new IWAccessControll();
		}
		return singleton;
	}
}
