package com.cozyla.widgets.calendar;

public final class CalendarEvent {
    public final String title;
    public final long beginMillis;
    public final long endMillis;
    public final boolean allDay;
    public final int color;

    public CalendarEvent(String title, long beginMillis, long endMillis, boolean allDay, int color) {
        this.title = title;
        this.beginMillis = beginMillis;
        this.endMillis = endMillis;
        this.allDay = allDay;
        this.color = color;
    }
}
