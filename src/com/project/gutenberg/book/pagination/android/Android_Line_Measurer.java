package com.project.gutenberg.book.pagination.android;

import android.graphics.Paint;
import com.project.gutenberg.book.pagination.Line_Measurer;

public class Android_Line_Measurer implements Line_Measurer {
    private Paint text_paint;

    public Android_Line_Measurer(Paint text_paint) {
        this.text_paint = text_paint;
    }

    public float measure_width(String s) {
        return text_paint.measureText(s);
    }
}
