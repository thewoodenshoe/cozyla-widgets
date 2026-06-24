package com.cozyla.widgets;

import static org.junit.Assert.assertEquals;

import com.cozyla.widgets.countdown.CountdownFormatter;

import org.junit.Test;

public class CountdownFormatterTest {
    @Test
    public void formatsMinuteSecondTimerValues() {
        assertEquals("0:00", CountdownFormatter.display(0L));
        assertEquals("0:10", CountdownFormatter.display(10_000L));
        assertEquals("5:00", CountdownFormatter.display(300_000L));
        assertEquals("99:59", CountdownFormatter.display(5_999_000L));
    }
}
