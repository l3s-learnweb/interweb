package de.l3s.interwebj.util;


import java.math.*;
import java.security.*;


public class RandomGenerator
{
	
	private static final int DEFAULT_BIT_COUNT = 144;
	
	private static RandomGenerator singleton;
	
	private SecureRandom random;
	

	public RandomGenerator()
	{
		random = new SecureRandom();
	}
	

	public String nextAlphaNumericId()
	{
		return nextAlphaNumericId(DEFAULT_BIT_COUNT);
	}
	

	public String nextAlphaNumericId(int bitCount)
	{
		return new BigInteger(bitCount, random).toString(36);
	}
	

	public String nextDecId()
	{
		return nextDecId(DEFAULT_BIT_COUNT);
	}
	

	public String nextDecId(int bitCount)
	{
		return new BigInteger(bitCount, random).toString(10);
	}
	

	public String nextHexId()
	{
		return nextHexId(DEFAULT_BIT_COUNT);
	}
	

	public String nextHexId(int bitCount)
	{
		return new BigInteger(bitCount, random).toString(16);
	}
	

	public static RandomGenerator getInstance()
	{
		if (singleton == null)
		{
			singleton = new RandomGenerator();
		}
		return singleton;
	}
	

	public static void main(String[] args)
	{
		RandomGenerator randomGenerator = RandomGenerator.getInstance();
		System.out.println(randomGenerator.nextHexId());
	}
}
