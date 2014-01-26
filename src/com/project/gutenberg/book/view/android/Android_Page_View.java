package com.project.gutenberg.book.view.android;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import com.project.gutenberg.book.pagination.Line_Measurer;
import com.project.gutenberg.book.pagination.android.Android_Line_Measurer;
import com.project.gutenberg.book.view.Book_Formatting;
import com.project.gutenberg.book.view.Book_View;
import com.project.gutenberg.book.view.Page_View;

class Android_Page_View extends Page_View {
    private Page page;
    private Paint text_painter;
    Android_Page_View(Context context, Android_Book_View book_view, int page_stack_id) {
        this.book_view = book_view;
        page = new Page(context);
        this.page_stack_id = page_stack_id;
        this.text_painter = book_view.text_painter;
    }
    public void add_view(int index) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
        if (index == -1) {

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
        // TODO add basic kerning.
        public void onDraw(Canvas c) {
            draw_background(c);
            String[] text = book_view.get_page_lines(page_stack_id+1);
            Book_Formatting format = book_view.get_formatting();
            int[] line_y_coordinates = format.get_line_y_coordinates();
            if (text == null) {return;}
            for (int i=0; i < text.length; i++) {
                if (text[i]!= null) {
                    kern(c,book_view,line_y_coordinates[i],text,i);
                    //c.drawText(text[i], format.get_margin_width(), line_y_coordinates[i], text_painter);
                }
            }
        }
        public void draw_background(Canvas c) {
            c.drawColor(((Android_Book_View) book_view).background_color);
        }
        private void kern(Canvas c, Book_View book_view, int y_coordinate, String[] lines, int index) {
            float margin = book_view.get_formatting().get_margin_width();
            if (lines.length <= index+1 || lines[index+1].startsWith("  ") || lines[index+1].equals("")) {
                c.drawText(lines[index],margin,y_coordinate,text_painter);
            } else {
                float kern_space = book_view.get_formatting().get_line_width() - book_view.get_line_measurer().measure_width(lines[index]);
                kern_space = (float)Math.min(kern_space, book_view.get_formatting().get_line_width()*.25);
                float kern_size = kern_space/lines[index].length();
                float[] char_widths = book_view.get_line_measurer().char_widths(lines[index]);
                int kerns_skipped = 0;
                for (int i=0; i < lines[index].length(); i++) {
                    if (lines[index].charAt(i) != ' ') {
                        kerns_skipped = i;break;
                    }
                }
                for (int i=kerns_skipped; i < lines[index].length();i++) {
                    char ch = lines[index].charAt(i);
                    c.drawText("" + ch, margin + sum_array(char_widths, i) + (i-kerns_skipped)*kern_size, y_coordinate,text_painter);
                    if (ch == '?' || ch == '!' || ch == ',' || ch == '.' || ch == '"' || ch == '\'' || ch == ';' || ch == ':') {
                        kerns_skipped++;
                    }
                }
            }
        }
    }
    private float sum_array(float[] arr, int end_index) {
        float sum = 0;
        for (int i=0; i < end_index; i++) {
            sum += arr[i];
        }
        return sum;
    }


}
