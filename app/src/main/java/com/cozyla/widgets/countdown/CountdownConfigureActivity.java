package com.cozyla.widgets.countdown;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cozyla.widgets.R;

public class CountdownConfigureActivity extends Activity {
    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private EditText labelInput;
    private EditText minutesInput;
    private TextView statusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);

        appWidgetId = getIntent().getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
        );
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        buildContent();
    }

    private void buildContent() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(36, 36, 36, 36);

        TextView title = new TextView(this);
        title.setText(R.string.countdown_config_title);
        title.setTextSize(28);
        title.setTextColor(getColor(R.color.calendar_config_text));
        title.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        root.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText(R.string.countdown_config_subtitle);
        subtitle.setTextSize(15);
        subtitle.setTextColor(getColor(R.color.calendar_config_secondary));
        subtitle.setPadding(0, 8, 0, 24);
        root.addView(subtitle);

        labelInput = new EditText(this);
        labelInput.setHint(R.string.countdown_config_label);
        labelInput.setSingleLine(true);
        labelInput.setText(CountdownPreferences.label(this, appWidgetId));
        root.addView(labelInput, fieldParams());

        minutesInput = new EditText(this);
        minutesInput.setHint(R.string.countdown_config_minutes);
        minutesInput.setSingleLine(true);
        minutesInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        minutesInput.setText("25");
        root.addView(minutesInput, fieldParams());

        statusView = new TextView(this);
        statusView.setTextColor(getColor(R.color.calendar_widget_now));
        statusView.setPadding(0, 8, 0, 8);
        root.addView(statusView);

        LinearLayout buttons = new LinearLayout(this);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        buttons.setPadding(0, 16, 0, 0);

        Button cancel = new Button(this);
        cancel.setText(R.string.countdown_config_cancel);
        cancel.setOnClickListener(v -> finish());
        buttons.addView(cancel, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        Button save = new Button(this);
        save.setText(R.string.countdown_config_save);
        save.setOnClickListener(v -> save());
        buttons.addView(save, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        root.addView(buttons);

        setContentView(root);
    }

    private static LinearLayout.LayoutParams fieldParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 12);
        return params;
    }

    private void save() {
        String label = labelInput.getText().toString().trim();
        String minutesText = minutesInput.getText().toString().trim();
        long minutes;
        try {
            minutes = Long.parseLong(minutesText);
        } catch (NumberFormatException ex) {
            minutes = 0L;
        }

        if (label.isEmpty() || minutes <= 0L) {
            statusView.setText(R.string.countdown_config_invalid);
            return;
        }

        long targetMillis = System.currentTimeMillis() + minutes * 60_000L;
        CountdownPreferences.save(this, appWidgetId, label, targetMillis);
        CountdownWidgetProvider.updateWidget(this, appWidgetId);

        Intent result = new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, result);
        finish();
    }
}
