package com.project.gutenberg.library;

import android.database.Cursor;

public class BookResource {
    private String id;
    private String title;
    private String author;

    public BookResource(Cursor cursor) {
        id = cursor.getString(1).replace("etext","");
        title = cursor.getString(2);
        author = cursor.getString(3);
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
}
