package de.l3s.interwebj.core.query;

public enum SearchScope {
    text,
    tags;

    public static SearchScope find(String name) {
        try {
            return SearchScope.valueOf(name.toLowerCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
