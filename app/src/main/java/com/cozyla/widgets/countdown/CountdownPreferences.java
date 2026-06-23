package com.cozyla.widgets.countdown;

import android.content.Context;

public final class CountdownPreferences {
    private static final String PREFERENCES = "countdown_widget_preferences";
    private static final String KEY_DURATION_PREFIX = "duration_";
    private static final String KEY_TARGET_PREFIX = "target_";
    private static final String KEY_RUNNING_PREFIX = "running_";
    private static final String KEY_DONE_PREFIX = "done_";
    private static final long DEFAULT_DURATION_MILLIS = 5L * 60L * 1000L;
    private static final long MAX_DURATION_MILLIS = 99L * 60L * 1000L + 59L * 1000L;

    private CountdownPreferences() {
    }

    public static State state(Context context, int appWidgetId, long nowMillis) {
        long storedDuration = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
                .getLong(KEY_DURATION_PREFIX + appWidgetId, DEFAULT_DURATION_MILLIS);
        boolean running = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
                .getBoolean(KEY_RUNNING_PREFIX + appWidgetId, false);
        boolean done = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
                .getBoolean(KEY_DONE_PREFIX + appWidgetId, false);
        long targetMillis = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
                .getLong(KEY_TARGET_PREFIX + appWidgetId, nowMillis + storedDuration);
        long remaining = running ? Math.max(0L, targetMillis - nowMillis) : clampDuration(storedDuration);
        if (running && remaining == 0L) {
            running = false;
            done = true;
        }
        return new State(clampDuration(remaining), targetMillis, running, done);
    }

    public static void setDuration(Context context, int appWidgetId, long durationMillis) {
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .putLong(KEY_DURATION_PREFIX + appWidgetId, clampDuration(durationMillis))
                .putBoolean(KEY_RUNNING_PREFIX + appWidgetId, false)
                .putBoolean(KEY_DONE_PREFIX + appWidgetId, false)
                .remove(KEY_TARGET_PREFIX + appWidgetId)
                .apply();
    }

    public static void start(Context context, int appWidgetId, long nowMillis) {
        State state = state(context, appWidgetId, nowMillis);
        long duration = state.remainingMillis <= 0L ? DEFAULT_DURATION_MILLIS : state.remainingMillis;
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .putLong(KEY_DURATION_PREFIX + appWidgetId, duration)
                .putLong(KEY_TARGET_PREFIX + appWidgetId, nowMillis + duration)
                .putBoolean(KEY_RUNNING_PREFIX + appWidgetId, true)
                .putBoolean(KEY_DONE_PREFIX + appWidgetId, false)
                .apply();
    }

    public static void pause(Context context, int appWidgetId, long nowMillis) {
        State state = state(context, appWidgetId, nowMillis);
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .putLong(KEY_DURATION_PREFIX + appWidgetId, state.remainingMillis)
                .putBoolean(KEY_RUNNING_PREFIX + appWidgetId, false)
                .putBoolean(KEY_DONE_PREFIX + appWidgetId, false)
                .remove(KEY_TARGET_PREFIX + appWidgetId)
                .apply();
    }

    public static void finish(Context context, int appWidgetId) {
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .putLong(KEY_DURATION_PREFIX + appWidgetId, 0L)
                .putBoolean(KEY_RUNNING_PREFIX + appWidgetId, false)
                .putBoolean(KEY_DONE_PREFIX + appWidgetId, true)
                .remove(KEY_TARGET_PREFIX + appWidgetId)
                .apply();
    }

    public static void reset(Context context, int appWidgetId) {
        setDuration(context, appWidgetId, DEFAULT_DURATION_MILLIS);
    }

    public static void delete(Context context, int appWidgetId) {
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .remove(KEY_DURATION_PREFIX + appWidgetId)
                .remove(KEY_TARGET_PREFIX + appWidgetId)
                .remove(KEY_RUNNING_PREFIX + appWidgetId)
                .remove(KEY_DONE_PREFIX + appWidgetId)
                .apply();
    }

    public static long clampDuration(long durationMillis) {
        if (durationMillis < 0L) {
            return 0L;
        }
        return Math.min(durationMillis, MAX_DURATION_MILLIS);
    }

    public static final class State {
        public final long remainingMillis;
        public final long targetMillis;
        public final boolean running;
        public final boolean done;

        State(long remainingMillis, long targetMillis, boolean running, boolean done) {
            this.remainingMillis = remainingMillis;
            this.targetMillis = targetMillis;
            this.running = running;
            this.done = done;
        }
    }
}
