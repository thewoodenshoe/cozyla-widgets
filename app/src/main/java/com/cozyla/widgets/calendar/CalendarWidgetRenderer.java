package com.cozyla.widgets.calendar;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;

import com.cozyla.widgets.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public final class CalendarWidgetRenderer {
    private static final int[] DAY_COLUMN_IDS = {
            R.id.calendar_day_1,
            R.id.calendar_day_2,
            R.id.calendar_day_3,
            R.id.calendar_day_4,
            R.id.calendar_day_5,
            R.id.calendar_day_6,
            R.id.calendar_day_7
    };

    private static final int[] DAY_LABEL_IDS = {
            R.id.calendar_day_label_1,
            R.id.calendar_day_label_2,
            R.id.calendar_day_label_3,
            R.id.calendar_day_label_4,
            R.id.calendar_day_label_5,
            R.id.calendar_day_label_6,
            R.id.calendar_day_label_7
    };

    private static final int[][] EVENT_VIEW_IDS = {
            {R.id.calendar_event_1_1, R.id.calendar_event_1_2, R.id.calendar_event_1_3, R.id.calendar_event_1_4},
            {R.id.calendar_event_2_1, R.id.calendar_event_2_2, R.id.calendar_event_2_3, R.id.calendar_event_2_4},
            {R.id.calendar_event_3_1, R.id.calendar_event_3_2, R.id.calendar_event_3_3, R.id.calendar_event_3_4},
            {R.id.calendar_event_4_1, R.id.calendar_event_4_2, R.id.calendar_event_4_3, R.id.calendar_event_4_4},
            {R.id.calendar_event_5_1, R.id.calendar_event_5_2, R.id.calendar_event_5_3, R.id.calendar_event_5_4},
            {R.id.calendar_event_6_1, R.id.calendar_event_6_2, R.id.calendar_event_6_3, R.id.calendar_event_6_4},
            {R.id.calendar_event_7_1, R.id.calendar_event_7_2, R.id.calendar_event_7_3, R.id.calendar_event_7_4}
    };

    private static final int[] SUMMARY_ALL_DAY_VIEW_IDS = {
            R.id.calendar_summary_all_day_1,
            R.id.calendar_summary_all_day_2,
            R.id.calendar_summary_all_day_3,
            R.id.calendar_summary_all_day_4,
            R.id.calendar_summary_all_day_5,
            R.id.calendar_summary_all_day_6,
            R.id.calendar_summary_all_day_7
    };

    private static final int[] TIMELINE_DAY_LABEL_IDS = {
            R.id.calendar_timeline_day_label_1,
            R.id.calendar_timeline_day_label_2,
            R.id.calendar_timeline_day_label_3,
            R.id.calendar_timeline_day_label_4,
            R.id.calendar_timeline_day_label_5,
            R.id.calendar_timeline_day_label_6,
            R.id.calendar_timeline_day_label_7
    };

    private static final int[] ALL_DAY_COLUMN_IDS = {
            R.id.calendar_all_day_1,
            R.id.calendar_all_day_2,
            R.id.calendar_all_day_3,
            R.id.calendar_all_day_4,
            R.id.calendar_all_day_5,
            R.id.calendar_all_day_6,
            R.id.calendar_all_day_7
    };

    private static final int[][] ALL_DAY_EVENT_VIEW_IDS = {
            {R.id.calendar_all_day_event_1_1, R.id.calendar_all_day_event_1_2},
            {R.id.calendar_all_day_event_2_1, R.id.calendar_all_day_event_2_2},
            {R.id.calendar_all_day_event_3_1, R.id.calendar_all_day_event_3_2},
            {R.id.calendar_all_day_event_4_1, R.id.calendar_all_day_event_4_2},
            {R.id.calendar_all_day_event_5_1, R.id.calendar_all_day_event_5_2},
            {R.id.calendar_all_day_event_6_1, R.id.calendar_all_day_event_6_2},
            {R.id.calendar_all_day_event_7_1, R.id.calendar_all_day_event_7_2}
    };

    private static final int[] TIMELINE_DAY_COLUMN_IDS = {
            R.id.calendar_timeline_day_1,
            R.id.calendar_timeline_day_2,
            R.id.calendar_timeline_day_3,
            R.id.calendar_timeline_day_4,
            R.id.calendar_timeline_day_5,
            R.id.calendar_timeline_day_6,
            R.id.calendar_timeline_day_7
    };

    private static final int[][] TIMELINE_EVENT_VIEW_IDS = {
            {R.id.calendar_timeline_event_1_1, R.id.calendar_timeline_event_1_2, R.id.calendar_timeline_event_1_3,
                    R.id.calendar_timeline_event_1_4, R.id.calendar_timeline_event_1_5, R.id.calendar_timeline_event_1_6,
                    R.id.calendar_timeline_event_1_7, R.id.calendar_timeline_event_1_8, R.id.calendar_timeline_event_1_9,
                    R.id.calendar_timeline_event_1_10},
            {R.id.calendar_timeline_event_2_1, R.id.calendar_timeline_event_2_2, R.id.calendar_timeline_event_2_3,
                    R.id.calendar_timeline_event_2_4, R.id.calendar_timeline_event_2_5, R.id.calendar_timeline_event_2_6,
                    R.id.calendar_timeline_event_2_7, R.id.calendar_timeline_event_2_8, R.id.calendar_timeline_event_2_9,
                    R.id.calendar_timeline_event_2_10},
            {R.id.calendar_timeline_event_3_1, R.id.calendar_timeline_event_3_2, R.id.calendar_timeline_event_3_3,
                    R.id.calendar_timeline_event_3_4, R.id.calendar_timeline_event_3_5, R.id.calendar_timeline_event_3_6,
                    R.id.calendar_timeline_event_3_7, R.id.calendar_timeline_event_3_8, R.id.calendar_timeline_event_3_9,
                    R.id.calendar_timeline_event_3_10},
            {R.id.calendar_timeline_event_4_1, R.id.calendar_timeline_event_4_2, R.id.calendar_timeline_event_4_3,
                    R.id.calendar_timeline_event_4_4, R.id.calendar_timeline_event_4_5, R.id.calendar_timeline_event_4_6,
                    R.id.calendar_timeline_event_4_7, R.id.calendar_timeline_event_4_8, R.id.calendar_timeline_event_4_9,
                    R.id.calendar_timeline_event_4_10},
            {R.id.calendar_timeline_event_5_1, R.id.calendar_timeline_event_5_2, R.id.calendar_timeline_event_5_3,
                    R.id.calendar_timeline_event_5_4, R.id.calendar_timeline_event_5_5, R.id.calendar_timeline_event_5_6,
                    R.id.calendar_timeline_event_5_7, R.id.calendar_timeline_event_5_8, R.id.calendar_timeline_event_5_9,
                    R.id.calendar_timeline_event_5_10},
            {R.id.calendar_timeline_event_6_1, R.id.calendar_timeline_event_6_2, R.id.calendar_timeline_event_6_3,
                    R.id.calendar_timeline_event_6_4, R.id.calendar_timeline_event_6_5, R.id.calendar_timeline_event_6_6,
                    R.id.calendar_timeline_event_6_7, R.id.calendar_timeline_event_6_8, R.id.calendar_timeline_event_6_9,
                    R.id.calendar_timeline_event_6_10},
            {R.id.calendar_timeline_event_7_1, R.id.calendar_timeline_event_7_2, R.id.calendar_timeline_event_7_3,
                    R.id.calendar_timeline_event_7_4, R.id.calendar_timeline_event_7_5, R.id.calendar_timeline_event_7_6,
                    R.id.calendar_timeline_event_7_7, R.id.calendar_timeline_event_7_8, R.id.calendar_timeline_event_7_9,
                    R.id.calendar_timeline_event_7_10}
    };

    private static final int[] NOW_LINE_IDS = {
            R.id.calendar_now_line_1,
            R.id.calendar_now_line_2,
            R.id.calendar_now_line_3,
            R.id.calendar_now_line_4,
            R.id.calendar_now_line_5,
            R.id.calendar_now_line_6,
            R.id.calendar_now_line_7
    };

    private static final int[] HOUR_LABEL_IDS = {
            R.id.calendar_hour_label_1, R.id.calendar_hour_label_2,
            R.id.calendar_hour_label_3, R.id.calendar_hour_label_4,
            R.id.calendar_hour_label_5, R.id.calendar_hour_label_6,
            R.id.calendar_hour_label_7, R.id.calendar_hour_label_8,
            R.id.calendar_hour_label_9, R.id.calendar_hour_label_10,
            R.id.calendar_hour_label_11, R.id.calendar_hour_label_12,
            R.id.calendar_hour_label_13, R.id.calendar_hour_label_14,
            R.id.calendar_hour_label_15, R.id.calendar_hour_label_16,
            R.id.calendar_hour_label_17, R.id.calendar_hour_label_18,
            R.id.calendar_hour_label_19, R.id.calendar_hour_label_20,
            R.id.calendar_hour_label_21, R.id.calendar_hour_label_22,
            R.id.calendar_hour_label_23, R.id.calendar_hour_label_24
    };

    private static final int[] HOUR_ROW_IDS = {
            R.id.calendar_hour_row_1, R.id.calendar_hour_row_2,
            R.id.calendar_hour_row_3, R.id.calendar_hour_row_4,
            R.id.calendar_hour_row_5, R.id.calendar_hour_row_6,
            R.id.calendar_hour_row_7, R.id.calendar_hour_row_8,
            R.id.calendar_hour_row_9, R.id.calendar_hour_row_10,
            R.id.calendar_hour_row_11, R.id.calendar_hour_row_12,
            R.id.calendar_hour_row_13, R.id.calendar_hour_row_14,
            R.id.calendar_hour_row_15, R.id.calendar_hour_row_16,
            R.id.calendar_hour_row_17, R.id.calendar_hour_row_18,
            R.id.calendar_hour_row_19, R.id.calendar_hour_row_20,
            R.id.calendar_hour_row_21, R.id.calendar_hour_row_22,
            R.id.calendar_hour_row_23, R.id.calendar_hour_row_24
    };

    private CalendarWidgetRenderer() {
    }

    public static RemoteViews loading(Context context, int appWidgetId) {
        return status(context, appWidgetId, context.getString(R.string.calendar_widget_loading), false);
    }

    public static RemoteViews permissionRequired(Context context, int appWidgetId) {
        return status(
                context,
                appWidgetId,
                context.getString(R.string.calendar_widget_permission_required),
                true
        );
    }

    public static RemoteViews updateFailed(Context context, int appWidgetId) {
        return status(
                context,
                appWidgetId,
                context.getString(R.string.calendar_widget_update_failed),
                false
        );
    }

    public static RemoteViews render(
            Context context,
            int appWidgetId,
            List<CalendarEvent> events,
            long nowMillis
    ) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        CalendarWidgetSizePolicy.Profile profile = CalendarWidgetSizePolicy.fromOptions(
                manager.getAppWidgetOptions(appWidgetId)
        );
        CalendarWidgetMode mode = CalendarWidgetPreferences.mode(context, appWidgetId);
        CalendarDisplayRange displayRange = CalendarWidgetPreferences.displayRange(
                context,
                appWidgetId
        );
        WeekWindow week = WeekWindow.containing(
                nowMillis,
                CalendarWidgetPreferences.weekOffset(context, appWidgetId),
                TimeZone.getDefault()
        );
        RemoteViews views = baseViews(context, appWidgetId, profile, week);
        views.setViewVisibility(R.id.calendar_status, View.GONE);
        boolean showTimeline = profile.timelineVisible && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S;
        views.setViewVisibility(R.id.calendar_day_grid, showTimeline ? View.GONE : View.VISIBLE);
        views.setViewVisibility(R.id.calendar_timeline, showTimeline ? View.VISIBLE : View.GONE);

        if (showTimeline) {
            applyTimeline(
                    context,
                    views,
                    mode,
                    week,
                    events,
                    nowMillis,
                    profile,
                    displayRange
            );
        } else {
            applySummary(context, views, mode, week, events, nowMillis, profile);
        }

        return views;
    }

    private static RemoteViews status(
            Context context,
            int appWidgetId,
            String message,
            boolean openConfiguration
    ) {
        CalendarWidgetSizePolicy.Profile profile = CalendarWidgetSizePolicy.fromOptions(
                AppWidgetManager.getInstance(context).getAppWidgetOptions(appWidgetId)
        );
        WeekWindow week = WeekWindow.containing(
                System.currentTimeMillis(),
                CalendarWidgetPreferences.weekOffset(context, appWidgetId),
                TimeZone.getDefault()
        );
        RemoteViews views = baseViews(context, appWidgetId, profile, week);
        views.setViewVisibility(R.id.calendar_day_grid, View.GONE);
        views.setViewVisibility(R.id.calendar_timeline, View.GONE);
        views.setViewVisibility(R.id.calendar_status, View.VISIBLE);
        views.setTextViewText(R.id.calendar_status, message);
        if (openConfiguration) {
            views.setOnClickPendingIntent(
                    R.id.calendar_widget_root,
                    configurationIntent(context, appWidgetId)
            );
        }
        return views;
    }

    private static RemoteViews baseViews(
            Context context,
            int appWidgetId,
            CalendarWidgetSizePolicy.Profile profile,
            WeekWindow week
    ) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_calendar);
        views.setViewVisibility(
                R.id.calendar_toolbar,
                profile.toolbarVisible ? View.VISIBLE : View.GONE
        );
        int secondaryVisibility = profile.secondaryActionsVisible ? View.VISIBLE : View.GONE;
        views.setViewVisibility(R.id.calendar_today, secondaryVisibility);
        views.setViewVisibility(R.id.calendar_refresh, secondaryVisibility);
        views.setViewVisibility(R.id.calendar_settings, secondaryVisibility);
        views.setTextViewText(R.id.calendar_week_label, formatWeekLabel(week));

        views.setOnClickPendingIntent(
                R.id.calendar_previous,
                broadcastIntent(context, appWidgetId, CalendarWidgetProvider.ACTION_PREVIOUS_WEEK, 1)
        );
        views.setOnClickPendingIntent(
                R.id.calendar_next,
                broadcastIntent(context, appWidgetId, CalendarWidgetProvider.ACTION_NEXT_WEEK, 2)
        );
        views.setOnClickPendingIntent(
                R.id.calendar_today,
                broadcastIntent(context, appWidgetId, CalendarWidgetProvider.ACTION_TODAY, 3)
        );
        views.setOnClickPendingIntent(
                R.id.calendar_refresh,
                broadcastIntent(context, appWidgetId, CalendarWidgetProvider.ACTION_REFRESH, 4)
        );
        views.setOnClickPendingIntent(
                R.id.calendar_settings,
                configurationIntent(context, appWidgetId)
        );
        views.setOnClickPendingIntent(
                R.id.calendar_widget_root,
                openCalendarIntent(context, appWidgetId, week.startMillis())
        );
        return views;
    }

    private static void applySummary(
            Context context,
            RemoteViews views,
            CalendarWidgetMode mode,
            WeekWindow week,
            List<CalendarEvent> events,
            long nowMillis,
            CalendarWidgetSizePolicy.Profile profile
    ) {
        for (int dayIndex = 0; dayIndex < DAY_COLUMN_IDS.length; dayIndex++) {
            boolean visibleDay = dayIndex < mode.dayCount();
            views.setViewVisibility(DAY_COLUMN_IDS[dayIndex], visibleDay ? View.VISIBLE : View.GONE);
            if (!visibleDay) {
                continue;
            }

            long dayStart = week.dayStartMillis(dayIndex);
            long dayEnd = week.dayStartMillis(dayIndex + 1);
            applyDayLabel(
                    context,
                    views,
                    DAY_LABEL_IDS[dayIndex],
                    dayStart,
                    nowMillis,
                    profile.compactDayLabels
            );
            List<CalendarEvent> dayEvents = eventsOnDay(
                    events,
                    dayStart,
                    dayEnd,
                    week.timeZone()
            );
            applySummaryAllDayEvent(
                    context,
                    views,
                    SUMMARY_ALL_DAY_VIEW_IDS[dayIndex],
                    dayEvents
            );
            applyEvents(
                    context,
                    views,
                    EVENT_VIEW_IDS[dayIndex],
                    timedEvents(dayEvents),
                    profile
            );
        }
    }

    private static void applyTimeline(
            Context context,
            RemoteViews views,
            CalendarWidgetMode mode,
            WeekWindow week,
            List<CalendarEvent> events,
            long nowMillis,
            CalendarWidgetSizePolicy.Profile profile,
            CalendarDisplayRange displayRange
    ) {
        applyHourLabels(context, views, week.timeZone(), displayRange);
        float dayWidthDp = profile.timelineDayWidthDp(mode.dayCount());

        for (int dayIndex = 0; dayIndex < TIMELINE_DAY_COLUMN_IDS.length; dayIndex++) {
            boolean visibleDay = dayIndex < mode.dayCount();
            int visibility = visibleDay ? View.VISIBLE : View.GONE;
            views.setViewVisibility(TIMELINE_DAY_LABEL_IDS[dayIndex], visibility);
            views.setViewVisibility(ALL_DAY_COLUMN_IDS[dayIndex], visibility);
            views.setViewVisibility(TIMELINE_DAY_COLUMN_IDS[dayIndex], visibility);
            if (!visibleDay) {
                continue;
            }

            long dayStart = week.dayStartMillis(dayIndex);
            long dayEnd = week.dayStartMillis(dayIndex + 1);
            List<CalendarEvent> dayEvents = eventsOnDay(
                    events,
                    dayStart,
                    dayEnd,
                    week.timeZone()
            );
            applyDayLabel(
                    context,
                    views,
                    TIMELINE_DAY_LABEL_IDS[dayIndex],
                    dayStart,
                    nowMillis,
                    false
            );
            applyAllDayEvents(
                    context,
                    views,
                    ALL_DAY_EVENT_VIEW_IDS[dayIndex],
                    dayEvents
            );
            applyTimedEvents(
                    context,
                    views,
                    TIMELINE_EVENT_VIEW_IDS[dayIndex],
                    CalendarTimelineLayout.position(
                            dayEvents,
                            dayStart,
                            dayEnd,
                            week.timeZone(),
                            displayRange
                    ),
                    profile.timelineHeightDp,
                    dayWidthDp,
                    displayRange
            );
            applyNowLine(
                    views,
                    NOW_LINE_IDS[dayIndex],
                    dayStart,
                    nowMillis,
                    week.timeZone(),
                    profile.timelineHeightDp,
                    displayRange
            );
        }
    }

    private static List<CalendarEvent> eventsOnDay(
            List<CalendarEvent> events,
            long dayStart,
            long dayEnd,
            TimeZone timeZone
    ) {
        List<CalendarEvent> dayEvents = new ArrayList<>();
        for (CalendarEvent event : events) {
            if (occursOnDay(event, dayStart, dayEnd, timeZone)) {
                dayEvents.add(event);
            }
        }
        return dayEvents;
    }

    private static List<CalendarEvent> timedEvents(List<CalendarEvent> events) {
        List<CalendarEvent> timedEvents = new ArrayList<>();
        for (CalendarEvent event : events) {
            if (!event.allDay) {
                timedEvents.add(event);
            }
        }
        return timedEvents;
    }

    private static void applySummaryAllDayEvent(
            Context context,
            RemoteViews views,
            int viewId,
            List<CalendarEvent> events
    ) {
        views.setViewVisibility(viewId, View.GONE);
        List<CalendarEvent> allDayEvents = new ArrayList<>();
        for (CalendarEvent event : events) {
            if (event.allDay) {
                allDayEvents.add(event);
            }
        }
        if (allDayEvents.isEmpty()) {
            return;
        }

        CalendarEvent first = allDayEvents.get(0);
        String text = first.title;
        if (allDayEvents.size() > 1) {
            text += " +" + (allDayEvents.size() - 1);
        }
        views.setViewVisibility(viewId, View.VISIBLE);
        views.setTextViewText(viewId, text);
        views.setTextColor(viewId, context.getColor(R.color.calendar_widget_event_text));
        views.setInt(viewId, "setBackgroundColor", pastelColor(first.color));
    }

    private static void applyAllDayEvents(
            Context context,
            RemoteViews views,
            int[] viewIds,
            List<CalendarEvent> events
    ) {
        for (int viewId : viewIds) {
            views.setViewVisibility(viewId, View.GONE);
        }

        List<CalendarEvent> allDayEvents = new ArrayList<>();
        for (CalendarEvent event : events) {
            if (event.allDay) {
                allDayEvents.add(event);
            }
        }
        if (allDayEvents.isEmpty()) {
            return;
        }

        int eventCount = allDayEvents.size() > viewIds.length
                ? viewIds.length - 1
                : allDayEvents.size();
        for (int index = 0; index < eventCount; index++) {
            CalendarEvent event = allDayEvents.get(index);
            int viewId = viewIds[index];
            views.setViewVisibility(viewId, View.VISIBLE);
            views.setTextViewText(viewId, event.title);
            views.setTextColor(viewId, context.getColor(R.color.calendar_widget_event_text));
            views.setInt(viewId, "setBackgroundColor", pastelColor(event.color));
        }

        if (allDayEvents.size() > eventCount) {
            int overflowViewId = viewIds[viewIds.length - 1];
            views.setViewVisibility(overflowViewId, View.VISIBLE);
            views.setTextViewText(overflowViewId, "+" + (allDayEvents.size() - eventCount) + " more");
            views.setTextColor(overflowViewId, context.getColor(R.color.calendar_widget_secondary));
            views.setInt(overflowViewId, "setBackgroundColor", Color.TRANSPARENT);
        }
    }

    @android.annotation.TargetApi(Build.VERSION_CODES.S)
    private static void applyTimedEvents(
            Context context,
            RemoteViews views,
            int[] viewIds,
            List<CalendarTimelineLayout.PositionedEvent> events,
            int timelineHeightDp,
            float dayWidthDp,
            CalendarDisplayRange displayRange
    ) {
        for (int viewId : viewIds) {
            views.setViewVisibility(viewId, View.GONE);
        }

        int renderedCount = Math.min(events.size(), viewIds.length);
        for (int index = 0; index < renderedCount; index++) {
            CalendarTimelineLayout.PositionedEvent positioned = events.get(index);
            int viewId = viewIds[index];
            float topDp = timelineHeightDp
                    * (positioned.startMinute - displayRange.startMinute())
                    / (float) displayRange.minutesShown();
            float naturalHeightDp = timelineHeightDp
                    * (positioned.endMinute - positioned.startMinute)
                    / (float) displayRange.minutesShown();
            float heightDp = Math.max(12f, naturalHeightDp);
            heightDp = Math.min(heightDp, timelineHeightDp - topDp);

            float laneWidthDp = Math.max(8f, (dayWidthDp - 2f) / positioned.laneCount);
            float leftDp = 1f + positioned.lane * laneWidthDp;
            float widthDp = Math.max(8f, laneWidthDp - 1f);

            views.setViewVisibility(viewId, View.VISIBLE);
            views.setViewLayoutMargin(
                    viewId,
                    RemoteViews.MARGIN_TOP,
                    topDp,
                    TypedValue.COMPLEX_UNIT_DIP
            );
            views.setViewLayoutMargin(
                    viewId,
                    RemoteViews.MARGIN_LEFT,
                    leftDp,
                    TypedValue.COMPLEX_UNIT_DIP
            );
            views.setViewLayoutHeight(viewId, heightDp, TypedValue.COMPLEX_UNIT_DIP);
            views.setViewLayoutWidth(viewId, widthDp, TypedValue.COMPLEX_UNIT_DIP);
            views.setTextViewTextSize(
                    viewId,
                    TypedValue.COMPLEX_UNIT_SP,
                    widthDp < 45f ? 7f : 8f
            );
            views.setTextColor(viewId, context.getColor(R.color.calendar_widget_event_text));
            views.setInt(viewId, "setBackgroundColor", pastelColor(positioned.event.color));

            if (index == viewIds.length - 1 && events.size() > viewIds.length) {
                views.setTextViewText(viewId, "+" + (events.size() - viewIds.length + 1) + " more");
            } else {
                views.setTextViewText(
                        viewId,
                        formatTimelineEvent(context, positioned.event, heightDp, widthDp)
                );
            }
        }
    }

    @android.annotation.TargetApi(Build.VERSION_CODES.S)
    private static void applyNowLine(
            RemoteViews views,
            int viewId,
            long dayStartMillis,
            long nowMillis,
            TimeZone timeZone,
            int timelineHeightDp,
            CalendarDisplayRange displayRange
    ) {
        views.setViewVisibility(viewId, View.GONE);
        if (!sameLocalDate(dayStartMillis, nowMillis, timeZone)) {
            return;
        }

        Calendar now = Calendar.getInstance(timeZone);
        now.setTimeInMillis(nowMillis);
        int minute = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE);
        if (minute < displayRange.startMinute() || minute >= displayRange.endMinute()) {
            return;
        }

        float topDp = timelineHeightDp
                * (minute - displayRange.startMinute())
                / (float) displayRange.minutesShown();
        views.setViewVisibility(viewId, View.VISIBLE);
        views.setViewLayoutMargin(
                viewId,
                RemoteViews.MARGIN_TOP,
                topDp,
                TypedValue.COMPLEX_UNIT_DIP
        );
    }

    private static void applyHourLabels(
            Context context,
            RemoteViews views,
            TimeZone timeZone,
            CalendarDisplayRange displayRange
    ) {
        boolean use24HourTime = android.text.format.DateFormat.is24HourFormat(context);
        SimpleDateFormat formatter = new SimpleDateFormat(use24HourTime ? "HH:mm" : "h a", Locale.getDefault());
        formatter.setTimeZone(timeZone);
        Calendar hour = Calendar.getInstance(timeZone);
        hour.clear();
        hour.set(2026, Calendar.JANUARY, 1, displayRange.startHour, 0, 0);
        for (int index = 0; index < HOUR_LABEL_IDS.length; index++) {
            boolean visible = index < displayRange.hourCount();
            int visibility = visible ? View.VISIBLE : View.GONE;
            views.setViewVisibility(HOUR_LABEL_IDS[index], visibility);
            views.setViewVisibility(HOUR_ROW_IDS[index], visibility);
            if (visible) {
                views.setTextViewText(HOUR_LABEL_IDS[index], formatter.format(hour.getTime()));
                hour.add(Calendar.HOUR_OF_DAY, 1);
            }
        }
    }

    private static String formatTimelineEvent(
            Context context,
            CalendarEvent event,
            float heightDp,
            float widthDp
    ) {
        if (heightDp < 28f || widthDp < 50f) {
            return event.title;
        }
        java.text.DateFormat formatter = android.text.format.DateFormat.getTimeFormat(context);
        return event.title
                + "\n"
                + formatter.format(new Date(event.beginMillis))
                + " - "
                + formatter.format(new Date(event.endMillis));
    }

    private static void applyDayLabel(
            Context context,
            RemoteViews views,
            int viewId,
            long dayMillis,
            long nowMillis,
            boolean compact
    ) {
        String pattern = compact ? "EEEEE\nd" : "EEE\nd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());
        formatter.setTimeZone(TimeZone.getDefault());
        views.setTextViewText(viewId, formatter.format(new Date(dayMillis)).toUpperCase(Locale.getDefault()));
        views.setTextViewTextSize(
                viewId,
                TypedValue.COMPLEX_UNIT_SP,
                compact ? 9f : 11f
        );

        if (sameLocalDate(dayMillis, nowMillis, TimeZone.getDefault())) {
            views.setInt(viewId, "setBackgroundResource", R.drawable.calendar_today_background);
            views.setTextColor(viewId, context.getColor(R.color.calendar_widget_today_text));
        } else {
            views.setInt(viewId, "setBackgroundColor", Color.TRANSPARENT);
            views.setTextColor(viewId, context.getColor(R.color.calendar_widget_secondary));
        }
    }

    private static void applyEvents(
            Context context,
            RemoteViews views,
            int[] eventViewIds,
            List<CalendarEvent> events,
            CalendarWidgetSizePolicy.Profile profile
    ) {
        for (int eventViewId : eventViewIds) {
            views.setViewVisibility(eventViewId, View.GONE);
        }

        int slotCount = Math.min(profile.maxEventsPerDay, eventViewIds.length);
        if (events.isEmpty()) {
            int emptyViewId = eventViewIds[0];
            views.setViewVisibility(emptyViewId, View.VISIBLE);
            views.setTextViewText(emptyViewId, context.getString(R.string.calendar_widget_no_events));
            views.setTextColor(emptyViewId, context.getColor(R.color.calendar_widget_empty));
            views.setInt(emptyViewId, "setBackgroundColor", Color.TRANSPARENT);
            return;
        }

        int renderedEvents = events.size() > slotCount && slotCount > 1
                ? slotCount - 1
                : Math.min(events.size(), slotCount);
        for (int index = 0; index < renderedEvents; index++) {
            CalendarEvent event = events.get(index);
            int eventViewId = eventViewIds[index];
            views.setViewVisibility(eventViewId, View.VISIBLE);
            views.setTextViewText(eventViewId, formatEvent(context, event, profile.showEventTimes));
            views.setTextViewTextSize(
                    eventViewId,
                    TypedValue.COMPLEX_UNIT_SP,
                    profile.compactDayLabels ? 8f : 10f
            );
            views.setTextColor(eventViewId, context.getColor(R.color.calendar_widget_event_text));
            views.setInt(eventViewId, "setBackgroundColor", pastelColor(event.color));
        }

        if (events.size() > renderedEvents) {
            if (slotCount == 1) {
                int overflow = events.size() - 1;
                if (overflow > 0) {
                    CalendarEvent event = events.get(0);
                    views.setTextViewText(
                            eventViewIds[0],
                            formatEvent(context, event, false) + " +" + overflow
                    );
                }
            } else {
                int overflowViewId = eventViewIds[slotCount - 1];
                views.setViewVisibility(overflowViewId, View.VISIBLE);
                views.setTextViewText(
                        overflowViewId,
                        "+" + (events.size() - renderedEvents) + " more"
                );
                views.setTextColor(overflowViewId, context.getColor(R.color.calendar_widget_secondary));
                views.setInt(overflowViewId, "setBackgroundColor", Color.TRANSPARENT);
            }
        }
    }

    private static String formatEvent(Context context, CalendarEvent event, boolean showTime) {
        if (event.allDay) {
            return showTime
                    ? context.getString(R.string.calendar_widget_all_day) + "\n" + event.title
                    : event.title;
        }
        if (!showTime) {
            return event.title;
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        timeFormat.setTimeZone(TimeZone.getDefault());
        return timeFormat.format(new Date(event.beginMillis)) + "\n" + event.title;
    }

    private static boolean occursOnDay(
            CalendarEvent event,
            long dayStart,
            long dayEnd,
            TimeZone displayTimeZone
    ) {
        if (!event.allDay) {
            return event.beginMillis < dayEnd && event.endMillis > dayStart;
        }

        Calendar displayDay = Calendar.getInstance(displayTimeZone);
        displayDay.setTimeInMillis(dayStart);
        Calendar utcDay = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        utcDay.clear();
        utcDay.set(
                displayDay.get(Calendar.YEAR),
                displayDay.get(Calendar.MONTH),
                displayDay.get(Calendar.DAY_OF_MONTH)
        );
        long allDayStart = utcDay.getTimeInMillis();
        utcDay.add(Calendar.DAY_OF_MONTH, 1);
        long allDayEnd = utcDay.getTimeInMillis();
        return event.beginMillis < allDayEnd && event.endMillis > allDayStart;
    }

    private static boolean sameLocalDate(long first, long second, TimeZone timeZone) {
        Calendar firstDate = Calendar.getInstance(timeZone);
        firstDate.setTimeInMillis(first);
        Calendar secondDate = Calendar.getInstance(timeZone);
        secondDate.setTimeInMillis(second);
        return firstDate.get(Calendar.ERA) == secondDate.get(Calendar.ERA)
                && firstDate.get(Calendar.YEAR) == secondDate.get(Calendar.YEAR)
                && firstDate.get(Calendar.DAY_OF_YEAR) == secondDate.get(Calendar.DAY_OF_YEAR);
    }

    private static String formatWeekLabel(WeekWindow week) {
        long start = week.startMillis();
        long end = week.dayStartMillis(6);
        Calendar startDate = Calendar.getInstance(week.timeZone());
        startDate.setTimeInMillis(start);
        Calendar endDate = Calendar.getInstance(week.timeZone());
        endDate.setTimeInMillis(end);
        boolean sameMonth = startDate.get(Calendar.YEAR) == endDate.get(Calendar.YEAR)
                && startDate.get(Calendar.MONTH) == endDate.get(Calendar.MONTH);
        SimpleDateFormat startFormat = new SimpleDateFormat("MMM d", Locale.getDefault());
        SimpleDateFormat endFormat = new SimpleDateFormat(
                sameMonth ? "d, yyyy" : "MMM d, yyyy",
                Locale.getDefault()
        );
        startFormat.setTimeZone(week.timeZone());
        endFormat.setTimeZone(week.timeZone());
        return startFormat.format(new Date(start)) + " - " + endFormat.format(new Date(end));
    }

    private static int pastelColor(int color) {
        float sourceWeight = 0.30f;
        int red = Math.round(Color.red(color) * sourceWeight + 255 * (1 - sourceWeight));
        int green = Math.round(Color.green(color) * sourceWeight + 255 * (1 - sourceWeight));
        int blue = Math.round(Color.blue(color) * sourceWeight + 255 * (1 - sourceWeight));
        return Color.rgb(red, green, blue);
    }

    private static PendingIntent broadcastIntent(
            Context context,
            int appWidgetId,
            String action,
            int actionCode
    ) {
        Intent intent = new Intent(context, CalendarWidgetProvider.class)
                .setAction(action)
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        return PendingIntent.getBroadcast(
                context,
                appWidgetId * 10 + actionCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    private static PendingIntent configurationIntent(Context context, int appWidgetId) {
        Intent intent = new Intent(context, CalendarWidgetConfigureActivity.class)
                .setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE)
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        return PendingIntent.getActivity(
                context,
                appWidgetId * 10 + 5,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    private static PendingIntent openCalendarIntent(Context context, int appWidgetId, long timeMillis) {
        Uri uri = CalendarContract.CONTENT_URI.buildUpon()
                .appendPath("time")
                .appendPath(String.valueOf(timeMillis))
                .build();
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        return PendingIntent.getActivity(
                context,
                appWidgetId * 10 + 6,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }
}
