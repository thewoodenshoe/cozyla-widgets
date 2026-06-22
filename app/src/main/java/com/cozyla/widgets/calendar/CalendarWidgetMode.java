package com.cozyla.widgets.calendar;

public enum CalendarWidgetMode {
    WEEK("week", 7),
    WORKWEEK("workweek", 5);

    private final String storedValue;
    private final int dayCount;

    CalendarWidgetMode(String storedValue, int dayCount) {
        this.storedValue = storedValue;
        this.dayCount = dayCount;
    }

    public String storedValue() {
        return storedValue;
    }

    public int dayCount() {
        return dayCount;
    }

    public static CalendarWidgetMode fromStoredValue(String value) {
        for (CalendarWidgetMode mode : values()) {
            if (mode.storedValue.equals(value)) {
                return mode;
            }
        }
        return WEEK;
    }
}
