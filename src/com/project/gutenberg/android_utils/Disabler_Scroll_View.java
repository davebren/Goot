package com.project.gutenberg.android_utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class Disabler_Scroll_View extends ScrollView {
    private boolean enableScrolling = true;

    public boolean isEnableScrolling() {
        return enableScrolling;
    }
    public void setEnableScrolling(boolean enableScrolling) {
        this.enableScrolling = enableScrolling;
    }
    public Disabler_Scroll_View(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public Disabler_Scroll_View(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public Disabler_Scroll_View(Context context) {
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
