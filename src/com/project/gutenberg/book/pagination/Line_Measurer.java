package com.project.gutenberg.book.pagination;


public interface Line_Measurer {
    public float measure_width(String s);
    public float[] char_widths(String s);
}
