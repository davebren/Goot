package com.project.gutenberg.book;

public class Page {
    private String[] pageText;
    public Page(String[] pageText) {
        this.pageText = pageText;
    }
    public String[] getPageText() {
        return pageText;
    }
}
