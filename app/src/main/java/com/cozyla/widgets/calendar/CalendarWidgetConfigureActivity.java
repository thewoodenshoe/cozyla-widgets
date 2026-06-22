package com.cozyla.widgets.calendar;

import android.Manifest;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.cozyla.widgets.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CalendarWidgetConfigureActivity extends Activity {
    private static final int CALENDAR_PERMISSION_REQUEST = 4102;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private TextView statusView;
    private Button grantButton;
    private Button saveButton;
    private ListView calendarList;
    private RadioButton weekMode;
    private RadioButton workweekMode;
    private Spinner startTimeSpinner;
    private Spinner endTimeSpinner;
    private List<CalendarRepository.CalendarChoice> calendarChoices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
        setContentView(R.layout.activity_calendar_widget_configure);

        appWidgetId = getIntent().getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
        );
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        statusView = findViewById(R.id.calendar_config_status);
        grantButton = findViewById(R.id.calendar_grant_permission);
        saveButton = findViewById(R.id.calendar_config_save);
        calendarList = findViewById(R.id.calendar_list);
        weekMode = findViewById(R.id.calendar_mode_week);
        workweekMode = findViewById(R.id.calendar_mode_workweek);
        startTimeSpinner = findViewById(R.id.calendar_start_time);
        endTimeSpinner = findViewById(R.id.calendar_end_time);

        CalendarWidgetMode storedMode = CalendarWidgetPreferences.mode(this, appWidgetId);
        weekMode.setChecked(storedMode == CalendarWidgetMode.WEEK);
        workweekMode.setChecked(storedMode == CalendarWidgetMode.WORKWEEK);
        configureTimeSpinners(CalendarWidgetPreferences.displayRange(this, appWidgetId));
        if (CalendarWidgetPreferences.isConfigured(this, appWidgetId)) {
            saveButton.setText(R.string.calendar_config_update);
        }

        findViewById(R.id.calendar_config_cancel).setOnClickListener(view -> finish());
        grantButton.setOnClickListener(view -> requestCalendarPermission());
        saveButton.setOnClickListener(view -> saveConfiguration());

        if (hasCalendarPermission()) {
            loadCalendars();
        } else {
            showPermissionRequired(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != CALENDAR_PERMISSION_REQUEST) {
            return;
        }

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadCalendars();
        } else {
            showPermissionRequired(false);
        }
    }

    @Override
    protected void onDestroy() {
        executor.shutdownNow();
        super.onDestroy();
    }

    private void requestCalendarPermission() {
        requestPermissions(
                new String[]{Manifest.permission.READ_CALENDAR},
                CALENDAR_PERMISSION_REQUEST
        );
    }

    private boolean hasCalendarPermission() {
        return checkSelfPermission(Manifest.permission.READ_CALENDAR)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void showPermissionRequired(boolean firstRequest) {
        statusView.setText(firstRequest
                ? R.string.calendar_config_permission_explanation
                : R.string.calendar_config_permission_denied);
        statusView.setVisibility(View.VISIBLE);
        grantButton.setVisibility(View.VISIBLE);
        calendarList.setVisibility(View.GONE);
        saveButton.setEnabled(false);
    }

    private void loadCalendars() {
        statusView.setText(R.string.calendar_config_loading);
        statusView.setVisibility(View.VISIBLE);
        grantButton.setVisibility(View.GONE);
        calendarList.setVisibility(View.GONE);
        saveButton.setEnabled(false);

        executor.execute(() -> {
            try {
                List<CalendarRepository.CalendarChoice> loaded = new CalendarRepository(
                        getContentResolver()
                ).loadVisibleCalendars();
                runOnUiThread(() -> showCalendars(loaded));
            } catch (RuntimeException error) {
                runOnUiThread(() -> {
                    statusView.setText(R.string.calendar_config_load_failed);
                    statusView.setVisibility(View.VISIBLE);
                });
            }
        });
    }

    private void showCalendars(List<CalendarRepository.CalendarChoice> loaded) {
        if (isFinishing() || isDestroyed()) {
            return;
        }

        calendarChoices = loaded;
        if (loaded.isEmpty()) {
            statusView.setText(R.string.calendar_config_no_calendars);
            statusView.setVisibility(View.VISIBLE);
            calendarList.setVisibility(View.GONE);
            saveButton.setEnabled(false);
            return;
        }

        List<String> labels = new ArrayList<>();
        for (CalendarRepository.CalendarChoice calendar : loaded) {
            labels.add(calendar.label());
        }
        calendarList.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_multiple_choice,
                labels
        ));

        Set<Long> storedIds = CalendarWidgetPreferences.calendarIds(this, appWidgetId);
        boolean selectAll = !CalendarWidgetPreferences.isConfigured(this, appWidgetId);
        for (int index = 0; index < loaded.size(); index++) {
            calendarList.setItemChecked(index, selectAll || storedIds.contains(loaded.get(index).id));
        }

        statusView.setVisibility(View.GONE);
        calendarList.setVisibility(View.VISIBLE);
        saveButton.setEnabled(true);
    }

    private void saveConfiguration() {
        Set<Long> selectedIds = new HashSet<>();
        for (int index = 0; index < calendarChoices.size(); index++) {
            if (calendarList.isItemChecked(index)) {
                selectedIds.add(calendarChoices.get(index).id);
            }
        }

        if (selectedIds.isEmpty()) {
            statusView.setText(R.string.calendar_config_select_one);
            statusView.setVisibility(View.VISIBLE);
            return;
        }

        CalendarWidgetMode mode = workweekMode.isChecked()
                ? CalendarWidgetMode.WORKWEEK
                : CalendarWidgetMode.WEEK;
        int startHour = startTimeSpinner.getSelectedItemPosition();
        int endHour = endTimeSpinner.getSelectedItemPosition() + 1;
        if (!CalendarDisplayRange.isValid(startHour, endHour)) {
            statusView.setText(R.string.calendar_config_invalid_time_range);
            statusView.setVisibility(View.VISIBLE);
            return;
        }
        CalendarWidgetPreferences.save(
                this,
                appWidgetId,
                mode,
                selectedIds,
                CalendarDisplayRange.of(startHour, endHour)
        );
        CalendarWidgetUpdateScheduler.schedule(this, appWidgetId);

        Intent result = new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, result);
        finish();
    }

    private void configureTimeSpinners(CalendarDisplayRange displayRange) {
        List<String> startLabels = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            startLabels.add(formatHour(hour, false));
        }
        List<String> endLabels = new ArrayList<>();
        for (int hour = 1; hour <= 24; hour++) {
            endLabels.add(formatHour(hour, hour == 24));
        }

        startTimeSpinner.setAdapter(timeAdapter(startLabels));
        endTimeSpinner.setAdapter(timeAdapter(endLabels));
        startTimeSpinner.setSelection(displayRange.startHour);
        endTimeSpinner.setSelection(displayRange.endHour - 1);
    }

    private ArrayAdapter<String> timeAdapter(List<String> labels) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                labels
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    private String formatHour(int hour, boolean midnightEnd) {
        boolean use24HourTime = android.text.format.DateFormat.is24HourFormat(this);
        SimpleDateFormat formatter = new SimpleDateFormat(
                use24HourTime ? "HH:mm" : "h:mm a",
                Locale.getDefault()
        );
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(2026, Calendar.JANUARY, 1, hour % 24, 0, 0);
        String label = formatter.format(calendar.getTime());
        return midnightEnd
                ? getString(R.string.calendar_config_midnight_end, label)
                : label;
    }
}
