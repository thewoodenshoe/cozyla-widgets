package com.cozyla.widgets.countdown;

import android.content.Context;

public final class CountdownPreferences {
    private static final String PREFERENCES = "countdown_widget_preferences";
    private static final String KEY_LABEL_PREFIX = "label_";
    private static final String KEY_TARGET_PREFIX = "target_";

    private CountdownPreferences() {
    }

    public static void save(Context context, int appWidgetId, String label, long targetMillis) {
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_LABEL_PREFIX + appWidgetId, label)
                .putLong(KEY_TARGET_PREFIX + appWidgetId, targetMillis)
                .apply();
    }

    public static String label(Context context, int appWidgetId) {
        return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
                .getString(KEY_LABEL_PREFIX + appWidgetId, context.getString(com.cozyla.widgets.R.string.countdown_default_label));
    }

    public static long targetMillis(Context context, int appWidgetId) {
        return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
                .getLong(KEY_TARGET_PREFIX + appWidgetId, System.currentTimeMillis() + 25L * 60L * 1000L);
    }

    public static void delete(Context context, int appWidgetId) {
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .remove(KEY_LABEL_PREFIX + appWidgetId)
                .remove(KEY_TARGET_PREFIX + appWidgetId)
                .apply();
    }
}
