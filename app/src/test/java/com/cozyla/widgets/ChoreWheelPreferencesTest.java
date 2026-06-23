package com.cozyla.widgets;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.cozyla.widgets.chores.ChoreWheelPreferences;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class ChoreWheelPreferencesTest {
    @Test
    public void sanitizeTrimsBlanksAndCapsAtEightItems() {
        List<String> chores = ChoreWheelPreferences.sanitize(Arrays.asList(
                " Dishes ",
                "",
                "Trash",
                "Vacuum",
                "Laundry",
                "Counters",
                "Floors",
                "Windows",
                "Garage",
                "Ignored"
        ));

        assertEquals(8, chores.size());
        assertEquals("Dishes", chores.get(0));
        assertEquals("Garage", chores.get(7));
    }

    @Test
    public void storesChoresPerWidget() {
        Context context = ApplicationProvider.getApplicationContext();
        ChoreWheelPreferences.save(context, 101, Arrays.asList("Dishes", "Trash"));
        ChoreWheelPreferences.saveSelectedIndex(context, 101, 1);

        assertEquals(Arrays.asList("Dishes", "Trash"), ChoreWheelPreferences.chores(context, 101));
        assertEquals(1, ChoreWheelPreferences.selectedIndex(context, 101));
    }
}
