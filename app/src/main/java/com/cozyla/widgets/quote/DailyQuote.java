package com.cozyla.widgets.quote;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public final class DailyQuote {
    private static final String[] QUOTES = {
            "Do the simple things well.",
            "Small progress still counts.",
            "Make the useful thing obvious.",
            "A calm plan beats a rushed one.",
            "Leave it better than you found it.",
            "Start with the next honest step.",
            "Good systems make good habits easier.",
            "Focus is a decision repeated.",
            "The best shortcut is clarity.",
            "Keep promises small and real.",
            "Measure what you can improve.",
            "Attention is a limited budget.",
            "A finished small thing beats a perfect idea.",
            "Make room for what matters.",
            "Reliable beats dramatic.",
            "Better choices compound quietly."
    };

    private DailyQuote() {
    }

    public static String quoteFor(Date date, TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTime(date);
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        int year = calendar.get(Calendar.YEAR);
        int index = Math.floorMod((year * 366) + dayOfYear, QUOTES.length);
        return QUOTES[index];
    }
}
