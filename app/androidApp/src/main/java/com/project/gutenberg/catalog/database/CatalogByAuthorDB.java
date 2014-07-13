package com.project.gutenberg.catalog.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class CatalogByAuthorDB extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "catalog_by_author.sqlite";
    private static final int DATABASE_VERSION = 1;

    SQLiteDatabase db;

    private final String title_table = "books_by_title";
    private final String author_table = "books_by_author";
    private final String title_key = "title";
    private final String author_key = "author";
    private final String id_key = "id";

    public CatalogByAuthorDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = getReadableDatabase();
    }
    public Cursor getAuthorCursor() {
        Cursor cursor = db.query(author_table,null,null,null,null,null,null);
        cursor.moveToFirst();
        return cursor;
    }
    public Cursor getAuthorCursor(String query) {
        query = CatalogByTitleDB.escapeQuery(query);
        Cursor cursor = db.query(author_table,null,author_key +" LIKE '%"+query+"%'",null,null,null,null);
        cursor.moveToFirst();
        return cursor;
    }
    public Cursor getAuthorCursor(String[] ids) {
        Cursor cursor = db.query(author_table,null,id_key + " in" + getInParameters(ids) + " ",ids,null,null,null);
        cursor.moveToFirst();
        return cursor;
    }
    public Cursor getAuthorCursor(String query, String[] ids) {
        query = CatalogByTitleDB.escapeQuery(query);
        Cursor cursor = db.query(author_table,null,id_key + " in" + getInParameters(ids) + " AND " + author_key + " LIKE + '%" + query + "%'",ids,null,null,null);
        cursor.moveToFirst();
        return cursor;
    }
    public String getInParameters(String[] args) {
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