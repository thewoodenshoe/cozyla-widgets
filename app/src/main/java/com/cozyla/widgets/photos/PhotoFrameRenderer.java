package com.cozyla.widgets.photos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.net.Uri;
import android.text.TextPaint;

import java.io.InputStream;

public final class PhotoFrameRenderer {
    private PhotoFrameRenderer() {
    }

    public static Bitmap render(Context context, Uri uri, int width, int height, String label) {
        int safeWidth = Math.max(360, width);
        int safeHeight = Math.max(220, height);
        Bitmap bitmap = Bitmap.createBitmap(safeWidth, safeHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        drawBackground(canvas, paint);

        Bitmap photo = uri == null ? null : decodeBitmap(context, uri, safeWidth, safeHeight);
        if (photo == null) {
            drawEmpty(canvas, label == null ? "Cannot find album/photos" : label);
        } else {
            drawPhoto(canvas, photo, paint);
            drawOverlay(canvas, label, paint);
        }
        return bitmap;
    }

    private static Bitmap decodeBitmap(Context context, Uri uri, int width, int height) {
        try (InputStream boundsStream = context.getContentResolver().openInputStream(uri)) {
            if (boundsStream == null) {
                return null;
            }
            BitmapFactory.Options bounds = new BitmapFactory.Options();
            bounds.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(boundsStream, null, bounds);
            int sample = 1;
            while ((bounds.outWidth / sample) > width * 2 || (bounds.outHeight / sample) > height * 2) {
                sample *= 2;
            }
            try (InputStream imageStream = context.getContentResolver().openInputStream(uri)) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = sample;
                return BitmapFactory.decodeStream(imageStream, null, options);
            }
        } catch (Exception ex) {
            return null;
        }
    }

    private static void drawBackground(Canvas canvas, Paint paint) {
        paint.setShader(new LinearGradient(
                0,
                0,
                canvas.getWidth(),
                canvas.getHeight(),
                new int[]{Color.rgb(18, 24, 38), Color.rgb(55, 44, 96), Color.rgb(12, 70, 80)},
                null,
                Shader.TileMode.CLAMP
        ));
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
        paint.setShader(null);
    }

    private static void drawPhoto(Canvas canvas, Bitmap photo, Paint paint) {
        float scale = Math.max(canvas.getWidth() / (float) photo.getWidth(), canvas.getHeight() / (float) photo.getHeight());
        float width = photo.getWidth() * scale;
        float height = photo.getHeight() * scale;
        RectF destination = new RectF(
                (canvas.getWidth() - width) / 2f,
                (canvas.getHeight() - height) / 2f,
                (canvas.getWidth() + width) / 2f,
                (canvas.getHeight() + height) / 2f
        );
        canvas.drawBitmap(photo, null, destination, paint);
    }

    private static void drawOverlay(Canvas canvas, String label, Paint paint) {
        int height = canvas.getHeight();
        paint.setShader(new LinearGradient(
                0,
                height * 0.62f,
                0,
                height,
                Color.TRANSPARENT,
                Color.argb(180, 0, 0, 0),
                Shader.TileMode.CLAMP
        ));
        canvas.drawRect(0, height * 0.58f, canvas.getWidth(), height, paint);
        paint.setShader(null);
        TextPaint text = textPaint(Color.WHITE, Math.max(18f, canvas.getHeight() * 0.07f), true);
        canvas.drawText(label == null || label.isEmpty() ? "Photo Frame" : label, 24f, height - 28f, text);
    }

    private static void drawEmpty(Canvas canvas, String message) {
        TextPaint title = textPaint(Color.WHITE, Math.max(24f, canvas.getHeight() * 0.1f), true);
        title.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Photo Frame", canvas.getWidth() / 2f, canvas.getHeight() * 0.42f, title);
        TextPaint body = textPaint(Color.rgb(220, 238, 244), Math.max(16f, canvas.getHeight() * 0.055f), false);
        body.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(message, canvas.getWidth() / 2f, canvas.getHeight() * 0.56f, body);
    }

    private static TextPaint textPaint(int color, float size, boolean bold) {
        TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setTextSize(size);
        paint.setFakeBoldText(bold);
        paint.setShadowLayer(4f, 0f, 2f, Color.argb(120, 0, 0, 0));
        return paint;
    }
}
