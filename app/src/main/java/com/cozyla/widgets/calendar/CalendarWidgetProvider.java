package com.cozyla.widgets.calendar;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public final class CalendarWidgetProvider extends AppWidgetProvider {
    public static final String ACTION_PREVIOUS_WEEK = "com.cozyla.widgets.calendar.PREVIOUS_WEEK";
    public static final String ACTION_NEXT_WEEK = "com.cozyla.widgets.calendar.NEXT_WEEK";
    public static final String ACTION_TODAY = "com.cozyla.widgets.calendar.TODAY";
    public static final String ACTION_REFRESH = "com.cozyla.widgets.calendar.REFRESH";

    @Override
    public void onUpdate(Context context, AppWidgetManager manager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            manager.updateAppWidget(appWidgetId, CalendarWidgetRenderer.loading(context, appWidgetId));
            CalendarWidgetUpdateScheduler.schedule(context, appWidgetId);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(
            Context context,
            AppWidgetManager manager,
            int appWidgetId,
            Bundle newOptions
    ) {
        manager.updateAppWidget(appWidgetId, CalendarWidgetRenderer.loading(context, appWidgetId));
        CalendarWidgetUpdateScheduler.schedule(context, appWidgetId);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            CalendarWidgetUpdateScheduler.cancel(context, appWidgetId);
            CalendarWidgetPreferences.delete(context, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ACTION_PREVIOUS_WEEK.equals(action)
                || ACTION_NEXT_WEEK.equals(action)
                || ACTION_TODAY.equals(action)
                || ACTION_REFRESH.equals(action)) {
            int appWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID
            );
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                if (ACTION_PREVIOUS_WEEK.equals(action)) {
                    CalendarWidgetPreferences.changeWeekOffset(context, appWidgetId, -1);
                } else if (ACTION_NEXT_WEEK.equals(action)) {
                    CalendarWidgetPreferences.changeWeekOffset(context, appWidgetId, 1);
                } else if (ACTION_TODAY.equals(action)) {
                    CalendarWidgetPreferences.resetWeekOffset(context, appWidgetId);
                }

                AppWidgetManager.getInstance(context).updateAppWidget(
                        appWidgetId,
                        CalendarWidgetRenderer.loading(context, appWidgetId)
                );
                CalendarWidgetUpdateScheduler.schedule(context, appWidgetId);
            }
            return;
        }

        super.onReceive(context, intent);
    }
}
