package com.cozyla.widgets.countdown;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;

public class CountdownWidgetUpdateJobService extends JobService {
    private static final int JOB_ID = 3100;

    public static void schedule(Context context) {
        JobScheduler scheduler = context.getSystemService(JobScheduler.class);
        if (scheduler == null) {
            return;
        }
        JobInfo jobInfo = new JobInfo.Builder(
                JOB_ID,
                new ComponentName(context, CountdownWidgetUpdateJobService.class)
        )
                .setMinimumLatency(60_000L)
                .setOverrideDeadline(90_000L)
                .build();
        scheduler.schedule(jobInfo);
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        CountdownWidgetProvider.refreshAllWidgets(this);
        jobFinished(params, false);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
