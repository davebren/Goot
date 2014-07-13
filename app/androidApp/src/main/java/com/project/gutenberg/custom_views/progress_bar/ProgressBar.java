package com.project.gutenberg.custom_views.progress_bar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.project.gutenberg.R;


public class ProgressBar extends View {
    Paint paint = new Paint();
    int width;
    int height;
    double progress = 0;
    NinePatchDrawable ninePatch;

    public ProgressBar(Context context) {
        super(context);
        paint.setColor(getResources().getColor(R.color.progress_bar));
    }
    public ProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(getResources().getColor(R.color.progress_bar));
    }
    public ProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint.setColor(getResources().getColor(R.color.progress_bar));
    }
    public void setProgress(double progress) {
        this.progress = progress;
        invalidate();
    }
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, (int)(progress*width), height, paint);
    }
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        this.width = width;
        this.height = height;
    }
}


