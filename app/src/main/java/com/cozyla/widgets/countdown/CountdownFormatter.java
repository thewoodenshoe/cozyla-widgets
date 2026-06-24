package com.cozyla.widgets.countdown;

public final class CountdownFormatter {
    private CountdownFormatter() {
    }

    public static String display(long millis) {
        long totalSeconds = Math.max(0L, (millis + 999L) / 1000L);
        long minutes = totalSeconds / 60L;
        long seconds = totalSeconds % 60L;
        return minutes + ":" + (seconds < 10L ? "0" : "") + seconds;
    }
}
