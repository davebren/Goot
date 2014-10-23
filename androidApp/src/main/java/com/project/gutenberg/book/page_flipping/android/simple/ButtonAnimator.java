package com.project.gutenberg.book.page_flipping.android.simple;

import android.view.animation.AlphaAnimation;

public class ButtonAnimator {
    private AlphaAnimation fadeOut;
    private final long default_duration = 3000;
    public ButtonAnimator() {
        fadeOut = new AlphaAnimation(1.0f,0.0f);
        fadeOut.setDuration(default_duration);
    }
    public ButtonAnimator(long duration) {
        fadeOut = new AlphaAnimation(1.0f,0.0f);
        fadeOut.setDuration(default_duration);
    }
    public AlphaAnimation getFadeOut() {
        return fadeOut;
    }
}
