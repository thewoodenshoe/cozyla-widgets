package com.cozyla.widgets.chores;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class ChoreWheelPreferences {
    public static final int MAX_CHORES = 8;
    private static final String PREFS_NAME = "chore_wheel_widgets";
    private static final String KEY_CHORES_PREFIX = "chores_";
    private static final String KEY_SELECTED_PREFIX = "selected_";
    private static final List<String> DEFAULT_CHORES = Collections.unmodifiableList(Arrays.asList(
            "Dishes",
            "Laundry",
            "Trash",
            "Vacuum"
    ));

    private ChoreWheelPreferences() {
    }

    public static void save(Context context, int appWidgetId, List<String> chores) {
        List<String> sanitized = sanitize(chores);
        SharedPreferences.Editor editor = prefs(context).edit();
        editor.putString(KEY_CHORES_PREFIX + appWidgetId, toJson(sanitized));
        editor.putInt(KEY_SELECTED_PREFIX + appWidgetId, 0);
        editor.apply();
    }

    public static void saveSelectedIndex(Context context, int appWidgetId, int selectedIndex) {
        prefs(context).edit()
                .putInt(KEY_SELECTED_PREFIX + appWidgetId, selectedIndex)
                .apply();
    }

    public static List<String> chores(Context context, int appWidgetId) {
        String raw = prefs(context).getString(KEY_CHORES_PREFIX + appWidgetId, null);
        if (raw == null) {
            return DEFAULT_CHORES;
        }
        List<String> parsed = parseJson(raw);
        return parsed.isEmpty() ? DEFAULT_CHORES : parsed;
    }

    public static int selectedIndex(Context context, int appWidgetId) {
        List<String> chores = chores(context, appWidgetId);
        int stored = prefs(context).getInt(KEY_SELECTED_PREFIX + appWidgetId, 0);
        return Math.floorMod(stored, chores.size());
    }

    public static List<String> sanitize(List<String> input) {
        List<String> chores = new ArrayList<>();
        for (String chore : input) {
            if (chore == null) {
                continue;
            }
            String trimmed = chore.trim().replaceAll("\\s+", " ");
            if (!trimmed.isEmpty()) {
                chores.add(trimmed);
            }
            if (chores.size() == MAX_CHORES) {
                break;
            }
        }
        return chores;
    }

    public static void delete(Context context, int appWidgetId) {
        prefs(context).edit()
                .remove(KEY_CHORES_PREFIX + appWidgetId)
                .remove(KEY_SELECTED_PREFIX + appWidgetId)
                .apply();
    }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    private static String toJson(List<String> chores) {
        JSONArray array = new JSONArray();
        for (String chore : chores) {
            array.put(chore);
        }
        return array.toString();
    }

    private static List<String> parseJson(String raw) {
        List<String> chores = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(raw);
            for (int index = 0; index < array.length() && chores.size() < MAX_CHORES; index++) {
                chores.add(array.optString(index, ""));
            }
        } catch (JSONException ignored) {
            return Collections.emptyList();
        }
        return sanitize(chores);
    }
}
