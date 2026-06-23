package com.cozyla.widgets.weather;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class WeatherApiClientTest {
    @Test
    public void parsesTopLevelAndArrayNumbers() {
        String json = "{\"current\":{\"temperature_2m\":81.6,\"weather_code\":2,\"wind_speed_10m\":12.4},\"daily\":{\"temperature_2m_max\":[89.2],\"temperature_2m_min\":[73.9],\"uv_index_max\":[8.1]}}";

        assertEquals(81.6d, WeatherApiClient.numberAfter(json, "\"temperature_2m\""), 0.001d);
        assertEquals(89.2d, WeatherApiClient.firstArrayNumberAfter(json, "\"temperature_2m_max\""), 0.001d);
    }

    @Test
    public void parsesWeatherValuesWithoutReadingUnitLabels() throws Exception {
        String json = "{"
                + "\"current_units\":{\"temperature_2m\":\"°F\",\"weather_code\":\"wmo code\",\"wind_speed_10m\":\"mp/h\"},"
                + "\"current\":{\"temperature_2m\":81.6,\"weather_code\":2,\"wind_speed_10m\":12.4},"
                + "\"daily_units\":{\"temperature_2m_max\":\"°F\",\"uv_index_max\":\"\"},"
                + "\"daily\":{\"temperature_2m_max\":[89.2],\"temperature_2m_min\":[73.9],\"uv_index_max\":[8.1]}"
                + "}";

        WeatherApiClient.WeatherSnapshot snapshot = WeatherApiClient.parseWeather(json);

        assertEquals(81.6d, snapshot.temperatureF, 0.001d);
        assertEquals(2, snapshot.weatherCode);
        assertEquals(12.4d, snapshot.windMph, 0.001d);
        assertEquals(89.2d, snapshot.highF, 0.001d);
        assertEquals(73.9d, snapshot.lowF, 0.001d);
        assertEquals(8.1d, snapshot.uvIndex, 0.001d);
    }

    @Test
    public void parsesFirstCitySearchResult() throws Exception {
        String json = "{\"results\":[{\"name\":\"Charleston\",\"admin1\":\"South Carolina\",\"country_code\":\"US\",\"latitude\":32.7765,\"longitude\":-79.9311}]}";

        WeatherApiClient.LocationResult result = WeatherApiClient.parseFirstLocation(json);

        assertEquals("Charleston, South Carolina", result.displayName);
        assertEquals(32.7765d, result.latitude, 0.0001d);
        assertEquals(-79.9311d, result.longitude, 0.0001d);
    }

    @Test
    public void emptyCitySearchResultReturnsNull() throws Exception {
        assertNull(WeatherApiClient.parseFirstLocation("{\"results\":[]}"));
    }
}
