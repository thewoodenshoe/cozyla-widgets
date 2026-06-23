package com.cozyla.widgets.quote;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public final class DailyQuote {
    private static final String AUTHOR = "Cozyla Widgets";
    private static final Quote[] QUOTES = {
            new Quote("Do the simple things well.", AUTHOR),
            new Quote("Small progress still counts.", AUTHOR),
            new Quote("Make the useful thing obvious.", AUTHOR),
            new Quote("A calm plan beats a rushed one.", AUTHOR),
            new Quote("Leave it better than you found it.", AUTHOR),
            new Quote("Start with the next honest step.", AUTHOR),
            new Quote("Good systems make good habits easier.", AUTHOR),
            new Quote("Focus is a decision repeated.", AUTHOR),
            new Quote("The best shortcut is clarity.", AUTHOR),
            new Quote("Keep promises small and real.", AUTHOR),
            new Quote("Measure what you can improve.", AUTHOR),
            new Quote("Attention is a limited budget.", AUTHOR),
            new Quote("A finished small thing beats a perfect idea.", AUTHOR),
            new Quote("Make room for what matters.", AUTHOR),
            new Quote("Reliable beats dramatic.", AUTHOR),
            new Quote("Better choices compound quietly.", AUTHOR)
    };

    private DailyQuote() {
    }

    public static String quoteFor(Date date, TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTime(date);
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        int year = calendar.get(Calendar.YEAR);
        int index = Math.floorMod((year * 366) + dayOfYear, QUOTES.length);
        return QUOTES[index].formatted();
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
