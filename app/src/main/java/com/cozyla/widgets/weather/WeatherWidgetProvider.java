package com.cozyla.widgets.weather;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.RemoteViews;

import com.cozyla.widgets.R;

public class WeatherWidgetProvider extends AppWidgetProvider {
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
            appWidgetManager.updateAppWidget(appWidgetId, buildViews(context, appWidgetManager, appWidgetId));
        }
        WeatherWidgetUpdateJobService.schedule(context);
    }

    @Override
    public void onAppWidgetOptionsChanged(
            Context context,
            AppWidgetManager appWidgetManager,
            int appWidgetId,
            Bundle newOptions
    ) {
        appWidgetManager.updateAppWidget(appWidgetId, buildViews(context, appWidgetManager, appWidgetId));
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            WeatherPreferences.delete(context, appWidgetId);
        }
    }

    public static void updateWidget(Context context, int appWidgetId) {
        updateWidget(context, appWidgetId, true);
    }

    static void updateWidget(Context context, int appWidgetId, boolean scheduleUpdate) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(appWidgetId, buildViews(context, manager, appWidgetId));
        if (scheduleUpdate) {
            WeatherWidgetUpdateJobService.schedule(context);
        }
    }

    public static void refreshAllWidgets(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName provider = new ComponentName(context, WeatherWidgetProvider.class);
        int[] widgetIds = manager.getAppWidgetIds(provider);
        for (int appWidgetId : widgetIds) {
            manager.updateAppWidget(appWidgetId, buildViews(context, manager, appWidgetId));
        }
        WeatherWidgetUpdateJobService.schedule(context);
    }

    static RemoteViews buildViews(Context context, AppWidgetManager manager, int appWidgetId) {
        String place = WeatherPreferences.place(context, appWidgetId);
        WeatherData data = WeatherFormatter.decode(WeatherPreferences.encodedData(context, appWidgetId), place);
        int[] size = bitmapSize(context, manager, appWidgetId);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_weather);
        views.setImageViewBitmap(R.id.weather_widget_image, WeatherWidgetRenderer.render(data, size[0], size[1]));
        Intent configureIntent = new Intent(context, WeatherConfigureActivity.class)
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent configurePendingIntent = PendingIntent.getActivity(
                context,
                41000 + appWidgetId,
                configureIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        views.setOnClickPendingIntent(R.id.weather_widget_root, configurePendingIntent);
        return views;
    }

    private static int[] bitmapSize(Context context, AppWidgetManager manager, int appWidgetId) {
        Bundle options = manager.getAppWidgetOptions(appWidgetId);
        int widthDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, 320);
        int heightDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, 180);
        int width = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                Math.max(220, widthDp),
                context.getResources().getDisplayMetrics()
        ));
        int height = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                Math.max(130, heightDp),
                context.getResources().getDisplayMetrics()
        ));
        return new int[]{Math.min(width, 1600), Math.min(height, 900)};
    }
}
