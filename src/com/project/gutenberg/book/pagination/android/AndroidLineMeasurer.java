package com.project.gutenberg.book.pagination.android;

import android.graphics.Paint;
import com.project.gutenberg.book.pagination.LineMeasurer;

import java.util.HashMap;

public class AndroidLineMeasurer implements LineMeasurer {
    private Paint textPaint;
    private HashMap<Character, Float> precomputedWidths = new HashMap<Character, Float>();

    public AndroidLineMeasurer(Paint textPaint) {
        this.textPaint = textPaint;
    }
    public float measureWidth(String s) {
        float width = 0;
        for (int i=0;i<s.length();i++) {
            char c = s.charAt(i);
            Float charWidth = precomputedWidths.get(c);
            if (charWidth == null) {
                charWidth = textPaint.measureText(""+c);
                precomputedWidths.put(c, charWidth);
            }
            width += charWidth;
        }
        return width;
    }
    public float[] charWidths(String s) {
        float[] widths = new float[s.length()];
        for (int i=0; i < s.length(); i++) {
            char c = s.charAt(i);
            Float charWidth = precomputedWidths.get(c);
            if (charWidth == null) {
                charWidth = textPaint.measureText(""+c);
                precomputedWidths.put(c, charWidth);
            }
            widths[i] = charWidth;
        }
        return widths;
    }
}
