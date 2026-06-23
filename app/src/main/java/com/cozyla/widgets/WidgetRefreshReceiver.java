package com.cozyla.widgets;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cozyla.widgets.calendar.CalendarWidgetProvider;
import com.cozyla.widgets.chores.ChoreWheelProvider;
import com.cozyla.widgets.clock.ClockWidgetProvider;
import com.cozyla.widgets.countdown.CountdownWidgetProvider;
import com.cozyla.widgets.photos.PhotoFrameWidgetProvider;
import com.cozyla.widgets.quote.QuoteWidgetProvider;
import com.cozyla.widgets.quote.QuoteWidgetUpdateJobService;
import com.cozyla.widgets.weather.WeatherWidgetProvider;

public final class WidgetRefreshReceiver extends BroadcastReceiver {
    public static final String ACTION_REFRESH_ALL = "com.cozyla.widgets.REFRESH_ALL_WIDGETS";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)
                || Intent.ACTION_MY_PACKAGE_REPLACED.equals(action)
                || Intent.ACTION_USER_UNLOCKED.equals(action)
                || ACTION_REFRESH_ALL.equals(action)) {
            refreshAllWidgets(context);
        }
    }

    public static void refreshAllWidgets(Context context) {
        ClockWidgetProvider.refreshAllWidgets(context);
        CalendarWidgetProvider.refreshAllWidgets(context);
        QuoteWidgetProvider.refreshAllWidgets(context);
        QuoteWidgetUpdateJobService.schedule(context);
        ChoreWheelProvider.refreshAllWidgets(context);
        CountdownWidgetProvider.refreshAllWidgets(context);
        WeatherWidgetProvider.refreshAllWidgets(context);
        PhotoFrameWidgetProvider.refreshAllWidgets(context);
    }
}
