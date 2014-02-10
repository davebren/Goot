package com;

import android.app.Application;
import android.graphics.Typeface;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.project.gutenberg.catalog.database.Catalog_By_Author_DB;
import com.project.gutenberg.catalog.database.Catalog_By_Title_DB;

public class GutenApplication extends Application {
    public Typeface typeface;
    public RequestQueue volley;
    public Catalog_By_Title_DB catalog_by_title;
    public Catalog_By_Author_DB catalog_by_author;


    public void onCreate() {
        typeface = Typeface.createFromAsset(getAssets(),"fonts/roboto_light.ttf");
        volley = Volley.newRequestQueue(this);
        catalog_by_title = new Catalog_By_Title_DB(this);
        catalog_by_author = new Catalog_By_Author_DB(this);
    }
}
