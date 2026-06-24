package com.cozyla.widgets.weather;

import android.content.Context;
import android.content.SharedPreferences;

public final class WeatherPreferences {
    public static final String DEFAULT_PLACE = "Charleston, SC";
    public static final String DEFAULT_LATITUDE = "32.7765";
    public static final String DEFAULT_LONGITUDE = "-79.9311";
    public static final String DEFAULT_TIDE_STATION = "8665530";

    private static final String PREFERENCES = "weather_widget_preferences";
    private static final String KEY_PLACE_PREFIX = "place_";
    private static final String KEY_LAT_PREFIX = "lat_";
    private static final String KEY_LON_PREFIX = "lon_";
    private static final String KEY_TIDE_STATION_PREFIX = "tide_station_";
    private static final String KEY_DATA_PREFIX = "data_";

    private WeatherPreferences() {
    }

    public static void saveConfig(
            Context context,
            int appWidgetId,
            String place,
            String latitude,
            String longitude,
            String tideStation
    ) {
        preferences(context).edit()
                .putString(KEY_PLACE_PREFIX + appWidgetId, clean(place))
                .putString(KEY_LAT_PREFIX + appWidgetId, clean(latitude))
                .putString(KEY_LON_PREFIX + appWidgetId, clean(longitude))
                .putString(KEY_TIDE_STATION_PREFIX + appWidgetId, clean(tideStation))
                .apply();
    }

    public static String place(Context context, int appWidgetId) {
        String value = preferences(context).getString(KEY_PLACE_PREFIX + appWidgetId, DEFAULT_PLACE);
        return value == null || value.trim().isEmpty() || "Weather".equalsIgnoreCase(value.trim())
                ? DEFAULT_PLACE
                : value.trim();
    }

    public static String latitude(Context context, int appWidgetId) {
        String value = preferences(context).getString(KEY_LAT_PREFIX + appWidgetId, DEFAULT_LATITUDE);
        return value == null || value.trim().isEmpty() ? DEFAULT_LATITUDE : value.trim();
    }

    public static String longitude(Context context, int appWidgetId) {
        String value = preferences(context).getString(KEY_LON_PREFIX + appWidgetId, DEFAULT_LONGITUDE);
        return value == null || value.trim().isEmpty() ? DEFAULT_LONGITUDE : value.trim();
    }

    public static String tideStation(Context context, int appWidgetId) {
        return preferences(context).getString(KEY_TIDE_STATION_PREFIX + appWidgetId, DEFAULT_TIDE_STATION);
    }

    public static void saveData(Context context, int appWidgetId, String encoded) {
        preferences(context).edit().putString(KEY_DATA_PREFIX + appWidgetId, encoded).apply();
    }

    public static String encodedData(Context context, int appWidgetId) {
        return preferences(context).getString(KEY_DATA_PREFIX + appWidgetId, "");
    }

    public static void delete(Context context, int appWidgetId) {
        preferences(context).edit()
                .remove(KEY_PLACE_PREFIX + appWidgetId)
                .remove(KEY_LAT_PREFIX + appWidgetId)
                .remove(KEY_LON_PREFIX + appWidgetId)
                .remove(KEY_TIDE_STATION_PREFIX + appWidgetId)
                .remove(KEY_DATA_PREFIX + appWidgetId)
                .apply();
    }

    private static SharedPreferences preferences(Context context) {
        return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }

    private static String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
