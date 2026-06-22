package com.cozyla.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.content.res.XmlResourceParser;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.xmlpull.v1.XmlPullParser;

@RunWith(RobolectricTestRunner.class)
public class CalendarWidgetProviderMetadataTest {
    private static final String ANDROID_NS = "http://schemas.android.com/apk/res/android";

    @Test
    public void metadataDeclaresResizableConfigurableHomeScreenWidget() throws Exception {
        Context context = ApplicationProvider.getApplicationContext();
        XmlResourceParser parser = context.getResources().getXml(R.xml.calendar_widget_info);
        moveToProviderTag(parser);

        assertEquals(1, parser.getAttributeIntValue(ANDROID_NS, "widgetCategory", -1));
        assertEquals(3, parser.getAttributeIntValue(ANDROID_NS, "resizeMode", -1));
        assertEquals(7, parser.getAttributeIntValue(ANDROID_NS, "targetCellWidth", -1));
        assertEquals(4, parser.getAttributeIntValue(ANDROID_NS, "targetCellHeight", -1));
        assertEquals(
                "com.cozyla.widgets.calendar.CalendarWidgetConfigureActivity",
                parser.getAttributeValue(ANDROID_NS, "configure")
        );

        assertPresent(parser, "minWidth");
        assertPresent(parser, "minHeight");
        assertPresent(parser, "minResizeWidth");
        assertPresent(parser, "minResizeHeight");
        assertPresent(parser, "widgetFeatures");

        assertEquals(
                R.layout.widget_calendar,
                parser.getAttributeResourceValue(ANDROID_NS, "initialLayout", 0)
        );
        assertEquals(
                R.layout.widget_calendar_preview,
                parser.getAttributeResourceValue(ANDROID_NS, "previewLayout", 0)
        );
        assertEquals(
                R.drawable.calendar_widget_preview_image,
                parser.getAttributeResourceValue(ANDROID_NS, "previewImage", 0)
        );
        assertEquals(
                R.string.calendar_widget_description,
                parser.getAttributeResourceValue(ANDROID_NS, "description", 0)
        );
    }

    private static void moveToProviderTag(XmlResourceParser parser) throws Exception {
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() == XmlPullParser.START_TAG
                    && "appwidget-provider".equals(parser.getName())) {
                return;
            }
        }
        throw new AssertionError("appwidget-provider tag not found");
    }

    private static void assertPresent(XmlResourceParser parser, String name) {
        assertNotNull(name + " must be declared", parser.getAttributeValue(ANDROID_NS, name));
    }
}
