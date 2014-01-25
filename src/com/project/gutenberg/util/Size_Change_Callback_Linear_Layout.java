package com.project.gutenberg.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.project.gutenberg.util.Response_Callback;

public class Size_Change_Callback_Linear_Layout extends LinearLayout {
    private Response_Callback<Integer[]> response_callback;

    public Size_Change_Callback_Linear_Layout(Context context) {
        super(context);
    }
    public Size_Change_Callback_Linear_Layout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public Size_Change_Callback_Linear_Layout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public void set_response_callback(Response_Callback<Integer[]> response_callback) {
        this.response_callback=response_callback;
    }
    public void onSizeChanged (int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (response_callback != null)response_callback.on_response(new Integer[] {w,h,oldw,oldh});
    }
}
