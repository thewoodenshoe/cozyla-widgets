package com.cozyla.widgets.photos;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.TypedValue;
import android.widget.RemoteViews;

import com.cozyla.widgets.R;

import java.util.List;

public class PhotoFrameWidgetProvider extends AppWidgetProvider {
    public static final String ACTION_NEXT_PHOTO = "com.cozyla.widgets.photos.NEXT_PHOTO";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (Intent.ACTION_MY_PACKAGE_REPLACED.equals(intent.getAction())) {
            refreshAllWidgets(context);
            return;
        }
        if (ACTION_NEXT_PHOTO.equals(intent.getAction())) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                List<Uri> uris = PhotoFramePreferences.uris(context, appWidgetId);
                PhotoFramePreferences.advance(context, appWidgetId, uris.size());
                updateWidget(context, appWidgetId);
            }
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, buildViews(context, appWidgetManager, appWidgetId));
            scheduleIfNeeded(context, appWidgetId);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(
            Context context,
            AppWidgetManager appWidgetManager,
            int appWidgetId,
            Bundle newOptions
    ) {
        updateWidget(context, appWidgetId);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            cancelSlideshow(context, appWidgetId);
            PhotoFramePreferences.delete(context, appWidgetId);
        }
    }

    public static void updateWidget(Context context, int appWidgetId) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(appWidgetId, buildViews(context, manager, appWidgetId));
        scheduleIfNeeded(context, appWidgetId);
    }

    public static void refreshAllWidgets(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        int[] ids = manager.getAppWidgetIds(new ComponentName(context, PhotoFrameWidgetProvider.class));
        for (int id : ids) {
            manager.updateAppWidget(id, buildViews(context, manager, id));
            scheduleIfNeeded(context, id);
        }
    }

    static RemoteViews buildViews(Context context, AppWidgetManager manager, int appWidgetId) {
        int[] size = bitmapSize(context, manager, appWidgetId);
        List<Uri> uris = PhotoFramePreferences.uris(context, appWidgetId);
        int index = PhotoFramePreferences.index(context, appWidgetId, uris.size());
        boolean slideshow = PhotoFramePreferences.slideshow(context, appWidgetId);
        Uri uri = uris.isEmpty() ? null : uris.get(index);
        String label = uris.isEmpty()
                ? context.getString(R.string.photo_widget_empty)
                : (slideshow ? context.getString(R.string.photo_widget_slideshow) : context.getString(R.string.photo_widget_static));

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_photo_frame);
        views.setImageViewBitmap(R.id.photo_widget_image, PhotoFrameRenderer.render(context, uri, size[0], size[1], label));
        views.setOnClickPendingIntent(R.id.photo_widget_root, openGooglePhotosIntent(context));
        return views;
    }

    private static PendingIntent openGooglePhotosIntent(Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.google.android.apps.photos");
        if (intent == null) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://photos.google.com/"));
        }
        return PendingIntent.getActivity(
                context,
                52000,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    private static int[] bitmapSize(Context context, AppWidgetManager manager, int appWidgetId) {
        Bundle options = manager.getAppWidgetOptions(appWidgetId);
        int widthDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, 320);
        int heightDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, 220);
        int width = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Math.max(180, widthDp), context.getResources().getDisplayMetrics()));
        int height = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Math.max(140, heightDp), context.getResources().getDisplayMetrics()));
        return new int[]{Math.min(width, 1600), Math.min(height, 1000)};
    }

    private static void scheduleIfNeeded(Context context, int appWidgetId) {
        List<Uri> uris = PhotoFramePreferences.uris(context, appWidgetId);
        if (!PhotoFramePreferences.slideshow(context, appWidgetId) || uris.size() < 2) {
            cancelSlideshow(context, appWidgetId);
            return;
        }
        AlarmManager alarmManager = context.getSystemService(AlarmManager.class);
        if (alarmManager == null) {
            return;
        }
        long delayMillis = PhotoFramePreferences.intervalMinutes(context, appWidgetId) * 60_000L;
        alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + delayMillis,
                slideshowIntent(context, appWidgetId)
        );
    }

    private static void cancelSlideshow(Context context, int appWidgetId) {
        AlarmManager alarmManager = context.getSystemService(AlarmManager.class);
        if (alarmManager != null) {
            alarmManager.cancel(slideshowIntent(context, appWidgetId));
        }
    }

    private static PendingIntent slideshowIntent(Context context, int appWidgetId) {
        Intent intent = new Intent(context, PhotoFrameWidgetProvider.class)
                .setAction(ACTION_NEXT_PHOTO)
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        return PendingIntent.getBroadcast(
                context,
                52000 + appWidgetId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }
}
