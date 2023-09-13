package de.l3s.interweb.connector.slideshare;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

import de.l3s.interweb.core.search.ContentType;
import de.l3s.interweb.core.search.SearchSort;
import de.l3s.interweb.core.search.Thumbnail;

public final class SlideShareUtils {
    static Thumbnail parseThumbnail(String url) {
        if (url == null) {
            return null;
        }

        int width = 0, height = 0;
        int widthStart = url.indexOf("width=");
        if (widthStart != -1) {
            width = Integer.parseInt(url.substring(widthStart + 6, url.indexOf("&", widthStart)));
        }
        int heightStart = url.indexOf("height=");
        if (heightStart != -1) {
            height = Integer.parseInt(url.substring(heightStart + 7, url.indexOf("&", heightStart)));
        }

        return new Thumbnail(url, width, height);
    }

    static String convertContentType(Set<ContentType> contentTypes) {
        if (contentTypes.size() == 1) {
            if (contentTypes.contains(ContentType.presentations)) {
                return "presentations";
            } else if (contentTypes.contains(ContentType.webpages)) {
                return "documents";
            } else if (contentTypes.contains(ContentType.videos)) {
                return "videos";
            }
        }

        return "all";
    }

    static String convertSort(SearchSort sort) {
        return switch (sort) {
            case date -> "latest";
            case popularity -> "mostviewed";
            default -> "relevance";
        };
    }

    static ContentType createType(int slideshowType) {
        return switch (slideshowType) {
            case 0 -> ContentType.presentations;
            case 1 -> ContentType.webpages;
            case 2 -> ContentType.images;
            case 3 -> ContentType.videos;
            default -> {
                yield null;
            }
        };
    }

    static String hash(String input) {
        try {
            MessageDigest msdDigest = MessageDigest.getInstance("SHA-1");
            msdDigest.update(input.getBytes(StandardCharsets.UTF_8), 0, input.length());
            return new BigInteger(1, msdDigest.digest()).toString(16).toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}
