package com.cozyla.widgets.countdown;

public final class CountdownFormatter {
    private CountdownFormatter() {
    }

    public static Parts parts(long nowMillis, long targetMillis) {
        long remainingMinutes = Math.max(0L, (targetMillis - nowMillis + 59_999L) / 60_000L);
        long days = remainingMinutes / (24L * 60L);
        long hours = (remainingMinutes % (24L * 60L)) / 60L;
        long minutes = remainingMinutes % 60L;
        return new Parts(days, hours, minutes, remainingMinutes == 0L);
    }

    public static final class Parts {
        public final long days;
        public final long hours;
        public final long minutes;
        public final boolean done;

        private Parts(long days, long hours, long minutes, boolean done) {
            this.days = days;
            this.hours = hours;
            this.minutes = minutes;
            this.done = done;
        }
    }
}
