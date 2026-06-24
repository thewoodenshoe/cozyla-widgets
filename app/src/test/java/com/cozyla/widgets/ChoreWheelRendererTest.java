package com.cozyla.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.cozyla.widgets.chores.ChoreWheelPainter;
import com.cozyla.widgets.chores.ChoreWheelRenderer;
import com.cozyla.widgets.chores.ChoreWheelSlot;

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
        Bitmap bitmap = ChoreWheelRenderer.render(Arrays.asList(
                "Dishes",
                "Trash",
                "No chores"
        ), 2);

        assertEquals(384, bitmap.getWidth());
        assertEquals(384, bitmap.getHeight());
        assertTrue(Color.alpha(bitmap.getPixel(192, 192)) > 0);
        assertTrue(Color.alpha(bitmap.getPixel(192, 32)) > 0);
        assertEquals(0, Color.alpha(bitmap.getPixel(0, 0)));
    }

    @Test
    public void rendersExplicitNoChoresSlot() {
        ChoreWheelSlot noChores = new ChoreWheelSlot("No chores", true);
        Bitmap bitmap = ChoreWheelRenderer.renderSlots(Arrays.asList(
                new ChoreWheelSlot("Dishes", false),
                new ChoreWheelSlot("Laundry", false),
                noChores
        ), 2);

        assertTrue(Color.alpha(bitmap.getPixel(192, 192)) > 0);
        assertTrue(ChoreWheelPainter.usesNoChoresStyle(noChores));
    }
}
