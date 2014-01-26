package com.project.gutenberg.library;

import android.database.Cursor;

public class Book_Resource {
    private String resource_URI;
    private String title;
    private String author;

    public Book_Resource(Cursor cursor) {
        resource_URI = cursor.getString(1);
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

    public String getResource_URI() {
        return resource_URI;
    }

    public void setResource_URI(String resource_URI) {
        this.resource_URI = resource_URI;
    }
}
