package com.cozyla.widgets.calendar;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class CalendarWidgetPreferences {
    private static final String PREFERENCES = "calendar_widget_preferences";
    private static final String MODE_PREFIX = "mode_";
    private static final String CALENDARS_PREFIX = "calendars_";
    private static final String WEEK_OFFSET_PREFIX = "week_offset_";
    private static final String START_HOUR_PREFIX = "start_hour_";
    private static final String END_HOUR_PREFIX = "end_hour_";
    private static final String CONFIGURED_PREFIX = "configured_";

    private CalendarWidgetPreferences() {
    }

    public static void save(
            Context context,
            int appWidgetId,
            CalendarWidgetMode mode,
            Set<Long> calendarIds
    ) {
        save(context, appWidgetId, mode, calendarIds, CalendarDisplayRange.defaults());
    }

    public static void save(
            Context context,
            int appWidgetId,
            CalendarWidgetMode mode,
            Set<Long> calendarIds,
            CalendarDisplayRange displayRange
    ) {
        Set<String> storedIds = new HashSet<>();
        for (Long calendarId : calendarIds) {
            storedIds.add(String.valueOf(calendarId));
        }

        preferences(context).edit()
                .putString(MODE_PREFIX + appWidgetId, mode.storedValue())
                .putStringSet(CALENDARS_PREFIX + appWidgetId, storedIds)
                .putInt(START_HOUR_PREFIX + appWidgetId, displayRange.startHour)
                .putInt(END_HOUR_PREFIX + appWidgetId, displayRange.endHour)
                .putBoolean(CONFIGURED_PREFIX + appWidgetId, true)
                .apply();
    }

    public static CalendarWidgetMode mode(Context context, int appWidgetId) {
        String stored = preferences(context).getString(MODE_PREFIX + appWidgetId, null);
        return CalendarWidgetMode.fromStoredValue(stored);
    }

    public static Set<Long> calendarIds(Context context, int appWidgetId) {
        Set<String> stored = preferences(context).getStringSet(
                CALENDARS_PREFIX + appWidgetId,
                Collections.emptySet()
        );
        Set<Long> result = new HashSet<>();
        if (stored == null) {
            return result;
        }

        for (String value : stored) {
            try {
                result.add(Long.parseLong(value));
            } catch (NumberFormatException ignored) {
                // Ignore a corrupt preference instead of breaking every widget update.
            }
        }
        return result;
    }

    public static boolean isConfigured(Context context, int appWidgetId) {
        return preferences(context).getBoolean(CONFIGURED_PREFIX + appWidgetId, false);
    }

    public static int weekOffset(Context context, int appWidgetId) {
        return preferences(context).getInt(WEEK_OFFSET_PREFIX + appWidgetId, 0);
    }

    public static CalendarDisplayRange displayRange(Context context, int appWidgetId) {
        SharedPreferences preferences = preferences(context);
        return CalendarDisplayRange.fromStored(
                preferences.getInt(
                        START_HOUR_PREFIX + appWidgetId,
                        CalendarDisplayRange.DEFAULT_START_HOUR
                ),
                preferences.getInt(
                        END_HOUR_PREFIX + appWidgetId,
                        CalendarDisplayRange.DEFAULT_END_HOUR
                )
        );
    }

    public static void changeWeekOffset(Context context, int appWidgetId, int delta) {
        int nextOffset = Math.max(-52, Math.min(52, weekOffset(context, appWidgetId) + delta));
        preferences(context).edit().putInt(WEEK_OFFSET_PREFIX + appWidgetId, nextOffset).apply();
    }

    public static void resetWeekOffset(Context context, int appWidgetId) {
        preferences(context).edit().putInt(WEEK_OFFSET_PREFIX + appWidgetId, 0).apply();
    }

    public static void delete(Context context, int appWidgetId) {
        preferences(context).edit()
                .remove(MODE_PREFIX + appWidgetId)
                .remove(CALENDARS_PREFIX + appWidgetId)
                .remove(WEEK_OFFSET_PREFIX + appWidgetId)
                .remove(START_HOUR_PREFIX + appWidgetId)
                .remove(END_HOUR_PREFIX + appWidgetId)
                .remove(CONFIGURED_PREFIX + appWidgetId)
                .apply();
    }

    private static SharedPreferences preferences(Context context) {
        return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }
}
