package com;

import android.app.Application;
import android.graphics.Typeface;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.project.gutenberg.catalog.database.CatalogByAuthorDB;
import com.project.gutenberg.catalog.database.CatalogByTitleDB;
//
public class GutenApplication extends Application {
    public Typeface typeface;
    public RequestQueue volley;
    public CatalogByTitleDB catalogByTitleDB;
    public CatalogByAuthorDB catalogByAuthorDB;


    public void onCreate() {
        typeface = Typeface.createFromAsset(getAssets(),"fonts/roboto_light.ttf");
        volley = Volley.newRequestQueue(this);
        catalogByTitleDB = new CatalogByTitleDB(this);
        catalogByAuthorDB = new CatalogByAuthorDB(this);
    }
}
