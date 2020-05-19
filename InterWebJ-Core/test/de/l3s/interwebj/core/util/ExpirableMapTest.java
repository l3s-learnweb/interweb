package de.l3s.interwebj.core.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

class ExpirableMapTest {

    @Test
    void testExpirable() throws InterruptedException {
        ExpirationPolicy policy = new ExpirationPolicy.Builder().timeToIdle(1, TimeUnit.SECONDS).build();
        ExpirableMap<String, String> map = new ExpirableMap<String, String>(policy);

        Expirable<String, String> expirable = new Expirable<String, String>("a", "aaa", policy);
        map.put(expirable.getKey(), expirable.getValue());

        assertTrue(map.containsKey("a"));
        assertEquals("aaa", map.get("a"));

        Thread.sleep(1500);

        assertFalse(map.containsKey("a"));
    }
}
