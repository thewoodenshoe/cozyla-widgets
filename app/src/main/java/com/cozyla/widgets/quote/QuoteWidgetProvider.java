package com.cozyla.widgets.quote;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import com.cozyla.widgets.MainActivity;
import com.cozyla.widgets.R;

import java.util.Date;
import java.util.TimeZone;

public class QuoteWidgetProvider extends AppWidgetProvider {
    private static final String PREFERENCES = "quote_widget_preferences";
    private static final String KEY_QUOTE = "quote";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (Intent.ACTION_MY_PACKAGE_REPLACED.equals(intent.getAction())) {
            refreshAllWidgets(context);
            QuoteWidgetUpdateJobService.schedule(context);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, buildViews(context));
        }
        QuoteWidgetUpdateJobService.schedule(context);
    }

    public static void saveQuote(Context context, String quote) {
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_QUOTE, quote)
                .apply();
    }

    public static void refreshAllWidgets(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName provider = new ComponentName(context, QuoteWidgetProvider.class);
        int[] widgetIds = appWidgetManager.getAppWidgetIds(provider);
        for (int appWidgetId : widgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, buildViews(context));
        }
    }

    private static RemoteViews buildViews(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_quote);
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        views.setTextViewText(R.id.quote_widget_title, context.getString(R.string.quote_widget_title));
        views.setTextViewText(
                R.id.quote_widget_text,
                preferences.getString(
                        KEY_QUOTE,
                        DailyQuote.fallbackQuoteFor(new Date(), TimeZone.getDefault())
                )
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
