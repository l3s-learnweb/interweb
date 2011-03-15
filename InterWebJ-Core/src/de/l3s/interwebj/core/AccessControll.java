package de.l3s.interwebj.core;


import java.util.*;


public class AccessControll
{
	
	private static AccessControll singleton;
	
	private List<ResourceConstraint> resourceConstraints;
	

	public AccessControll()
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
		constraint = new ResourceConstraint("/css/.*", 20);
		resourceConstraints.add(constraint);
		constraint = new ResourceConstraint("/img/.*", 20);
		resourceConstraints.add(constraint);
		constraint = new ResourceConstraint("(/view/)?", 20);
		resourceConstraints.add(constraint);
		constraint = new ResourceConstraint("/view/index\\.xhtml", 20);
		resourceConstraints.add(constraint);
		constraint = new ResourceConstraint("/view/register\\.xhtml", 20);
		resourceConstraints.add(constraint);
		constraint = new ResourceConstraint("/view/login\\.xhtml", 20);
		resourceConstraints.add(constraint);
		constraint = new ResourceConstraint("/view/javax\\.faces\\.resource/jsf\\.js",
		                                    20);
		resourceConstraints.add(constraint);
		constraint = new ResourceConstraint("/.*", "user", 10);
		resourceConstraints.add(constraint);
		constraint = new ResourceConstraint("/view/admin/(.*)?", "manager", 20);
		resourceConstraints.add(constraint);
		// allow all
		//		constraint = new ResourceConstraint("/.*", 100);
		//		resourceConstraints.add(constraint);
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
		if (principal == null)
		{
			return isPublicResource(resource);
		}
		String role = resourceConstraint.getRole();
		return principal.hasRole(role);
	}
	

	public boolean isPublicResource(String resource)
	{
		ResourceConstraint resourceConstraint = getResourceConstraint(resource);
		return (resourceConstraint.getRole() == null);
	}
	

	public static AccessControll getInstance()
	{
		if (singleton == null)
		{
			singleton = new AccessControll();
		}
		return singleton;
	}
}
