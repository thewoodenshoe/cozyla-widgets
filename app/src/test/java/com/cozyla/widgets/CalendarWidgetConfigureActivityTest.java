package com.cozyla.widgets;

import static org.junit.Assert.assertEquals;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.Spinner;

import androidx.test.core.app.ApplicationProvider;

import com.cozyla.widgets.calendar.CalendarDisplayRange;
import com.cozyla.widgets.calendar.CalendarWidgetConfigureActivity;
import com.cozyla.widgets.calendar.CalendarWidgetMode;
import com.cozyla.widgets.calendar.CalendarWidgetPreferences;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.Collections;

@RunWith(RobolectricTestRunner.class)
public class CalendarWidgetConfigureActivityTest {
    private static final int WIDGET_ID = 9108;

    @Test
    public void restoresTimelineRangeIntoHourSelectors() {
        Context context = ApplicationProvider.getApplicationContext();
        CalendarWidgetPreferences.save(
                context,
                WIDGET_ID,
                CalendarWidgetMode.WEEK,
                Collections.singleton(42L),
                CalendarDisplayRange.of(8, 22)
        );
        Intent intent = new Intent(context, CalendarWidgetConfigureActivity.class)
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, WIDGET_ID);

        CalendarWidgetConfigureActivity activity = Robolectric.buildActivity(
                CalendarWidgetConfigureActivity.class,
                intent
        ).setup().get();

        Spinner start = activity.findViewById(R.id.calendar_start_time);
        Spinner end = activity.findViewById(R.id.calendar_end_time);
        assertEquals(24, start.getCount());
        assertEquals(24, end.getCount());
        assertEquals(8, start.getSelectedItemPosition());
        assertEquals(21, end.getSelectedItemPosition());
    }
}
