package com.cozyla.widgets.countdown;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;

public class CountdownActionActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyAction(getIntent());
        finish();
        overridePendingTransition(0, 0);
    }

    private void applyAction(Intent intent) {
        if (intent == null) {
            return;
        }
        int appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
        );
        CountdownWidgetProvider.handleAction(this, intent.getAction(), appWidgetId);
    }
}
