package com.project.gutenberg.book.view.android;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.project.gutenberg.book.Book;
import com.project.gutenberg.book.page_flipping.android.Simple_Page_Flipper;
import com.project.gutenberg.book.pagination.Page_Splitter;
import com.project.gutenberg.book.pagination.android.Android_Line_Measurer;
import com.project.gutenberg.book.view.Book_View;
import com.project.gutenberg.book.view.Page_View;
import com.project.gutenberg.com.project.gutenberg.util.Debug;
import com.project.gutenberg.com.project.gutenberg.util.Shared_Prefs;

public class Android_Book_View extends Book_View {
    protected int background_color = Color.parseColor("#f7f5f5");
    private int standard_text_grey = Color.parseColor("#4e4b47");
    private Shared_Prefs prefs;
    private RelativeLayout page_holder;
    private Context context;
    protected Paint text_painter;


    public Android_Book_View(Book book, Context context, Shared_Prefs prefs, LinearLayout.LayoutParams fill_screen_params, int view_width, int view_height, int flip_style) {
        super(view_width, view_height, prefs.get_book_font_size());
        this.prefs = prefs;
        this.context = context;
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
        page_holder.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                Debug.log("on touch, " + event.getAction() + ":" + MotionEvent.ACTION_UP + ", " + event.getX() + ", " + (formatting.get_total_width() / 2));
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getX() > formatting.get_total_width()/2) {
                        page_flipper.next_page();
                    } else {
                        page_flipper.prev_page();
                    }
                }
                return true;
            }
        });
    }
    private void initialize_page_views() {
        text_painter = new Paint();
        text_painter.setTextSize(prefs.get_book_font_size());
        text_painter.setColor(standard_text_grey);
        prev_page = new Android_Page_View(context, this, prev_page_stack_id);
        current_page = new Android_Page_View(context, this, current_page_stack_id);
        next_page = new Android_Page_View(context, this, next_page_stack_id);
        current_page.add_view(-1);
        next_page.add_view(0);
    }
    public RelativeLayout get_page_holder() {
        return page_holder;
    }

    public void initialize_page_flipper() {
        page_flipper = new Simple_Page_Flipper(this, prev_page, current_page, next_page, book, page_holder);
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
}
