package com.project.gutenberg;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class Shared_Prefs {
    private static final String APP_SHARED_PREFS = "project.gutendroid"; //  Name of the file -.xml
    private SharedPreferences app_shared_preferences;
    private Editor prefs_editor;

    public Shared_Prefs(Context context) {
        app_shared_preferences = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        prefs_editor = app_shared_preferences.edit();
    }
    public SharedPreferences get_default() {
        return app_shared_preferences;
    }
    public String get_typeface() {
        return app_shared_preferences.getString("typeface","Roboto Light");
    }
    public void set_typeface(String typeface) {
        prefs_editor.putString("typeface",typeface).commit();
    }
    public String get_orientation() {
        return app_shared_preferences.getString("orientation","portrait");
    }
    public void set_orientation(String orientation) {
        prefs_editor.putString("orientation",orientation).commit();
    }
    public boolean get_mute_sound() {
     return app_shared_preferences.getBoolean("mute_sound", false);
    }
    public void set_mute_sound(boolean mute) {
     prefs_editor.putBoolean("mute_sound", mute).commit();
    }
    public void set_book_font_scale(float font_size) {
        prefs_editor.putFloat("book_font_size", font_size).commit();
    }
    public float get_book_font_scale() {
        return app_shared_preferences.getFloat("book_font_size", 1.0f);
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
    public String get_page_flip_style() {
        return app_shared_preferences.getString("page_flip_style","slide");
    }
    public void set_page_flip_style(String style) {
        prefs_editor.putString("page_flip_style",style);
    }





}