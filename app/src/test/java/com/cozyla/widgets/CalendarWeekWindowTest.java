package com.cozyla.widgets;

import static org.junit.Assert.assertEquals;

import com.cozyla.widgets.calendar.WeekWindow;

import org.junit.Test;

import java.util.Calendar;
import java.util.TimeZone;

public class CalendarWeekWindowTest {
    private static final TimeZone NEW_YORK = TimeZone.getTimeZone("America/New_York");

    @Test
    public void sundayStillBelongsToPreviousMondayFirstWeek() {
        Calendar input = date(2026, Calendar.JUNE, 28, 15);
        WeekWindow week = WeekWindow.containing(input.getTimeInMillis(), 0, NEW_YORK);

        assertDate(week.startMillis(), 2026, Calendar.JUNE, 22);
        assertDate(week.dayStartMillis(6), 2026, Calendar.JUNE, 28);
        assertDate(week.endExclusiveMillis(7), 2026, Calendar.JUNE, 29);
    }

    @Test
    public void offsetMovesWholeWeeksWithoutChangingMondayBoundary() {
        Calendar input = date(2026, Calendar.JUNE, 22, 9);

        assertDate(
                WeekWindow.containing(input.getTimeInMillis(), -1, NEW_YORK).startMillis(),
                2026,
                Calendar.JUNE,
                15
        );
        assertDate(
                WeekWindow.containing(input.getTimeInMillis(), 1, NEW_YORK).startMillis(),
                2026,
                Calendar.JUNE,
                29
        );
    }

    @Test
    public void workweekEndsAtSaturdayBoundary() {
        Calendar input = date(2026, Calendar.JUNE, 22, 9);
        WeekWindow week = WeekWindow.containing(input.getTimeInMillis(), 0, NEW_YORK);

        assertDate(week.endExclusiveMillis(5), 2026, Calendar.JUNE, 27);
    }

    private static Calendar date(int year, int month, int day, int hour) {
        Calendar calendar = Calendar.getInstance(NEW_YORK);
        calendar.clear();
        calendar.set(year, month, day, hour, 0, 0);
        return calendar;
    }

    private static void assertDate(long millis, int year, int month, int day) {
        Calendar actual = Calendar.getInstance(NEW_YORK);
        actual.setTimeInMillis(millis);
        assertEquals(year, actual.get(Calendar.YEAR));
        assertEquals(month, actual.get(Calendar.MONTH));
        assertEquals(day, actual.get(Calendar.DAY_OF_MONTH));
    }
}
