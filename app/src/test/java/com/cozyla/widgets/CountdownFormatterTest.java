package com.cozyla.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.cozyla.widgets.countdown.CountdownFormatter;

import org.junit.Test;

public class CountdownFormatterTest {
    @Test
    public void formatsRemainingTimeIntoDaysHoursAndMinutes() {
        CountdownFormatter.Parts parts = CountdownFormatter.parts(1_000L, 1_000L + 2L * 86_400_000L + 3L * 3_600_000L + 4L * 60_000L);

        assertEquals(2L, parts.days);
        assertEquals(3L, parts.hours);
        assertEquals(4L, parts.minutes);
        assertFalse(parts.done);
    }

    @Test
    public void marksPastTargetAsDone() {
        CountdownFormatter.Parts parts = CountdownFormatter.parts(10_000L, 1_000L);

        assertEquals(0L, parts.days);
        assertEquals(0L, parts.hours);
        assertEquals(0L, parts.minutes);
        assertTrue(parts.done);
    }
}
