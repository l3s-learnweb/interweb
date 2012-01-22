package de.l3s.interwebj.core;


import java.util.*;


public class AccessControll
{
	
	private static final String SYSTEM_ROLE = "system";
	
	private List<ResourceConstraint> constraints;
	

	public AccessControll()
	{
		initConstraints();
	}
	

	public boolean isAuthorized(InterWebPrincipal principal,
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
	

	private ResourceConstraint buildPublicConstraint(String pattern)
	{
		return new ResourceConstraint(pattern, Integer.MAX_VALUE);
	}
	

	private ResourceConstraint buildRestrictedConstraint(String pattern)
	{
		return new ResourceConstraint(pattern, SYSTEM_ROLE, Integer.MAX_VALUE);
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
		constraints.add(buildPublicConstraint("/callback.*"));
		constraints.add(buildPublicConstraint("(/view/)?"));
		constraints.add(buildPublicConstraint("/view/index\\.xhtml"));
		constraints.add(buildPublicConstraint("/view/register\\.xhtml"));
		constraints.add(buildPublicConstraint("/view/login\\.xhtml"));
		constraints.add(buildPublicConstraint("/view/search\\.xhtml"));
		constraints.add(buildPublicConstraint("/view/profile_matcher\\.xhtml"));
		constraints.add(buildPublicConstraint("/view/api\\.xhtml"));
		constraints.add(buildPublicConstraint("/view/rfRes/.*"));
		constraints.add(buildPublicConstraint("/view/javax\\.faces\\.resource/.*"));
		constraints.add(buildPublicConstraint("/view/login\\.xhtml"));
		// User access resources
		constraints.add(new ResourceConstraint("/.*",
		                                       InterWebPrincipal.DEFAULT_ROLE,
		                                       10));
		// Manager access resources
		constraints.add(new ResourceConstraint("/view/admin/(.*)?",
		                                       InterWebPrincipal.MANAGER_ROLE,
		                                       20));
		// Restricted access resources
		constraints.add(buildRestrictedConstraint("no such resource"));
	}
}
