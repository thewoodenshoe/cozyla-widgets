package com.cozyla.widgets.chores;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.cozyla.widgets.R;

import java.util.ArrayList;
import java.util.List;

public class ChoreWheelConfigureActivity extends Activity {
    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private TextView statusView;
    private CheckBox includeNoChores;
    private final List<EditText> choreInputs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
        setContentView(R.layout.activity_chore_wheel_configure);

        appWidgetId = getIntent().getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
        );
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        statusView = findViewById(R.id.chore_config_status);
        includeNoChores = findViewById(R.id.chore_include_no_chores);
        bindInputs();
        prefillInputs();
        includeNoChores.setOnCheckedChangeListener((button, checked) -> updateSlotEightState());

        Button cancelButton = findViewById(R.id.chore_config_cancel);
        Button saveButton = findViewById(R.id.chore_config_save);
        cancelButton.setOnClickListener(view -> finish());
        saveButton.setOnClickListener(view -> saveConfiguration());
    }

    private void bindInputs() {
        choreInputs.add(findViewById(R.id.chore_input_1));
        choreInputs.add(findViewById(R.id.chore_input_2));
        choreInputs.add(findViewById(R.id.chore_input_3));
        choreInputs.add(findViewById(R.id.chore_input_4));
        choreInputs.add(findViewById(R.id.chore_input_5));
        choreInputs.add(findViewById(R.id.chore_input_6));
        choreInputs.add(findViewById(R.id.chore_input_7));
        choreInputs.add(findViewById(R.id.chore_input_8));
    }

    private void prefillInputs() {
        List<String> chores = ChoreWheelPreferences.chores(this, appWidgetId);
        includeNoChores.setChecked(ChoreWheelPreferences.includeNoChores(this, appWidgetId));
        for (int index = 0; index < chores.size() && index < choreInputs.size(); index++) {
            choreInputs.get(index).setText(chores.get(index));
        }
        updateSlotEightState();
    }

    private void saveConfiguration() {
        List<String> chores = new ArrayList<>();
        int editableSlots = includeNoChores.isChecked()
                ? ChoreWheelPreferences.MAX_CHORES - 1
                : ChoreWheelPreferences.MAX_CHORES;
        for (int index = 0; index < editableSlots; index++) {
            chores.add(choreInputs.get(index).getText().toString());
        }
        List<String> sanitized = ChoreWheelPreferences.sanitize(chores, editableSlots);
        boolean enoughChoices = includeNoChores.isChecked()
                ? sanitized.size() >= 1
                : sanitized.size() >= 2;
        if (!enoughChoices) {
            statusView.setText(includeNoChores.isChecked()
                    ? R.string.chore_config_need_one_with_no_chores
                    : R.string.chore_config_need_two);
            return;
        }

        ChoreWheelPreferences.save(this, appWidgetId, sanitized, includeNoChores.isChecked());
        ChoreWheelProvider.updateWidget(this, appWidgetId);

        Intent result = new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, result);
        finish();
    }

    private void updateSlotEightState() {
        EditText slotEight = choreInputs.get(ChoreWheelPreferences.MAX_CHORES - 1);
        boolean enabled = !includeNoChores.isChecked();
        slotEight.setEnabled(enabled);
        slotEight.setText(enabled ? slotEight.getText() : "");
        slotEight.setHint(enabled
                ? getString(R.string.chore_config_item_hint)
                : getString(R.string.chore_config_no_chores_slot_hint));
    }
}
