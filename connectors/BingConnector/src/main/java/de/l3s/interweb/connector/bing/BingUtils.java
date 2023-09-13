package de.l3s.interweb.connector.bing;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class BingUtils {

    /**
     * Filter search results by the following case-insensitive age values: day, week, month.
     * To get articles discovered by Bing during a specific timeframe, specify a date range in the form, YYYY-MM-DD..YYYY-MM-DD.
     * To limit the results to a single date, set this parameter to a specific date, e.g. freshness=2019-02-04.
     */
    static String createFreshness(LocalDate dateFrom, LocalDate dateTill) {
        if (dateTill != null) {
            if (dateTill.plusDays(2).isAfter(LocalDate.now())) { // add 1 day for safety
                return "day";
            } else if (dateTill.plusDays(9).isAfter(LocalDate.now())) { // add 2 days for safety
                return "week";
            } else if (dateTill.plusDays(34).isAfter(LocalDate.now())) { // add 3 days for safety
                return "month";
            }
        }

        if (dateFrom != null) {
            String dateFromFormat = DateTimeFormatter.ISO_LOCAL_DATE.format(dateFrom);

            if (dateTill != null && !dateFrom.equals(dateTill)) {
                String dateTillFormat = DateTimeFormatter.ISO_LOCAL_DATE.format(dateTill);
                return dateFromFormat + ".." + dateTillFormat;
            }

            return dateFromFormat;
        }

        return null;
    }

    static String getMarket(String language) {
        if (language == null) return null;
        return switch (language.toLowerCase()) {
            case "ar" -> "ar-XA";
            case "bg" -> "bg-BG";
            case "cs" -> "cs-CZ";
            case "da" -> "da-DK";
            case "de" -> "de-DE";
            case "el" -> "el-GR";
            case "es" -> "es-ES";
            case "et" -> "et-EE";
            case "fi" -> "fi-FI";
            case "fr" -> "fr-FR";
            case "he" -> "he-IL";
            case "hr" -> "hr-HR";
            case "hu" -> "hu-HU";
            case "it" -> "it-IT";
            case "ja" -> "ja-JP";
            case "ko" -> "ko-KR";
            case "lt" -> "lt-LT";
            case "lv" -> "lv-LV";
            case "nb" -> "nb-NO";
            case "nl" -> "nl-NL";
            case "pl" -> "pl-PL";
            case "pt" -> "pt-PT";
            case "ro" -> "ro-RO";
            case "ru" -> "ru-RU";
            case "sk" -> "sk-SK";
            case "sl" -> "sl-SL";
            case "sv" -> "sv-SE";
            case "th" -> "th-TH";
            case "tr" -> "tr-TR";
            case "uk" -> "uk-UA";
            case "zh" -> "zh-CN";
            default -> "en-US";
        };
    }
}
