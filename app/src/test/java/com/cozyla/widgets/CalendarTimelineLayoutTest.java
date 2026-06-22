package com.cozyla.widgets;

import static org.junit.Assert.assertEquals;

import android.graphics.Color;

import com.cozyla.widgets.calendar.CalendarDisplayRange;
import com.cozyla.widgets.calendar.CalendarEvent;
import com.cozyla.widgets.calendar.CalendarTimelineLayout;

import org.junit.Test;

import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

public class CalendarTimelineLayoutTest {
    private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("America/New_York");

    @Test
    public void clipsEventsToSixAmAndMidnightAndExcludesAllDayEvents() {
        long dayStart = time(2026, 5, 22, 0, 0);
        long dayEnd = time(2026, 5, 23, 0, 0);
        List<CalendarEvent> events = Arrays.asList(
                event("Before", time(2026, 5, 22, 1, 0), time(2026, 5, 22, 2, 0), false),
                event("Morning edge", time(2026, 5, 22, 5, 30), time(2026, 5, 22, 6, 30), false),
                event("Late", time(2026, 5, 22, 23, 30), time(2026, 5, 23, 0, 30), false),
                event("All day", dayStart, dayEnd, true)
        );

        List<CalendarTimelineLayout.PositionedEvent> result = CalendarTimelineLayout.position(
                events,
                dayStart,
                dayEnd,
                TIME_ZONE,
                CalendarDisplayRange.defaults()
        );

        assertEquals(2, result.size());
        assertPosition(result.get(0), "Morning edge", 360, 390, 0, 1);
        assertPosition(result.get(1), "Late", 1410, 1440, 0, 1);
    }

    @Test
    public void overlappingEventsShareLanesButAdjacentEventsDoNot() {
        long dayStart = time(2026, 5, 22, 0, 0);
        long dayEnd = time(2026, 5, 23, 0, 0);
        List<CalendarEvent> events = Arrays.asList(
                event("Long", time(2026, 5, 22, 9, 0), time(2026, 5, 22, 11, 0), false),
                event("Early overlap", time(2026, 5, 22, 9, 30), time(2026, 5, 22, 10, 0), false),
                event("Reused lane", time(2026, 5, 22, 10, 30), time(2026, 5, 22, 12, 0), false),
                event("Adjacent", time(2026, 5, 22, 12, 0), time(2026, 5, 22, 13, 0), false)
        );

        List<CalendarTimelineLayout.PositionedEvent> result = CalendarTimelineLayout.position(
                events,
                dayStart,
                dayEnd,
                TIME_ZONE,
                CalendarDisplayRange.defaults()
        );

        assertPosition(result.get(0), "Long", 540, 660, 0, 2);
        assertPosition(result.get(1), "Early overlap", 570, 600, 1, 2);
        assertPosition(result.get(2), "Reused lane", 630, 720, 1, 2);
        assertPosition(result.get(3), "Adjacent", 720, 780, 0, 1);
    }

    @Test
    public void customRangeClipsBothEdgesAndExcludesOutsideEvents() {
        long dayStart = time(2026, 5, 22, 0, 0);
        long dayEnd = time(2026, 5, 23, 0, 0);
        List<CalendarEvent> events = Arrays.asList(
                event("Too early", time(2026, 5, 22, 6, 0), time(2026, 5, 22, 7, 0), false),
                event("Start edge", time(2026, 5, 22, 7, 30), time(2026, 5, 22, 8, 30), false),
                event("End edge", time(2026, 5, 22, 21, 30), time(2026, 5, 22, 23, 0), false),
                event("Too late", time(2026, 5, 22, 23, 0), time(2026, 5, 23, 0, 0), false)
        );

        List<CalendarTimelineLayout.PositionedEvent> result = CalendarTimelineLayout.position(
                events,
                dayStart,
                dayEnd,
                TIME_ZONE,
                CalendarDisplayRange.of(8, 22)
        );

        assertEquals(2, result.size());
        assertPosition(result.get(0), "Start edge", 480, 510, 0, 1);
        assertPosition(result.get(1), "End edge", 1290, 1320, 0, 1);
    }

    private static CalendarEvent event(
            String title,
            long beginMillis,
            long endMillis,
            boolean allDay
    ) {
        return new CalendarEvent(title, beginMillis, endMillis, allDay, Color.BLUE);
    }

    private static long time(int year, int month, int day, int hour, int minute) {
        GregorianCalendar calendar = new GregorianCalendar(TIME_ZONE);
        calendar.clear();
        calendar.set(year, month, day, hour, minute, 0);
        return calendar.getTimeInMillis();
    }

    private static void assertPosition(
            CalendarTimelineLayout.PositionedEvent event,
            String title,
            int startMinute,
            int endMinute,
            int lane,
            int laneCount
    ) {
        assertEquals(title, event.event.title);
        assertEquals(startMinute, event.startMinute);
        assertEquals(endMinute, event.endMinute);
        assertEquals(lane, event.lane);
        assertEquals(laneCount, event.laneCount);
    }
}
