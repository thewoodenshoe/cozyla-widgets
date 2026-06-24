package com.cozyla.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.net.Uri;

import androidx.test.core.app.ApplicationProvider;

import com.cozyla.widgets.photos.PhotoFramePreferences;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class PhotoFramePreferencesTest {
    @Test
    public void storesSelectedPhotoUrisAndAdvancesIndex() {
        Context context = ApplicationProvider.getApplicationContext();
        int widgetId = 442;
        List<Uri> uris = List.of(Uri.parse("content://photos/1"), Uri.parse("content://photos/2"));

        PhotoFramePreferences.save(context, widgetId, uris, true, 5);
        PhotoFramePreferences.advance(context, widgetId, uris.size());

        assertTrue(PhotoFramePreferences.slideshow(context, widgetId));
        assertEquals(5, PhotoFramePreferences.intervalMinutes(context, widgetId));
        assertEquals(1, PhotoFramePreferences.index(context, widgetId, uris.size()));
        assertEquals(uris, PhotoFramePreferences.uris(context, widgetId));
    }

    @Test
    public void clampsSlideshowInterval() {
        assertEquals(1, PhotoFramePreferences.clampInterval(0));
        assertEquals(1440, PhotoFramePreferences.clampInterval(3000));
    }
}
