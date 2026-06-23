package com.cozyla.widgets;

import android.app.Activity;
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

        layout.addView(title);
        layout.addView(body);
        setContentView(layout);
    }
}
