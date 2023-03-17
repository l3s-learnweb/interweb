package de.l3s.interweb.core.query;

public enum SearchExtra {
    statistics,
    duration,
    tags;

    public static SearchExtra find(String name) {
        try {
            return SearchExtra.valueOf(name.toLowerCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
