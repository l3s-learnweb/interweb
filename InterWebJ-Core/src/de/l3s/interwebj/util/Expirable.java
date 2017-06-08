package de.l3s.interwebj.util;

import static de.l3s.interwebj.util.Assertions.notNull;

import java.util.concurrent.TimeUnit;

public final class Expirable<K, V>
{

    private final K key;
    private final V value;
    private final long creationTime;
    private long accessTime;
    private final ExpirationPolicy policy;

    public Expirable(K key, V value, ExpirationPolicy policy)
    {
	notNull(key, "key");
	notNull(value, "value");
	notNull(policy, "policy");
	this.key = key;
	this.value = value;
	this.policy = policy;
	this.creationTime = System.nanoTime();
	this.accessTime = System.nanoTime();
    }

    public long getAccessTime(TimeUnit unit)
    {
	return unit.convert(accessTime, TimeUnit.NANOSECONDS);
    }

    public long getCreationTime(TimeUnit unit)
    {
	return unit.convert(creationTime, TimeUnit.NANOSECONDS);
    }

    public K getKey()
    {
	return key;
    }

    public ExpirationPolicy getPolicy()
    {
	return policy;
    }

    public V getValue()
    {
	return value;
    }

    public void onAccess()
    {
	accessTime = System.nanoTime();
    }
}
