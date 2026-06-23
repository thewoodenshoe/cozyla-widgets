package com.cozyla.widgets.weather;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public final class WeatherFormatter {
    private WeatherFormatter() {
    }

    public static String uvStrength(double uvIndex) {
        if (uvIndex < 3d) {
            return "Low";
        }
        if (uvIndex < 6d) {
            return "Moderate";
        }
        if (uvIndex < 8d) {
            return "High";
        }
        if (uvIndex < 11d) {
            return "Very high";
        }
        return "Extreme";
    }

    public static double moonPhase(long millis) {
        double daysSinceNewMoon = (millis - 947182440000L) / 86_400_000d;
        return positiveModulo(daysSinceNewMoon, 29.530588853d) / 29.530588853d;
    }

    public static String moonLabel(double phase) {
        double p = positiveModulo(phase, 1d);
        if (p < 0.03d || p > 0.97d) return "New moon";
        if (p < 0.22d) return "Waxing crescent";
        if (p < 0.28d) return "First quarter";
        if (p < 0.47d) return "Waxing gibbous";
        if (p < 0.53d) return "Full moon";
        if (p < 0.72d) return "Waning gibbous";
        if (p < 0.78d) return "Last quarter";
        return "Waning crescent";
    }

    public static String weatherCode(int code) {
        return switch (code) {
            case 0 -> "Clear";
            case 1, 2 -> "Partly cloudy";
            case 3 -> "Cloudy";
            case 45, 48 -> "Fog";
            case 51, 53, 55, 56, 57 -> "Drizzle";
            case 61, 63, 65, 66, 67 -> "Rain";
            case 71, 73, 75, 77 -> "Snow";
            case 80, 81, 82 -> "Showers";
            case 95, 96, 99 -> "Storms";
            default -> "Weather";
        };
    }

    public static String encode(WeatherData data) {
        StringBuilder builder = new StringBuilder();
        append(builder, data.place);
        append(builder, data.condition);
        append(builder, Integer.toString(data.temperatureF));
        append(builder, Integer.toString(data.highF));
        append(builder, Integer.toString(data.lowF));
        append(builder, Integer.toString(data.windMph));
        append(builder, Double.toString(data.uvIndex));
        append(builder, Long.toString(data.updatedAtMillis));
        append(builder, Integer.toString(data.tides.size()));
        for (WeatherData.TideEvent tide : data.tides) {
            append(builder, tide.type);
            append(builder, tide.time);
        }
        return builder.toString();
    }

    public static WeatherData decode(String encoded, String fallbackPlace) {
        if (encoded == null || encoded.isEmpty()) {
            return WeatherData.placeholder(fallbackPlace);
        }
        List<String> parts = split(encoded);
        try {
            int tideCount = Integer.parseInt(parts.get(8));
            List<WeatherData.TideEvent> tides = new ArrayList<>();
            int index = 9;
            for (int tideIndex = 0; tideIndex < tideCount && index + 1 < parts.size(); tideIndex++) {
                tides.add(new WeatherData.TideEvent(parts.get(index), parts.get(index + 1)));
                index += 2;
            }
            String place = parts.get(0);
            return new WeatherData(
                    place == null || place.trim().isEmpty() || "Weather".equalsIgnoreCase(place.trim())
                            ? fallbackPlace
                            : place,
                    parts.get(1),
                    Integer.parseInt(parts.get(2)),
                    Integer.parseInt(parts.get(3)),
                    Integer.parseInt(parts.get(4)),
                    Integer.parseInt(parts.get(5)),
                    Double.parseDouble(parts.get(6)),
                    Long.parseLong(parts.get(7)),
                    tides
            );
        } catch (RuntimeException ex) {
            return WeatherData.placeholder(fallbackPlace);
        }
    }

    public static String tideTime(String noaaTime) {
        try {
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
            parser.setTimeZone(TimeZone.getDefault());
            Date date = parser.parse(noaaTime);
            SimpleDateFormat display = new SimpleDateFormat("h:mm a", Locale.US);
            return date == null ? "--" : display.format(date);
        } catch (Exception ex) {
            return "--";
        }
    }

    private static void append(StringBuilder builder, String value) {
        builder.append(value == null ? "" : value.replace("\\", "\\\\").replace("|", "\\p"))
                .append('|');
    }

    private static List<String> split(String encoded) {
        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean escaped = false;
        for (int index = 0; index < encoded.length(); index++) {
            char c = encoded.charAt(index);
            if (escaped) {
                current.append(c == 'p' ? '|' : c);
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
            } else if (c == '|') {
                parts.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        if (current.length() > 0) {
            parts.add(current.toString());
        }
        return parts;
    }

    private static double positiveModulo(double value, double modulo) {
        return ((value % modulo) + modulo) % modulo;
    }
}
