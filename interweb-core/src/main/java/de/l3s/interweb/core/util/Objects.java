package de.l3s.interweb.core.util;

public final class Objects {

    private Objects() {
    }

    /**
     * Returns the first non-<code>null</code> object from the provided array of objects.
     *
     * @param values The objects to test.
     * @return The first non-<code>null</code> object or <code>null</code> if all values are <code>null</code>.
     */
    @SafeVarargs
    public static <T> T firstNonNull(T... values) {
        if (values != null) {
            for (T value : values) {
                if (value != null) {
                    return value;
                }
            }
        }
        return null;
    }
}
