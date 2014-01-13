package com.project.gutenberg.book.page_flipping;

import com.project.gutenberg.book.Book;
import com.project.gutenberg.book.pagination.Page_Splitter;
import com.project.gutenberg.book.view.Book_View;
import com.project.gutenberg.book.view.Page_View;

public abstract class Page_Flipper {
    protected Book_View book_view;
    protected Page_View prev_page;
    protected Page_View current_page;
    protected Page_View next_page;
    protected Book book;

    protected Page_Flipper(Book_View book_view, Page_View prev_page, Page_View current_page, Page_View next_page, Book book) {
        this.book_view = book_view;
        this.prev_page = prev_page;
        this.current_page = current_page;
        this.next_page = next_page;
        this.book = book;
    }
    public void next_page() {

    }
    public void prev_page() {

    }


}
