package com.cozyla.widgets.chores;

import java.util.ArrayList;
import java.util.List;

public final class ChoreWheelSlot {
    public final String label;
    public final boolean noChores;

    public ChoreWheelSlot(String label, boolean noChores) {
        this.label = label;
        this.noChores = noChores;
    }

    public static List<ChoreWheelSlot> chores(List<String> labels) {
        List<ChoreWheelSlot> slots = new ArrayList<>();
        for (String label : labels) {
            slots.add(new ChoreWheelSlot(label, false));
        }
        return slots;
    }

    public static List<String> labels(List<ChoreWheelSlot> slots) {
        List<String> labels = new ArrayList<>();
        for (ChoreWheelSlot slot : slots) {
            labels.add(slot.label);
        }
        return labels;
    }
}
