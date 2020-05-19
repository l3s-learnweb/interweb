package de.l3s.interwebj.core.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RandomGeneratorTest {

    @Test
    void nextOAuthCredentials() {
        RandomGenerator randomGenerator = RandomGenerator.getInstance();

        var a = randomGenerator.nextOAuthCredentials();
        var b = randomGenerator.nextOAuthCredentials();
        assertNotEquals(a, b);
    }
}
