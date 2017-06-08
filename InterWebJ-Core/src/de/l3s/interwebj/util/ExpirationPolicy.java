package de.l3s.interwebj.util;

import static de.l3s.interwebj.util.Assertions.*;

import java.util.concurrent.*;

public final class ExpirationPolicy
{

    public static final class Builder
    {

	private long timeToIdle;
	private long timeToLive;

	public ExpirationPolicy build()
	{
	    return new ExpirationPolicy(this);
	}

	public Builder timeToIdle(long duration, TimeUnit unit)
	{
	    timeToIdle = unit.toNanos(duration);
	    return this;
	}

	public Builder timeToLive(long duration, TimeUnit unit)
	{
	    timeToLive = notNegative(unit.toNanos(duration));
	    return this;
	}
    }

    private final long timeToIdle;
    private final long timeToLive;

    private final boolean eternal;

    private ExpirationPolicy(Builder builder)
    {
	timeToIdle = builder.timeToIdle;
	timeToLive = builder.timeToLive;
	eternal = (timeToIdle == 0) && (timeToLive == 0);
    }

    public long getTimeToIdle(TimeUnit unit)
    {
	return unit.convert(timeToIdle, TimeUnit.NANOSECONDS);
    }

    public long getTimeToLive(TimeUnit unit)
    {
	return unit.convert(timeToLive, TimeUnit.NANOSECONDS);
    }

    public boolean isEternal()
    {
	return eternal;
    }
}
