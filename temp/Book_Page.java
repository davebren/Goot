/*package com.project.gutenberg;

import android.content.Context;
import android.graphics.*;
import android.view.View;
import android.widget.RelativeLayout;
import com.project.gutenberg.com.project.gutenberg.util.Debug;

public class Book_Page extends View {

    private int background_color = Color.parseColor("#f7f5f5");
    private int standard_text_grey = Color.parseColor("#4e4b47");
    private int view_width;
    private int view_height;
    private int font_size;
    private int line_spacing;
    private int line_width;
    private int margin_width;
    private int[] text_line_positions;

    private Paint text_paint;

    private String[] lines_of_text;

    private Book_View book_view;

    private int page_stack_id = -1;


    public Book_Page(Context context, Book_View parent, int page_stack_id) {
        super(context);
        this.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT));
        font_size = Home.prefs.get_text_size();
        line_spacing = (int)(font_size*0.5);
        book_view = parent;
        this.page_stack_id = page_stack_id;

    }

    public void onSizeChanged (int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        view_width = w;
        view_height = h;
        margin_width = (int)(view_width*0.05);
        line_width = view_width - (margin_width*2);

        text_paint = new Paint();
        text_paint.setTextSize(font_size);
        text_paint.setColor(standard_text_grey);
        set_line_positions();
    }

    public void set_line_positions() {
        int free_space = view_height;
        int starting_position = font_size + line_spacing*3;
        free_space = free_space - (font_size*2 + line_spacing*4); // header, footer space.
        int number_of_lines = free_space/(font_size+line_spacing);
        text_line_positions = new int[number_of_lines];

        text_line_positions[0] = starting_position;
        for (int i=1; i < text_line_positions.length; i++) {
            text_line_positions[i] = text_line_positions[i-1]+font_size+line_spacing;
        }
        lines_of_text = new String[number_of_lines];
    }
    public void onDraw(Canvas c) {
         draw_background(c);
         for (int i=0; i < book_view.lines_of_text[page_stack_id+1].length; i++) {
             if (book_view.lines_of_text[page_stack_id+1][i] != null) {
                 Debug.log("draw: " + book_view.lines_of_text[page_stack_id+1][i]);
                 c.drawText(book_view.lines_of_text[page_stack_id+1][i], margin_width, book_view.text_line_positions[i], book_view.text_paint);
             }
         }
    }

    public void draw_background(Canvas c) {
        c.drawColor(background_color);
    }

    public void draw_header(Canvas c, Rect bounds) {

    }
    public void draw_footer(Canvas c, Rect bounds) {

    }
    protected void set_page_stack_id(int id) {
        page_stack_id = id;
    }






}
*/