package de.l3s.interwebj.core.util;

import static de.l3s.interwebj.core.util.Assertions.notNull;

import java.lang.ref.WeakReference;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ExpirableMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V> {

    private final ConcurrentMap<K, Expirable<K, V>> delegate;
    private final ScheduledExecutorService scheduler;
    private final ExpirationPolicy defaultPolicy;

    public ExpirableMap(ExpirationPolicy defaultPolicy) {
        delegate = new ConcurrentHashMap<K, Expirable<K, V>>();
        scheduler = Executors.newScheduledThreadPool(0);
        this.defaultPolicy = defaultPolicy;
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public boolean containsKey(Object key) {
        notNull(key, "key");
        Expirable<K, V> expirable = delegate.get(key);
        if (expirable == null) {
            return false;
        } else if (hasExpired(expirable)) {
            handleExpiration(expirable);
            return false;
        }
        return true;
    }

    @Override
    public boolean containsValue(Object value) {
        notNull(value, "value");
        for (Expirable<K, V> expirable : delegate.values()) {
            if (expirable.getValue().equals(value)) {
                if (hasExpired(expirable)) {
                    handleExpiration(expirable);
                    continue;
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return new EntrySet();
    }

    @Override
    public V get(Object key) {
        notNull(key, "key");
        Expirable<K, V> expirable = delegate.get(key);
        if (expirable == null) {
            return null;
        } else if (hasExpired(expirable)) {
            handleExpiration(expirable);
            return null;
        }
        expirable.onAccess();
        scheduleTimeToIdle(expirable);
        return expirable.getValue();
    }

    @Override
    public Set<K> keySet() {
        return delegate.keySet();
    }

    @Override
    public V put(K key, V value) {
        return put(key, value, defaultPolicy);
    }

    public V put(K key, V value, ExpirationPolicy policy) {
        notNull(key, "key");
        notNull(value, "value");
        notNull(policy, "policy");
        Expirable<K, V> expirable = new Expirable<K, V>(key, value, policy);
        Expirable<K, V> old = delegate.put(key, expirable);
        scheduleTimeToLive(expirable);
        scheduleTimeToIdle(expirable);
        if (old == null || hasExpired(old)) {
            return null;
        }
        return old.getValue();
    }

    public V putIfAbsent(K key, V value) {
        return putIfAbsent(key, value, defaultPolicy);
    }

    public V putIfAbsent(K key, V value, ExpirationPolicy policy) {
        notNull(key, "key");
        notNull(value, "value");
        notNull(policy, "policy");
        while (true) {
            Expirable<K, V> expirable = new Expirable<K, V>(key, value, policy);
            Expirable<K, V> old = delegate.putIfAbsent(key, expirable);
            if (old == null) {
                scheduleTimeToLive(expirable);
                scheduleTimeToIdle(expirable);
                return null;
            } else if (hasExpired(old)) {
                handleExpiration(old);
                continue;
            } else {
                expirable.onAccess();
                scheduleTimeToIdle(expirable);
                return old.getValue();
            }
        }
    }

    @Override
    public V remove(Object key) {
        notNull(key, "key");
        Expirable<K, V> old = delegate.remove(key);
        if (old == null || hasExpired(old)) {
            return null;
        }
        return old.getValue();
    }

    public boolean remove(Object key, Object value) {
        notNull(key, "key");
        notNull(value, "value");
        Expirable<K, V> old = delegate.get(key);
        if (old == null) {
            return false;
        } else if (hasExpired(old)) {
            handleExpiration(old);
            return false;
        } else if (old.getValue().equals(value)) {
            return delegate.remove(key, old);
        }
        return false;
    }

    public V replace(K key, V value) {
        return replace(key, value, defaultPolicy);
    }

    public V replace(K key, V value, ExpirationPolicy policy) {
        notNull(key, "key");
        notNull(value, "value");
        notNull(policy, "policy");
        Expirable<K, V> old = delegate.get(key);
        if (old == null) {
            return null;
        } else if (hasExpired(old)) {
            handleExpiration(old);
            return null;
        }
        Expirable<K, V> expirable = new Expirable<K, V>(key, value, policy);
        old = delegate.replace(key, expirable);
        if (old == null) {
            return null;
        }
        scheduleTimeToLive(expirable);
        scheduleTimeToIdle(expirable);
        return old.getValue();
    }

    public boolean replace(K key, V oldValue, V newValue) {
        return replace(key, oldValue, newValue, defaultPolicy);
    }

    public boolean replace(K key, V oldValue, V newValue, ExpirationPolicy policy) {
        notNull(key, "key");
        notNull(oldValue, "oldValue");
        notNull(newValue, "newValue");
        notNull(policy, "policy");
        Expirable<K, V> old = delegate.get(key);
        if (old == null) {
            return false;
        } else if (hasExpired(old)) {
            handleExpiration(old);
            return false;
        } else if (!oldValue.equals(old.getValue())) {
            return false;
        }
        Expirable<K, V> expirable = new Expirable<K, V>(key, newValue, policy);
        if (delegate.replace(key, old, expirable)) {
            scheduleTimeToLive(expirable);
            scheduleTimeToIdle(expirable);
            return true;
        }
        return false;
    }

    @Override
    public int size() {
        return delegate.size();
    }

    private void handleExpiration(Expirable<K, V> expirable) {
        delegate.remove(expirable.getKey(), expirable);
    }

    private boolean hasExpired(Expirable<K, V> expirable) {
        ExpirationPolicy policy = expirable.getPolicy();
        long currentTime = System.nanoTime();
        return !policy.isEternal() && (isPastTime(policy.getTimeToIdle(TimeUnit.NANOSECONDS), expirable.getAccessTime(TimeUnit.NANOSECONDS), currentTime)
            || isPastTime(policy.getTimeToLive(TimeUnit.NANOSECONDS), expirable.getCreationTime(TimeUnit.NANOSECONDS), currentTime));
    }

    private boolean isPastTime(long policyTime, long baseTime, long currentTime) {
        return (policyTime != 0) && (currentTime > (policyTime + baseTime));
    }

    private void scheduleTimeToIdle(Expirable<K, V> expirable) {
        long timeToIdle = expirable.getPolicy().getTimeToIdle(TimeUnit.NANOSECONDS);
        if (timeToIdle != 0) {
            scheduler.schedule(new ExpirationTask<K, V>(this, expirable), timeToIdle, TimeUnit.NANOSECONDS);
        }
    }

    private void scheduleTimeToLive(Expirable<K, V> expirable) {
        long timeToLive = expirable.getPolicy().getTimeToLive(TimeUnit.NANOSECONDS);
        if (timeToLive != 0) {
            scheduler.schedule(new ExpirationTask<K, V>(this, expirable), timeToLive, TimeUnit.NANOSECONDS);
        }
    }

    private static final class ExpirationTask<K, V> implements Runnable {

        private final WeakReference<Expirable<K, V>> expirableRef;
        private final WeakReference<ExpirableMap<K, V>> mapRef;

        ExpirationTask(ExpirableMap<K, V> map, Expirable<K, V> expirable) {
            expirableRef = new WeakReference<Expirable<K, V>>(expirable);
            mapRef = new WeakReference<ExpirableMap<K, V>>(map);
        }

        public void run() {
            ExpirableMap<K, V> map = mapRef.get();
            Expirable<K, V> expirable = expirableRef.get();
            if ((map != null) && (expirable != null)) {
                if (map.hasExpired(expirable)) {
                    map.handleExpiration(expirable);
                }
            }
        }
    }

    private final class EntrySet extends AbstractSet<java.util.Map.Entry<K, V>> implements Set<java.util.Map.Entry<K, V>> {

        @Override
        public Iterator<Entry<K, V>> iterator() {
            return new Iterator<Map.Entry<K, V>>() {

                Iterator<Entry<K, Expirable<K, V>>> iterator = ExpirableMap.this.delegate.entrySet().iterator();

                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public Entry<K, V> next() {
                    final Entry<K, Expirable<K, V>> entry = iterator.next();

                    return new Entry<K, V>() {

                        @Override
                        public K getKey() {
                            return entry.getKey();
                        }

                        @Override
                        public V getValue() {
                            return entry.getValue().getValue();
                        }

                        @Override
                        public V setValue(V value) {
                            throw new UnsupportedOperationException("Not implemented");
                        }

                    };
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException("Not implemented");
                }
            };
        }

        @Override
        public int size() {
            return delegate.size();
        }
    }
}
