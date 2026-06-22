package com.cozyla.widgets.calendar;

import android.appwidget.AppWidgetManager;
import android.os.Bundle;

public final class CalendarWidgetSizePolicy {
    private CalendarWidgetSizePolicy() {
    }

    public static Profile fromOptions(Bundle options) {
        int width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 620);
        int height = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 300);
        return fromDimensions(width, height);
    }

    public static Profile fromDimensions(int widthDp, int heightDp) {
        boolean toolbarVisible = heightDp >= 160;
        boolean secondaryActionsVisible = toolbarVisible && widthDp >= 560;
        boolean showEventTimes = widthDp >= 500 && heightDp >= 190;
        boolean timelineVisible = widthDp >= 500 && heightDp >= 360;
        int maxEventsPerDay;

        if (heightDp < 160) {
            maxEventsPerDay = 1;
        } else if (heightDp < 240) {
            maxEventsPerDay = 2;
        } else if (heightDp < 360) {
            maxEventsPerDay = 3;
        } else {
            maxEventsPerDay = 4;
        }

        return new Profile(
                toolbarVisible,
                secondaryActionsVisible,
                showEventTimes,
                maxEventsPerDay,
                widthDp < 420,
                timelineVisible,
                widthDp,
                Math.max(180, heightDp - 138)
        );
    }

    public static final class Profile {
        public final boolean toolbarVisible;
        public final boolean secondaryActionsVisible;
        public final boolean showEventTimes;
        public final int maxEventsPerDay;
        public final boolean compactDayLabels;
        public final boolean timelineVisible;
        public final int widthDp;
        public final int timelineHeightDp;

        private Profile(
                boolean toolbarVisible,
                boolean secondaryActionsVisible,
                boolean showEventTimes,
                int maxEventsPerDay,
                boolean compactDayLabels,
                boolean timelineVisible,
                int widthDp,
                int timelineHeightDp
        ) {
            this.toolbarVisible = toolbarVisible;
            this.secondaryActionsVisible = secondaryActionsVisible;
            this.showEventTimes = showEventTimes;
            this.maxEventsPerDay = maxEventsPerDay;
            this.compactDayLabels = compactDayLabels;
            this.timelineVisible = timelineVisible;
            this.widthDp = widthDp;
            this.timelineHeightDp = timelineHeightDp;
        }

        public float timelineDayWidthDp(int dayCount) {
            return Math.max(1f, (widthDp - 54f) / dayCount);
        }
    }
}
