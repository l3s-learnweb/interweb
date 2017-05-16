package de.l3s.interwebj;

public class IllegalResponseException extends Exception
{

    /**
     * 
     */
    private static final long serialVersionUID = 4056030077679896098L;

    public IllegalResponseException()
    {
        super();
    }

    public IllegalResponseException(String message)
    {
        super(message);
    }

    public IllegalResponseException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public IllegalResponseException(Throwable cause)
    {
        super(cause);
    }
}
