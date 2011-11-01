package de.l3s.profileMatcher;

public abstract class StringDistance {
	
	protected String string1;

	/**
	 * 
	 * @param string1 The String to compare to
	 */
	public StringDistance(String string1) 
	{
		this.string1 = string1;
	}
	
	public abstract double getDistance(String string2);
	
	
	
	
}
