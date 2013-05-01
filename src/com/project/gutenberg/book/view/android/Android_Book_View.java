package com.project.gutenberg.book.view.android;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.project.gutenberg.book.Book;
import com.project.gutenberg.book.view.Book_View;
import com.project.gutenberg.book.view.Page_View;
import com.project.gutenberg.com.project.gutenberg.util.Shared_Prefs;

public class Android_Book_View extends Book_View {
    protected int background_color = Color.parseColor("#f7f5f5");
    private int standard_text_grey = Color.parseColor("#4e4b47");
    private Shared_Prefs prefs;
    private RelativeLayout page_holder;
    private Context context;
    protected Paint text_painter;

    public Android_Book_View(Book book, Context context, Shared_Prefs prefs, LinearLayout.LayoutParams fill_screen_params, int view_width, int view_height) {
        super(view_width, view_height, prefs.get_book_font_size());
        this.prefs = prefs;
        this.context = context;
        initialize_page_holder(fill_screen_params);
        initialize_page_views();

    }
    private void initialize_page_holder(LinearLayout.LayoutParams fill_screen_params) {
        page_holder = new RelativeLayout(context);
        page_holder.setLayoutParams(fill_screen_params);
    }
    private void initialize_page_views() {
        text_painter = new Paint();
        text_painter.setTextSize(prefs.get_book_font_size());
        text_painter.setColor(standard_text_grey);
        prev_page = new Android_Page_View(context, this, prev_page_stack_id);
        current_page = new Android_Page_View(context, this, current_page_stack_id);
        next_page = new Android_Page_View(context, this, next_page_stack_id);
        next_page.add_view(-1);
        current_page.add_view(-1);
    }
    protected RelativeLayout get_page_holder() {
        return page_holder;
    }


}
