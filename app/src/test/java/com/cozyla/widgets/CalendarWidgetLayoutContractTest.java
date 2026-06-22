package com.cozyla.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.xmlpull.v1.XmlPullParser;

import java.util.HashSet;
import java.util.Set;

@RunWith(RobolectricTestRunner.class)
public class CalendarWidgetLayoutContractTest {
    private static final String ANDROID_NS = "http://schemas.android.com/apk/res/android";

    @Test
    public void layoutKeepsSummaryAndTimelineCapacityForSevenDays() throws Exception {
        Context context = ApplicationProvider.getApplicationContext();
        Resources resources = context.getResources();
        XmlResourceParser parser = resources.getXml(R.layout.widget_calendar);
        Set<Integer> dayColumns = new HashSet<>();
        Set<Integer> eventSlots = new HashSet<>();
        Set<Integer> timelineColumns = new HashSet<>();
        Set<Integer> timelineEventSlots = new HashSet<>();
        Set<Integer> allDayEventSlots = new HashSet<>();
        Set<Integer> summaryAllDaySlots = new HashSet<>();
        Set<Integer> hourLabels = new HashSet<>();
        Set<Integer> hourRows = new HashSet<>();

        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            int id = parser.getAttributeResourceValue(ANDROID_NS, "id", 0);
            if (id == 0) {
                continue;
            }
            String name = resources.getResourceEntryName(id);
            if (name.matches("calendar_day_[1-7]")) {
                dayColumns.add(id);
            } else if (name.matches("calendar_event_[1-7]_[1-4]")) {
                eventSlots.add(id);
            } else if (name.matches("calendar_timeline_day_[1-7]")) {
                timelineColumns.add(id);
            } else if (name.matches("calendar_timeline_event_[1-7]_(?:[1-9]|10)")) {
                timelineEventSlots.add(id);
            } else if (name.matches("calendar_all_day_event_[1-7]_[1-2]")) {
                allDayEventSlots.add(id);
            } else if (name.matches("calendar_summary_all_day_[1-7]")) {
                summaryAllDaySlots.add(id);
            } else if (name.matches("calendar_hour_label_(?:[1-9]|1[0-9]|2[0-4])")) {
                hourLabels.add(id);
            } else if (name.matches("calendar_hour_row_(?:[1-9]|1[0-9]|2[0-4])")) {
                hourRows.add(id);
            }
        }

        assertEquals(7, dayColumns.size());
        assertEquals(28, eventSlots.size());
        assertEquals(7, timelineColumns.size());
        assertEquals(70, timelineEventSlots.size());
        assertEquals(14, allDayEventSlots.size());
        assertEquals(7, summaryAllDaySlots.size());
        assertEquals(24, hourLabels.size());
        assertEquals(24, hourRows.size());
    }

    @Test
    public void everyToolbarIconHasAnAccessibleDescriptionAndTouchSize() throws Exception {
        Context context = ApplicationProvider.getApplicationContext();
        XmlResourceParser parser = context.getResources().getXml(R.layout.widget_calendar);
        int imageCount = 0;

        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() == XmlPullParser.START_TAG
                    && "ImageView".equals(parser.getName())) {
                imageCount++;
                assertNotNull(parser.getAttributeValue(ANDROID_NS, "contentDescription"));
                assertTrue(parser.getAttributeValue(ANDROID_NS, "layout_width").contains("48"));
                assertTrue(parser.getAttributeValue(ANDROID_NS, "layout_height").contains("48"));
            }
        }

        assertEquals(5, imageCount);
    }
}
