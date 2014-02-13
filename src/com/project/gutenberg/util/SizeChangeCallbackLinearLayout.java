package com.project.gutenberg.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class SizeChangeCallbackLinearLayout extends LinearLayout {
    private ResponseCallback<Integer[]> responseCallback;

    public SizeChangeCallbackLinearLayout(Context context) {
        super(context);
    }
    public SizeChangeCallbackLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public SizeChangeCallbackLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public void setResponseCallback(ResponseCallback<Integer[]> responseCallback) {
        this.responseCallback =responseCallback;
    }
    public void onSizeChanged (int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (responseCallback != null) responseCallback.onResponse(new Integer[]{w, h, oldw, oldh});
    }
}
