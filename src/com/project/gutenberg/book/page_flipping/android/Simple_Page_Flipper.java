package com.project.gutenberg.book.page_flipping.android;

import android.widget.RelativeLayout;
import com.project.gutenberg.book.Book;
import com.project.gutenberg.book.Chapter;
import com.project.gutenberg.book.page_flipping.Page_Flipper;
import com.project.gutenberg.book.view.Book_View;
import com.project.gutenberg.book.view.Page_View;
import com.project.gutenberg.util.Debug;

public class Simple_Page_Flipper extends Page_Flipper {
    private RelativeLayout page_holder;

    public Simple_Page_Flipper(Book_View book_view, Page_View prev_page, Page_View current_page, Page_View next_page, Book book, RelativeLayout page_holder) {
        super(book_view, prev_page, current_page, next_page, book);

    }
    public void next_page() {
        Chapter current_chapter = book.get_chapter(book.get_current_chapter());
        if (current_chapter.number_of_pages() > current_chapter.get_list_relative_current_page_index()+1) {
            current_page.remove_view();
            Debug.log("remove view");
            current_page.add_view(0);
            Debug.log("add view back");
            Debug.log("pre swap: " + prev_page.get_page_stack_id() + ", " + current_page.get_page_stack_id() + ", " + next_page.get_page_stack_id());
            Page_View temp = current_page;
            current_page = next_page;
            next_page = temp;
            Debug.log("post swap: " + prev_page.get_page_stack_id() + ", " + current_page.get_page_stack_id() + ", " + next_page.get_page_stack_id());
            String[][] lines_of_text = new String[3][];
            lines_of_text[0] = book_view.get_page_lines(1);
            lines_of_text[1] = current_chapter.get_next_page().get_page_text();
            if (current_chapter.number_of_pages() > current_chapter.get_list_relative_current_page_index()+1) {
                lines_of_text[2] = current_chapter.peek_next_page().get_page_text();
            } else if (!current_chapter.last_page_loaded) {
                lines_of_text[2] = new String[1];
                lines_of_text[2][0] = "";
                current_chapter.add_loading_hook(1, true, book_view);
            }
            book_view.set_prev_current_next_page_lines(lines_of_text);
            current_page.set_page_stack_id(0);
            next_page.set_page_stack_id(1);
            book_view.set_prev_current_next_page_lines(lines_of_text);
        } else if (current_chapter.last_page_loaded) {
            if (book.get_current_chapter()+1 < book.number_of_chapters()) {

            } else {

            }
        } else {

        }

    }
    public void prev_page() {

    }
}
