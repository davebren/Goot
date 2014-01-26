package com;

import android.app.Application;
import android.graphics.Typeface;

public class GutenApplication extends Application {
    public Typeface typeface;

    public void onCreate() {
        typeface = Typeface.createFromAsset(getAssets(),"fonts/roboto_light.ttf");
    }
}
