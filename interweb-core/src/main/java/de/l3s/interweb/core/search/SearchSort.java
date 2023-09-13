package de.l3s.interweb.core.search;

public enum SearchSort {
    date,
    popularity,
    relevance;

    public static SearchSort find(String name) {
        try {
            return SearchSort.valueOf(name.toLowerCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
