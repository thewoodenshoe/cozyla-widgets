package com.cozyla.widgets.calendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TimeZone;

public final class CalendarTimelineLayout {
    private CalendarTimelineLayout() {
    }

    public static List<PositionedEvent> position(
            List<CalendarEvent> events,
            long dayStartMillis,
            long dayEndMillis,
            TimeZone timeZone,
            CalendarDisplayRange displayRange
    ) {
        int startMinute = displayRange.startMinute();
        int endMinute = displayRange.endMinute();
        long displayStartMillis = atHour(dayStartMillis, displayRange.startHour, timeZone);
        long displayEndMillis = displayRange.endHour == 24
                ? dayEndMillis
                : atHour(dayStartMillis, displayRange.endHour, timeZone);
        List<PositionedEvent> positioned = new ArrayList<>();

        for (CalendarEvent event : events) {
            if (event.allDay
                    || event.endMillis <= displayStartMillis
                    || event.beginMillis >= displayEndMillis) {
                continue;
            }

            long clippedStart = Math.max(event.beginMillis, displayStartMillis);
            long clippedEnd = Math.min(event.endMillis, displayEndMillis);
            int eventStartMinute = clippedStart == displayStartMillis
                    ? startMinute
                    : minuteOfDay(clippedStart, timeZone);
            int eventEndMinute = clippedEnd == displayEndMillis
                    ? endMinute
                    : minuteOfDay(clippedEnd, timeZone);

            eventStartMinute = clamp(eventStartMinute, startMinute, endMinute);
            eventEndMinute = clamp(eventEndMinute, startMinute, endMinute);
            if (eventEndMinute <= eventStartMinute) {
                int elapsedMinutes = (int) Math.ceil((clippedEnd - clippedStart) / 60_000d);
                eventEndMinute = Math.min(
                        endMinute,
                        eventStartMinute + Math.max(1, elapsedMinutes)
                );
            }
            if (eventEndMinute > eventStartMinute) {
                positioned.add(new PositionedEvent(event, eventStartMinute, eventEndMinute));
            }
        }

        Collections.sort(positioned, new Comparator<PositionedEvent>() {
            @Override
            public int compare(PositionedEvent first, PositionedEvent second) {
                int startComparison = Integer.compare(first.startMinute, second.startMinute);
                if (startComparison != 0) {
                    return startComparison;
                }
                int endComparison = Integer.compare(first.endMinute, second.endMinute);
                if (endComparison != 0) {
                    return endComparison;
                }
                return String.CASE_INSENSITIVE_ORDER.compare(first.event.title, second.event.title);
            }
        });
        assignOverlapLanes(positioned);
        return positioned;
    }

    private static void assignOverlapLanes(List<PositionedEvent> events) {
        int clusterStart = 0;
        while (clusterStart < events.size()) {
            int clusterEndMinute = events.get(clusterStart).endMinute;
            int clusterEnd = clusterStart + 1;
            while (clusterEnd < events.size()
                    && events.get(clusterEnd).startMinute < clusterEndMinute) {
                clusterEndMinute = Math.max(clusterEndMinute, events.get(clusterEnd).endMinute);
                clusterEnd++;
            }
            assignClusterLanes(events, clusterStart, clusterEnd);
            clusterStart = clusterEnd;
        }
    }

    private static void assignClusterLanes(List<PositionedEvent> events, int start, int end) {
        List<Integer> laneEndMinutes = new ArrayList<>();
        for (int index = start; index < end; index++) {
            PositionedEvent event = events.get(index);
            int lane = 0;
            while (lane < laneEndMinutes.size() && laneEndMinutes.get(lane) > event.startMinute) {
                lane++;
            }
            if (lane == laneEndMinutes.size()) {
                laneEndMinutes.add(event.endMinute);
            } else {
                laneEndMinutes.set(lane, event.endMinute);
            }
            event.lane = lane;
        }

        int laneCount = Math.max(1, laneEndMinutes.size());
        for (int index = start; index < end; index++) {
            events.get(index).laneCount = laneCount;
        }
    }

    private static long atHour(long dayStartMillis, int hour, TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTimeInMillis(dayStartMillis);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private static int minuteOfDay(long timeMillis, TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTimeInMillis(timeMillis);
        return calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
    }

    private static int clamp(int value, int minimum, int maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }

    public static final class PositionedEvent {
        public final CalendarEvent event;
        public final int startMinute;
        public final int endMinute;
        public int lane;
        public int laneCount = 1;

        private PositionedEvent(CalendarEvent event, int startMinute, int endMinute) {
            this.event = event;
            this.startMinute = startMinute;
            this.endMinute = endMinute;
        }
    }
}
