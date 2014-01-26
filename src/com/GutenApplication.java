package com;

import android.app.Application;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.project.gutenberg.Shared_Prefs;
import com.project.gutenberg.catalog.database.Catalog_DB;

public class GutenApplication extends Application {
    public Typeface typeface;
    public RequestQueue volley;
    public Catalog_DB catalog;

    public void onCreate() {
        typeface = Typeface.createFromAsset(getAssets(),"fonts/roboto_light.ttf");
        volley = Volley.newRequestQueue(this);
        catalog = new Catalog_DB(this);
    }
}
