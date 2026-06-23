package com.cozyla.widgets;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

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
    private static final String TAG = "CozylaWidgetRefresh";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)
                || Intent.ACTION_MY_PACKAGE_REPLACED.equals(action)
                || Intent.ACTION_USER_UNLOCKED.equals(action)
                || ACTION_REFRESH_ALL.equals(action)) {
            refreshAllWidgets(context);
        } else if (Intent.ACTION_PACKAGE_REPLACED.equals(action) && isThisPackage(context, intent)) {
            refreshAllWidgets(context);
        }
    }

    public static void refreshAllWidgets(Context context) {
        Log.i(TAG, "Refreshing all Cozyla widgets");
        refreshWidget("clock", () -> ClockWidgetProvider.refreshAllWidgets(context));
        refreshWidget("calendar", () -> CalendarWidgetProvider.refreshAllWidgets(context));
        refreshWidget("quote", () -> QuoteWidgetProvider.refreshAllWidgets(context));
        refreshWidget("quote update", () -> QuoteWidgetUpdateJobService.schedule(context));
        refreshWidget("chore wheel", () -> ChoreWheelProvider.refreshAllWidgets(context));
        refreshWidget("countdown", () -> CountdownWidgetProvider.refreshAllWidgets(context));
        refreshWidget("weather", () -> WeatherWidgetProvider.refreshAllWidgets(context));
        refreshWidget("photo frame", () -> PhotoFrameWidgetProvider.refreshAllWidgets(context));
    }

    private static boolean isThisPackage(Context context, Intent intent) {
        Uri data = intent.getData();
        return data != null && context.getPackageName().equals(data.getSchemeSpecificPart());
    }

    private static void refreshWidget(String name, Runnable action) {
        try {
            action.run();
        } catch (RuntimeException error) {
            Log.e(TAG, "Widget refresh failed: " + name, error);
        }
    }
}
