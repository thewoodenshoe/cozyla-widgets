package com.cozyla.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.cozyla.widgets.weather.WeatherData;
import com.cozyla.widgets.weather.WeatherFormatter;

import org.junit.Test;

import java.util.List;

public class WeatherFormatterTest {
    @Test
    public void uvStrengthBucketsAreStable() {
        assertEquals("Low", WeatherFormatter.uvStrength(1.9d));
        assertEquals("Moderate", WeatherFormatter.uvStrength(4.2d));
        assertEquals("High", WeatherFormatter.uvStrength(6.5d));
        assertEquals("Very high", WeatherFormatter.uvStrength(9.5d));
        assertEquals("Extreme", WeatherFormatter.uvStrength(11.2d));
    }

    @Test
    public void moonPhaseReturnsNormalizedValue() {
        double phase = WeatherFormatter.moonPhase(1782216000000L);

        assertTrue(phase >= 0d);
        assertTrue(phase < 1d);
    }

    @Test
    public void weatherDataRoundTripsThroughPreferencesEncoding() {
        WeatherData data = new WeatherData(
                "Beach",
                "Clear",
                82,
                88,
                74,
                11,
                7.2d,
                1782216000000L,
                List.of(new WeatherData.TideEvent("High", "9:42 AM"))
        );

        WeatherData decoded = WeatherFormatter.decode(WeatherFormatter.encode(data), "Fallback");

        assertEquals("Beach", decoded.place);
        assertEquals("High", decoded.tides.get(0).type);
        assertEquals("9:42 AM", decoded.tides.get(0).time);
    }

    @Test
    public void decodeUsesFallbackPlaceForOldPlaceholderData() {
        WeatherData oldData = new WeatherData(
                "Weather",
                "Clear",
                80,
                88,
                72,
                6,
                4.2d,
                1782216000000L,
                List.of(new WeatherData.TideEvent("High", "9:42 AM"))
        );

        WeatherData decoded = WeatherFormatter.decode(
                WeatherFormatter.encode(oldData),
                "Charleston, SC"
        );

        assertEquals("Charleston, SC", decoded.place);
    }
}
