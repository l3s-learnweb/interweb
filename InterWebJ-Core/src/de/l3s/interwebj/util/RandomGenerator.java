package de.l3s.interwebj.util;

import java.math.*;
import java.security.*;

import org.apache.commons.lang.*;

import de.l3s.interwebj.*;

public class RandomGenerator
{

    private static final int DEFAULT_BIT_COUNT = 144;

    private static char[] alphanumericChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_".toCharArray();

    private static RandomGenerator singleton;

    private SecureRandom random;

    public RandomGenerator()
    {
	random = new SecureRandom();
    }

    public String nextAlphanumericId()
    {
	return nextAlphanumericId(16);
    }

    public String nextAlphanumericId(int charCount)
    {
	return RandomStringUtils.random(charCount, alphanumericChars);
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

    public AuthCredentials nextOAuthCredentials()
    {
	String key = nextAlphanumericId(16);
	String secret = nextAlphanumericId(24);
	return new AuthCredentials(key, secret);
    }

    public String nextOAuthToken()
    {
	return nextAlphanumericId(16);
    }

    public static RandomGenerator getInstance()
    {
	if(singleton == null)
	{
	    singleton = new RandomGenerator();
	}
	return singleton;
    }

    public static void main(String[] args)
    {
	RandomGenerator randomGenerator = RandomGenerator.getInstance();
	System.out.println(randomGenerator.nextOAuthCredentials());
    }
}
