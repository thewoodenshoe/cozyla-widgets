package com.cozyla.widgets.chores;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.cozyla.widgets.R;

import java.util.List;

public class ChoreWheelProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, buildViews(context, appWidgetId));
        }
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
        List<ChoreWheelSlot> slots = ChoreWheelPreferences.wheelSlots(context, appWidgetId);
        List<String> chores = ChoreWheelSlot.labels(slots);
        int selected = ChoreWheelPreferences.selectedIndex(context, appWidgetId);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_chore_wheel);
        views.setTextViewText(R.id.chore_widget_title, context.getString(R.string.chore_widget_title));
        views.setTextViewText(R.id.chore_widget_selected, chores.get(selected));
        views.setTextViewText(R.id.chore_widget_list, listText(chores));
        views.setImageViewBitmap(R.id.chore_widget_wheel, ChoreWheelRenderer.render(chores, selected));

        PendingIntent spinIntent = PendingIntent.getActivity(
                context,
                appWidgetId,
                new Intent(context, ChoreWheelSpinActivity.class)
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
