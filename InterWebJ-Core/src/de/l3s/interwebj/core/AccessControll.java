package de.l3s.interwebj.core;


import java.util.*;


public class AccessControll
{
	
	private static AccessControll singleton;
	
	private List<ResourceConstraint> constraints;
	

	public AccessControll()
	{
		initConstraints();
	}
	

	private ResourceConstraint buildPublicConstraint(String pattern)
	{
		return new ResourceConstraint(pattern, Integer.MAX_VALUE);
	}
	

	private ResourceConstraint getResourceConstraint(String resource)
	{
		ResourceConstraint resourceConstraint = null;
		for (ResourceConstraint constraint : constraints)
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
		// Public access resources
		constraints = new LinkedList<ResourceConstraint>();
		constraints.add(buildPublicConstraint("/api/.*"));
		constraints.add(buildPublicConstraint("/css/.*"));
		constraints.add(buildPublicConstraint("/img/.*"));
		constraints.add(buildPublicConstraint("(/view/)?"));
		constraints.add(buildPublicConstraint("/view/index\\.xhtml"));
		constraints.add(buildPublicConstraint("/view/register\\.xhtml"));
		constraints.add(buildPublicConstraint("/view/login\\.xhtml"));
		constraints.add(buildPublicConstraint("/view/rfRes/.*"));
		constraints.add(buildPublicConstraint("/view/javax\\.faces\\.resource/.*"));
		constraints.add(buildPublicConstraint("/view/login\\.xhtml"));
		constraints.add(buildPublicConstraint("/view/login\\.xhtml"));
		// User access resources
		constraints.add(new ResourceConstraint("/.*", "user", 10));
		// Manager access resources
		constraints.add(new ResourceConstraint("/view/admin/(.*)?",
		                                       "manager",
		                                       20));
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
		if (isPublicResource(resource))
		{
			return true;
		}
		if (principal == null)
		{
			return false;
		}
		String role = resourceConstraint.getRole();
		return (role == null) || principal.hasRole(role);
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
	

	public static void main(String[] args)
	{
		AccessControll accessControll = new AccessControll();
		Environment.logger.debug(accessControll.isPublicResource("/css/main.css"));
		;
	}
}
