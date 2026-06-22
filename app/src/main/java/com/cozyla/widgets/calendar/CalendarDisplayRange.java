package com.cozyla.widgets.calendar;

public final class CalendarDisplayRange {
    public static final int DEFAULT_START_HOUR = 6;
    public static final int DEFAULT_END_HOUR = 24;

    public final int startHour;
    public final int endHour;

    private CalendarDisplayRange(int startHour, int endHour) {
        this.startHour = startHour;
        this.endHour = endHour;
    }

    public static CalendarDisplayRange of(int startHour, int endHour) {
        if (!isValid(startHour, endHour)) {
            throw new IllegalArgumentException("End hour must be later than start hour");
        }
        return new CalendarDisplayRange(startHour, endHour);
    }

    public static CalendarDisplayRange fromStored(int startHour, int endHour) {
        if (!isValid(startHour, endHour)) {
            return defaults();
        }
        return new CalendarDisplayRange(startHour, endHour);
    }

    public static CalendarDisplayRange defaults() {
        return new CalendarDisplayRange(DEFAULT_START_HOUR, DEFAULT_END_HOUR);
    }

    public static boolean isValid(int startHour, int endHour) {
        return startHour >= 0 && startHour <= 23
                && endHour >= 1 && endHour <= 24
                && endHour > startHour;
    }

    public int startMinute() {
        return startHour * 60;
    }

    public int endMinute() {
        return endHour * 60;
    }

    public int minutesShown() {
        return (endHour - startHour) * 60;
    }

    public int hourCount() {
        return endHour - startHour;
    }
}
