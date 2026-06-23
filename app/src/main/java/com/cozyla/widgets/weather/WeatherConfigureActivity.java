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
    private EditText placeInput;
    private EditText latitudeInput;
    private EditText longitudeInput;
    private EditText tideStationInput;
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

        placeInput = input(R.string.weather_config_place, InputType.TYPE_CLASS_TEXT);
        placeInput.setText(WeatherPreferences.place(this, appWidgetId));
        root.addView(placeInput, fieldParams());

        latitudeInput = input(R.string.weather_config_latitude, InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        latitudeInput.setText(WeatherPreferences.latitude(this, appWidgetId));
        root.addView(latitudeInput, fieldParams());

        longitudeInput = input(R.string.weather_config_longitude, InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        longitudeInput.setText(WeatherPreferences.longitude(this, appWidgetId));
        root.addView(longitudeInput, fieldParams());

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

        Button save = new Button(this);
        save.setText(R.string.weather_config_save);
        save.setOnClickListener(v -> save());
        buttons.addView(save, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
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

    private void save() {
        String place = placeInput.getText().toString().trim();
        String latitude = latitudeInput.getText().toString().trim();
        String longitude = longitudeInput.getText().toString().trim();
        if (place.isEmpty() || !validCoordinate(latitude, -90d, 90d) || !validCoordinate(longitude, -180d, 180d)) {
            statusView.setText(R.string.weather_config_invalid);
            return;
        }

        WeatherPreferences.saveConfig(this, appWidgetId, place, latitude, longitude, tideStationInput.getText().toString());
        WeatherWidgetProvider.updateWidget(this, appWidgetId);
        WeatherWidgetUpdateJobService.schedule(this);

        Intent result = new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, result);
        finish();
    }

    private static boolean validCoordinate(String value, double min, double max) {
        try {
            double coordinate = Double.parseDouble(value);
            return coordinate >= min && coordinate <= max;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
