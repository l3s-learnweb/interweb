package de.l3s.interweb.core.util;

public class Assertions {

    public static void notEmpty(Object object, String name) {
        notNull(object, name);

        if (object.toString().isEmpty()) {
            throw new IllegalArgumentException("Argument [" + name + "] must not be empty string");
        }
    }

    public static long notNegative(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("Value must not be negative");
        }
        return value;
    }

    public static void notNull(Object object, String name) {
        if (object == null) {
            throw new NullPointerException("Argument [" + name + "] must not be null");
        }
    }

    public static long notZero(long value, String name) {
        if (value == 0) {
            throw new IllegalArgumentException("Value must not be equal zero");
        }
        return value;
    }
}
