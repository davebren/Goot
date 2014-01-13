package com.project.gutenberg.book.view.android;


import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.widget.RelativeLayout;
import com.project.gutenberg.book.view.Book_Formatting;
import com.project.gutenberg.book.view.Page_View;

class Android_Page_View extends Page_View {
    private Page page;
    Android_Page_View(Context context, Android_Book_View book_view, int page_stack_id) {
        this.book_view = book_view;
        page = new Page(context);
        this.page_stack_id = page_stack_id;
    }
    public void add_view(int index) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
        if (index == -1) {
            ((Android_Book_View) book_view).get_page_holder().addView(page);
        } else {
            ((Android_Book_View) book_view).get_page_holder().addView(page, index);
        }
    }
    public void remove_view() {
        ((Android_Book_View) book_view).get_page_holder().removeView(page);
    }
    public void set_page_stack_id(int id) {
        page_stack_id = id;
    }
    public int get_page_stack_id() {
        return page_stack_id;
    }
    public void invalidate() {
        page.invalidate();
    }
    private class Page extends View {
        public Page(Context context) {
            super(context);
        }
        public void onDraw(Canvas c) {
            draw_background(c);
            String[] text = book_view.get_page_lines(page_stack_id+1);
            Book_Formatting format = book_view.get_formatting();
            int[] line_y_coordinates = format.get_line_y_coordinates();
            if (text == null) {return;}
            for (int i=0; i < text.length; i++) {
                if (text[i]!= null) {
                    c.drawText(text[i], format.get_margin_width(), line_y_coordinates[i], ((Android_Book_View) book_view).text_painter);
                }
            }
        }
        public void draw_background(Canvas c) {
            c.drawColor(((Android_Book_View) book_view).background_color);
        }
    }


}
