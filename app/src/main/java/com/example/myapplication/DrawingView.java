package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class DrawingView extends View {

    private Path drawPath;
    private Paint drawPaint;
    private int paintColor = Color.BLACK;
    private float strokeWidth = 10f;

    private final List<FingerPath> paths = new ArrayList<>();

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    private void setupDrawing() {
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(strokeWidth);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(@androidx.annotation.NonNull Canvas canvas) {
        for (FingerPath fp : paths) {
            drawPaint.setColor(fp.color);
            drawPaint.setStrokeWidth(fp.strokeWidth);
            canvas.drawPath(fp.path, drawPaint);
        }
        drawPaint.setColor(paintColor);
        drawPaint.setStrokeWidth(strokeWidth);
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                paths.add(new FingerPath(paintColor, strokeWidth, drawPath));
                drawPath = new Path();
                performClick();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    public void setPaintColor(int color) {
        invalidate();
        paintColor = color;
    }

    public void setStrokeWidth(float width) {
        invalidate();
        strokeWidth = width;
    }

    public void clear() {
        paths.clear();
        invalidate();
    }

    public Bitmap getBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas);
        return bitmap;
    }

    private static class FingerPath {
        int color;
        float strokeWidth;
        Path path;

        FingerPath(int color, float strokeWidth, Path path) {
            this.color = color;
            this.strokeWidth = strokeWidth;
            this.path = path;
        }
    }
}
