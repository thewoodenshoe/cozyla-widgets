package com.cozyla.widgets.weather;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class WeatherData {
    public final String place;
    public final String condition;
    public final int temperatureF;
    public final int highF;
    public final int lowF;
    public final int windMph;
    public final double uvIndex;
    public final long updatedAtMillis;
    public final List<TideEvent> tides;

    public WeatherData(
            String place,
            String condition,
            int temperatureF,
            int highF,
            int lowF,
            int windMph,
            double uvIndex,
            long updatedAtMillis,
            List<TideEvent> tides
    ) {
        this.place = place;
        this.condition = condition;
        this.temperatureF = temperatureF;
        this.highF = highF;
        this.lowF = lowF;
        this.windMph = windMph;
        this.uvIndex = uvIndex;
        this.updatedAtMillis = updatedAtMillis;
        this.tides = Collections.unmodifiableList(new ArrayList<>(tides));
    }

    public static WeatherData placeholder(String place) {
        return new WeatherData(
                place,
                "Tap to set weather",
                72,
                76,
                64,
                8,
                5.2d,
                System.currentTimeMillis(),
                List.of(
                        new TideEvent("High", "9:42 AM"),
                        new TideEvent("Low", "3:58 PM")
                )
        );
    }

    public static final class TideEvent {
        public final String type;
        public final String time;

        public TideEvent(String type, String time) {
            this.type = type;
            this.time = time;
        }
    }
}
