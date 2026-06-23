package com.cozyla.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.cozyla.widgets.chores.ChoreWheelRenderer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.GraphicsMode;

import java.util.Arrays;

@RunWith(RobolectricTestRunner.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class ChoreWheelRendererTest {
    @Test
    public void rendersNonEmptyWheelBitmap() {
        Bitmap bitmap = ChoreWheelRenderer.render(Arrays.asList("Dishes", "Trash", "Laundry"), 1);

        assertEquals(384, bitmap.getWidth());
        assertEquals(384, bitmap.getHeight());
        assertTrue(Color.alpha(bitmap.getPixel(192, 192)) > 0);
        assertTrue(Color.alpha(bitmap.getPixel(192, 32)) > 0);
        assertEquals(0, Color.alpha(bitmap.getPixel(0, 0)));
    }
}
