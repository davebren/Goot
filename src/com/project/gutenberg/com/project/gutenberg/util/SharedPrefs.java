package com.project.gutenberg.com.project.gutenberg.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SharedPrefs {
     private static final String APP_SHARED_PREFS = "spanish.sensei"; //  Name of the file -.xml
     private SharedPreferences appSharedPrefs;
     private Editor prefsEditor;
     
     

     public SharedPrefs(Context context)
     {
         appSharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
         prefsEditor = appSharedPrefs.edit();
         
     }
     public SharedPreferences get_default() {
    	 return appSharedPrefs;
     }

     public boolean get_mute_sound() {
    	 return appSharedPrefs.getBoolean("mute_sound", false);
     }
     public void set_mute_sound(boolean mute) {
    	 prefsEditor.putBoolean("mute_sound", mute).commit();
     }
}