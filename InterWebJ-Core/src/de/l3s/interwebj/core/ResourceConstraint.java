package de.l3s.interwebj.core;


import java.util.regex.*;


public class ResourceConstraint
{
	
	private Pattern pattern;
	private int weight;
	private String role;
	

	public ResourceConstraint(String pattern, int weight)
	{
		this(pattern, null, weight);
	}
	

	public ResourceConstraint(String patternString, String role, int weight)
	{
		pattern = Pattern.compile(patternString);
		this.role = role;
		this.weight = weight;
	}
	

	public String getRole()
	{
		return role;
	}
	

	public int getWeight()
	{
		return weight;
	}
	

	public boolean matches(String path)
	{
		return pattern.matcher(path).matches();
	}
}
