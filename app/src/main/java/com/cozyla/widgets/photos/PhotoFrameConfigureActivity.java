package com.cozyla.widgets.photos;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cozyla.widgets.R;

import java.util.ArrayList;
import java.util.List;

public class PhotoFrameConfigureActivity extends Activity {
    private static final int REQUEST_PICK_PHOTOS = 5201;
    private static final int REQUEST_BROWSE_PHOTOS = 5202;
    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private final List<Uri> selectedUris = new ArrayList<>();
    private TextView statusView;
    private CheckBox slideshowInput;
    private EditText intervalInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
        appWidgetId = getIntent().getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }
        selectedUris.addAll(PhotoFramePreferences.uris(this, appWidgetId));
        buildContent();
    }

    private void buildContent() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(36, 36, 36, 36);

        TextView title = new TextView(this);
        title.setText(R.string.photo_config_title);
        title.setTextSize(28);
        title.setTextColor(getColor(R.color.calendar_config_text));
        title.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        root.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText(R.string.photo_config_subtitle);
        subtitle.setTextSize(15);
        subtitle.setTextColor(getColor(R.color.calendar_config_secondary));
        subtitle.setPadding(0, 8, 0, 24);
        root.addView(subtitle);

        Button pick = new Button(this);
        pick.setText(R.string.photo_config_pick);
        pick.setOnClickListener(v -> launchPhotoPicker());
        root.addView(pick, fieldParams());

        Button browse = new Button(this);
        browse.setText(R.string.photo_config_browse_files);
        browse.setOnClickListener(v -> launchDocumentPicker());
        root.addView(browse, fieldParams());

        slideshowInput = new CheckBox(this);
        slideshowInput.setText(R.string.photo_config_slideshow);
        slideshowInput.setChecked(PhotoFramePreferences.slideshow(this, appWidgetId));
        root.addView(slideshowInput, fieldParams());

        intervalInput = new EditText(this);
        intervalInput.setHint(R.string.photo_config_interval);
        intervalInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        intervalInput.setSingleLine(true);
        intervalInput.setText(Integer.toString(PhotoFramePreferences.intervalMinutes(this, appWidgetId)));
        root.addView(intervalInput, fieldParams());

        statusView = new TextView(this);
        statusView.setText(selectedUris.isEmpty()
                ? getString(R.string.photo_config_no_album)
                : getString(R.string.photo_config_selected, selectedUris.size()));
        statusView.setTextColor(getColor(R.color.calendar_config_secondary));
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
        save.setText(R.string.photo_config_save);
        save.setOnClickListener(v -> save());
        buttons.addView(save, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        root.addView(buttons);
        setContentView(root);
    }

    private void launchPhotoPicker() {
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
            intent.putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX, Math.min(MediaStore.getPickImagesMaxLimit(), 50));
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_PICK_PHOTOS);
    }

    private void launchDocumentPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_BROWSE_PHOTOS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode != REQUEST_PICK_PHOTOS && requestCode != REQUEST_BROWSE_PHOTOS)
                || resultCode != RESULT_OK
                || data == null) {
            return;
        }
        selectedUris.clear();
        ClipData clipData = data.getClipData();
        if (clipData != null) {
            for (int index = 0; index < clipData.getItemCount(); index++) {
                addUri(clipData.getItemAt(index).getUri());
            }
        } else {
            addUri(data.getData());
        }
        statusView.setText(selectedUris.isEmpty()
                ? getString(R.string.photo_config_no_album)
                : getString(R.string.photo_config_selected, selectedUris.size()));
    }

    private void addUri(Uri uri) {
        if (uri == null) {
            return;
        }
        try {
            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (SecurityException ignored) {
            // Photo Picker grants may be durable without persistable URI permission.
        }
        selectedUris.add(uri);
    }

    private void save() {
        if (selectedUris.isEmpty()) {
            statusView.setTextColor(getColor(com.cozyla.widgets.R.color.calendar_widget_now));
            statusView.setText(R.string.photo_config_no_album);
            return;
        }
        int interval;
        try {
            interval = Integer.parseInt(intervalInput.getText().toString().trim());
        } catch (NumberFormatException ex) {
            interval = PhotoFramePreferences.DEFAULT_INTERVAL_MINUTES;
        }
        PhotoFramePreferences.save(this, appWidgetId, selectedUris, slideshowInput.isChecked(), interval);
        PhotoFrameWidgetProvider.updateWidget(this, appWidgetId);
        Intent result = new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, result);
        finish();
    }

    private static LinearLayout.LayoutParams fieldParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 12);
        return params;
    }
}
