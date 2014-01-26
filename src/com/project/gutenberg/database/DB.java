package com.project.gutenberg.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB extends SQLiteOpenHelper {
    private static final String name="gutenberg.db";
    private static final int version = 0;

    public DB(Context context) {
        super(context,name,null,version);
    }
    public void onCreate(SQLiteDatabase db) {

    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
