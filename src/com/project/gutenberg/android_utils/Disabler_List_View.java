package com.project.gutenberg.android_utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class Disabler_List_View extends ListView {
    public Disabler_List_View(Context context) {
        super(context);
    }
    public Disabler_List_View(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public Disabler_List_View(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public boolean dispatchTouchEvent(MotionEvent ev){
        if(ev.getAction()==MotionEvent.ACTION_MOVE) return true;
        return super.dispatchTouchEvent(ev);
    }
}
