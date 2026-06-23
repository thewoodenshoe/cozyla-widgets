package com.cozyla.widgets.quote;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.cozyla.widgets.MainActivity;
import com.cozyla.widgets.R;

import java.util.Date;
import java.util.TimeZone;

public class QuoteWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, buildViews(context));
        }
    }

    private static RemoteViews buildViews(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_quote);
        views.setTextViewText(R.id.quote_widget_title, context.getString(R.string.quote_widget_title));
        views.setTextViewText(
                R.id.quote_widget_text,
                DailyQuote.quoteFor(new Date(), TimeZone.getDefault())
        );

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                20,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        views.setOnClickPendingIntent(R.id.quote_widget_root, pendingIntent);
        return views;
    }
}
