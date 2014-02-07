package com.project.gutenberg.catalog.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class Catalog_DB extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "catalog.sqlite";
    private static final int DATABASE_VERSION = 1;

    SQLiteDatabase db;

    private final String title_table = "books_by_title";
    private final String author_table = "books_by_author";
    private final String title_key = "title";
    private final String author_key = "author";
    private final String id_key = "id";

    public Catalog_DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = getReadableDatabase();
    }
    public void log_rows() {
        Cursor cursor = db.query(title_table,null,null,null,null,null,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String s = "";
            for (int i=1; i < cursor.getColumnCount(); i++) {
                s += cursor.getString(i) + ", ";
            }
            Log.d("gutendroid", "db row: " + s);
            cursor.moveToNext();
        }
    }
    public Cursor get_title_cursor() {
        Cursor cursor = db.query(title_table,null,null,null,null,null,null);
        cursor.moveToFirst();
        return cursor;
    }
    public Cursor get_title_cursor(String search) {
        Cursor cursor = db.query(title_table,null,title_key +" LIKE '%"+search+"%'",null,null,null,null);
        cursor.moveToFirst();
        return cursor;
    }
    public Cursor get_title_cursor(String[] ids) {
        Cursor cursor = db.query(title_table,null,id_key + " in" + get_in_paramters(ids) + " ",ids,null,null,null);
        cursor.moveToFirst();
        return cursor;
    }
    public Cursor get_title_cursor(String query, String[] ids) {
        Cursor cursor = db.query(title_table,null,id_key + " in" + get_in_paramters(ids) + " AND " + title_key + " LIKE + '%" + query + "%'",ids,null,null,null);
        cursor.moveToFirst();
        return cursor;
    }
    public String get_in_paramters(String[] args) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (int i=0; i < args.length; i++) {
            sb.append("?");
            if (i != args.length -1) sb.append(",");
        }
        sb.append(")");
        return sb.toString();
    }
}