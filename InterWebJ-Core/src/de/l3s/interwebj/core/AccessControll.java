package de.l3s.interwebj.core;


import java.util.*;


public class AccessControll
{
	
	private static AccessControll singleton;
	
	private List<ResourceConstraint> constraints;
	private String contextName;
	

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
		constraints.add(buildPublicConstraint("/InterWebJ/api/.*"));
		constraints.add(buildPublicConstraint("/InterWebJ/css/.*"));
		constraints.add(buildPublicConstraint("/InterWebJ/img/.*"));
		constraints.add(buildPublicConstraint("/InterWebJ(/view/)?"));
		constraints.add(buildPublicConstraint("/InterWebJ/view/index\\.xhtml"));
		constraints.add(buildPublicConstraint("/InterWebJ/view/register\\.xhtml"));
		constraints.add(buildPublicConstraint("/InterWebJ/view/login\\.xhtml"));
		constraints.add(buildPublicConstraint("/InterWebJ/view/rfRes/.*"));
		constraints.add(buildPublicConstraint("/InterWebJ/view/javax\\.faces\\.resource/.*"));
		constraints.add(buildPublicConstraint("/InterWebJ/view/login\\.xhtml"));
		constraints.add(buildPublicConstraint("/InterWebJ/view/login\\.xhtml"));
		// User access resources
		constraints.add(new ResourceConstraint("/InterWebJ/.*", "user", 10));
		// Manager access resources
		constraints.add(new ResourceConstraint("/InterWebJ/view/admin/(.*)?",
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
		Environment.logger.debug("grant access: "
		                         + accessControll.isPublicResource("/css/main.css"));
		;
	}
}
