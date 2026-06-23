package com.cozyla.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.test.core.app.ApplicationProvider;

import com.cozyla.widgets.photos.PhotoFrameRenderer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.GraphicsMode;

@RunWith(RobolectricTestRunner.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class PhotoFrameRendererTest {
    @Test
    public void rendersEmptyStateAtMultipleWidgetSizes() {
        Context context = ApplicationProvider.getApplicationContext();
        Bitmap compact = PhotoFrameRenderer.render(context, null, 360, 220, "Cannot find album/photos");
        Bitmap wide = PhotoFrameRenderer.render(context, null, 960, 480, "Cannot find album/photos");

        assertEquals(360, compact.getWidth());
        assertEquals(220, compact.getHeight());
        assertEquals(960, wide.getWidth());
        assertEquals(480, wide.getHeight());
        assertTrue(Color.alpha(compact.getPixel(30, 30)) > 0);
        assertTrue(Color.alpha(wide.getPixel(80, 80)) > 0);
    }
}
