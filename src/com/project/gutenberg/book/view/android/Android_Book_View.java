package com.project.gutenberg.book.view.android;

import android.content.Context;
import android.graphics.Color;
import android.text.TextPaint;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.project.gutenberg.Action_Bar_Handler;
import com.project.gutenberg.R;
import com.project.gutenberg.book.Book;
import com.project.gutenberg.book.page_flipping.android.simple.Simple_Page_Flipper;
import com.project.gutenberg.book.pagination.android.Android_Line_Measurer;
import com.project.gutenberg.book.view.Book_View;
import com.project.gutenberg.util.Shared_Prefs;

public class Android_Book_View extends Book_View {
    protected int background_color = Color.parseColor("#f7f5f5");
    private int standard_text_grey = Color.parseColor("#4e4b47");
    private Shared_Prefs prefs;
    private ViewGroup page_holder;
    private Context context;
    protected TextPaint text_painter;
    private Action_Bar_Handler action_bar_handler;

    public Android_Book_View(Book book, Context context, Shared_Prefs prefs, LinearLayout.LayoutParams fill_screen_params, int view_width, int view_height, int flip_style, Action_Bar_Handler action_bar_handler) {
        super(view_width, view_height, (int)(context.getResources().getDimensionPixelSize(R.dimen.book_default_font_size)*prefs.get_book_font_scale()));
        this.prefs = prefs;
        this.context = context;
        this.action_bar_handler = action_bar_handler;
        text_painter = new TextPaint();

        initialize_page_holder(fill_screen_params);
        initialize_page_views();
        line_measurer = new Android_Line_Measurer(text_painter);
        this.flip_style = flip_style;
        this.book = book;
        initialize_page_flipper();
    }
    private void initialize_page_holder(LinearLayout.LayoutParams fill_screen_params) {
        page_holder = new RelativeLayout(context);
        page_holder.setLayoutParams(fill_screen_params);
    }
    private void initialize_page_views() {
        text_painter.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.book_default_font_size)*prefs.get_book_font_scale());
        text_painter.setColor(standard_text_grey);
        prev_page = new Android_Page_View(context, this, prev_page_stack_id);
        current_page = new Android_Page_View(context, this, current_page_stack_id);
        next_page = new Android_Page_View(context, this, next_page_stack_id);
        current_page.add_view(0);
        next_page.add_view(0);
    }
    public ViewGroup get_page_holder() {
        return page_holder;
    }
    public void initialize_page_flipper() {
        page_flipper = new Simple_Page_Flipper(this, context, prev_page, current_page, next_page, book);
    }
    public void loading_hook_completed_receiver(String[] lines_of_text, int stack_id) {
        if (stack_id == -1) {
            this.set_prev_current_next_page_lines(lines_of_text, 0);
            prev_page.invalidate();
        } else if (stack_id == 0) {
            this.set_prev_current_next_page_lines(lines_of_text, 1);
            current_page.invalidate();
        } else if (stack_id == 1) {
            this.set_prev_current_next_page_lines(lines_of_text, 2);
            next_page.invalidate();
        }
    }
    public Action_Bar_Handler get_action_bar_handler() {
        return action_bar_handler;
    }
}
