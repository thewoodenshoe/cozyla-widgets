package com.cozyla.widgets.countdown;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.RemoteViews;

import com.cozyla.widgets.R;

public class CountdownWidgetProvider extends AppWidgetProvider {
    public static final String ACTION_ADD_MINUTE = "com.cozyla.widgets.countdown.ADD_MINUTE";
    public static final String ACTION_SUBTRACT_MINUTE = "com.cozyla.widgets.countdown.SUBTRACT_MINUTE";
    public static final String ACTION_ADD_TEN_SECONDS = "com.cozyla.widgets.countdown.ADD_TEN_SECONDS";
    public static final String ACTION_SUBTRACT_TEN_SECONDS = "com.cozyla.widgets.countdown.SUBTRACT_TEN_SECONDS";
    public static final String ACTION_START_PAUSE = "com.cozyla.widgets.countdown.START_PAUSE";
    public static final String ACTION_RESET = "com.cozyla.widgets.countdown.RESET";
    public static final String ACTION_FINISH = "com.cozyla.widgets.countdown.FINISH";

    private static final long MINUTE = 60_000L;
    private static final long TEN_SECONDS = 10_000L;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        if (Intent.ACTION_MY_PACKAGE_REPLACED.equals(action)) {
            refreshAllWidgets(context);
            return;
        }

        int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            return;
        }

        long now = System.currentTimeMillis();
        CountdownPreferences.State state = CountdownPreferences.state(context, appWidgetId, now);
        if (ACTION_ADD_MINUTE.equals(action)) {
            adjustStoppedDuration(context, appWidgetId, state, MINUTE);
        } else if (ACTION_SUBTRACT_MINUTE.equals(action)) {
            adjustStoppedDuration(context, appWidgetId, state, -MINUTE);
        } else if (ACTION_ADD_TEN_SECONDS.equals(action)) {
            adjustStoppedDuration(context, appWidgetId, state, TEN_SECONDS);
        } else if (ACTION_SUBTRACT_TEN_SECONDS.equals(action)) {
            adjustStoppedDuration(context, appWidgetId, state, -TEN_SECONDS);
        } else if (ACTION_START_PAUSE.equals(action)) {
            if (state.running) {
                CountdownPreferences.pause(context, appWidgetId, now);
                cancelAlarm(context, appWidgetId);
            } else {
                CountdownPreferences.start(context, appWidgetId, now);
                scheduleFinishAlarm(context, appWidgetId, CountdownPreferences.state(context, appWidgetId, now).targetMillis);
            }
        } else if (ACTION_RESET.equals(action)) {
            CountdownPreferences.reset(context, appWidgetId);
            cancelAlarm(context, appWidgetId);
        } else if (ACTION_FINISH.equals(action)) {
            CountdownPreferences.finish(context, appWidgetId);
            cancelAlarm(context, appWidgetId);
            beep(context);
        }
        updateWidget(context, appWidgetId);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, buildViews(context, appWidgetId));
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            cancelAlarm(context, appWidgetId);
            CountdownPreferences.delete(context, appWidgetId);
        }
    }

    public static void updateWidget(Context context, int appWidgetId) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(appWidgetId, buildViews(context, appWidgetId));
    }

    public static void refreshAllWidgets(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName provider = new ComponentName(context, CountdownWidgetProvider.class);
        int[] widgetIds = manager.getAppWidgetIds(provider);
        for (int widgetId : widgetIds) {
            manager.updateAppWidget(widgetId, buildViews(context, widgetId));
        }
    }

    static RemoteViews buildViews(Context context, int appWidgetId) {
        long now = System.currentTimeMillis();
        CountdownPreferences.State state = CountdownPreferences.state(context, appWidgetId, now);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_countdown);

        boolean supportsCountdownChronometer = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
        long chronometerBase = SystemClock.elapsedRealtime() + state.remainingMillis;
        views.setChronometer(R.id.countdown_chronometer, chronometerBase, null, state.running);
        if (supportsCountdownChronometer) {
            views.setChronometerCountDown(R.id.countdown_chronometer, true);
        }
        views.setTextViewText(R.id.countdown_status, state.done
                ? context.getString(R.string.countdown_done)
                : context.getString(state.running ? R.string.countdown_running : R.string.countdown_ready));
        views.setTextViewText(R.id.countdown_start_pause, context.getString(state.running
                ? R.string.countdown_pause
                : R.string.countdown_start));
        views.setTextViewText(R.id.countdown_static_time, CountdownFormatter.display(state.remainingMillis));
        views.setViewVisibility(R.id.countdown_static_time, state.running && supportsCountdownChronometer ? android.view.View.GONE : android.view.View.VISIBLE);
        views.setViewVisibility(R.id.countdown_chronometer, state.running && supportsCountdownChronometer ? android.view.View.VISIBLE : android.view.View.GONE);

        bindAction(context, views, appWidgetId, R.id.countdown_add_minute, ACTION_ADD_MINUTE);
        bindAction(context, views, appWidgetId, R.id.countdown_subtract_minute, ACTION_SUBTRACT_MINUTE);
        bindAction(context, views, appWidgetId, R.id.countdown_add_ten_seconds, ACTION_ADD_TEN_SECONDS);
        bindAction(context, views, appWidgetId, R.id.countdown_subtract_ten_seconds, ACTION_SUBTRACT_TEN_SECONDS);
        bindAction(context, views, appWidgetId, R.id.countdown_start_pause, ACTION_START_PAUSE);
        bindAction(context, views, appWidgetId, R.id.countdown_reset, ACTION_RESET);
        return views;
    }

    private static void adjustStoppedDuration(
            Context context,
            int appWidgetId,
            CountdownPreferences.State state,
            long deltaMillis
    ) {
        if (!state.running) {
            CountdownPreferences.setDuration(context, appWidgetId, state.remainingMillis + deltaMillis);
        }
    }

    private static void bindAction(Context context, RemoteViews views, int appWidgetId, int viewId, String action) {
        Intent intent = new Intent(context, CountdownWidgetProvider.class)
                .setAction(action)
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode(appWidgetId, action),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        views.setOnClickPendingIntent(viewId, pendingIntent);
    }

    private static void scheduleFinishAlarm(Context context, int appWidgetId, long targetMillis) {
        AlarmManager alarmManager = context.getSystemService(AlarmManager.class);
        if (alarmManager == null) {
            return;
        }
        PendingIntent finishIntent = finishIntent(context, appWidgetId);
        alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(targetMillis, finishIntent), finishIntent);
    }

    private static void cancelAlarm(Context context, int appWidgetId) {
        AlarmManager alarmManager = context.getSystemService(AlarmManager.class);
        if (alarmManager != null) {
            alarmManager.cancel(finishIntent(context, appWidgetId));
        }
    }

    private static PendingIntent finishIntent(Context context, int appWidgetId) {
        Intent intent = new Intent(context, CountdownWidgetProvider.class)
                .setAction(ACTION_FINISH)
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        return PendingIntent.getBroadcast(
                context,
                requestCode(appWidgetId, ACTION_FINISH),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    private static int requestCode(int appWidgetId, String action) {
        return (appWidgetId * 31) + action.hashCode();
    }

    private static void beep(Context context) {
        ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 1_200);
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(toneGenerator::release, 1_400);
        Vibrator vibrator = context.getSystemService(Vibrator.class);
        if (vibrator != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(new long[]{0L, 160L, 100L, 220L}, -1));
        }
    }
}
