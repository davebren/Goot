package com.project.gutenberg.util;

import android.util.Log;
import com.project.gutenberg.book.pagination.Line_Measurer;
import com.project.gutenberg.book.view.Book_Formatting;

public class Fast_Line_Splitter {
    private static int average_words_per_line;

    public static Object[] split(Line_Measurer line_measurer, Book_Formatting formatting, String[] words, Integer word_index, Integer paragraph_index, Integer line_count,Integer[] text_boundaries, String[] lines_of_text, float[] word_widths) {
        Action_Time_Analysis.start("split");
        Object[] return_object =  build_forward_(line_measurer,formatting,words,word_index,paragraph_index,line_count,text_boundaries,lines_of_text, word_widths);
        //Object[] return_object =  build_forward(line_measurer,formatting,words,word_index,paragraph_index,line_count,text_boundaries,lines_of_text);
        Action_Time_Analysis.end("split");
        return return_object;
    }
    public static Object[] build_forward_(Line_Measurer line_measurer, Book_Formatting formatting, String[] words, int word_index, int paragraph_index, int line_count,Integer[] text_boundaries, String[] lines_of_text, float[] word_widths) {
        Object[] return_object = new Object[5];
        return_object[1]=new Boolean(false);
        int words_added = 0;
        float space_width = line_measurer.measure_width(" ");
        float current_width = line_measurer.measure_width(lines_of_text[line_count]);
        for (int i=word_index; i < words.length; i++) {
            float new_width = current_width + space_width + word_widths[i];
            if (new_width > formatting.get_line_width()) {
                i--;
                line_count++;
                if (line_count == lines_of_text.length) { // paragraph cutoff
                    text_boundaries[3] = text_boundaries[0];
                    text_boundaries[4] = paragraph_index;
                    text_boundaries[5] = i;
                    return_object[1]=true;
                    break;
                } else {
                    lines_of_text[line_count] = "";
                    current_width=0;
                }
            } else {
                words_added++;
                lines_of_text[line_count] += " " + words[i];
                current_width = new_width;
            }
        }
        return_object[0] = word_index;
        return_object[2]=paragraph_index;
        return_object[3]=line_count;
        return_object[4]= words_added;
        return return_object;
    }
    public static float[] word_widths(String[] words, Line_Measurer line_measurer) {
        float[] widths = new float[words.length];
        for (int i=0; i < words.length; i++) {
            widths[i] = line_measurer.measure_width(words[i]);
        }
        return widths;
    }
}
