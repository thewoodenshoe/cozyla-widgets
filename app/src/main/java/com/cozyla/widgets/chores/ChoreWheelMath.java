package com.cozyla.widgets.chores;

public final class ChoreWheelMath {
    private ChoreWheelMath() {
    }

    public static float rotationForIndex(int index, int slotCount) {
        return normalizeDegrees(-index * sweep(slotCount));
    }

    public static int indexAtPointer(float rotationDegrees, int slotCount) {
        return Math.floorMod(Math.round(-normalizeSignedDegrees(rotationDegrees) / sweep(slotCount)), slotCount);
    }

    private static float sweep(int slotCount) {
        return 360f / slotCount;
    }

    private static float normalizeDegrees(float degrees) {
        float normalized = degrees % 360f;
        return normalized < 0f ? normalized + 360f : normalized;
    }

    private static float normalizeSignedDegrees(float degrees) {
        float normalized = normalizeDegrees(degrees);
        return normalized > 180f ? normalized - 360f : normalized;
    }
}
