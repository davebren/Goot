package com.project.gutenberg.book.pagination.android;

import android.graphics.Paint;
import com.project.gutenberg.book.pagination.Line_Measurer;
import com.project.gutenberg.util.Action_Time_Analysis;

import java.util.HashMap;

public class Android_Line_Measurer implements Line_Measurer {
    private Paint text_paint;
    private HashMap<Character, Float> precomputed_widths = new HashMap<Character, Float>();

    public Android_Line_Measurer(Paint text_paint) {
        this.text_paint = text_paint;
    }
    public float measure_width(String s) {
        Action_Time_Analysis.start("Android_Line_Measurer.measure_width");
        float width = 0;
        for (int i=0;i<s.length();i++) {
            char c = s.charAt(i);
            Float char_width = precomputed_widths.get(c);
            if (char_width == null) {
                char_width = text_paint.measureText(""+c);
                precomputed_widths.put(c,char_width);
            }
            width += char_width;
        }
        Action_Time_Analysis.end("Android_Line_Measurer.measure_width");
        return width;
    }
}
