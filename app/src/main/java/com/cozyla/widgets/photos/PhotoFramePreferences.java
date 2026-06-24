package com.cozyla.widgets.photos;

import android.content.Context;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public final class PhotoFramePreferences {
    private static final String PREFERENCES = "photo_frame_widget_preferences";
    private static final String KEY_URIS_PREFIX = "uris_";
    private static final String KEY_SLIDESHOW_PREFIX = "slideshow_";
    private static final String KEY_INTERVAL_PREFIX = "interval_";
    private static final String KEY_INDEX_PREFIX = "index_";
    public static final int DEFAULT_INTERVAL_MINUTES = 5;

    private PhotoFramePreferences() {
    }

    public static void save(Context context, int appWidgetId, List<Uri> uris, boolean slideshow, int intervalMinutes) {
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_URIS_PREFIX + appWidgetId, encode(uris))
                .putBoolean(KEY_SLIDESHOW_PREFIX + appWidgetId, slideshow)
                .putInt(KEY_INTERVAL_PREFIX + appWidgetId, clampInterval(intervalMinutes))
                .putInt(KEY_INDEX_PREFIX + appWidgetId, 0)
                .apply();
    }

    public static List<Uri> uris(Context context, int appWidgetId) {
        String encoded = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
                .getString(KEY_URIS_PREFIX + appWidgetId, "");
        List<Uri> uris = new ArrayList<>();
        if (encoded == null || encoded.isEmpty()) {
            return uris;
        }
        for (String item : encoded.split("\\n")) {
            if (!item.trim().isEmpty()) {
                uris.add(Uri.parse(item.trim()));
            }
        }
        return uris;
    }

    public static boolean slideshow(Context context, int appWidgetId) {
        return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
                .getBoolean(KEY_SLIDESHOW_PREFIX + appWidgetId, false);
    }

    public static int intervalMinutes(Context context, int appWidgetId) {
        return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
                .getInt(KEY_INTERVAL_PREFIX + appWidgetId, DEFAULT_INTERVAL_MINUTES);
    }

    public static int index(Context context, int appWidgetId, int size) {
        int stored = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
                .getInt(KEY_INDEX_PREFIX + appWidgetId, 0);
        return size <= 0 ? 0 : Math.floorMod(stored, size);
    }

    public static void advance(Context context, int appWidgetId, int size) {
        if (size <= 0) {
            return;
        }
        int next = index(context, appWidgetId, size) + 1;
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .putInt(KEY_INDEX_PREFIX + appWidgetId, next % size)
                .apply();
    }

    public static void delete(Context context, int appWidgetId) {
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .remove(KEY_URIS_PREFIX + appWidgetId)
                .remove(KEY_SLIDESHOW_PREFIX + appWidgetId)
                .remove(KEY_INTERVAL_PREFIX + appWidgetId)
                .remove(KEY_INDEX_PREFIX + appWidgetId)
                .apply();
    }

    public static int clampInterval(int intervalMinutes) {
        if (intervalMinutes < 1) {
            return 1;
        }
        return Math.min(intervalMinutes, 24 * 60);
    }

    private static String encode(List<Uri> uris) {
        StringBuilder builder = new StringBuilder();
        for (Uri uri : uris) {
            if (uri != null) {
                builder.append(uri).append('\n');
            }
        }
        return builder.toString();
    }
}
