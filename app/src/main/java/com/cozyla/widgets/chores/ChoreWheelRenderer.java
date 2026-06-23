package com.cozyla.widgets.chores;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;

import java.util.List;

public final class ChoreWheelRenderer {
    private static final int SIZE = 384;
    private static final float SCALE = SIZE / 720f;
    private static final int GOLD = Color.rgb(217, 166, 45);
    private static final int GOLD_LIGHT = Color.rgb(255, 230, 124);
    private static final int RED = Color.rgb(196, 18, 34);
    private static final int CREAM = Color.rgb(255, 248, 213);
    private static final int INK = Color.rgb(35, 73, 160);

    private ChoreWheelRenderer() {
    }

    public static Bitmap render(List<String> chores, int selectedIndex) {
        List<String> safeChores = ChoreWheelPreferences.sanitize(chores);
        if (safeChores.isEmpty()) {
            safeChores = ChoreWheelPreferences.sanitize(java.util.Collections.singletonList("Add chores"));
        }

        Bitmap bitmap = Bitmap.createBitmap(SIZE, SIZE, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.TRANSPARENT);

        float center = SIZE / 2f;
        float outerRadius = scaled(330f);
        float wheelRadius = scaled(286f);
        RectF outer = new RectF(center - outerRadius, center - outerRadius, center + outerRadius, center + outerRadius);
        RectF wheel = new RectF(center - wheelRadius, center - wheelRadius, center + wheelRadius, center + wheelRadius);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(GOLD);
        canvas.drawOval(outer, paint);
        paint.setColor(Color.rgb(242, 196, 68));
        canvas.drawOval(new RectF(
                center - scaled(306f),
                center - scaled(306f),
                center + scaled(306f),
                center + scaled(306f)
        ), paint);

        int selected = Math.floorMod(selectedIndex, safeChores.size());
        float sweep = 360f / safeChores.size();
        float start = -90f - (sweep / 2f) - (selected * sweep);
        for (int index = 0; index < safeChores.size(); index++) {
            paint.setColor(index % 2 == 0 ? RED : CREAM);
            canvas.drawArc(wheel, start + (index * sweep), sweep, true, paint);
        }

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(scaled(5f));
        paint.setColor(Color.rgb(122, 92, 22));
        canvas.drawOval(wheel, paint);
        paint.setStyle(Paint.Style.FILL);

        drawBulbs(canvas, center, outerRadius);
        drawLabels(canvas, safeChores, start, sweep, center);
        drawCenter(canvas, center);
        drawPointer(canvas, center);
        return bitmap;
    }

    private static void drawBulbs(Canvas canvas, float center, float radius) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(GOLD_LIGHT);
        for (int index = 0; index < 24; index++) {
            double angle = Math.toRadians(index * 15d);
            float x = center + (float) Math.cos(angle) * (radius - scaled(24f));
            float y = center + (float) Math.sin(angle) * (radius - scaled(24f));
            canvas.drawCircle(x, y, scaled(10f), paint);
        }
    }

    private static void drawLabels(Canvas canvas, List<String> chores, float start, float sweep, float center) {
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(INK);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);
        textPaint.setTextSize(scaled(30f));

        for (int index = 0; index < chores.size(); index++) {
            String label = shorten(chores.get(index));
            float angle = start + (index * sweep) + (sweep / 2f);
            canvas.save();
            canvas.rotate(angle, center, center);
            canvas.rotate(90f, center, center - scaled(168f));
            canvas.drawText(label, center, center - scaled(168f), textPaint);
            canvas.restore();
        }
    }

    private static String shorten(String label) {
        return label.length() <= 16 ? label : label.substring(0, 15) + "...";
    }

    private static void drawCenter(Canvas canvas, float center) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(GOLD);
        canvas.drawCircle(center, center, scaled(54f), paint);
        paint.setColor(Color.rgb(115, 0, 18));
        canvas.drawCircle(center, center, scaled(34f), paint);
    }

    private static void drawPointer(Canvas canvas, float center) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(GOLD);
        android.graphics.Path path = new android.graphics.Path();
        path.moveTo(center, scaled(48f));
        path.lineTo(center - scaled(25f), scaled(170f));
        path.lineTo(center + scaled(25f), scaled(170f));
        path.close();
        canvas.drawPath(path, paint);
        paint.setColor(Color.rgb(122, 92, 22));
        canvas.drawCircle(center, scaled(58f), scaled(10f), paint);
    }

    private static float scaled(float value) {
        return value * SCALE;
    }
}
