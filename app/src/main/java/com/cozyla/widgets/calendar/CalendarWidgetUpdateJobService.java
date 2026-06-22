package com.cozyla.widgets.calendar;

import android.Manifest;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.RemoteViews;

import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class CalendarWidgetUpdateJobService extends JobService {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public boolean onStartJob(JobParameters params) {
        int appWidgetId = params.getExtras().getInt(
                CalendarWidgetUpdateScheduler.EXTRA_WIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
        );
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            return false;
        }

        executor.execute(() -> {
            updateWidget(getApplicationContext(), appWidgetId);
            jobFinished(params, false);
        });
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    @Override
    public void onDestroy() {
        executor.shutdownNow();
        super.onDestroy();
    }

    private static void updateWidget(Context context, int appWidgetId) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        if (context.checkSelfPermission(Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            manager.updateAppWidget(
                    appWidgetId,
                    CalendarWidgetRenderer.permissionRequired(context, appWidgetId)
            );
            return;
        }

        try {
            CalendarWidgetMode mode = CalendarWidgetPreferences.mode(context, appWidgetId);
            WeekWindow week = WeekWindow.containing(
                    System.currentTimeMillis(),
                    CalendarWidgetPreferences.weekOffset(context, appWidgetId),
                    TimeZone.getDefault()
            );
            Set<Long> calendarIds = CalendarWidgetPreferences.calendarIds(context, appWidgetId);
            List<CalendarEvent> events = new CalendarRepository(context.getContentResolver()).loadEvents(
                    week.startMillis(),
                    week.endExclusiveMillis(mode.dayCount()),
                    calendarIds
            );
            RemoteViews views = CalendarWidgetRenderer.render(
                    context,
                    appWidgetId,
                    events,
                    System.currentTimeMillis()
            );
            manager.updateAppWidget(appWidgetId, views);
        } catch (SecurityException error) {
            manager.updateAppWidget(
                    appWidgetId,
                    CalendarWidgetRenderer.permissionRequired(context, appWidgetId)
            );
        } catch (RuntimeException error) {
            manager.updateAppWidget(
                    appWidgetId,
                    CalendarWidgetRenderer.updateFailed(context, appWidgetId)
            );
        }
    }
}
