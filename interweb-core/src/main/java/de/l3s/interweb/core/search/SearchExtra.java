package de.l3s.interweb.core.search;

public enum SearchExtra {
    stats,
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
