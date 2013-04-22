package com.project.gutenberg.com.project.gutenberg.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Shared_Prefs {
     private static final String APP_SHARED_PREFS = "project.gutendroid"; //  Name of the file -.xml
     private SharedPreferences app_shared_preferences;
     private Editor prefs_editor;
     
     

     public Shared_Prefs(Context context)
     {
         app_shared_preferences = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
         prefs_editor = app_shared_preferences.edit();
         
     }
     public SharedPreferences get_default() {
    	 return app_shared_preferences;
     }

     public boolean get_mute_sound() {
    	 return app_shared_preferences.getBoolean("mute_sound", false);
     }
     public void set_mute_sound(boolean mute) {
    	 prefs_editor.putBoolean("mute_sound", mute).commit();
     }

     public String get_menu_font() {
         return app_shared_preferences.getString("menu_font", "");
     }

    public void set_menu_font(String font) {
        prefs_editor.putString("menu_font", font).commit();
    }
    // TODO change to integer value indicating small, medium, large, xlarge.
    public int get_text_size() {
        return 25;
    }

    public int get_last_chapter(int book_id) {
        return app_shared_preferences.getInt("last_chapter_" + book_id, 0);
    }
    public int get_last_paragraph(int book_id) {
        return app_shared_preferences.getInt("last_book_" + book_id, 0);
    }
    public int get_last_word(int book_id) {
        return app_shared_preferences.getInt("last_word_" + book_id, 0);
    }

    public void set_last_chapter(int book_id, int chapter) {
        prefs_editor.putInt("last_chapter_" + book_id, chapter).commit();
    }
    public void set_last_paragraph(int book_id, int paragraph) {
        prefs_editor.putInt("last_book_" + book_id, paragraph).commit();
    }
    public void set_last_word(int book_id, int word) {
        prefs_editor.putInt("last_word_" + book_id, word).commit();
    }




}