package com.project.gutenberg.book;

import com.project.gutenberg.util.Action_Time_Analysis;

import java.util.LinkedList;

public class Book {
    private LinkedList<Chapter> chapters;
    private String book_title;
    private String book_author;
    private int current_chapter;

    public Book(String title, String author, LinkedList<Chapter> chapters) {
        this.book_title = title;
        this.book_author = author;
        this.chapters = chapters;
    }
    public Chapter get_chapter(int index) {
        Action_Time_Analysis.start("Book.get_chapter");
        try {
            return chapters.get(index);
        } catch(IndexOutOfBoundsException e1) {} catch(NullPointerException e2) {}
        Action_Time_Analysis.end("Book.get_chapter");
        return null;
    }
    public int number_of_chapters() {
        return chapters.size();
    }
    public void set_current_chapter(int current_chapter) {
        this.current_chapter = current_chapter;
    }
    public int get_current_chapter_index() {
        return current_chapter;
    }
    public Chapter get_current_chapter() {
        return chapters.get(current_chapter);
    }
    public void next_chapter() {
        current_chapter++;
    }
    public Chapter peek_next_chapter() {
        return chapters.get(current_chapter+1);
    }
    public String[] get_chapters() {
        String[] ret = new String[chapters.size()];
        for (int i=0; i<chapters.size();i++) {
            ret[i] = chapters.get(i).get_title();
        }
        return ret;
    }
    public boolean on_last_chapter() {
        return current_chapter == chapters.size()-1;
    }
}
