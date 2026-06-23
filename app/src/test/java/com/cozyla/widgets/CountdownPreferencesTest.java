package com.cozyla.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.cozyla.widgets.countdown.CountdownPreferences;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class CountdownPreferencesTest {
    @Test
    public void timerStartsPausesAndFinishesInsideWidgetState() {
        Context context = ApplicationProvider.getApplicationContext();
        int widgetId = 991;

        CountdownPreferences.setDuration(context, widgetId, 70_000L);
        CountdownPreferences.start(context, widgetId, 1_000L);
        CountdownPreferences.State running = CountdownPreferences.state(context, widgetId, 11_000L);
        assertTrue(running.running);
        assertEquals(60_000L, running.remainingMillis);

        CountdownPreferences.pause(context, widgetId, 11_000L);
        CountdownPreferences.State paused = CountdownPreferences.state(context, widgetId, 20_000L);
        assertFalse(paused.running);
        assertEquals(60_000L, paused.remainingMillis);

        CountdownPreferences.finish(context, widgetId);
        CountdownPreferences.State done = CountdownPreferences.state(context, widgetId, 20_000L);
        assertTrue(done.done);
        assertEquals(0L, done.remainingMillis);
    }

    @Test
    public void durationIsClampedToKitchenTimerRange() {
        assertEquals(0L, CountdownPreferences.clampDuration(-1L));
        assertEquals(5_999_000L, CountdownPreferences.clampDuration(100_000_000L));
    }
}
