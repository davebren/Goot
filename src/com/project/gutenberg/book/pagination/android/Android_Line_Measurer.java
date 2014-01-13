package com.project.gutenberg.book.pagination.android;

import android.graphics.Paint;
import com.project.gutenberg.book.pagination.Line_Measurer;
import com.project.gutenberg.util.Action_Time_Analysis;

public class Android_Line_Measurer implements Line_Measurer {
    private Paint text_paint;
    private float[] precomputed_widths = new float[256];

    public Android_Line_Measurer(Paint text_paint) {
        this.text_paint = text_paint;
        precompute_character_widths();
    }
    private void precompute_character_widths() {
        for (int i=0; i < precomputed_widths.length; i++) {
            precomputed_widths[i] = text_paint.measureText(""+(char)i);
        }
    }
    public float measure_width(String s) {
        Action_Time_Analysis.start("Android_Line_Measurer.measure_width");
        float width = text_paint.measureText(s);
        Action_Time_Analysis.end("Android_Line_Measurer.measure_width");
        return width;
    }
    public float measure_width_fast(String s) {
        Action_Time_Analysis.start("Android_Line_Measurer.measure_width_fast");
        float width = 0;
        for (int i=0;i<s.length();i++) {
            int c = s.charAt(i);
            if (c > 0x7f) {
                return measure_width(s);
            }
            width += precomputed_widths[c];
        }
        Action_Time_Analysis.end("Android_Line_Measurer.measure_width_fast");
        return width;
    }
}
