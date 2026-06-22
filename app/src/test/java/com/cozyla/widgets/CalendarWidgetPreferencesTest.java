package com.cozyla.widgets;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.cozyla.widgets.calendar.CalendarDisplayRange;
import com.cozyla.widgets.calendar.CalendarWidgetMode;
import com.cozyla.widgets.calendar.CalendarWidgetPreferences;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Collections;

@RunWith(RobolectricTestRunner.class)
public class CalendarWidgetPreferencesTest {
    private static final int WIDGET_ID = 9107;

    @Test
    public void savesAndDeletesDisplayRangePerWidget() {
        Context context = ApplicationProvider.getApplicationContext();
        CalendarWidgetPreferences.save(
                context,
                WIDGET_ID,
                CalendarWidgetMode.WEEK,
                Collections.singleton(42L),
                CalendarDisplayRange.of(8, 22)
        );

        CalendarDisplayRange saved = CalendarWidgetPreferences.displayRange(context, WIDGET_ID);
        assertEquals(8, saved.startHour);
        assertEquals(22, saved.endHour);

        CalendarWidgetPreferences.delete(context, WIDGET_ID);
        CalendarDisplayRange deleted = CalendarWidgetPreferences.displayRange(context, WIDGET_ID);
        assertEquals(6, deleted.startHour);
        assertEquals(24, deleted.endHour);
    }
}
