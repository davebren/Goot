package com.project.gutenberg;

import android.app.Application;
import android.graphics.Typeface;

import com.project.gutenberg.catalog.database.CatalogByAuthorDB;
import com.project.gutenberg.catalog.database.CatalogByTitleDB;

public class GutenApplication extends Application {
    public Typeface typeface;
    public CatalogByTitleDB catalogByTitleDB;
    public CatalogByAuthorDB catalogByAuthorDB;

    public void onCreate() {
        typeface = Typeface.createFromAsset(getAssets(), "fonts/roboto_light.ttf");
        catalogByTitleDB = new CatalogByTitleDB(this);
        catalogByAuthorDB = new CatalogByAuthorDB(this);
    }
}
