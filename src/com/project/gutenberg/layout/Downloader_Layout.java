package com.project.gutenberg.layout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

public class Downloader_Layout extends LinearLayout {
    private double percentage = 0;
    public Downloader_Layout(Context context) {
        super(context);
    }
    public Downloader_Layout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public Downloader_Layout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public void set_percentage(double percentage) {
        if (percentage != 0) Log.d("gutendroid", "set percentage: " + percentage);
        this.percentage = percentage;
    }
    public void onDraw(Canvas c) {
        super.onDraw(c);
        /*float right = (float)(c.getWidth()*percentage);
        Paint p = new Paint();
        p.setColor(Color.WHITE);
        c.drawRect(0,c.getHeight()-5,right,c.getHeight(),p);*/
    }
}
