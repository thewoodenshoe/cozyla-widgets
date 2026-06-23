package com.cozyla.widgets;

import static org.junit.Assert.assertEquals;

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
}
