package com.cozyla.widgets.quote;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QuoteWidgetUpdateJobService extends JobService {
    private static final int JOB_ID = 2100;
    private static final String QUOTE_URL = "https://zenquotes.io/api/today";
    private static final String TAG = "QuoteWidgetUpdate";
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void schedule(Context context) {
        JobScheduler scheduler = context.getSystemService(JobScheduler.class);
        if (scheduler == null) {
            return;
        }
        JobInfo jobInfo = new JobInfo.Builder(
                JOB_ID,
                new ComponentName(context, QuoteWidgetUpdateJobService.class)
        )
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setMinimumLatency(1_000)
                .setOverrideDeadline(30_000)
                .build();
        try {
            scheduler.schedule(jobInfo);
        } catch (SecurityException error) {
            Log.w(TAG, "Unable to schedule quote update job", error);
        }
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        executor.execute(() -> {
            boolean retry = false;
            try {
                String quote = DailyQuote.parseZenQuotesToday(fetchQuoteJson());
                QuoteWidgetProvider.saveQuote(this, quote);
                QuoteWidgetProvider.refreshAllWidgets(this);
            } catch (Exception ignored) {
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

    private static String fetchQuoteJson() throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(QUOTE_URL).openConnection();
        connection.setConnectTimeout(8_000);
        connection.setReadTimeout(8_000);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                connection.getInputStream(),
                StandardCharsets.UTF_8
        ))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        } finally {
            connection.disconnect();
        }
    }
}
