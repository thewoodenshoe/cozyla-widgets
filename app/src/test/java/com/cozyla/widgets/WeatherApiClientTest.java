package com.cozyla.widgets.weather;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class WeatherApiClientTest {
    @Test
    public void parsesTopLevelAndArrayNumbers() {
        String json = "{\"current\":{\"temperature_2m\":81.6,\"weather_code\":2,\"wind_speed_10m\":12.4},\"daily\":{\"temperature_2m_max\":[89.2],\"temperature_2m_min\":[73.9],\"uv_index_max\":[8.1]}}";

        assertEquals(81.6d, WeatherApiClient.numberAfter(json, "\"temperature_2m\""), 0.001d);
        assertEquals(89.2d, WeatherApiClient.firstArrayNumberAfter(json, "\"temperature_2m_max\""), 0.001d);
    }
}
