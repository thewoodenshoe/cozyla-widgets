package com.cozyla.widgets.countdown;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import com.cozyla.widgets.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35)
public class CountdownWidgetProviderTest {
    @Test
    public void runningTimerUsesCountdownChronometerForLocalSecondTicks() {
        Context context = ApplicationProvider.getApplicationContext();
        int widgetId = 8801;
        CountdownPreferences.delete(context, widgetId);
        CountdownPreferences.setDuration(context, widgetId, 125_000L);
        CountdownPreferences.start(context, widgetId, System.currentTimeMillis());

        RemoteViews views = CountdownWidgetProvider.buildViews(context, widgetId);
        View root = views.apply(context, new FrameLayout(context));

        assertEquals(View.GONE, root.findViewById(R.id.countdown_static_time).getVisibility());
        Chronometer chronometer = root.findViewById(R.id.countdown_chronometer);
        assertEquals(View.VISIBLE, chronometer.getVisibility());
        assertTrue(chronometer.isCountDown());
        assertTrue(chronometer.getBase() > SystemClock.elapsedRealtime());
    }

    @Test
    public void stoppedTimerKeepsStaticTimeVisible() {
        Context context = ApplicationProvider.getApplicationContext();
        int widgetId = 8802;
        CountdownPreferences.delete(context, widgetId);
        CountdownPreferences.setDuration(context, widgetId, 70_000L);

        RemoteViews views = CountdownWidgetProvider.buildViews(context, widgetId);
        View root = views.apply(context, new FrameLayout(context));

        assertEquals(View.GONE, root.findViewById(R.id.countdown_chronometer).getVisibility());
        TextView staticTime = root.findViewById(R.id.countdown_static_time);
        assertEquals(View.VISIBLE, staticTime.getVisibility());
        assertEquals("1:10", staticTime.getText().toString());
    }
}
