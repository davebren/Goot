package com.project.gutenberg.book.pagination;


public interface LineMeasurer {
    public float measureWidth(String s);
    public float[] charWidths(String s);
}
