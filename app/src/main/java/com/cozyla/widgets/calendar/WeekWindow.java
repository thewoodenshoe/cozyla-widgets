package com.cozyla.widgets.calendar;

import java.util.Calendar;
import java.util.TimeZone;

public final class WeekWindow {
    private final long startMillis;
    private final TimeZone timeZone;

    private WeekWindow(long startMillis, TimeZone timeZone) {
        this.startMillis = startMillis;
        this.timeZone = timeZone;
    }

    public static WeekWindow containing(long timestampMillis, int weekOffset, TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTimeInMillis(timestampMillis);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        int daysSinceMonday = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7;
        calendar.add(Calendar.DAY_OF_MONTH, -daysSinceMonday + (weekOffset * 7));
        return new WeekWindow(calendar.getTimeInMillis(), timeZone);
    }

    public long startMillis() {
        return startMillis;
    }

    public long dayStartMillis(int dayIndex) {
        Calendar day = Calendar.getInstance(timeZone);
        day.setTimeInMillis(startMillis);
        day.add(Calendar.DAY_OF_MONTH, dayIndex);
        return day.getTimeInMillis();
    }

    public long endExclusiveMillis(int dayCount) {
        return dayStartMillis(dayCount);
    }

    public TimeZone timeZone() {
        return timeZone;
    }
}
