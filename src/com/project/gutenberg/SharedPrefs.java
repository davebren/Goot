package com.project.gutenberg;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPrefs {
    private static final String APP_SHARED_PREFS = "project.gutendroid";
    private SharedPreferences appSharedPreferences;
    private Editor prefsEditor;

    public SharedPrefs(Context context) {
        appSharedPreferences = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        prefsEditor = appSharedPreferences.edit();
    }
    public String getTypeface() {
        return appSharedPreferences.getString("typeface","Roboto Light");
    }
    public void setTypeface(String typeface) {
        prefsEditor.putString("typeface", typeface).commit();
    }
    public String getOrientation() {
        return appSharedPreferences.getString("orientation","portrait");
    }
    public void setOrientation(String orientation) {
        prefsEditor.putString("orientation", orientation).commit();
    }
    public boolean getMuteSound() {
     return appSharedPreferences.getBoolean("mute_sound", false);
    }
    public void setMuteSound(boolean mute) {
     prefsEditor.putBoolean("mute_sound", mute).commit();
    }
    public void setBookFontScale(float font_size) {
        prefsEditor.putFloat("book_font_size", font_size).commit();
    }
    public float getBookFontScale() {
        return appSharedPreferences.getFloat("book_font_size", 1.0f);
    }
    public int getLastChapter(int book_id) {
        return appSharedPreferences.getInt("last_chapter_" + book_id, 0);
    }
    public int getLastParagraph(int book_id) {
        return appSharedPreferences.getInt("last_book_" + book_id, 0);
    }
    public int getLastWord(int book_id) {
        return appSharedPreferences.getInt("last_word_" + book_id, 0);
    }
    public void setLastChapter(int book_id, int chapter) {
        prefsEditor.putInt("last_chapter_" + book_id, chapter).commit();
    }
    public void setLastParagraph(int book_id, int paragraph) {
        prefsEditor.putInt("last_book_" + book_id, paragraph).commit();
    }
    public void setLastWord(int book_id, int word) {
        prefsEditor.putInt("last_word_" + book_id, word).commit();
    }
    public int getOpenBook() {
        return appSharedPreferences.getInt("open_book",-999);
    }
    public void setOpenBook(int book_id) {
        prefsEditor.putInt("open_book", book_id).commit();
    }
}