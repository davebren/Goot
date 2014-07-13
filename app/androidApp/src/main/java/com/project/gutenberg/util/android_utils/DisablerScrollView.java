package com.project.gutenberg.util.android_utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class DisablerScrollView extends ScrollView {
    private boolean enableScrolling = true;

    public boolean isEnableScrolling() {
        return enableScrolling;
    }
    public void setEnableScrolling(boolean enableScrolling) {
        this.enableScrolling = enableScrolling;
    }
    public DisablerScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public DisablerScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public DisablerScrollView(Context context) {
        super(context);
    }
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isEnableScrolling()) {
            return super.onInterceptTouchEvent(ev);
        } else {
            return false;
        }
    }
}
