package com.project.gutenberg.book;

import java.util.LinkedList;

public class Book {
    private LinkedList<Chapter> chapters;
    private String bookTitle;
    private String bookAuthor;
    private int currentChapter;

    public Book(String title, String author, LinkedList<Chapter> chapters) {
        this.bookTitle = title;
        this.bookAuthor = author;
        this.chapters = chapters;
    }
    public String getTitle() {
        return bookTitle;
    }
    public Chapter getChapter(int index) {
        try {
            return chapters.get(index);
        } catch(IndexOutOfBoundsException e1) {} catch(NullPointerException e2) {}
        return null;
    }
    public int numberOfChapters() {
        return chapters.size();
    }
    public void setCurrentChapter(int currentChapter) {
        this.currentChapter = currentChapter;
    }
    public int getCurrentChapterIndex() {
        return currentChapter;
    }
    public Chapter getCurrentChapter() {
        return chapters.get(currentChapter);
    }
    public void nextChapter() {
        currentChapter++;
    }
    public void previousChapter() {
        currentChapter--;
    }
    public Chapter peekNextChapter() {
        return chapters.get(currentChapter +1);
    }
    public Chapter peekPreviousChapter() {
        return chapters.get(currentChapter -1);
    }
    public String[] getChapters() {
        String[] ret = new String[chapters.size()];
        for (int i=0; i<chapters.size();i++) {
            ret[i] = chapters.get(i).getTitle();
            if (ret[i] == null) ret[i] = "";
        }
        return ret;
    }
    public boolean onLastChapter() {
        return currentChapter == chapters.size()-1;
    }
    public boolean onFirstChapter() {
        return currentChapter == 0;
    }
    public int getPageNumber() {
        int page_sum = 0;
        for (int i=0; i < currentChapter; i++) {
            page_sum += chapters.get(i).numberOfPages();
        }
        return page_sum + chapters.get(currentChapter).getListRelativeCurrentPageIndex()+1;
    }
    public int getNumberOfPages() {
        int page_sum = 0;
        for (int i=0; i < chapters.size(); i++) {
            page_sum += chapters.get(i).numberOfPages();
        }
        return page_sum;
    }
    public Integer[] getCurrentPageBoundaries() {
        return getCurrentChapter().getCurrentPageBoundaries();
    }
    public void setContainingPage(int chapter, int paragraph, int word) {
        currentChapter =chapter;
        chapters.get(chapter).setContainingPage(paragraph, word);
    }
}
