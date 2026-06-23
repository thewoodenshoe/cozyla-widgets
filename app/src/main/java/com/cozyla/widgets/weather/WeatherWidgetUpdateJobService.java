package com.cozyla.widgets.weather;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeatherWidgetUpdateJobService extends JobService {
    private static final int JOB_ID_NOW = 4100;
    private static final int JOB_ID_PERIODIC = 4101;
    private static final long PERIODIC_INTERVAL_MILLIS = 60L * 60L * 1000L;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void schedule(Context context) {
        JobScheduler scheduler = context.getSystemService(JobScheduler.class);
        if (scheduler == null) {
            return;
        }
        JobInfo jobInfo = new JobInfo.Builder(
                JOB_ID_NOW,
                new ComponentName(context, WeatherWidgetUpdateJobService.class)
        )
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setMinimumLatency(1_000L)
                .setOverrideDeadline(45_000L)
                .build();
        scheduler.schedule(jobInfo);
        JobInfo periodicJob = new JobInfo.Builder(
                JOB_ID_PERIODIC,
                new ComponentName(context, WeatherWidgetUpdateJobService.class)
        )
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(PERIODIC_INTERVAL_MILLIS)
                .build();
        scheduler.schedule(periodicJob);
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        executor.execute(() -> {
            boolean retry = false;
            try {
                updateConfiguredWidgets();
            } catch (Exception ex) {
                retry = true;
            }
            jobFinished(params, retry);
        });
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    private void updateConfiguredWidgets() {
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        int[] widgetIds = manager.getAppWidgetIds(new ComponentName(this, WeatherWidgetProvider.class));
        for (int appWidgetId : widgetIds) {
            String latitude = WeatherPreferences.latitude(this, appWidgetId);
            String longitude = WeatherPreferences.longitude(this, appWidgetId);
            if (latitude.isEmpty() || longitude.isEmpty()) {
                continue;
            }
            try {
                WeatherData data = WeatherApiClient.fetch(
                        WeatherPreferences.place(this, appWidgetId),
                        latitude,
                        longitude,
                        WeatherPreferences.tideStation(this, appWidgetId)
                );
                WeatherPreferences.saveData(this, appWidgetId, WeatherFormatter.encode(data));
                WeatherWidgetProvider.updateWidget(this, appWidgetId, false);
            } catch (Exception ignored) {
                WeatherWidgetProvider.updateWidget(this, appWidgetId, false);
            }
        }
    }
}
