package com.project.gutenberg.book;

public class Page {
    private String[] page_text;
    public Page(String[] page_text) {
        this.page_text = page_text;
    }
    public String[] get_page_text() {
        return page_text;
    }
}
