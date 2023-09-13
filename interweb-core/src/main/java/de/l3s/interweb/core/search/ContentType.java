package de.l3s.interweb.core.search;

public enum ContentType {
    webpages,
    videos,
    images,
    audios,
    news,
    presentations;

    public static ContentType find(String name) {
        try {
            return ContentType.valueOf(name.toLowerCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
