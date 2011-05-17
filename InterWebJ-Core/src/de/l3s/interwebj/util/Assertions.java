package de.l3s.interwebj.util;


public class Assertions
{
	
	public static long isNotZero(long value, String name)
	{
		if (value == 0)
		{
			throw new IllegalArgumentException("Value must not be zero");
		}
		return value;
	}
	

	public static long notNegative(long value)
	{
		if (value < 0)
		{
			throw new IllegalArgumentException("Value must not be negative");
		}
		return value;
	}
	

	public static void notNull(Object object, String name)
	{
		if (object == null)
		{
			throw new NullPointerException("Argument [" + name
			                               + "] must not be null");
		}
	}
}
