package com.cozyla.widgets;

import static org.junit.Assert.assertEquals;

import com.cozyla.widgets.quote.DailyQuote;

import org.junit.Test;

import java.util.Date;
import java.util.TimeZone;

public class QuoteWidgetTest {
    @Test
    public void quoteIsStableForSameLocalDay() {
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        String morning = DailyQuote.quoteFor(new Date(1782000000000L), timeZone);
        String evening = DailyQuote.quoteFor(new Date(1782050000000L), timeZone);

        assertEquals(morning, evening);
    }
}
