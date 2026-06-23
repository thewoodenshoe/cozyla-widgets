package com.cozyla.widgets.weather;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class WeatherApiClient {
    private WeatherApiClient() {
    }

    public static WeatherData fetch(
            String place,
            String latitude,
            String longitude,
            String tideStation
    ) throws Exception {
        double lat = Double.parseDouble(latitude);
        double lon = Double.parseDouble(longitude);
        String weatherUrl = "https://api.open-meteo.com/v1/forecast"
                + "?latitude=" + lat
                + "&longitude=" + lon
                + "&current=temperature_2m,weather_code,wind_speed_10m"
                + "&daily=temperature_2m_max,temperature_2m_min,uv_index_max"
                + "&temperature_unit=fahrenheit"
                + "&wind_speed_unit=mph"
                + "&forecast_days=1"
                + "&timezone=auto";
        String weatherJson = fetchText(weatherUrl);
        int weatherCode = (int) Math.round(numberAfter(weatherJson, "\"weather_code\""));
        List<WeatherData.TideEvent> tides = tideStation == null || tideStation.trim().isEmpty()
                ? List.of(new WeatherData.TideEvent("Tide", "Set station"))
                : fetchTides(tideStation.trim());
        return new WeatherData(
                place == null || place.trim().isEmpty() ? "Weather" : place.trim(),
                WeatherFormatter.weatherCode(weatherCode),
                (int) Math.round(numberAfter(weatherJson, "\"temperature_2m\"")),
                (int) Math.round(firstArrayNumberAfter(weatherJson, "\"temperature_2m_max\"")),
                (int) Math.round(firstArrayNumberAfter(weatherJson, "\"temperature_2m_min\"")),
                (int) Math.round(numberAfter(weatherJson, "\"wind_speed_10m\"")),
                firstArrayNumberAfter(weatherJson, "\"uv_index_max\""),
                System.currentTimeMillis(),
                tides
        );
    }

    private static List<WeatherData.TideEvent> fetchTides(String station) throws Exception {
        String today = new SimpleDateFormat("yyyyMMdd", Locale.US).format(new Date());
        String url = "https://api.tidesandcurrents.noaa.gov/api/prod/datagetter"
                + "?product=predictions"
                + "&application=cozyla_widgets"
                + "&begin_date=" + today
                + "&range=48"
                + "&datum=MLLW"
                + "&station=" + URLEncoder.encode(station, StandardCharsets.UTF_8.name())
                + "&time_zone=lst_ldt"
                + "&units=english"
                + "&interval=hilo"
                + "&format=json";
        String json = fetchText(url);
        List<WeatherData.TideEvent> tides = new ArrayList<>();
        int searchIndex = 0;
        while (tides.size() < 3) {
            int timeIndex = json.indexOf("\"t\"", searchIndex);
            int typeIndex = json.indexOf("\"type\"", searchIndex);
            if (timeIndex < 0 || typeIndex < 0) {
                break;
            }
            String time = stringValueAfter(json, timeIndex);
            String type = stringValueAfter(json, typeIndex);
            if (!time.isEmpty() && !type.isEmpty()) {
                tides.add(new WeatherData.TideEvent("H".equalsIgnoreCase(type) ? "High" : "Low", WeatherFormatter.tideTime(time)));
            }
            searchIndex = Math.max(timeIndex, typeIndex) + 6;
        }
        return tides.isEmpty()
                ? List.of(new WeatherData.TideEvent("Tide", "Unavailable"))
                : tides;
    }

    private static String fetchText(String url) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setConnectTimeout(10_000);
        connection.setReadTimeout(10_000);
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

    static double numberAfter(String json, String key) {
        int keyIndex = json.indexOf(key);
        if (keyIndex < 0) {
            return 0d;
        }
        int colon = json.indexOf(':', keyIndex + key.length());
        return parseNumberAt(json, colon + 1);
    }

    static double firstArrayNumberAfter(String json, String key) {
        int keyIndex = json.indexOf(key);
        if (keyIndex < 0) {
            return 0d;
        }
        int bracket = json.indexOf('[', keyIndex + key.length());
        return parseNumberAt(json, bracket + 1);
    }

    private static double parseNumberAt(String json, int index) {
        while (index < json.length() && " \n\r\t".indexOf(json.charAt(index)) >= 0) {
            index++;
        }
        int end = index;
        while (end < json.length()) {
            char c = json.charAt(end);
            if ((c >= '0' && c <= '9') || c == '-' || c == '.') {
                end++;
            } else {
                break;
            }
        }
        if (end <= index) {
            return 0d;
        }
        return Double.parseDouble(json.substring(index, end));
    }

    private static String stringValueAfter(String json, int keyIndex) {
        int colon = json.indexOf(':', keyIndex);
        int start = json.indexOf('"', colon + 1);
        int end = json.indexOf('"', start + 1);
        if (start < 0 || end < 0) {
            return "";
        }
        return json.substring(start + 1, end);
    }
}
