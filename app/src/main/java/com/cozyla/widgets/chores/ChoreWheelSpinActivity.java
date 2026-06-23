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
import android.widget.LinearLayout;
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
    private Button spinAgainButton;
    private Button doneButton;
    private ToneGenerator toneGenerator;
    private ValueAnimator currentAnimator;
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
        wheelView.setContentDescription(getString(R.string.chore_widget_spin));
        wheelView.setClickable(true);
        wheelView.setOnClickListener(view -> startSpin());
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

        LinearLayout buttonBar = new LinearLayout(this);
        buttonBar.setGravity(Gravity.CENTER);
        buttonBar.setOrientation(LinearLayout.HORIZONTAL);

        spinAgainButton = new Button(this);
        spinAgainButton.setText(R.string.chore_spin_again);
        spinAgainButton.setEnabled(false);
        spinAgainButton.setOnClickListener(view -> startSpin());
        buttonBar.addView(spinAgainButton, new LinearLayout.LayoutParams(dp(180), dp(56)));

        doneButton = new Button(this);
        doneButton.setText(R.string.chore_spin_done);
        doneButton.setEnabled(false);
        doneButton.setOnClickListener(view -> finish());
        LinearLayout.LayoutParams doneParams = new LinearLayout.LayoutParams(dp(180), dp(56));
        doneParams.setMarginStart(dp(12));
        buttonBar.addView(doneButton, doneParams);

        FrameLayout.LayoutParams buttonParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                dp(56),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL
        );
        buttonParams.setMargins(0, 0, 0, dp(14));
        root.addView(buttonBar, buttonParams);

        setContentView(root);
    }

    private void startSpin() {
        if (currentAnimator != null && currentAnimator.isRunning()) {
            return;
        }
        handler.removeCallbacksAndMessages(null);
        fireworksView.stop();
        resultView.setText(R.string.chore_spin_spinning);
        spinAgainButton.setEnabled(false);
        doneButton.setEnabled(false);

        int targetIndex = RANDOM.nextInt(slots.size());
        float sweep = 360f / slots.size();
        boolean closeCall = slots.size() > 1 && RANDOM.nextBoolean();
        float offset = 0f;
        if (closeCall) {
            float margin = 1.5f + RANDOM.nextFloat() * 4.0f;
            offset = (RANDOM.nextBoolean() ? 1f : -1f) * ((sweep / 2f) - margin);
        }

        float selectedCenter = ChoreWheelMath.rotationForIndex(targetIndex, slots.size());
        float targetRotation = normalizeDegrees(selectedCenter + offset);
        float currentRotation = wheelView.wheelRotation();
        float forwardDelta = normalizeDegrees(targetRotation - normalizeDegrees(currentRotation));
        float finalRotation = currentRotation + ((6 + RANDOM.nextInt(4)) * 360f) + forwardDelta;
        currentAnimator = ValueAnimator.ofFloat(wheelView.wheelRotation(), finalRotation);
        currentAnimator.setDuration(4600L);
        currentAnimator.setInterpolator(new DecelerateInterpolator(2.35f));
        currentAnimator.addUpdateListener(animation -> wheelView.setWheelRotation((Float) animation.getAnimatedValue()));
        currentAnimator.addListener(new AnimatorListenerAdapter() {
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
                spinAgainButton.setEnabled(true);
                doneButton.setEnabled(true);
            }
        });
        currentAnimator.start();
    }

    private void celebrateNoChores() {
        fireworksView.start();
        if (toneGenerator != null) {
            toneGenerator.release();
        }
        toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 70);
        int[] delays = {0, 90, 180, 310, 450, 620, 820, 1080};
        int[] tones = {
                ToneGenerator.TONE_PROP_BEEP,
                ToneGenerator.TONE_PROP_ACK,
                ToneGenerator.TONE_PROP_BEEP2,
                ToneGenerator.TONE_PROP_ACK,
                ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD,
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

    private static float normalizeDegrees(float degrees) {
        float normalized = degrees % 360f;
        return normalized < 0f ? normalized + 360f : normalized;
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

        float wheelRotation() {
            return wheelRotation;
        }

        @Override
        protected void onDraw(android.graphics.Canvas canvas) {
            super.onDraw(canvas);
            ChoreWheelPainter.drawWheel(canvas, slots, wheelRotation);
        }
    }

    private static final class FireworksView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final List<Burst> bursts = new ArrayList<>();
        private final List<Particle> particles = new ArrayList<>();
        private long startedAt;
        private boolean running;

        FireworksView(Activity context) {
            super(context);
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }

        void start() {
            bursts.clear();
            particles.clear();
            startedAt = android.os.SystemClock.uptimeMillis();
            running = true;
            setVisibility(View.VISIBLE);
            for (int burst = 0; burst < 16; burst++) {
                addBurst();
            }
            invalidate();
        }

        void stop() {
            running = false;
            bursts.clear();
            particles.clear();
            setVisibility(View.GONE);
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
            canvas.drawColor(Color.argb(118, 0, 0, 0));

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeCap(Paint.Cap.ROUND);
            for (Burst burst : bursts) {
                float age = seconds - burst.delaySeconds;
                if (age < 0f || age > 2.2f) {
                    continue;
                }
                float alpha = Math.max(0f, 1f - (age / 2.2f));
                paint.setStrokeWidth(2f + 8f * alpha);
                paint.setColor(Color.argb(
                        Math.round(alpha * 175f),
                        Color.red(burst.color),
                        Color.green(burst.color),
                        Color.blue(burst.color)
                ));
                float radius = burst.radius + age * 170f;
                canvas.drawCircle(burst.x, burst.y, radius, paint);
                canvas.drawCircle(burst.x, burst.y, radius * 0.55f, paint);
            }

            for (Particle particle : particles) {
                float age = seconds - particle.delaySeconds;
                if (age < 0f) {
                    continue;
                }
                float alpha = Math.max(0f, 1f - (age / 2.55f));
                if (alpha <= 0f) {
                    continue;
                }
                float x = particle.x + particle.vx * age;
                float y = particle.y + particle.vy * age + 180f * age * age;
                float previousAge = Math.max(0f, age - 0.055f);
                float previousX = particle.x + particle.vx * previousAge;
                float previousY = particle.y + particle.vy * previousAge + 180f * previousAge * previousAge;
                paint.setColor(Color.argb(
                        Math.round(alpha * 255f),
                        Color.red(particle.color),
                        Color.green(particle.color),
                        Color.blue(particle.color)
                ));
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(Math.max(2f, particle.radius * 0.55f));
                canvas.drawLine(previousX, previousY, x, y, paint);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(x, y, particle.radius, paint);
            }

            if (elapsed < 5200L) {
                postInvalidateOnAnimation();
            } else {
                running = false;
                setVisibility(View.GONE);
                bursts.clear();
                particles.clear();
            }
        }

        private void addBurst() {
            float centerX = 80f + RANDOM.nextFloat() * Math.max(1f, getWidth() - 160f);
            float centerY = 80f + RANDOM.nextFloat() * Math.max(1f, getHeight() * 0.58f);
            int color = fireworkColor();
            int count = 54 + RANDOM.nextInt(46);
            float delay = RANDOM.nextFloat() * 2.15f;
            bursts.add(new Burst(centerX, centerY, color, delay, 18f + RANDOM.nextFloat() * 28f));
            for (int index = 0; index < count; index++) {
                double angle = (Math.PI * 2d * index / count) + RANDOM.nextDouble() * 0.18d;
                float speed = 150f + RANDOM.nextFloat() * 560f;
                particles.add(new Particle(
                        centerX,
                        centerY,
                        (float) Math.cos(angle) * speed,
                        (float) Math.sin(angle) * speed,
                        3.2f + RANDOM.nextFloat() * 7.5f,
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
                    Color.rgb(211, 124, 255),
                    Color.rgb(255, 255, 255),
                    Color.rgb(255, 155, 55)
            };
            return colors[RANDOM.nextInt(colors.length)];
        }
    }

    private static final class Burst {
        final float x;
        final float y;
        final int color;
        final float delaySeconds;
        final float radius;

        Burst(float x, float y, int color, float delaySeconds, float radius) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.delaySeconds = delaySeconds;
            this.radius = radius;
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
