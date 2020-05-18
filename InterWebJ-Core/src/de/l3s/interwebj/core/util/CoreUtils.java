package de.l3s.interwebj.core.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.l3s.interwebj.core.query.Thumbnail;

public class CoreUtils {
    private static final Logger log = LogManager.getLogger(CoreUtils.class);

    private static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static List<String> convertToUniqueList(String s) {
        Set<String> list = new HashSet<String>();
        String[] tokens = s.split("[,\\s]");
        for (String token : tokens) {
            if (token.length() > 0) {
                list.add(token);
            }
        }
        return new ArrayList<String>(list);
    }

    public static String formatDate(DateFormat df, Date date) {
        return (date == null) ? null : df.format(date);
    }

    public static String formatDate(Date date) {
        return formatDate(DEFAULT_DATE_FORMAT, date);
    }

    public static String formatDate(DateFormat df, long millis) {
        return df.format(new Date(millis));
    }

    public static String formatDate(long millis) {
        return formatDate(DEFAULT_DATE_FORMAT, millis);
    }

    public static long parseDate(DateFormat df, String dateString) throws ParseException {
        return df.parse(dateString).getTime();
    }

    public static long parseDate(String dateString) throws ParseException {
        return parseDate(DEFAULT_DATE_FORMAT, dateString);
    }

    public static String ulrToHttps(String url) {
        if (url.startsWith("http://")) {
            return url.replace("http://", "https://");
        }
        return url;
    }

    public static String createImageCode(Thumbnail tn, int maxWidth, int maxHeight) {
        return createImageCode(tn.getUrl(), tn.getWidth(), tn.getHeight(), maxWidth, maxHeight);
    }

    public static String createImageCode(String url, int imageWidth, int imageHeight, int maxWidth, int maxHeight) {
        if (null == url || url.length() < 7 || imageWidth < 2 || imageHeight < 2) {
            return null;
        }

        int width = imageWidth;
        int height = imageHeight;

        if (width > maxWidth) {
            double ratio = (double) maxWidth / (double) width;
            height = (int) Math.ceil(height * ratio);
            width = maxWidth;
        }

        if (height > maxHeight) {
            double ratio = (double) maxHeight / (double) height;
            width = (int) (width * ratio);
            height = maxHeight;
        }

        return "<img src=\"" + ulrToHttps(url) + "\" width=\"" + width + "\" height=\"" + height + "\" alt=\"\" />";
    }
}