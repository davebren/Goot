package com.project.gutenberg.book.page_flipping.android;

import android.widget.RelativeLayout;
import com.project.gutenberg.book.Book;
import com.project.gutenberg.book.page_flipping.PageFlipper;
import com.project.gutenberg.book.view.BookView;
import com.project.gutenberg.book.view.PageView;

public class PageSlider extends PageFlipper {
    private RelativeLayout pageHolder;
    public PageSlider(BookView bookView, PageView prevPage, PageView currentPage, PageView nextPage, Book book, RelativeLayout pageHolder) {
        super(bookView, prevPage, currentPage, nextPage, book);
        this.pageHolder = pageHolder;
    }
    public void nextPage() {
        prevPage.removeView();
        currentPage.removeView();
        super.nextPage();
    }
    public void prevPage() {
        super.prevPage();
    }
}
