package com.cozyla.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.text.TextPaint;

import com.cozyla.widgets.chores.ChoreWheelPainter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ChoreWheelPainterTest {
    @Test
    public void segmentCenterAngleUsesSameGeometryAsArcPaint() {
        float sweep = 45f;
        float start = -90f - (sweep / 2f);

        assertEquals(-90f, ChoreWheelPainter.segmentCenterAngle(start, sweep, 0), 0.001f);
        assertEquals(-45f, ChoreWheelPainter.segmentCenterAngle(start, sweep, 1), 0.001f);
        assertEquals(225f, ChoreWheelPainter.segmentCenterAngle(start, sweep, 7), 0.001f);
    }

    @Test
    public void textRotationKeepsLowerHalfReadable() {
        assertEquals(0f, ChoreWheelPainter.textRotation(-90f), 0.001f);
        assertEquals(315f, ChoreWheelPainter.textRotation(45f), 0.001f);
        assertEquals(315f, ChoreWheelPainter.textRotation(225f), 0.001f);
    }

    @Test
    public void labelLayoutKeepsLongChoresInsideSegmentWidth() {
        TextPaint paint = new TextPaint();
        float maxWidth = ChoreWheelPainter.labelMaxWidth(286f, 45f);

        ChoreWheelPainter.LabelLayout layout = ChoreWheelPainter.layoutLabel(
                "Pick up 5 wrappers",
                paint,
                10f,
                26f,
                maxWidth,
                2
        );

        paint.setTextSize(layout.textSize);
        assertTrue(layout.lines.size() <= 2);
        for (String line : layout.lines) {
            assertTrue("Line too wide: " + line, paint.measureText(line) <= maxWidth);
        }
    }
}
