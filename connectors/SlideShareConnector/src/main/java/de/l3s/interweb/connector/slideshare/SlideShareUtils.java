package de.l3s.interweb.connector.slideshare;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Set;

import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.search.ContentType;
import de.l3s.interweb.core.search.SearchRanking;
import de.l3s.interweb.core.search.Thumbnail;

public final class SlideShareUtils {
    static Thumbnail parseThumbnail(String url) {
        if (url == null) {
            return null;
        }

        int width =0, height = 0;
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
            if (contentTypes.contains(ContentType.presentation)) {
                return "presentations";
            } else if (contentTypes.contains(ContentType.text)) {
                return "documents";
            } else if (contentTypes.contains(ContentType.video)) {
                return "videos";
            }
        }

        return "all";
    }

    static String convertRanking(SearchRanking ranking) {
        return switch (ranking) {
            case date -> "latest";
            case interestingness -> "mostviewed";
            default -> "relevance";
        };
    }

    static ContentType createType(int slideshowType) {
        return switch (slideshowType) {
            case 0 -> ContentType.presentation;
            case 1 -> ContentType.text;
            case 2 -> ContentType.image;
            case 3 -> ContentType.video;
            default -> {
                yield null;
            }
        };
    }

    static ZonedDateTime parseDate(String dateString) throws ConnectorException {
        if (dateString == null) {
            return null;
        }

        try {
            return ZonedDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z").withZone(ZoneId.systemDefault()));
        } catch (DateTimeParseException e) {
            throw new ConnectorException("dateString: [" + dateString + "] " + e.getMessage());
        }
    }

    static String getHash(String input) {
        try {
            MessageDigest msdDigest = MessageDigest.getInstance("SHA-1");
            msdDigest.update(input.getBytes(StandardCharsets.UTF_8), 0, input.length());
            return new BigInteger(1, msdDigest.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}
