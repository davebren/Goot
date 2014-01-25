package com.project.gutenberg.util;


import android.content.Context;
import android.graphics.Typeface;
import com.project.gutenberg.Shared_Prefs;

public class Fonts {
    public Typeface menu_font;

    public Fonts() {

    }
    public Fonts(Shared_Prefs prefs, Context context) {
        if (prefs.get_typeface().equals("")) {
            menu_font = null;
        } else {
            menu_font = Typeface.createFromAsset(context.getAssets(), "fonts/" + Typeface_Mappings.get_file_name(prefs.get_typeface()));
        }

    }

}
