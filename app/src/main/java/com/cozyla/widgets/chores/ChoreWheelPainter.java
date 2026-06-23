package com.cozyla.widgets.chores;

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

public final class ChoreWheelPainter {
    private static final int GOLD_DARK = Color.rgb(123, 82, 18);
    private static final int GOLD = Color.rgb(222, 165, 34);
    private static final int GOLD_LIGHT = Color.rgb(255, 232, 117);
    private static final int RED_DARK = Color.rgb(116, 9, 24);
    private static final int RED = Color.rgb(203, 18, 42);
    private static final int PEARL = Color.rgb(255, 247, 214);
    private static final int GREEN_DARK = Color.rgb(12, 100, 56);
    private static final int GREEN = Color.rgb(26, 172, 91);
    private static final int INK = Color.rgb(24, 39, 70);

    private ChoreWheelPainter() {
    }

    public static void drawWheel(Canvas canvas, List<String> labels, float rotationDegrees) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        float size = Math.min(width, height);
        float centerX = width / 2f;
        float centerY = height / 2f;
        float scale = size / 720f;
        float outerRadius = 334f * scale;
        float innerRadius = 286f * scale;

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        RectF outer = new RectF(
                centerX - outerRadius,
                centerY - outerRadius,
                centerX + outerRadius,
                centerY + outerRadius
        );
        RectF wheel = new RectF(
                centerX - innerRadius,
                centerY - innerRadius,
                centerX + innerRadius,
                centerY + innerRadius
        );

