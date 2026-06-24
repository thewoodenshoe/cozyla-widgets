package com.cozyla.widgets.quote;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public final class DailyQuote {
    private static final Quote[] FALLBACK_QUOTES = {
            new Quote("The only way to do great work is to love what you do.", "Steve Jobs"),
            new Quote("It always seems impossible until it is done.", "Nelson Mandela"),
            new Quote("What you do today can improve all your tomorrows.", "Ralph Marston"),
            new Quote("Well done is better than well said.", "Benjamin Franklin"),
            new Quote("If opportunity does not knock, build a door.", "Milton Berle"),
            new Quote("The secret of getting ahead is getting started.", "Mark Twain"),
            new Quote("Quality is not an act, it is a habit.", "Aristotle"),
            new Quote("Action is the foundational key to all success.", "Pablo Picasso")
    };

    private DailyQuote() {
    }

    public static String fallbackQuoteFor(Date date, TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTime(date);
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        int year = calendar.get(Calendar.YEAR);
        int index = Math.floorMod((year * 366) + dayOfYear, FALLBACK_QUOTES.length);
        return FALLBACK_QUOTES[index].formatted();
    }

    public static String format(String quote, String author) {
        String cleanQuote = clean(quote);
        String cleanAuthor = clean(author);
        if (cleanQuote.isEmpty()) {
            return fallbackQuoteFor(new Date(), TimeZone.getDefault());
        }
        String displayAuthor = cleanAuthor.isEmpty() ? "Unknown" : cleanAuthor;
        return cleanQuote + " - " + displayAuthor;
    }

    public static String parseZenQuotesToday(String json) throws Exception {
        String quote = extractJsonString(json, "q");
        String author = extractJsonString(json, "a");
        if (quote.isEmpty()) {
            throw new IllegalArgumentException("Quote response was empty");
        }
        return format(quote, author);
    }

    private static String clean(String value) {
        return value == null ? "" : value.trim().replace('\n', ' ');
    }

    private static String extractJsonString(String json, String key) {
        if (json == null || key == null) {
            return "";
        }
        String needle = "\"" + key + "\"";
        int keyIndex = json.indexOf(needle);
        if (keyIndex < 0) {
            return "";
        }
        int colonIndex = json.indexOf(':', keyIndex + needle.length());
        if (colonIndex < 0) {
            return "";
        }
        int startQuote = json.indexOf('"', colonIndex + 1);
        if (startQuote < 0) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        boolean escaped = false;
        for (int index = startQuote + 1; index < json.length(); index++) {
            char current = json.charAt(index);
            if (escaped) {
                builder.append(unescape(current));
                escaped = false;
            } else if (current == '\\') {
                escaped = true;
            } else if (current == '"') {
                return builder.toString();
            } else {
                builder.append(current);
            }
        }
        return "";
    }

    private static char unescape(char current) {
        return switch (current) {
            case 'n' -> ' ';
            case 'r' -> ' ';
            case 't' -> ' ';
            case '"' -> '"';
            case '\\' -> '\\';
            default -> current;
        };
    }

    private static final class Quote {
        private final String text;
        private final String author;

        private Quote(String text, String author) {
            this.text = text;
            this.author = author;
        }

        String formatted() {
            return text + " - " + author;
        }
    }
}
