package de.l3s.interwebj.connector.youtube;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class TokenStorage {

    private static TokenStorage instance = null;
    private static int capacity = 10000;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();
    private Map<Integer, String> storage;

    private TokenStorage() {
        storage = new LinkedHashMap<Integer, String>(capacity + 1, .75F, true) {
            private static final long serialVersionUID = -7231532816950321903L;

            public boolean removeEldestEntry(Map.Entry<Integer, String> eldest) {
                return size() > capacity;
            }
        };
    }

    public static synchronized TokenStorage getInstance() {
        if (instance == null) {
            instance = new TokenStorage();
        }

        return instance;
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
            String old = storage.get(id);
            if (null == old) {
                storage.put(id, resource);
            }
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
}
