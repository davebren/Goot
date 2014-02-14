package com.project.gutenberg.android_utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class DisablerListView extends ListView {
    public DisablerListView(Context context) {
        super(context);
    }
    public DisablerListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public DisablerListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public boolean dispatchTouchEvent(MotionEvent ev){
        if(ev.getAction()==MotionEvent.ACTION_MOVE) return true;
        return super.dispatchTouchEvent(ev);
    }
}
