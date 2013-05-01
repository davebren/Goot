package com.project.gutenberg.book;

import android.view.View;

import java.util.LinkedList;

public class Book {
    private LinkedList<Chapter> chapters;
    private String book_title;
    private String book_author;

    public Book(String title, String author, LinkedList<Chapter> chapters) {
        this.book_title = title;
        this.book_author = author;
        this.chapters = chapters;
    }
}