        drawGoldRim(canvas, outer, centerX, centerY, outerRadius, paint);
        drawSegments(canvas, labels, rotationDegrees, wheel, centerX, centerY, innerRadius, paint);
        drawBulbs(canvas, centerX, centerY, outerRadius, scale, paint);
        drawHub(canvas, centerX, centerY, scale, paint);
        drawPointer(canvas, centerX, centerY, scale, paint);
    }

    private static void drawGoldRim(
            Canvas canvas,
            RectF outer,
            float centerX,
            float centerY,
            float outerRadius,
            Paint paint
    ) {
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new RadialGradient(
                centerX - outerRadius * 0.25f,
                centerY - outerRadius * 0.35f,
                outerRadius * 1.25f,
                new int[]{GOLD_LIGHT, GOLD, GOLD_DARK},
                new float[]{0f, 0.52f, 1f},
                Shader.TileMode.CLAMP
        ));
        canvas.drawOval(outer, paint);
        paint.setShader(null);
    }

    private static void drawSegments(
            Canvas canvas,
            List<String> labels,
            float rotationDegrees,
            RectF wheel,
            float centerX,
            float centerY,
            float radius,
            Paint paint
    ) {
        float sweep = 360f / labels.size();
        float start = -90f - (sweep / 2f) + rotationDegrees;
        for (int index = 0; index < labels.size(); index++) {
            String label = labels.get(index);
            boolean noChores = ChoreWheelPreferences.NO_CHORES_LABEL.equals(label);
            if (noChores) {
                paint.setShader(new LinearGradient(
                        wheel.left,
                        wheel.top,
                        wheel.right,
                        wheel.bottom,
                        GREEN,
                        GREEN_DARK,
                        Shader.TileMode.CLAMP
                ));
            } else if (index % 2 == 0) {
                paint.setShader(new LinearGradient(
                        wheel.left,
                        wheel.top,
                        wheel.right,
                        wheel.bottom,
                        Color.rgb(239, 34, 57),
                        RED_DARK,
                        Shader.TileMode.CLAMP
                ));
            } else {
                paint.setShader(new LinearGradient(
                        wheel.left,
                        wheel.top,
                        wheel.right,
                        wheel.bottom,
                        Color.WHITE,
                        PEARL,
                        Shader.TileMode.CLAMP
                ));
            }
            canvas.drawArc(wheel, start + (index * sweep), sweep, true, paint);
            paint.setShader(null);
        }

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(radius * 0.012f);
        paint.setColor(GOLD_DARK);
        canvas.drawOval(wheel, paint);
        paint.setStyle(Paint.Style.FILL);
        drawLabels(canvas, labels, start, sweep, centerX, centerY, radius);
    }

    private static void drawLabels(
            Canvas canvas,
            List<String> labels,
            float start,
            float sweep,
            float centerX,
            float centerY,
            float radius
    ) {
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);
        textPaint.setTextSize(Math.max(18f, radius * 0.105f));
        textPaint.setShadowLayer(3f, 0f, 2f, Color.argb(120, 0, 0, 0));

        for (int index = 0; index < labels.size(); index++) {
            String label = labels.get(index);
            boolean lightSegment = index % 2 != 0 && !ChoreWheelPreferences.NO_CHORES_LABEL.equals(label);
            textPaint.setColor(lightSegment ? INK : Color.WHITE);
            float angle = start + (index * sweep) + (sweep / 2f);
            canvas.save();
            canvas.rotate(angle, centerX, centerY);
            canvas.rotate(90f, centerX, centerY - radius * 0.58f);
            canvas.drawText(shorten(label), centerX, centerY - radius * 0.58f, textPaint);
            canvas.restore();
        }
    }

    private static void drawBulbs(
            Canvas canvas,
            float centerX,
            float centerY,
            float radius,
            float scale,
            Paint paint
    ) {
        for (int index = 0; index < 32; index++) {
            double angle = Math.toRadians(index * 11.25d);
            float x = centerX + (float) Math.cos(angle) * (radius - 26f * scale);
            float y = centerY + (float) Math.sin(angle) * (radius - 26f * scale);
            paint.setShader(new RadialGradient(
                    x - 4f * scale,
                    y - 5f * scale,
                    18f * scale,
                    Color.WHITE,
                    GOLD_LIGHT,
                    Shader.TileMode.CLAMP
            ));
            canvas.drawCircle(x, y, 12f * scale, paint);
            paint.setShader(null);
        }
    }

    private static void drawHub(Canvas canvas, float centerX, float centerY, float scale, Paint paint) {
        paint.setShader(new RadialGradient(
                centerX - 18f * scale,
                centerY - 20f * scale,
                78f * scale,
                GOLD_LIGHT,
                GOLD_DARK,
                Shader.TileMode.CLAMP
        ));
        canvas.drawCircle(centerX, centerY, 58f * scale, paint);
        paint.setShader(null);
        paint.setColor(Color.rgb(92, 0, 18));
        canvas.drawCircle(centerX, centerY, 34f * scale, paint);
        paint.setColor(Color.rgb(180, 9, 32));
        canvas.drawCircle(centerX - 6f * scale, centerY - 7f * scale, 22f * scale, paint);
    }

    private static void drawPointer(Canvas canvas, float centerX, float centerY, float scale, Paint paint) {
        float wheelTop = centerY - 360f * scale;
        paint.setShader(new LinearGradient(
                centerX - 26f * scale,
                wheelTop + 38f * scale,
                centerX + 26f * scale,
                wheelTop + 180f * scale,
                GOLD_LIGHT,
                GOLD_DARK,
                Shader.TileMode.CLAMP
        ));
        Path path = new Path();
        path.moveTo(centerX, wheelTop + 44f * scale);
        path.lineTo(centerX - 28f * scale, wheelTop + 178f * scale);
        path.lineTo(centerX + 28f * scale, wheelTop + 178f * scale);
        path.close();
        canvas.drawPath(path, paint);
        paint.setShader(null);
        paint.setColor(GOLD_DARK);
        canvas.drawCircle(centerX, wheelTop + 56f * scale, 10f * scale, paint);
    }

    private static String shorten(String label) {
        return label.length() <= 15 ? label : label.substring(0, 14) + "...";
    }
}
