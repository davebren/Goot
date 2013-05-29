package com.project.gutenberg.book;

import android.view.View;

import java.util.LinkedList;

public class Book {
    private LinkedList<Chapter> chapters;
    private String book_title;
    private String book_author;
    private int current_chapter;
    private int current_page;

    public Book(String title, String author, LinkedList<Chapter> chapters) {
        this.book_title = title;
        this.book_author = author;
        this.chapters = chapters;
    }
    public Chapter get_chapter(int index) {
        try {
            return chapters.get(index);
        } catch(IndexOutOfBoundsException e1) {

        } catch(NullPointerException e2) {

        }
        return null;
    }
    public int number_of_chapters() {
        return chapters.size();
    }
    public void set_current_chapter(int current_chapter) {
        this.current_chapter = current_chapter;
    }
    public void increment_current_chapter() {
        current_chapter++;
    }
    public void set_current_page(int current_page) {
        this.current_page = current_page;
    }
    public void increment_current_page() {
        current_page++;
    }
    public int get_current_page() {
        return current_page;
    }
    public int get_current_chapter() {
        return current_chapter;
    }


}
