package com.cozyla.widgets.weather;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextPaint;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.GraphicsMode;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class WeatherWidgetRendererTest {
    @Test
    public void rendersNonEmptyResizableBitmap() {
        Bitmap bitmap = WeatherWidgetRenderer.render(sampleData(), 960, 540);

        assertEquals(960, bitmap.getWidth());
        assertEquals(540, bitmap.getHeight());
        assertTrue(Color.alpha(bitmap.getPixel(80, 80)) > 0);
        assertTrue(Color.alpha(bitmap.getPixel(780, 132)) > 0);
        assertTrue(Color.alpha(bitmap.getPixel(300, 340)) > 0);
    }

    @Test
    public void fittedTextStaysWithinContainer() {
        TextPaint paint = new TextPaint();
        paint.setTextSize(34f);

        WeatherWidgetRenderer.fitTextSize("A very long coastal place name", paint, 160f, 12f);

        assertTrue(paint.measureText("A very long coastal place name") <= 160f || paint.getTextSize() == 12f);
    }

    private static WeatherData sampleData() {
        return new WeatherData(
                "Coastal Weather",
                "Partly cloudy",
                82,
                88,
                74,
                11,
                8.1d,
                1782216000000L,
                List.of(
                        new WeatherData.TideEvent("High", "9:42 AM"),
                        new WeatherData.TideEvent("Low", "3:58 PM"),
                        new WeatherData.TideEvent("High", "10:11 PM")
                )
        );
    }
}
