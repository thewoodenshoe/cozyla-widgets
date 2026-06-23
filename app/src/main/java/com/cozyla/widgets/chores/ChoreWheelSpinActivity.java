package com.cozyla.widgets.chores;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cozyla.widgets.R;

import java.security.SecureRandom;
import java.util.List;

public class ChoreWheelSpinActivity extends Activity {
    private static final SecureRandom RANDOM = new SecureRandom();
    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private List<String> labels;
    private SpinWheelView wheelView;
    private TextView resultView;
    private Button doneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        appWidgetId = getIntent().getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
        );
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        labels = ChoreWheelPreferences.wheelLabels(this, appWidgetId);
        buildLayout();
        wheelView.post(this::startSpin);
    }

    private void buildLayout() {
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(getColor(R.color.chore_spin_background));

        wheelView = new SpinWheelView(this);
        FrameLayout.LayoutParams wheelParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        wheelParams.setMargins(dp(28), dp(42), dp(28), dp(120));
        root.addView(wheelView, wheelParams);

        resultView = new TextView(this);
        resultView.setText(R.string.chore_spin_spinning);
        resultView.setTextColor(getColor(R.color.chore_widget_text));
        resultView.setTextSize(28);
        resultView.setGravity(Gravity.CENTER);
        resultView.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        resultView.setShadowLayer(6f, 0f, 3f, android.graphics.Color.BLACK);
        FrameLayout.LayoutParams resultParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL
        );
        resultParams.setMargins(dp(24), 0, dp(24), dp(62));
        root.addView(resultView, resultParams);

        doneButton = new Button(this);
        doneButton.setText(R.string.chore_spin_done);
        doneButton.setEnabled(false);
        doneButton.setOnClickListener(view -> finish());
        FrameLayout.LayoutParams buttonParams = new FrameLayout.LayoutParams(
                dp(220),
                dp(56),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL
        );
        buttonParams.setMargins(0, 0, 0, dp(14));
        root.addView(doneButton, buttonParams);

        setContentView(root);
    }

    private void startSpin() {
        int selectedIndex = RANDOM.nextInt(labels.size());
        float sweep = 360f / labels.size();
        boolean closeCall = labels.size() > 1 && RANDOM.nextBoolean();
        float offset = 0f;
        if (closeCall) {
            float margin = 1.5f + RANDOM.nextFloat() * 4.0f;
            offset = (RANDOM.nextBoolean() ? 1f : -1f) * ((sweep / 2f) - margin);
        }

        float selectedCenter = normalizeDegrees(-selectedIndex * sweep);
        float finalRotation = ((6 + RANDOM.nextInt(4)) * 360f) + selectedCenter + offset;
        ValueAnimator animator = ValueAnimator.ofFloat(0f, finalRotation);
        animator.setDuration(4600L);
        animator.setInterpolator(new DecelerateInterpolator(2.35f));
        animator.addUpdateListener(animation -> wheelView.setWheelRotation((Float) animation.getAnimatedValue()));
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ChoreWheelPreferences.saveSelectedIndex(
                        ChoreWheelSpinActivity.this,
                        appWidgetId,
                        selectedIndex
                );
                ChoreWheelProvider.updateWidget(ChoreWheelSpinActivity.this, appWidgetId);
                resultView.setText(getString(R.string.chore_spin_result, labels.get(selectedIndex)));
                doneButton.setEnabled(true);
            }
        });
        animator.start();
    }

    private static float normalizeDegrees(float degrees) {
        float normalized = degrees % 360f;
        return normalized < 0f ? normalized + 360f : normalized;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private final class SpinWheelView extends View {
        private float wheelRotation;

        SpinWheelView(Activity context) {
            super(context);
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }

        void setWheelRotation(float wheelRotation) {
            this.wheelRotation = wheelRotation;
            invalidate();
        }

        @Override
        protected void onDraw(android.graphics.Canvas canvas) {
            super.onDraw(canvas);
            ChoreWheelPainter.drawWheel(canvas, labels, wheelRotation);
        }
    }
}
