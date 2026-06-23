package com.cozyla.widgets.countdown;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.cozyla.widgets.R;

public class CountdownWidgetProvider extends AppWidgetProvider {
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (Intent.ACTION_MY_PACKAGE_REPLACED.equals(intent.getAction())) {
            refreshAllWidgets(context);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, buildViews(context, appWidgetId));
        }
        CountdownWidgetUpdateJobService.schedule(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            CountdownPreferences.delete(context, appWidgetId);
        }
    }

    public static void updateWidget(Context context, int appWidgetId) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(appWidgetId, buildViews(context, appWidgetId));
        CountdownWidgetUpdateJobService.schedule(context);
    }

    public static void refreshAllWidgets(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName provider = new ComponentName(context, CountdownWidgetProvider.class);
        int[] widgetIds = manager.getAppWidgetIds(provider);
        for (int appWidgetId : widgetIds) {
            manager.updateAppWidget(appWidgetId, buildViews(context, appWidgetId));
        }
        CountdownWidgetUpdateJobService.schedule(context);
    }

    static RemoteViews buildViews(Context context, int appWidgetId) {
        String label = CountdownPreferences.label(context, appWidgetId);
        long targetMillis = CountdownPreferences.targetMillis(context, appWidgetId);
        CountdownFormatter.Parts parts = CountdownFormatter.parts(System.currentTimeMillis(), targetMillis);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_countdown);
        views.setTextViewText(R.id.countdown_title, label);
        views.setTextViewText(R.id.countdown_days_value, parts.done ? "0" : Long.toString(parts.days));
        views.setTextViewText(R.id.countdown_hours_value, parts.done ? "0" : twoDigits(parts.hours));
        views.setTextViewText(R.id.countdown_minutes_value, parts.done ? "0" : twoDigits(parts.minutes));
        views.setTextViewText(R.id.countdown_status, parts.done
                ? context.getString(R.string.countdown_done)
                : context.getString(R.string.countdown_widget_title));

        Intent configureIntent = new Intent(context, CountdownConfigureActivity.class)
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent configurePendingIntent = PendingIntent.getActivity(
                context,
                30000 + appWidgetId,
                configureIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        views.setOnClickPendingIntent(R.id.countdown_root, configurePendingIntent);
        return views;
    }

    private static String twoDigits(long value) {
        return value < 10L ? "0" + value : Long.toString(value);
    }
}
