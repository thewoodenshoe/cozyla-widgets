package com.cozyla.widgets.weather;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.text.TextPaint;

import java.util.List;
import java.util.Locale;

public final class WeatherWidgetRenderer {
    private static final int DEFAULT_WIDTH = 960;
    private static final int DEFAULT_HEIGHT = 540;
    private static final int NAVY = Color.rgb(11, 24, 43);
    private static final int INK = Color.rgb(6, 22, 35);
    private static final int WHITE = Color.WHITE;
    private static final int MUTED = Color.rgb(216, 232, 245);
    private static final int CYAN = Color.rgb(39, 211, 199);
    private static final int AMBER = Color.rgb(255, 190, 77);

    private WeatherWidgetRenderer() {
    }

    public static Bitmap render(WeatherData data) {
        return render(data, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public static Bitmap render(WeatherData data, int width, int height) {
        int safeWidth = Math.max(420, width);
        int safeHeight = Math.max(240, height);
        Bitmap bitmap = Bitmap.createBitmap(safeWidth, safeHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas, data);
        return bitmap;
    }

    private static void draw(Canvas canvas, WeatherData data) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        float scale = Math.min(width / 960f, height / 540f);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setShader(new LinearGradient(
                0,
                0,
                width,
                height,
                new int[]{Color.rgb(17, 42, 75), Color.rgb(37, 104, 124), Color.rgb(22, 34, 67)},
                new float[]{0f, 0.52f, 1f},
                Shader.TileMode.CLAMP
        ));
        canvas.drawRoundRect(new RectF(0, 0, width, height), 28f * scale, 28f * scale, paint);
        paint.setShader(null);

        drawAtmosphere(canvas, paint, scale);
        drawHeader(canvas, data, scale);
        drawTemperature(canvas, data, scale);
        drawMoon(canvas, data, scale);
        drawUv(canvas, data, scale);
        drawTides(canvas, data.tides, scale);
    }

    private static void drawAtmosphere(Canvas canvas, Paint paint, float scale) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        paint.setShader(new RadialGradient(
                width * 0.18f,
                height * 0.12f,
                width * 0.42f,
                Color.argb(160, 255, 213, 114),
                Color.TRANSPARENT,
                Shader.TileMode.CLAMP
        ));
        canvas.drawCircle(width * 0.18f, height * 0.12f, width * 0.42f, paint);
        paint.setShader(null);

        paint.setColor(Color.argb(42, 255, 255, 255));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2f * scale);
        for (int i = 0; i < 4; i++) {
            float y = height * (0.58f + i * 0.09f);
            Path wave = new Path();
            wave.moveTo(width * 0.02f, y);
            wave.cubicTo(width * 0.24f, y - 26f * scale, width * 0.42f, y + 22f * scale, width * 0.62f, y);
            wave.cubicTo(width * 0.78f, y - 18f * scale, width * 0.9f, y + 18f * scale, width * 0.98f, y);
            canvas.drawPath(wave, paint);
        }
        paint.setStyle(Paint.Style.FILL);
    }

    private static void drawHeader(Canvas canvas, WeatherData data, float scale) {
        TextPaint text = textPaint(WHITE, 28f * scale, true);
        drawFittedText(canvas, data.place, 36f * scale, 50f * scale, 480f * scale, text, 18f * scale);
        text.setTextSize(18f * scale);
        text.setFakeBoldText(false);
        text.setColor(MUTED);
        drawFittedText(canvas, data.condition + "  •  Wind " + data.windMph + " mph", 36f * scale, 80f * scale, 520f * scale, text, 12f * scale);
    }

    private static void drawTemperature(Canvas canvas, WeatherData data, float scale) {
        TextPaint temp = textPaint(WHITE, 92f * scale, true);
        canvas.drawText(data.temperatureF + "°", 36f * scale, 184f * scale, temp);
        TextPaint small = textPaint(MUTED, 22f * scale, true);
        canvas.drawText("H " + data.highF + "°  L " + data.lowF + "°", 42f * scale, 224f * scale, small);
    }

    private static void drawMoon(Canvas canvas, WeatherData data, float scale) {
        float centerX = 780f * scale;
        float centerY = 132f * scale;
        float radius = 58f * scale;
        double phase = WeatherFormatter.moonPhase(data.updatedAtMillis);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.argb(80, 255, 255, 255));
        canvas.drawCircle(centerX, centerY, radius + 17f * scale, paint);
        paint.setShader(new RadialGradient(
                centerX - radius * 0.32f,
                centerY - radius * 0.28f,
                radius * 1.25f,
                Color.rgb(255, 246, 196),
                Color.rgb(181, 197, 211),
                Shader.TileMode.CLAMP
        ));
        canvas.drawCircle(centerX, centerY, radius, paint);
        paint.setShader(null);

        float illumination = (float) ((1d - Math.cos(phase * Math.PI * 2d)) / 2d);
        float shadowOffset = (illumination - 0.5f) * radius * 2f;
        paint.setColor(Color.argb(185, 10, 20, 35));
        canvas.drawOval(new RectF(
                centerX - radius + shadowOffset,
                centerY - radius,
                centerX + radius + shadowOffset,
                centerY + radius
        ), paint);

        TextPaint text = textPaint(WHITE, 19f * scale, true);
        text.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(WeatherFormatter.moonLabel(phase), centerX, 224f * scale, text);
    }

    private static void drawUv(Canvas canvas, WeatherData data, float scale) {
        RectF card = new RectF(36f * scale, 270f * scale, 470f * scale, 392f * scale);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.argb(52, 255, 255, 255));
        canvas.drawRoundRect(card, 18f * scale, 18f * scale, paint);

        TextPaint title = textPaint(MUTED, 18f * scale, true);
        canvas.drawText("UV strength", card.left + 22f * scale, card.top + 35f * scale, title);

        TextPaint value = textPaint(WHITE, 34f * scale, true);
        canvas.drawText(String.format(Locale.US, "%.1f", data.uvIndex), card.left + 22f * scale, card.top + 82f * scale, value);
        TextPaint label = textPaint(WHITE, 24f * scale, true);
        canvas.drawText(WeatherFormatter.uvStrength(data.uvIndex), card.left + 108f * scale, card.top + 80f * scale, label);

        RectF track = new RectF(card.left + 22f * scale, card.bottom - 28f * scale, card.right - 22f * scale, card.bottom - 14f * scale);
        paint.setShader(new LinearGradient(track.left, 0, track.right, 0,
                new int[]{Color.rgb(36, 205, 93), Color.rgb(255, 213, 79), Color.rgb(255, 126, 48), Color.rgb(196, 49, 93)},
                null,
                Shader.TileMode.CLAMP));
        canvas.drawRoundRect(track, 8f * scale, 8f * scale, paint);
        paint.setShader(null);
        float marker = track.left + (float) Math.min(1d, data.uvIndex / 11d) * track.width();
        paint.setColor(WHITE);
        canvas.drawCircle(marker, track.centerY(), 9f * scale, paint);
    }

    private static void drawTides(Canvas canvas, List<WeatherData.TideEvent> tides, float scale) {
        RectF card = new RectF(505f * scale, 270f * scale, 924f * scale, 392f * scale);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.argb(52, 255, 255, 255));
        canvas.drawRoundRect(card, 18f * scale, 18f * scale, paint);
        TextPaint title = textPaint(MUTED, 18f * scale, true);
        canvas.drawText("Tides", card.left + 22f * scale, card.top + 35f * scale, title);

        int count = Math.min(3, tides.size());
        float slotWidth = (card.width() - 44f * scale) / Math.max(1, count);
        for (int index = 0; index < count; index++) {
            WeatherData.TideEvent tide = tides.get(index);
            float left = card.left + 22f * scale + index * slotWidth;
            TextPaint type = textPaint(WHITE, 24f * scale, true);
            drawFittedText(canvas, tide.type, left, card.top + 76f * scale, slotWidth - 10f * scale, type, 13f * scale);
            TextPaint time = textPaint(MUTED, 18f * scale, false);
            drawFittedText(canvas, tide.time, left, card.top + 103f * scale, slotWidth - 10f * scale, time, 11f * scale);
        }
    }

    private static TextPaint textPaint(int color, float size, boolean bold) {
        TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setTextSize(size);
        paint.setFakeBoldText(bold);
        paint.setShadowLayer(3f, 0f, 2f, Color.argb(90, 0, 0, 0));
        return paint;
    }

    static float fitTextSize(String text, TextPaint paint, float maxWidth, float minSize) {
        while (paint.getTextSize() > minSize && paint.measureText(text) > maxWidth) {
            paint.setTextSize(paint.getTextSize() - 1f);
        }
        return paint.getTextSize();
    }

    private static void drawFittedText(Canvas canvas, String text, float x, float y, float maxWidth, TextPaint paint, float minSize) {
        fitTextSize(text, paint, maxWidth, minSize);
        canvas.drawText(ellipsize(text, paint, maxWidth), x, y, paint);
    }

    private static String ellipsize(String text, TextPaint paint, float maxWidth) {
        if (paint.measureText(text) <= maxWidth) {
            return text;
        }
        String ellipsis = "...";
        int end = text.length();
        while (end > 0 && paint.measureText(text.substring(0, end) + ellipsis) > maxWidth) {
            end--;
        }
        return end == 0 ? ellipsis : text.substring(0, end) + ellipsis;
    }
}
