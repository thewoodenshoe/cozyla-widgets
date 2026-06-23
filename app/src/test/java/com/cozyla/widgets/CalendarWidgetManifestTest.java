package com.cozyla.widgets;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.Manifest;
import android.app.job.JobService;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;

@RunWith(RobolectricTestRunner.class)
public class CalendarWidgetManifestTest {
    @Test
    public void manifestRegistersPermissionProviderAndConfigurationActivity() throws Exception {
        Context context = ApplicationProvider.getApplicationContext();
        PackageInfo info = context.getPackageManager().getPackageInfo(
                context.getPackageName(),
                PackageManager.GET_PERMISSIONS
                        | PackageManager.GET_RECEIVERS
                        | PackageManager.GET_ACTIVITIES
                        | PackageManager.GET_SERVICES
        );

        assertTrue(Arrays.asList(info.requestedPermissions).contains(Manifest.permission.READ_CALENDAR));
        assertTrue(Arrays.asList(info.requestedPermissions).contains(Manifest.permission.INTERNET));
        assertTrue(Arrays.asList(info.requestedPermissions).contains(Manifest.permission.VIBRATE));
        assertFalse((info.applicationInfo.flags & ApplicationInfo.FLAG_ALLOW_BACKUP) != 0);
        assertFalse((info.applicationInfo.flags & ApplicationInfo.FLAG_USES_CLEARTEXT_TRAFFIC) != 0);
        assertFalse(component(
                info.receivers,
                "com.cozyla.widgets.calendar.CalendarWidgetProvider"
        ).exported);
        assertFalse(component(
                info.receivers,
                "com.cozyla.widgets.clock.ClockWidgetProvider"
        ).exported);
        assertFalse(component(
                info.receivers,
                "com.cozyla.widgets.quote.QuoteWidgetProvider"
        ).exported);
        assertFalse(component(
                info.receivers,
                "com.cozyla.widgets.chores.ChoreWheelProvider"
        ).exported);
        assertFalse(component(
                info.receivers,
                "com.cozyla.widgets.countdown.CountdownWidgetProvider"
        ).exported);
        assertFalse(component(
                info.receivers,
                "com.cozyla.widgets.weather.WeatherWidgetProvider"
        ).exported);
        assertFalse(component(
                info.receivers,
                "com.cozyla.widgets.photos.PhotoFrameWidgetProvider"
        ).exported);
        assertTrue(hasComponent(
                info.activities,
                "com.cozyla.widgets.calendar.CalendarWidgetConfigureActivity"
        ));
        assertTrue(hasComponent(
                info.activities,
                "com.cozyla.widgets.chores.ChoreWheelConfigureActivity"
        ));
        assertTrue(hasComponent(
                info.activities,
                "com.cozyla.widgets.weather.WeatherConfigureActivity"
        ));
        assertTrue(hasComponent(
                info.activities,
                "com.cozyla.widgets.photos.PhotoFrameConfigureActivity"
        ));
        assertFalse(component(
                info.activities,
                "com.cozyla.widgets.chores.ChoreWheelSpinActivity"
        ).exported);
        ServiceInfo jobService = service(
                info.services,
                "com.cozyla.widgets.calendar.CalendarWidgetUpdateJobService"
        );
        assertTrue(jobService.exported);
        org.junit.Assert.assertEquals(
                JobService.PERMISSION_BIND,
                jobService.permission
        );
        assertJobService(info, "com.cozyla.widgets.quote.QuoteWidgetUpdateJobService");
        assertJobService(info, "com.cozyla.widgets.weather.WeatherWidgetUpdateJobService");
    }

    private static void assertJobService(PackageInfo info, String className) {
        ServiceInfo jobService = service(info.services, className);
        assertTrue(jobService.exported);
        org.junit.Assert.assertEquals(JobService.PERMISSION_BIND, jobService.permission);
    }

    private static boolean hasComponent(ActivityInfo[] components, String className) {
        if (components == null) {
            return false;
        }
        for (ActivityInfo component : components) {
            if (className.equals(component.name)) {
                return true;
            }
        }
        return false;
    }

    private static ActivityInfo component(ActivityInfo[] components, String className) {
        org.junit.Assert.assertNotNull(components);
        for (ActivityInfo component : components) {
            if (className.equals(component.name)) {
                return component;
            }
        }
        throw new AssertionError("Missing component: " + className);
    }

    private static ServiceInfo service(ServiceInfo[] components, String className) {
        org.junit.Assert.assertNotNull(components);
        for (ServiceInfo component : components) {
            if (className.equals(component.name)) {
                return component;
            }
        }
        throw new AssertionError("Missing service: " + className);
    }
}
