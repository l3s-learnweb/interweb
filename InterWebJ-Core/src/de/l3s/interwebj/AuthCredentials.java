package de.l3s.interwebj;

import static de.l3s.interwebj.util.Assertions.*;

import java.io.Serializable;

public class AuthCredentials implements Serializable
{
    private static final long serialVersionUID = 1411969017572131214L;

    private String key;
    private String secret;

    public AuthCredentials(String key)
    {
	this(key, null);
    }

    public AuthCredentials(String key, String secret)
    {
	notNull(key, "key");
	notEmpty(key, "key");
	this.key = key;
	this.secret = secret;
    }

    public String getKey()
    {
	return key;
    }

    public String getSecret()
    {
	return secret;
    }

    @Override
    public String toString()
    {
	StringBuilder builder = new StringBuilder();
	builder.append("AuthCredentials [");
	if(key != null)
	{
	    builder.append("key=");
	    builder.append(key);
	    builder.append(", ");
	}
	if(secret != null)
	{
	    builder.append("secret=");
	    builder.append(secret);
	}
	builder.append("]");
	return builder.toString();
    }

}
