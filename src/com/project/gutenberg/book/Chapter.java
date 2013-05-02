package com.project.gutenberg.book;

import com.project.gutenberg.book.parsing.Book_Parser;

import java.util.LinkedList;

public class Chapter {
    private LinkedList<Page> pages;
    private LinkedList<String> paragraphs;
    private LinkedList<Integer[]> boundaries;
    private String title;
    private Book_Parser parser;
    private int chapter_index;


    public Chapter(Book_Parser parser, int chapter_index) {
        pages = new LinkedList<Page>();
        boundaries = new LinkedList<Integer[]>();
        this.parser = parser;
        this.chapter_index = chapter_index;
    }

    public void set_title(String title) {
        this.title = title;
    }
    public void set_paragraphs(LinkedList<String> paragraphs) {
        this.paragraphs = paragraphs;
    }
    public LinkedList<String> get_paragraphs() {
        if (paragraphs != null) {
           return paragraphs;
        } else {
            set_paragraphs(parser.parse_chapter(chapter_index));
            return paragraphs;
        }
    }



    public Page get_page(int index) {
        try {
            return pages.get(index);
        } catch(IndexOutOfBoundsException e1) {

        } catch(NullPointerException e2) {

        }
        return null;
    }
    public Integer[] get_last_boundary() {
        return boundaries.getLast();
    }
    public Integer[] get_first_boundary() {
        return boundaries.getFirst();
    }
    public synchronized void add_page(boolean before, Page p){
        if (before) {
            pages.addFirst(p);
        } else {
            pages.addLast(p);
        }
    }
    public synchronized void add_boundary(boolean before, Integer[] b) {
        if (before) {
            boundaries.addFirst(b);
        } else {
            boundaries.addLast(b);
        }
    }

}
