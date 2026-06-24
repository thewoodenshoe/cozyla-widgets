package com.cozyla.widgets;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WidgetRefreshReceiver.refreshAllWidgets(this);

        LinearLayout layout = new LinearLayout(this);
        layout.setGravity(Gravity.CENTER);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 48, 48, 48);

        TextView title = new TextView(this);
        title.setText(R.string.app_name);
        title.setTextSize(28);
        title.setGravity(Gravity.CENTER);

        TextView body = new TextView(this);
        body.setText(R.string.app_status);
        body.setTextSize(16);
        body.setGravity(Gravity.CENTER);
        body.setPadding(0, 20, 0, 0);

        TextView version = new TextView(this);
        version.setText(versionText());
        version.setTextSize(14);
        version.setGravity(Gravity.CENTER);
        version.setPadding(0, 16, 0, 0);

        layout.addView(title);
        layout.addView(body);
        layout.addView(version);
        setContentView(layout);
    }

    private String versionText() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            long code = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
                    ? info.getLongVersionCode()
                    : info.versionCode;
            return getString(R.string.app_version, info.versionName, code);
        } catch (Exception ex) {
            return getString(R.string.app_version, "unknown", 0);
        }
    }
}
