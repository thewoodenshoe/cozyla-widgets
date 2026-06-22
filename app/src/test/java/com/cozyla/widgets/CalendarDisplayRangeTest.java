package com.cozyla.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import com.cozyla.widgets.calendar.CalendarDisplayRange;

import org.junit.Test;

public class CalendarDisplayRangeTest {
    @Test
    public void acceptsWholeHourRangesAndCalculatesDuration() {
        CalendarDisplayRange range = CalendarDisplayRange.of(8, 22);

        assertEquals(8, range.startHour);
        assertEquals(22, range.endHour);
        assertEquals(480, range.startMinute());
        assertEquals(1320, range.endMinute());
        assertEquals(840, range.minutesShown());
        assertEquals(14, range.hourCount());
    }

    @Test
    public void rejectsBackwardsAndOutOfBoundsRanges() {
        assertFalse(CalendarDisplayRange.isValid(8, 8));
        assertFalse(CalendarDisplayRange.isValid(22, 8));
        assertFalse(CalendarDisplayRange.isValid(-1, 22));
        assertFalse(CalendarDisplayRange.isValid(8, 25));
        assertTrue(CalendarDisplayRange.isValid(0, 24));
        assertThrows(IllegalArgumentException.class, () -> CalendarDisplayRange.of(10, 9));
    }

    @Test
    public void corruptStoredRangeFallsBackToSixAmThroughMidnight() {
        CalendarDisplayRange range = CalendarDisplayRange.fromStored(20, 4);

        assertEquals(6, range.startHour);
        assertEquals(24, range.endHour);
    }
}
