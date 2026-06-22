package com.cozyla.widgets.calendar;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.provider.CalendarContract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public final class CalendarRepository {
    private static final String[] CALENDAR_PROJECTION = {
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.CALENDAR_COLOR
    };

    private static final String[] INSTANCE_PROJECTION = {
            CalendarContract.Instances.TITLE,
            CalendarContract.Instances.BEGIN,
            CalendarContract.Instances.END,
            CalendarContract.Instances.ALL_DAY,
            CalendarContract.Instances.EVENT_COLOR,
            CalendarContract.Instances.CALENDAR_COLOR,
            CalendarContract.Instances.CALENDAR_ID
    };

    private final ContentResolver contentResolver;

    public CalendarRepository(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public List<CalendarChoice> loadVisibleCalendars() {
        List<CalendarChoice> calendars = new ArrayList<>();
        String selection = CalendarContract.Calendars.VISIBLE + "=1";
        String sortOrder = CalendarContract.Calendars.ACCOUNT_NAME + ", "
                + CalendarContract.Calendars.CALENDAR_DISPLAY_NAME;

        try (Cursor cursor = contentResolver.query(
                CalendarContract.Calendars.CONTENT_URI,
                CALENDAR_PROJECTION,
                selection,
                null,
                sortOrder
        )) {
            if (cursor == null) {
                return calendars;
            }

            while (cursor.moveToNext()) {
                calendars.add(new CalendarChoice(
                        cursor.getLong(0),
                        safeText(cursor.getString(1), "Calendar"),
                        safeText(cursor.getString(2), "Android account"),
                        cursor.isNull(3) ? Color.GRAY : cursor.getInt(3)
                ));
            }
        }

        return calendars;
    }

    public List<CalendarEvent> loadEvents(
            long beginMillis,
            long endMillis,
            Set<Long> selectedCalendarIds
    ) {
        List<CalendarEvent> events = new ArrayList<>();
        if (selectedCalendarIds.isEmpty()) {
            return events;
        }

        try (Cursor cursor = CalendarContract.Instances.query(
                contentResolver,
                INSTANCE_PROJECTION,
                beginMillis,
                endMillis
        )) {
            if (cursor == null) {
                return events;
            }

            while (cursor.moveToNext()) {
                long calendarId = cursor.getLong(6);
                if (!selectedCalendarIds.contains(calendarId)) {
                    continue;
                }

                int eventColor = cursor.isNull(4) ? 0 : cursor.getInt(4);
                int calendarColor = cursor.isNull(5) ? Color.GRAY : cursor.getInt(5);
                events.add(new CalendarEvent(
                        safeText(cursor.getString(0), "Untitled event"),
                        cursor.getLong(1),
                        cursor.getLong(2),
                        cursor.getInt(3) != 0,
                        eventColor == 0 ? calendarColor : eventColor
                ));
            }
        }

        Collections.sort(events, new Comparator<CalendarEvent>() {
            @Override
            public int compare(CalendarEvent first, CalendarEvent second) {
                int timeComparison = Long.compare(first.beginMillis, second.beginMillis);
                if (timeComparison != 0) {
                    return timeComparison;
                }
                return String.CASE_INSENSITIVE_ORDER.compare(first.title, second.title);
            }
        });
        return events;
    }

    private static String safeText(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        return value.trim();
    }

    public static final class CalendarChoice {
        public final long id;
        public final String displayName;
        public final String accountName;
        public final int color;

        public CalendarChoice(long id, String displayName, String accountName, int color) {
            this.id = id;
            this.displayName = displayName;
            this.accountName = accountName;
            this.color = color;
        }

        public String label() {
            return displayName + " - " + accountName;
        }
    }
}
