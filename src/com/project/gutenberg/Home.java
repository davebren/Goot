package com.project.gutenberg;

import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import com.project.gutenberg.com.project.gutenberg.util.RootActivity;
import com.project.gutenberg.com.project.gutenberg.util.SharedPrefs;



/**
 *
 */
public class Home extends RootActivity {

    protected static SharedPrefs prefs;
    private Context context;
    public static int screen_height;
    public static int screen_width;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize_app();


    }

    private void initialize_app() {
        context = this;
        prefs = new SharedPrefs(context);
        TAG = Home.class.getName();
        Display display = getWindowManager().getDefaultDisplay();
        screen_height = display.getHeight();
        screen_width = display.getWidth();
        setContentView(R.layout.home);
    }

}