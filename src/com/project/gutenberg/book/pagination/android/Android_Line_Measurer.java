package com.project.gutenberg.book.pagination.android;

import android.graphics.Paint;
import com.project.gutenberg.book.pagination.Line_Measurer;
import com.project.gutenberg.util.Action_Time_Analysis;

public class Android_Line_Measurer implements Line_Measurer {
    private Paint text_paint;

    public Android_Line_Measurer(Paint text_paint) {
        this.text_paint = text_paint;
    }
    public float measure_width(String s) {
        Action_Time_Analysis.start("Android_Line_Measurer.measure_width");
        float width = text_paint.measureText(s);
        Action_Time_Analysis.end("Android_Line_Measurer.measure_width");
        return width;
    }
}
