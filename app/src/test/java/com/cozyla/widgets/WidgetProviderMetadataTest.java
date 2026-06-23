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
public class WidgetProviderMetadataTest {
    private static final String ANDROID_NS = "http://schemas.android.com/apk/res/android";

    @Test
    public void providerInfoPreservesResizableHomeScreenWidgetContract() throws Exception {
        assertWidgetMetadata(
                R.xml.clock_widget_info,
                R.layout.widget_clock,
                R.layout.widget_clock,
                R.string.clock_widget_description,
                4,
                2
        );
    }

    @Test
    public void quoteProviderInfoPreservesResizableHomeScreenWidgetContract() throws Exception {
        assertWidgetMetadata(
                R.xml.quote_widget_info,
                R.layout.widget_quote,
                R.layout.widget_quote,
                R.string.quote_widget_description,
                3,
                2
        );
    }

    @Test
    public void choreWheelProviderInfoPreservesResizableHomeScreenWidgetContract() throws Exception {
        assertWidgetMetadata(
                R.xml.chore_wheel_widget_info,
                R.layout.widget_chore_wheel,
                R.layout.widget_chore_wheel,
                R.string.chore_widget_description,
                5,
                3
        );
    }

    private static void assertWidgetMetadata(
            int xmlId,
            int initialLayout,
            int previewLayout,
            int description,
            int targetCellWidth,
            int targetCellHeight
    ) throws Exception {
        Context context = ApplicationProvider.getApplicationContext();
        XmlResourceParser parser = context.getResources().getXml(xmlId);
        moveToProviderTag(parser);

        assertEquals(1, parser.getAttributeIntValue(ANDROID_NS, "widgetCategory", -1));
        assertEquals(3, parser.getAttributeIntValue(ANDROID_NS, "resizeMode", -1));
        assertEquals(targetCellWidth, parser.getAttributeIntValue(ANDROID_NS, "targetCellWidth", -1));
        assertEquals(targetCellHeight, parser.getAttributeIntValue(ANDROID_NS, "targetCellHeight", -1));

        assertPresent(parser, "minWidth");
        assertPresent(parser, "minHeight");
        assertPresent(parser, "minResizeWidth");
        assertPresent(parser, "minResizeHeight");

        assertEquals(initialLayout, parser.getAttributeResourceValue(ANDROID_NS, "initialLayout", 0));
        assertEquals(previewLayout, parser.getAttributeResourceValue(ANDROID_NS, "previewLayout", 0));
        assertEquals(description, parser.getAttributeResourceValue(ANDROID_NS, "description", 0));
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
