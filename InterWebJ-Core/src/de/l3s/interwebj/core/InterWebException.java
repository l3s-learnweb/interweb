package de.l3s.interwebj.core;


public class InterWebException
    extends Exception
{
	
	private static final long serialVersionUID = 4178090074531977861L;
	

	public InterWebException()
	{
		super();
	}
	

	public InterWebException(String message)
	{
		super(message);
	}
	

	public InterWebException(String message, Throwable cause)
	{
		super(message, cause);
	}
	

	public InterWebException(Throwable cause)
	{
		super(cause);
	}
}
