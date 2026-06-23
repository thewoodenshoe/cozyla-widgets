package com.cozyla.widgets.weather;

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

public class WeatherConfigureActivity extends Activity {
    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private EditText cityInput;
    private EditText tideStationInput;
    private TextView statusView;
    private Button saveButton;

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
        title.setText(R.string.weather_config_title);
        title.setTextSize(28);
        title.setTextColor(getColor(R.color.calendar_config_text));
        title.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        root.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText(R.string.weather_config_subtitle);
        subtitle.setTextSize(15);
        subtitle.setTextColor(getColor(R.color.calendar_config_secondary));
        subtitle.setPadding(0, 8, 0, 24);
        root.addView(subtitle);

        cityInput = input(R.string.weather_config_city, InputType.TYPE_CLASS_TEXT);
        cityInput.setText(WeatherPreferences.place(this, appWidgetId));
        root.addView(cityInput, fieldParams());

        Button search = new Button(this);
        search.setText(R.string.weather_config_search);
        search.setOnClickListener(v -> resolveCity(false));
        root.addView(search, fieldParams());

        tideStationInput = input(R.string.weather_config_tide_station, InputType.TYPE_CLASS_TEXT);
        tideStationInput.setText(WeatherPreferences.tideStation(this, appWidgetId));
        root.addView(tideStationInput, fieldParams());

        statusView = new TextView(this);
        statusView.setTextColor(getColor(R.color.calendar_widget_now));
        statusView.setPadding(0, 8, 0, 8);
        root.addView(statusView);

        LinearLayout buttons = new LinearLayout(this);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        buttons.setPadding(0, 16, 0, 0);

        Button cancel = new Button(this);
        cancel.setText(R.string.weather_config_cancel);
        cancel.setOnClickListener(v -> finish());
        buttons.addView(cancel, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        saveButton = new Button(this);
        saveButton.setText(R.string.weather_config_save);
        saveButton.setOnClickListener(v -> resolveCity(true));
        buttons.addView(saveButton, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        root.addView(buttons);

        setContentView(root);
    }

    private EditText input(int hint, int inputType) {
        EditText input = new EditText(this);
        input.setHint(hint);
        input.setSingleLine(true);
        input.setInputType(inputType);
        return input;
    }

    private static LinearLayout.LayoutParams fieldParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 12);
        return params;
    }

    private void resolveCity(boolean saveAfterResolve) {
        String city = cityInput.getText().toString().trim();
        if (city.isEmpty()) {
            statusView.setText(R.string.weather_config_invalid_city);
            return;
        }

        setBusy(true, R.string.weather_config_searching);
        new Thread(() -> {
            try {
                WeatherApiClient.LocationResult result = WeatherApiClient.geocodeCity(city);
                runOnUiThread(() -> {
                    cityInput.setText(result.displayName);
                    statusView.setText(getString(R.string.weather_config_found, result.displayName));
                    setBusy(false, 0);
                    if (saveAfterResolve) {
                        save(result);
                    }
                });
            } catch (Exception ex) {
                runOnUiThread(() -> {
                    setBusy(false, 0);
                    statusView.setText(R.string.weather_config_city_not_found);
                });
            }
        }, "weather-city-search").start();
    }

    private void save(WeatherApiClient.LocationResult location) {
        WeatherPreferences.saveConfig(
                this,
                appWidgetId,
                location.displayName,
                coordinate(location.latitude),
                coordinate(location.longitude),
                tideStationInput.getText().toString()
        );
        WeatherWidgetProvider.updateWidget(this, appWidgetId);
        WeatherWidgetUpdateJobService.schedule(this);

        Intent result = new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, result);
        finish();
    }

    private void setBusy(boolean busy, int statusText) {
        saveButton.setEnabled(!busy);
        if (statusText != 0) {
            statusView.setText(statusText);
        }
    }

    private static String coordinate(double value) {
        return String.format(java.util.Locale.US, "%.6f", value);
    }
}
