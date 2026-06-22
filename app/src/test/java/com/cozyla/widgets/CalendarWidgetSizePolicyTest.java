package com.cozyla.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.cozyla.widgets.calendar.CalendarWidgetSizePolicy;

import org.junit.Test;

public class CalendarWidgetSizePolicyTest {
    @Test
    public void compactPlacementKeepsOneEventAndRemovesToolbar() {
        CalendarWidgetSizePolicy.Profile profile = CalendarWidgetSizePolicy.fromDimensions(300, 120);

        assertFalse(profile.toolbarVisible);
        assertFalse(profile.secondaryActionsVisible);
        assertFalse(profile.showEventTimes);
        assertTrue(profile.compactDayLabels);
        assertEquals(1, profile.maxEventsPerDay);
        assertFalse(profile.timelineVisible);
    }

    @Test
    public void wideShortPlacementKeepsNavigationWithoutTimes() {
        CalendarWidgetSizePolicy.Profile profile = CalendarWidgetSizePolicy.fromDimensions(620, 160);

        assertTrue(profile.toolbarVisible);
        assertTrue(profile.secondaryActionsVisible);
        assertFalse(profile.showEventTimes);
        assertEquals(2, profile.maxEventsPerDay);
        assertFalse(profile.timelineVisible);
    }

    @Test
    public void squarePlacementPrioritizesVerticalEventCapacity() {
        CalendarWidgetSizePolicy.Profile profile = CalendarWidgetSizePolicy.fromDimensions(300, 300);

        assertTrue(profile.toolbarVisible);
        assertFalse(profile.secondaryActionsVisible);
        assertFalse(profile.showEventTimes);
        assertTrue(profile.compactDayLabels);
        assertEquals(3, profile.maxEventsPerDay);
        assertFalse(profile.timelineVisible);
    }

    @Test
    public void defaultPlacementShowsTimesAndThreeEvents() {
        CalendarWidgetSizePolicy.Profile profile = CalendarWidgetSizePolicy.fromDimensions(620, 300);

        assertTrue(profile.toolbarVisible);
        assertTrue(profile.secondaryActionsVisible);
        assertTrue(profile.showEventTimes);
        assertFalse(profile.compactDayLabels);
        assertEquals(3, profile.maxEventsPerDay);
        assertFalse(profile.timelineVisible);
    }

    @Test
    public void largeTabletPlacementUsesAllFourEventSlots() {
        CalendarWidgetSizePolicy.Profile profile = CalendarWidgetSizePolicy.fromDimensions(1000, 500);

        assertTrue(profile.toolbarVisible);
        assertTrue(profile.secondaryActionsVisible);
        assertTrue(profile.showEventTimes);
        assertFalse(profile.compactDayLabels);
        assertEquals(4, profile.maxEventsPerDay);
        assertTrue(profile.timelineVisible);
        assertEquals(362, profile.timelineHeightDp);
        assertEquals(135.1f, profile.timelineDayWidthDp(7), 0.1f);
    }
}
