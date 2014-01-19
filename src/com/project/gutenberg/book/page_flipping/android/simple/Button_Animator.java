package com.project.gutenberg.book.page_flipping.android.simple;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class Button_Animator {
    private AlphaAnimation fade_out;
    private final long default_duration = 3000;
    public Button_Animator() {
        fade_out = new AlphaAnimation(1.0f,0.0f);
        fade_out.setDuration(default_duration);
    }
    public Button_Animator(long duration) {
        fade_out = new AlphaAnimation(1.0f,0.0f);
        fade_out.setDuration(default_duration);
    }
    public AlphaAnimation get_fade_out() {
        return fade_out;
    }
}
