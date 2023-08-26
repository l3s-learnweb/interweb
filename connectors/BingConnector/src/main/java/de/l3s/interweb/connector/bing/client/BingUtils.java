package de.l3s.interweb.connector.bing.client;

public final class BingUtils {
    public static String getMarket(String language) {
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
