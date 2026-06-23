package com.cozyla.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.cozyla.widgets.quote.DailyQuote;

import org.junit.Test;

import java.util.Date;
import java.util.TimeZone;

public class QuoteWidgetTest {
    @Test
    public void quoteIsStableForSameLocalDay() {
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        String morning = DailyQuote.fallbackQuoteFor(new Date(1782000000000L), timeZone);
        String evening = DailyQuote.fallbackQuoteFor(new Date(1782050000000L), timeZone);

        assertEquals(morning, evening);
    }

    @Test
    public void quoteIncludesAuthorAttribution() {
        String quote = DailyQuote.fallbackQuoteFor(new Date(1782000000000L), TimeZone.getTimeZone("UTC"));

        assertTrue(quote, quote.contains(" - "));
    }

    @Test
    public void parsesZenQuotesTodayResponseWithAuthor() throws Exception {
        String quote = DailyQuote.parseZenQuotesToday("[{\"q\":\"The only way to do great work is to love what you do.\",\"a\":\"Steve Jobs\"}]");

        assertEquals("The only way to do great work is to love what you do. - Steve Jobs", quote);
    }

    @Test
    public void keepsUnknownAuthorInRequiredDisplayFormat() throws Exception {
        String quote = DailyQuote.parseZenQuotesToday("[{\"q\":\"You may be one person in this world.\",\"a\":\"Unknown\"}]");

        assertEquals("You may be one person in this world. - Unknown", quote);
    }
}
