package com.project.gutenberg.com.project.gutenberg.util;


import android.content.Context;
import android.graphics.Typeface;

public class Fonts {
    public Typeface menu_font;

    public Fonts() {

    }
    public Fonts(Shared_Prefs prefs, Context context) {
        if (prefs.get_menu_font().equals("")) {
            menu_font = null;
        } else {
            menu_font = Typeface.createFromAsset(context.getAssets(), prefs.get_menu_font());
        }

    }

}
