package com.cozyla.widgets.chores;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cozyla.widgets.R;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class ChoreWheelSpinActivity extends Activity {
    private static final SecureRandom RANDOM = new SecureRandom();
    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private List<ChoreWheelSlot> slots;
    private FrameLayout root;
    private SpinWheelView wheelView;
    private FireworksView fireworksView;
    private TextView resultView;
    private Button doneButton;
    private ToneGenerator toneGenerator;
    private final Handler handler = new Handler(Looper.getMainLooper());

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

        slots = ChoreWheelPreferences.wheelSlots(this, appWidgetId);
        buildLayout();
        wheelView.post(this::startSpin);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        if (toneGenerator != null) {
            toneGenerator.release();
        }
        super.onDestroy();
    }

    private void buildLayout() {
        root = new FrameLayout(this);
        root.setBackgroundColor(getColor(R.color.chore_spin_background));

        wheelView = new SpinWheelView(this);
        FrameLayout.LayoutParams wheelParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        wheelParams.setMargins(dp(28), dp(42), dp(28), dp(120));
        root.addView(wheelView, wheelParams);

        fireworksView = new FireworksView(this);
        fireworksView.setVisibility(View.GONE);
        root.addView(fireworksView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));

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
        int targetIndex = RANDOM.nextInt(slots.size());
        float sweep = 360f / slots.size();
        boolean closeCall = slots.size() > 1 && RANDOM.nextBoolean();
        float offset = 0f;
        if (closeCall) {
            float margin = 1.5f + RANDOM.nextFloat() * 4.0f;
            offset = (RANDOM.nextBoolean() ? 1f : -1f) * ((sweep / 2f) - margin);
        }

        float selectedCenter = ChoreWheelMath.rotationForIndex(targetIndex, slots.size());
        float finalRotation = ((6 + RANDOM.nextInt(4)) * 360f) + selectedCenter + offset;
        ValueAnimator animator = ValueAnimator.ofFloat(0f, finalRotation);
        animator.setDuration(4600L);
        animator.setInterpolator(new DecelerateInterpolator(2.35f));
        animator.addUpdateListener(animation -> wheelView.setWheelRotation((Float) animation.getAnimatedValue()));
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                int actualIndex = ChoreWheelMath.indexAtPointer(finalRotation, slots.size());
                ChoreWheelSlot actualSlot = slots.get(actualIndex);
                ChoreWheelPreferences.saveSelectedIndex(
                        ChoreWheelSpinActivity.this,
                        appWidgetId,
                        actualIndex
                );
                ChoreWheelProvider.updateWidget(ChoreWheelSpinActivity.this, appWidgetId);
                resultView.setText(actualSlot.noChores
                        ? getString(R.string.chore_spin_no_chores_result)
                        : getString(R.string.chore_spin_result, actualSlot.label));
                if (actualSlot.noChores) {
                    celebrateNoChores();
                }
                doneButton.setEnabled(true);
            }
        });
        animator.start();
    }

    private void celebrateNoChores() {
        fireworksView.start();
        toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 70);
        int[] delays = {0, 140, 280, 520, 760};
        int[] tones = {
                ToneGenerator.TONE_PROP_BEEP,
                ToneGenerator.TONE_PROP_ACK,
                ToneGenerator.TONE_PROP_BEEP2,
                ToneGenerator.TONE_PROP_ACK,
                ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD
        };
        for (int index = 0; index < delays.length; index++) {
            final int tone = tones[index];
            handler.postDelayed(() -> {
                if (toneGenerator != null) {
                    toneGenerator.startTone(tone, 120);
                }
            }, delays[index]);
        }
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
            ChoreWheelPainter.drawWheel(canvas, slots, wheelRotation);
        }
    }

    private static final class FireworksView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final List<Particle> particles = new ArrayList<>();
        private long startedAt;
        private boolean running;

        FireworksView(Activity context) {
            super(context);
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }

        void start() {
            particles.clear();
            startedAt = android.os.SystemClock.uptimeMillis();
            running = true;
            setVisibility(View.VISIBLE);
            for (int burst = 0; burst < 7; burst++) {
                addBurst();
            }
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (!running) {
                return;
            }

            long elapsed = android.os.SystemClock.uptimeMillis() - startedAt;
            float seconds = elapsed / 1000f;
            paint.setStyle(Paint.Style.FILL);
            for (Particle particle : particles) {
                float age = seconds - particle.delaySeconds;
                if (age < 0f) {
                    continue;
                }
                float alpha = Math.max(0f, 1f - (age / 1.65f));
                if (alpha <= 0f) {
                    continue;
                }
                float x = particle.x + particle.vx * age;
                float y = particle.y + particle.vy * age + 180f * age * age;
                paint.setColor(Color.argb(
                        Math.round(alpha * 255f),
                        Color.red(particle.color),
                        Color.green(particle.color),
                        Color.blue(particle.color)
                ));
                canvas.drawCircle(x, y, particle.radius, paint);
            }

            if (elapsed < 2400L) {
                postInvalidateOnAnimation();
            } else {
                running = false;
                setVisibility(View.GONE);
            }
        }

        private void addBurst() {
            float centerX = 80f + RANDOM.nextFloat() * Math.max(1f, getWidth() - 160f);
            float centerY = 90f + RANDOM.nextFloat() * Math.max(1f, getHeight() * 0.42f);
            int color = fireworkColor();
            int count = 34 + RANDOM.nextInt(24);
            float delay = RANDOM.nextFloat() * 0.55f;
            for (int index = 0; index < count; index++) {
                double angle = (Math.PI * 2d * index / count) + RANDOM.nextDouble() * 0.18d;
                float speed = 120f + RANDOM.nextFloat() * 360f;
                particles.add(new Particle(
                        centerX,
                        centerY,
                        (float) Math.cos(angle) * speed,
                        (float) Math.sin(angle) * speed,
                        3.5f + RANDOM.nextFloat() * 6f,
                        color,
                        delay
                ));
            }
        }

        private static int fireworkColor() {
            int[] colors = {
                    Color.rgb(255, 233, 92),
                    Color.rgb(255, 74, 96),
                    Color.rgb(85, 220, 255),
                    Color.rgb(88, 255, 156),
                    Color.rgb(211, 124, 255)
            };
            return colors[RANDOM.nextInt(colors.length)];
        }
    }

    private static final class Particle {
        final float x;
        final float y;
        final float vx;
        final float vy;
        final float radius;
        final int color;
        final float delaySeconds;

        Particle(float x, float y, float vx, float vy, float radius, int color, float delaySeconds) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.radius = radius;
            this.color = color;
            this.delaySeconds = delaySeconds;
        }
    }
}
