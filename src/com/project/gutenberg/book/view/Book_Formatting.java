package com.project.gutenberg.book.view;

import android.graphics.Paint;

public class Book_Formatting {

    private int margin_width;
    private int line_width;
    private int line_spacing;
    private int lines_per_page;
    private int[] line_y_coordinates;

    public Book_Formatting(int width, int height, int font_size) {
        initialize_formatting(width, height, font_size);
    }

    public void initialize_formatting(int width, int height, int font_size) {
        margin_width = (int)(width*0.05);
        line_width = width - (margin_width*2);
        line_spacing = (int)(font_size*0.5);
        int free_space = height;
        int starting_position = font_size + line_spacing*3;
        free_space = free_space - (font_size*2 + line_spacing*4); // header, footer space.
        lines_per_page = free_space/(font_size+line_spacing);
        line_y_coordinates = new int[lines_per_page];
        line_y_coordinates[0] = starting_position;
        for (int i=1; i < line_y_coordinates.length; i++) {
            line_y_coordinates[i] = line_y_coordinates[i-1]+font_size+line_spacing;
        }

    }
    public int get_lines_per_page() {
        return lines_per_page;
    }
    public int get_line_width() {
        return line_width;
    }
    public int[] get_line_y_coordinates() {
        return line_y_coordinates;
    }
    public int get_margin_width() {
        return margin_width;
    }
}
