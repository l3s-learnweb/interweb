package de.l3s.interwebj.core.query;

public enum ContentType {
    text,
    video,
    image,
    audio,
    presentation;

    public static ContentType find(String name) {
        try {
            return ContentType.valueOf(name.toLowerCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
