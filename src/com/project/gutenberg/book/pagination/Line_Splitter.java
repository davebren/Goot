package com.project.gutenberg.book.pagination;

import android.util.Log;
import com.project.gutenberg.book.pagination.Line_Measurer;
import com.project.gutenberg.book.view.Book_Formatting;
import com.project.gutenberg.util.Action_Time_Analysis;

import java.util.ArrayList;
import java.util.List;

public class Line_Splitter {
    public static Object[] split(Line_Measurer line_measurer, Book_Formatting formatting, String[] words, Integer word_index, Integer paragraph_index, Integer line_count,Integer[] text_boundaries, String[] lines_of_text, float[] word_widths) {
        Action_Time_Analysis.start("split");
        Object[] return_object =  build_forward_(line_measurer,formatting,words,word_index,paragraph_index,line_count,text_boundaries,lines_of_text, word_widths);
        Action_Time_Analysis.end("split");
        return return_object;
    }
    public static Object[] build_forward_(Line_Measurer line_measurer, Book_Formatting formatting, String[] words, int word_index, int paragraph_index, int line_count,Integer[] text_boundaries, String[] lines_of_text, float[] word_widths) {
        Object[] return_object = new Object[5];
        return_object[1]=new Boolean(false);
        int words_added = 0;
        int line_words_added = 0;
        float space_width = line_measurer.measure_width(" ");
        float current_width = line_measurer.measure_width(lines_of_text[line_count]);
        for (int i=word_index; i < words.length; i++) {
            float new_width = current_width + space_width + word_widths[i];
            if (new_width > formatting.get_line_width()) {
                if (line_words_added == 0) {
                    int overflow_index = split_single_word(words[i],word_widths[i],line_measurer,formatting);
                    String first_part = words[i].substring(0,overflow_index);
                    lines_of_text[line_count] = first_part;
                    words[i] = words[i].substring(overflow_index,words[i].length());
                    word_widths[i] = word_widths[i]- line_measurer.measure_width(first_part);
                    i--;
                    line_count++;
                    lines_of_text[line_count] = "";
                    current_width=0;
                    Log.d("gutendroid", "long word split: " + first_part + " --- " +  words[i+1]);
                    Log.d("gutendroid", "long word split: " + i+1 + ", " + line_count + ", " + lines_of_text.length);
                    line_words_added=0;
                    continue;
                }
                i--;
                line_count++;
                line_words_added=0;
                if (line_count == lines_of_text.length) { // paragraph cutoff
                    Log.d("gutendroid", "paragraph cutoff");
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
                line_words_added++;
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
    private static int split_single_word(String word, float word_width, Line_Measurer measurer, Book_Formatting formatting) {
        for (int i=0; i < word.length(); i++) {
            if (measurer.measure_width(word.substring(0,i)) > formatting.get_line_width()) {
                return i;
            }
        }
        return word.length()-1;
    }
    public static String[] fast_split(final String text, char separator) {
        final List<String> result = new ArrayList<String>();
        if (text != null && text.length() > 0) {
            int index1 = 0;
            int index2 = text.indexOf(separator);
            while (index2 >= 0) {
                String token = text.substring(index1, index2);
                result.add(token);
                index1 = index2 + 1;
                index2 = text.indexOf(separator, index1);
            }
            if (index1 < text.length() - 1) {
                result.add(text.substring(index1));
            }
        }
        return result.toArray(new String[result.size()]);
    }
}
