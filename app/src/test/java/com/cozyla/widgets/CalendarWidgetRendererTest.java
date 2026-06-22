package com.cozyla.widgets;

import static org.junit.Assert.assertEquals;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import com.cozyla.widgets.calendar.CalendarEvent;
import com.cozyla.widgets.calendar.CalendarDisplayRange;
import com.cozyla.widgets.calendar.CalendarWidgetMode;
import com.cozyla.widgets.calendar.CalendarWidgetPreferences;
import com.cozyla.widgets.calendar.CalendarWidgetProvider;
import com.cozyla.widgets.calendar.CalendarWidgetRenderer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;

import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.TimeZone;

@RunWith(RobolectricTestRunner.class)
public class CalendarWidgetRendererTest {
    private static final int WIDGET_ID = 7204;
    private static final int COMPACT_WIDGET_ID = 7205;

    @Test
    public void largeWorkweekAppliesTimelinePositionsAndCompactAllDayStrip() {
        Context context = ApplicationProvider.getApplicationContext();
        long mondayMorning = new GregorianCalendar(
                2026,
                java.util.Calendar.JUNE,
                22,
                9,
                0
        ).getTimeInMillis();
        CalendarWidgetPreferences.save(
                context,
                WIDGET_ID,
                CalendarWidgetMode.WORKWEEK,
                Collections.singleton(12L),
                CalendarDisplayRange.of(8, 22)
        );
        Bundle options = new Bundle();
        options.putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 1000);
        options.putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 500);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        Shadows.shadowOf(manager).bindAppWidgetId(
                WIDGET_ID,
                new ComponentName(context, CalendarWidgetProvider.class)
        );
        manager.updateAppWidgetOptions(WIDGET_ID, options);

        RemoteViews views = CalendarWidgetRenderer.render(
                context,
                WIDGET_ID,
                Arrays.asList(
                        new CalendarEvent(
                                "Planning",
                                mondayMorning,
                                mondayMorning + 3_600_000,
                                false,
                                Color.rgb(20, 108, 90)
                        ),
                        new CalendarEvent(
                                "Overlap",
                                mondayMorning + 1_800_000,
                                mondayMorning + 5_400_000,
                                false,
                                Color.rgb(217, 119, 6)
                        ),
                        new CalendarEvent(
                                "Launch day",
                                utcMidnight(2026, java.util.Calendar.JUNE, 23),
                                utcMidnight(2026, java.util.Calendar.JUNE, 24),
                                true,
                                Color.rgb(37, 99, 235)
                        )
                ),
                mondayMorning
        );
        View root = views.apply(context, new FrameLayout(context));

        assertEquals(View.GONE, root.findViewById(R.id.calendar_day_grid).getVisibility());
        assertEquals(View.VISIBLE, root.findViewById(R.id.calendar_timeline).getVisibility());
        assertEquals(View.VISIBLE, root.findViewById(R.id.calendar_timeline_day_5).getVisibility());
        assertEquals(View.GONE, root.findViewById(R.id.calendar_timeline_day_6).getVisibility());
        assertEquals(View.GONE, root.findViewById(R.id.calendar_timeline_day_7).getVisibility());
        assertEquals(View.GONE, root.findViewById(R.id.calendar_status).getVisibility());

        TextView timedEvent = root.findViewById(R.id.calendar_timeline_event_1_1);
        FrameLayout.LayoutParams timedLayout = (FrameLayout.LayoutParams) timedEvent.getLayoutParams();
        assertEquals(View.VISIBLE, timedEvent.getVisibility());
        org.junit.Assert.assertTrue(timedLayout.topMargin > 0);
        org.junit.Assert.assertTrue(timedLayout.height > 0);
        TextView overlappingEvent = root.findViewById(R.id.calendar_timeline_event_1_2);
        FrameLayout.LayoutParams overlappingLayout =
                (FrameLayout.LayoutParams) overlappingEvent.getLayoutParams();
        assertEquals(View.VISIBLE, overlappingEvent.getVisibility());
        assertEquals(timedLayout.width, overlappingLayout.width);
        org.junit.Assert.assertTrue(overlappingLayout.leftMargin > timedLayout.leftMargin);

        TextView allDayEvent = root.findViewById(R.id.calendar_all_day_event_2_1);
        assertEquals(View.VISIBLE, allDayEvent.getVisibility());
        assertEquals("Launch day", allDayEvent.getText().toString());
        org.junit.Assert.assertTrue(allDayEvent.getLayoutParams().height < timedLayout.topMargin);
        org.junit.Assert.assertTrue(
                root.<TextView>findViewById(R.id.calendar_hour_label_1).getText().length() > 0
        );
        assertEquals(View.VISIBLE, root.findViewById(R.id.calendar_hour_label_14).getVisibility());
        assertEquals(View.GONE, root.findViewById(R.id.calendar_hour_label_15).getVisibility());
        assertEquals(View.VISIBLE, root.findViewById(R.id.calendar_hour_row_14).getVisibility());
        assertEquals(View.GONE, root.findViewById(R.id.calendar_hour_row_15).getVisibility());
        org.junit.Assert.assertTrue(timedLayout.height > 20);
    }

    @Test
    public void compactWeekKeepsAllDayEventInFixedStripAboveTimedSummary() {
        Context context = ApplicationProvider.getApplicationContext();
        long mondayMorning = new GregorianCalendar(
                2026,
                java.util.Calendar.JUNE,
                22,
                9,
                0
        ).getTimeInMillis();
        CalendarWidgetPreferences.save(
                context,
                COMPACT_WIDGET_ID,
                CalendarWidgetMode.WEEK,
                Collections.singleton(12L)
        );
        Bundle options = new Bundle();
        options.putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 300);
        options.putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 120);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        Shadows.shadowOf(manager).bindAppWidgetId(
                COMPACT_WIDGET_ID,
                new ComponentName(context, CalendarWidgetProvider.class)
        );
        manager.updateAppWidgetOptions(COMPACT_WIDGET_ID, options);

        RemoteViews views = CalendarWidgetRenderer.render(
                context,
                COMPACT_WIDGET_ID,
                Arrays.asList(
                        new CalendarEvent(
                                "All day first",
                                utcMidnight(2026, java.util.Calendar.JUNE, 22),
                                utcMidnight(2026, java.util.Calendar.JUNE, 23),
                                true,
                                Color.BLUE
                        ),
                        new CalendarEvent(
                                "Timed event",
                                mondayMorning,
                                mondayMorning + 3_600_000,
                                false,
                                Color.GREEN
                        )
                ),
                mondayMorning
        );
        View root = views.apply(context, new FrameLayout(context));

        assertEquals(View.VISIBLE, root.findViewById(R.id.calendar_day_grid).getVisibility());
        assertEquals(View.GONE, root.findViewById(R.id.calendar_timeline).getVisibility());
        TextView allDay = root.findViewById(R.id.calendar_summary_all_day_1);
        assertEquals(View.VISIBLE, allDay.getVisibility());
        assertEquals("All day first", allDay.getText().toString());
        assertEquals("Timed event", root.<TextView>findViewById(R.id.calendar_event_1_1).getText().toString());
        org.junit.Assert.assertTrue(allDay.getLayoutParams().height > 0);
    }

    private static long utcMidnight(int year, int month, int day) {
        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        calendar.clear();
        calendar.set(year, month, day, 0, 0, 0);
        return calendar.getTimeInMillis();
    }
}
