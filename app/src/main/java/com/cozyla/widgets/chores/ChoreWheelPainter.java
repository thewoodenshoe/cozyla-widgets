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

import java.util.ArrayList;
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

    public static void drawWheel(Canvas canvas, List<ChoreWheelSlot> slots, float rotationDegrees) {
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
        drawSegments(canvas, slots, rotationDegrees, wheel, centerX, centerY, innerRadius, paint);
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
            List<ChoreWheelSlot> slots,
            float rotationDegrees,
            RectF wheel,
            float centerX,
            float centerY,
            float radius,
            Paint paint
    ) {
        float sweep = 360f / slots.size();
        float start = -90f - (sweep / 2f) + rotationDegrees;
        for (int index = 0; index < slots.size(); index++) {
            ChoreWheelSlot slot = slots.get(index);
            if (slot.noChores) {
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
        drawLabels(canvas, slots, start, sweep, centerX, centerY, radius);
    }

    private static void drawLabels(
            Canvas canvas,
            List<ChoreWheelSlot> slots,
            float start,
            float sweep,
            float centerX,
            float centerY,
            float radius
    ) {
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);
        textPaint.setShadowLayer(3f, 0f, 2f, Color.argb(120, 0, 0, 0));
        float textRadius = radius * 0.58f;
        float maxTextWidth = labelMaxWidth(radius, sweep);

        for (int index = 0; index < slots.size(); index++) {
            ChoreWheelSlot slot = slots.get(index);
            boolean lightSegment = index % 2 != 0 && !slot.noChores;
            textPaint.setColor(lightSegment ? INK : Color.WHITE);
            float angle = segmentCenterAngle(start, sweep, index);
            double radians = Math.toRadians(angle);
            float x = centerX + (float) Math.cos(radians) * textRadius;
            float y = centerY + (float) Math.sin(radians) * textRadius;
            LabelLayout labelLayout = layoutLabel(
                    slot.label,
                    textPaint,
                    Math.max(10f, radius * 0.055f),
                    Math.max(18f, radius * 0.092f),
                    maxTextWidth,
                    2
            );
            canvas.save();
            canvas.translate(x, y);
            canvas.rotate(textRotation(angle));
            float lineHeight = labelLayout.textSize * 1.08f;
            float baseline = -((labelLayout.lines.size() - 1) * lineHeight / 2f)
                    + labelLayout.textSize * 0.35f;
            for (String line : labelLayout.lines) {
                canvas.drawText(line, 0f, baseline, textPaint);
                baseline += lineHeight;
            }
            canvas.restore();
        }
    }

    public static LabelLayout layoutLabel(
            String label,
            TextPaint paint,
            float minTextSize,
            float maxTextSize,
            float maxWidth,
            int maxLines
    ) {
        float textSize = maxTextSize;
        while (textSize > minTextSize) {
            paint.setTextSize(textSize);
            List<String> lines = wrapLabel(label, paint, maxWidth, maxLines);
            if (fits(lines, paint, maxWidth, maxLines)) {
                return new LabelLayout(lines, textSize);
            }
            textSize -= 1f;
        }

        paint.setTextSize(minTextSize);
        List<String> lines = wrapLabel(label, paint, maxWidth, maxLines);
        if (lines.size() > maxLines) {
            lines = new ArrayList<>(lines.subList(0, maxLines));
        }
        if (!lines.isEmpty()) {
            int last = lines.size() - 1;
            lines.set(last, ellipsize(lines.get(last), paint, maxWidth));
        }
        return new LabelLayout(lines, minTextSize);
    }

    public static float labelMaxWidth(float radius, float sweep) {
        double halfSweep = Math.toRadians(sweep / 2f);
        return (float) (2d * radius * 0.58d * Math.sin(halfSweep)) * 0.72f;
    }

    public static float segmentCenterAngle(float start, float sweep, int index) {
        return start + (index * sweep) + (sweep / 2f);
    }

    public static float textRotation(float segmentCenterAngle) {
        float rotation = segmentCenterAngle + 90f;
        float normalized = ((rotation % 360f) + 360f) % 360f;
        if (normalized > 90f && normalized < 270f) {
            rotation += 180f;
        }
        return rotation;
    }

    private static List<String> wrapLabel(
            String label,
            TextPaint paint,
            float maxWidth,
            int maxLines
    ) {
        String[] words = label.trim().split("\\s+");
        List<String> lines = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (String word : words) {
            String candidate = current.length() == 0 ? word : current + " " + word;
            if (paint.measureText(candidate) <= maxWidth) {
                current.setLength(0);
                current.append(candidate);
            } else {
                if (current.length() > 0) {
                    lines.add(current.toString());
                    current.setLength(0);
                }
                if (paint.measureText(word) <= maxWidth) {
                    current.append(word);
                } else {
                    lines.add(ellipsize(word, paint, maxWidth));
                }
            }
            if (lines.size() == maxLines) {
                break;
            }
        }
        if (current.length() > 0 && lines.size() < maxLines) {
            lines.add(current.toString());
        }
        if (lines.isEmpty()) {
            lines.add("");
        }
        return lines;
    }

    private static boolean fits(List<String> lines, TextPaint paint, float maxWidth, int maxLines) {
        if (lines.size() > maxLines) {
            return false;
        }
        for (String line : lines) {
            if (paint.measureText(line) > maxWidth) {
                return false;
            }
        }
        return true;
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
        path.moveTo(centerX - 34f * scale, wheelTop + 44f * scale);
        path.lineTo(centerX + 34f * scale, wheelTop + 44f * scale);
        path.lineTo(centerX, wheelTop + 178f * scale);
        path.close();
        canvas.drawPath(path, paint);
        paint.setShader(null);
        paint.setColor(GOLD_DARK);
        canvas.drawCircle(centerX, wheelTop + 56f * scale, 10f * scale, paint);
    }

    public static boolean usesNoChoresStyle(ChoreWheelSlot slot) {
        return slot.noChores;
    }

    public static final class LabelLayout {
        public final List<String> lines;
        public final float textSize;

        LabelLayout(List<String> lines, float textSize) {
            this.lines = lines;
            this.textSize = textSize;
        }
    }
}
