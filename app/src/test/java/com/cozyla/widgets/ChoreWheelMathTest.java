package com.cozyla.widgets;

import static org.junit.Assert.assertEquals;

import com.cozyla.widgets.chores.ChoreWheelMath;

import org.junit.Test;

public class ChoreWheelMathTest {
    @Test
    public void indexAtPointerMatchesRotationForEverySlot() {
        for (int slotCount = 2; slotCount <= 8; slotCount++) {
            for (int index = 0; index < slotCount; index++) {
                float rotation = ChoreWheelMath.rotationForIndex(index, slotCount);

                assertEquals(index, ChoreWheelMath.indexAtPointer(rotation, slotCount));
            }
        }
    }

    @Test
    public void indexAtPointerStillMatchesCloseCallInsideSegment() {
        int slotCount = 8;
        float halfSweep = 360f / slotCount / 2f;
        for (int index = 0; index < slotCount; index++) {
            float rotation = ChoreWheelMath.rotationForIndex(index, slotCount);

            assertEquals(index, ChoreWheelMath.indexAtPointer(rotation + halfSweep - 2f, slotCount));
            assertEquals(index, ChoreWheelMath.indexAtPointer(rotation - halfSweep + 2f, slotCount));
        }
    }
}
