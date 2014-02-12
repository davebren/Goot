package com.project.gutenberg.book;

import android.util.Log;
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
    public String get_title() {
        return book_title;
    }
    public Chapter get_chapter(int index) {
        try {
            return chapters.get(index);
        } catch(IndexOutOfBoundsException e1) {} catch(NullPointerException e2) {}
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
    public void previous_chapter() {
        current_chapter--;
    }
    public Chapter peek_next_chapter() {
        return chapters.get(current_chapter+1);
    }
    public Chapter peek_previous_chapter() {
        return chapters.get(current_chapter-1);
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
    public boolean on_first_chapter() {
        return current_chapter == 0;
    }
    public int get_page_number() {
        int page_sum = 0;
        for (int i=0; i < current_chapter; i++) {
            page_sum += chapters.get(i).number_of_pages();
        }
        return page_sum + chapters.get(current_chapter).get_list_relative_current_page_index()+1;
    }
    public int get_number_of_pages() {
        int page_sum = 0;
        for (int i=0; i < chapters.size(); i++) {
            page_sum += chapters.get(i).number_of_pages();
        }
        return page_sum;
    }
    public Integer[] close() {
        return get_current_chapter().get_current_page_boundaries();
    }
    public void set_containing_page(int chapter, int paragraph, int word) {
        current_chapter=chapter;
        chapters.get(chapter).set_containing_page(paragraph,word);
    }
}
