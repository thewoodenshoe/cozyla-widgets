package com.cozyla.widgets.chores;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.cozyla.widgets.R;

import java.security.SecureRandom;
import java.util.List;

public class ChoreWheelProvider extends AppWidgetProvider {
    private static final String ACTION_SPIN = "com.cozyla.widgets.chores.ACTION_SPIN";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, buildViews(context, appWidgetId));
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (!ACTION_SPIN.equals(intent.getAction())) {
            return;
        }
        int appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
        );
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            return;
        }

        List<String> chores = ChoreWheelPreferences.chores(context, appWidgetId);
        int selected = RANDOM.nextInt(chores.size());
        ChoreWheelPreferences.saveSelectedIndex(context, appWidgetId, selected);
        updateWidget(context, appWidgetId);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            ChoreWheelPreferences.delete(context, appWidgetId);
        }
    }

    public static void updateWidget(Context context, int appWidgetId) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(appWidgetId, buildViews(context, appWidgetId));
    }

    private static RemoteViews buildViews(Context context, int appWidgetId) {
        List<String> chores = ChoreWheelPreferences.chores(context, appWidgetId);
        int selected = ChoreWheelPreferences.selectedIndex(context, appWidgetId);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_chore_wheel);
        views.setTextViewText(R.id.chore_widget_title, context.getString(R.string.chore_widget_title));
        views.setTextViewText(R.id.chore_widget_selected, chores.get(selected));
        views.setTextViewText(R.id.chore_widget_list, listText(chores));
        views.setImageViewBitmap(R.id.chore_widget_wheel, ChoreWheelRenderer.render(chores, selected));

        PendingIntent spinIntent = PendingIntent.getBroadcast(
                context,
                appWidgetId,
                new Intent(context, ChoreWheelProvider.class)
                        .setAction(ACTION_SPIN)
                        .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        views.setOnClickPendingIntent(R.id.chore_widget_wheel, spinIntent);
        views.setOnClickPendingIntent(R.id.chore_widget_spin, spinIntent);
        return views;
    }

    private static String listText(List<String> chores) {
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < chores.size(); index++) {
            if (index > 0) {
                builder.append('\n');
            }
            builder.append(index + 1).append(". ").append(chores.get(index));
        }
        return builder.toString();
    }
}
