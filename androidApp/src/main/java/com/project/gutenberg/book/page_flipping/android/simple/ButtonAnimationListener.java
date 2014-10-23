package com.project.gutenberg.book.page_flipping.android.simple;

import android.graphics.Color;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;

public class ButtonAnimationListener implements Animation.AnimationListener {
    private Button view;
    private boolean animating = false;

    public void onAnimationStart(Animation animation) {
        animating = true;
    }
    public void onAnimationEnd(Animation animation) {
        animating = false;
        view.setAlpha(0f);
        view.setText("");
        view.setBackgroundColor(Color.TRANSPARENT);
    }
    public void onAnimationRepeat(Animation animation) {

    }
    public boolean isAnimating() {
        return animating;
    }
    public ButtonAnimationListener setView(Button view) {
        this.view = view;
        return this;
    }
}
