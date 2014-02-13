package com.project.gutenberg.book.page_flipping;

import com.project.gutenberg.book.Book;
import com.project.gutenberg.book.view.BookView;
import com.project.gutenberg.book.view.PageView;

public abstract class PageFlipper {
    protected BookView bookView;
    protected PageView prevPage;
    protected PageView currentPage;
    protected PageView nextPage;
    protected Book book;

    protected PageFlipper(BookView bookView, PageView prevPage, PageView currentPage, PageView nextPage, Book book) {
        this.bookView = bookView;
        this.prevPage = prevPage;
        this.currentPage = currentPage;
        this.nextPage = nextPage;
        this.book = book;
    }
    public void nextPage() {

    }
    public void prevPage() {

    }
    public void jumpToChapter(int chapter_index) {

    }


}
