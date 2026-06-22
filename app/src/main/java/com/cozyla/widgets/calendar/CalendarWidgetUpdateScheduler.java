package com.cozyla.widgets.calendar;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.PersistableBundle;

public final class CalendarWidgetUpdateScheduler {
    static final String EXTRA_WIDGET_ID = "app_widget_id";
    private static final int JOB_ID_BASE = 20_000;

    private CalendarWidgetUpdateScheduler() {
    }

    public static void schedule(Context context, int appWidgetId) {
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            return;
        }

        PersistableBundle extras = new PersistableBundle();
        extras.putInt(EXTRA_WIDGET_ID, appWidgetId);
        JobInfo job = new JobInfo.Builder(jobId(appWidgetId), new ComponentName(
                context,
                CalendarWidgetUpdateJobService.class
        ))
                .setExtras(extras)
                .setOverrideDeadline(0)
                .build();
        JobScheduler scheduler = context.getSystemService(JobScheduler.class);
        if (scheduler != null) {
            scheduler.schedule(job);
        }
    }

    public static void cancel(Context context, int appWidgetId) {
        JobScheduler scheduler = context.getSystemService(JobScheduler.class);
        if (scheduler != null) {
            scheduler.cancel(jobId(appWidgetId));
        }
    }

    private static int jobId(int appWidgetId) {
        return JOB_ID_BASE + appWidgetId;
    }
}
