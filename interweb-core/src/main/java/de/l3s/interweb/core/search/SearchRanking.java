package de.l3s.interweb.core.search;

public enum SearchRanking {
    date,
    interestingness,
    relevance;

    public static SearchRanking find(String name) {
        try {
            return SearchRanking.valueOf(name.toLowerCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
