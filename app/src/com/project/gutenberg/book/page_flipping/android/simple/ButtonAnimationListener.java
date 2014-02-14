package com.project.gutenberg.book.page_flipping.android.simple;

import android.view.View;
import android.view.animation.Animation;

public class ButtonAnimationListener implements Animation.AnimationListener {
    private View view;


    private boolean animating = false;
    public void onAnimationStart(Animation animation) {
        animating = true;
    }
    public void onAnimationEnd(Animation animation) {
        animating = false;
        view.setAlpha(0f);
    }
    public void onAnimationRepeat(Animation animation) {

    }
    public boolean is_animating() {
        return animating;
    }
    public ButtonAnimationListener set_view(View view) {
        this.view = view;
        return this;
    }
}
