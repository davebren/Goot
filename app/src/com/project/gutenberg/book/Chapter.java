package com.project.gutenberg.book;

import com.project.gutenberg.book.parsing.BookParser;
import com.project.gutenberg.book.view.BookView;
import com.project.gutenberg.util.Debug;

import java.util.LinkedList;
import java.util.NoSuchElementException;

public class Chapter {
    private LinkedList<Page> pages;
    private LinkedList<String> paragraphs;
    private LinkedList<Integer[]> boundaries;
    private String title;
    private BookParser parser;
    private int chapterIndex;
    public boolean firstPageLoaded;
    public boolean lastPageLoaded;
    private int firstLoadedPage;
    private int lastLoadedPage;

    private int listRelativeCurrentPageIndex = 0;

    private boolean loadingHook = false;
    private int loadingHookStackIndex = 0;
    private boolean loadingHookNext = false;
    private BookView loadingHookBookView;

    public Chapter(BookParser parser, int chapterIndex) {
        pages = new LinkedList<Page>();
        boundaries = new LinkedList<Integer[]>();
        this.parser = parser;
        this.chapterIndex = chapterIndex;
    }

    public synchronized void setTitle(String title) {
        this.title = title;
    }
    public synchronized String getTitle() {
        return title;
    }
    public synchronized void setParagraphs(LinkedList<String> paragraphs) {
        this.paragraphs = paragraphs;
    }
    public synchronized LinkedList<String> getParagraphs() {
        if (paragraphs != null) {
           return paragraphs;
        } else {
            setParagraphs(parser.parseChapter(chapterIndex));
            return paragraphs;
        }
    }
    public synchronized int numberOfPages() {
        return pages.size();
    }
    public synchronized Page getPage(int index) {
        try {
            return pages.get(index);
        } catch(IndexOutOfBoundsException e1) {

        } catch(NullPointerException e2) {

        } catch(NoSuchElementException e3) {

        }
        return null;
    }
    public synchronized Integer[] getLastBoundary() {
        try {
            return boundaries.getLast();
        } catch(IndexOutOfBoundsException e1) {

        } catch(NullPointerException e2) {

        } catch(NoSuchElementException e3) {

        }
        return null;
    }
    public synchronized Integer[] getFirstBoundary() {
        try {
            return boundaries.getFirst();
        } catch(IndexOutOfBoundsException e1) {

        } catch(NullPointerException e2) {

        } catch(NoSuchElementException e3) {

        }
        return null;
    }
    public synchronized Integer[] getCurrentPageBoundaries() {
        return boundaries.get(listRelativeCurrentPageIndex);
    }
    public synchronized void addPage(boolean before, Page p){
        if (pages.size() == 0) {
            firstLoadedPage = 0;
            lastLoadedPage = 0;
            listRelativeCurrentPageIndex =0;
            pages.add(p);
        } else if (before) {
            pages.addFirst(p);
            firstLoadedPage--;
            listRelativeCurrentPageIndex++;
        } else {
            pages.addLast(p);
            lastLoadedPage++;
            if (loadingHook) {
                loadingHookBookView.setPrevCurrentNextPageLines(p.getPageText(), loadingHookStackIndex);
            }
        }
    }
    public synchronized void addBoundary(boolean before, Integer[] b) {
        if (before) {
            boundaries.addFirst(b);
        } else {
            boundaries.addLast(b);
        }
    }
    public synchronized int getListRelativeCurrentPageIndex() {
        return listRelativeCurrentPageIndex;
    }
    public synchronized Page nextPage() {
        listRelativeCurrentPageIndex++;
        Debug.log("get next page: " + listRelativeCurrentPageIndex + "/" + pages.size());
        return pages.get(listRelativeCurrentPageIndex);
    }
    public synchronized Page previousPage() {
        listRelativeCurrentPageIndex--;
        return pages.get(listRelativeCurrentPageIndex);
    }
    public synchronized Page peekNextPage() {
        Debug.log("peek next page: " + (listRelativeCurrentPageIndex +1) + "/" + pages.size());
        return pages.get(listRelativeCurrentPageIndex +1);
    }
    public synchronized Page peekCurrentPage() {
        return pages.get(listRelativeCurrentPageIndex);
    }
    public synchronized Page peekPreviousPage() {
        return pages.get(listRelativeCurrentPageIndex -1);
    }
    public synchronized Page peekLastPage() {
        return pages.get(pages.size()-1);
    }
    public synchronized boolean onLastPage() {
        return lastPageLoaded && pages.size()-1 == listRelativeCurrentPageIndex;
    }
    public synchronized boolean onPenultimatePage() {
        return lastPageLoaded && pages.size()-2 == listRelativeCurrentPageIndex;
    }
    public synchronized boolean onFirstPage() {
        return listRelativeCurrentPageIndex == 0;
    }
    public synchronized boolean onSecondPage() {
        return listRelativeCurrentPageIndex == 1;
    }
    public synchronized boolean setLastPage() {
        if (!lastPageLoaded) return false;
        listRelativeCurrentPageIndex = pages.size()-1;
        return true;
    }
    public synchronized void setFirstPage() {
        listRelativeCurrentPageIndex = 0;
    }
    public void setContainingPage(int paragraph, int word) {
        for (int i=0; i < boundaries.size(); i++) {
            Integer[] boundary = boundaries.get(i);
            if (boundary[4] > paragraph) {
                listRelativeCurrentPageIndex = i;return;
            }
            if (boundary[4] == paragraph) {
                if (boundary[5] > word || boundary[4] > paragraph) {
                    listRelativeCurrentPageIndex = i;return;
                }
            }
        }
        listRelativeCurrentPageIndex =0;
    }
}
