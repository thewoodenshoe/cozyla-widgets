package com.cozyla.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.res.XmlResourceParser;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.xmlpull.v1.XmlPullParser;

import java.util.HashSet;
import java.util.Set;

@RunWith(RobolectricTestRunner.class)
public class WidgetLayoutContractTest {
    private static final String ANDROID_NS = "http://schemas.android.com/apk/res/android";

    @Test
    public void widgetLayoutUsesRemoteViewsSupportedClockViews() throws Exception {
        Context context = ApplicationProvider.getApplicationContext();
        XmlResourceParser parser = context.getResources().getXml(R.layout.widget_clock);
        Set<Integer> textClockIds = new HashSet<>();

        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() == XmlPullParser.START_TAG
                    && "TextClock".equals(parser.getName())) {
                textClockIds.add(parser.getAttributeResourceValue(ANDROID_NS, "id", 0));
                assertTrue("TextClock must have a 12-hour format",
                        parser.getAttributeValue(ANDROID_NS, "format12Hour") != null);
                assertTrue("TextClock must have a 24-hour format",
                        parser.getAttributeValue(ANDROID_NS, "format24Hour") != null);
            }
        }

        assertEquals(2, textClockIds.size());
        assertTrue(textClockIds.contains(R.id.widget_time));
        assertTrue(textClockIds.contains(R.id.widget_date));
    }
}
