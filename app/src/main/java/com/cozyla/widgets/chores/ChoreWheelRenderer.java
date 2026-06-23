package com.cozyla.widgets.chores;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

import java.util.List;

public final class ChoreWheelRenderer {
    private static final int SIZE = 384;
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
        int selected = Math.floorMod(selectedIndex, safeChores.size());
        ChoreWheelPainter.drawWheel(canvas, safeChores, -selected * (360f / safeChores.size()));
        return bitmap;
    }
}
