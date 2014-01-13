package com.project.gutenberg.book.page_flipping.android;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.project.gutenberg.book.Book;
import com.project.gutenberg.book.page_flipping.Page_Flipper;
import com.project.gutenberg.book.view.Book_View;
import com.project.gutenberg.book.view.Page_View;

public class Page_Slider extends Page_Flipper {
    private RelativeLayout page_holder;
    public Page_Slider(Book_View book_view, Page_View prev_page, Page_View current_page, Page_View next_page, Book book, RelativeLayout page_holder) {
        super(book_view, prev_page, current_page, next_page, book);
        this.page_holder = page_holder;
    }
    public void next_page() {
        prev_page.remove_view();
        current_page.remove_view();
        super.next_page();
    }
    public void prev_page() {
        super.prev_page();
    }
}
