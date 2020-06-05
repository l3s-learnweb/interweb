package de.l3s.interwebj.connector.youtube;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class TokenStorage {

    private static TokenStorage instance = null;
    private static final int capacity = 10000;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();
    private final Map<Integer, String> storage;

    private TokenStorage() {
        storage = new TokenStorageMap<>(capacity + 1, .75F, true);
    }

    public String get(int id) {
        readLock.lock();
        try {
            return storage.get(id);
        } finally {
            readLock.unlock();
        }
    }

    public void put(int id, String resource) {
        writeLock.lock();
        try {
            storage.putIfAbsent(id, resource);
        } finally {
            writeLock.unlock();
        }
    }

    public void remove(int id) {
        writeLock.lock();
        try {
            storage.remove(id);
        } finally {
            writeLock.unlock();
        }
    }

    public void clear() {
        writeLock.lock();
        try {
            storage.clear();
        } finally {
            writeLock.unlock();
        }
    }

    public static synchronized TokenStorage getInstance() {
        if (instance == null) {
            instance = new TokenStorage();
        }

        return instance;
    }

    private static final class TokenStorageMap<K, V> extends LinkedHashMap<K, V> {
        private static final long serialVersionUID = -7231532816950321903L;

        private TokenStorageMap(final int initialCapacity, final float loadFactor, final boolean accessOrder) {
            super(initialCapacity, loadFactor, accessOrder);
        }

        public boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > capacity;
        }
    }
}
