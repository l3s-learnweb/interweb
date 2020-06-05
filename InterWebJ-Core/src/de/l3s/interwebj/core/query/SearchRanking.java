package de.l3s.interwebj.core.query;

public enum SearchRanking {
    date,
    relevance,
    interestingness;

    public static SearchRanking find(String name) {
        try {
            return SearchRanking.valueOf(name.toLowerCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
