package com.project.gutenberg.book.pagination;

import com.project.gutenberg.book.Book;
import com.project.gutenberg.book.Chapter;
import com.project.gutenberg.book.Page;
import com.project.gutenberg.book.view.Book_Formatting;
import com.project.gutenberg.book.view.Book_View;
import com.project.gutenberg.util.Action_Time_Analysis;
import com.project.gutenberg.util.Cores;
import com.project.gutenberg.util.Debug;
import com.project.gutenberg.util.Response_Callback;

public class Page_Splitter {
    private Book book;
    private int current_chapter;
    protected String[][] prev_current_next_page_lines;
    private Book_Formatting formatting;
    private Line_Measurer line_measurer;
    static int cores_finished = 0;

    public Page_Splitter(Book book, Book_Formatting formatting, Line_Measurer line_measurer, int current_chapter) {
        this.formatting = formatting;
        this.line_measurer = line_measurer;
        this.book = book;
        this.book.set_current_chapter(current_chapter);
        prev_current_next_page_lines = new String[3][formatting.get_lines_per_page()];
    }
    public void paginate(Response_Callback<Void> pages_loaded_callback) {
        int cores = Cores.getNumCores();
        for (int i=0; i < cores; i++) {
            new Load_All_Pages(pages_loaded_callback, i, cores);
        }
    }
    private String[] get_next_page_lines(Integer[] prev_boundaries) {
        Integer[] text_boundaries = new Integer[6];

        String[] lines_of_text = new String[formatting.get_lines_per_page()];
        lines_of_text[0] = "";
        int line_count = 0;

        if (prev_boundaries[3] >= book.number_of_chapters()) { // book is over.
            return null;
        } else if (prev_boundaries[4] == -1) { // start new chapter
            text_boundaries[0] = prev_boundaries[3]; // chapter start index
            text_boundaries[1] = 0; // paragraph start index
            text_boundaries[2] = -1; // word start index
        } else if (prev_boundaries[5] == -1) { // start new paragraph
            text_boundaries[0] = prev_boundaries[3];
            text_boundaries[1] = prev_boundaries[4];
            text_boundaries[2] = -1;
        } else {
            text_boundaries[0] = prev_boundaries[3];
            text_boundaries[1] = prev_boundaries[4];
            text_boundaries[2] = prev_boundaries[5];
        }
        int paragraph_index = text_boundaries[1];
        boolean reading_first_paragraph = true;
        A: for (String paragraph : book.get_chapter(text_boundaries[0]).get_paragraphs().subList(text_boundaries[1], book.get_chapter(text_boundaries[0]).get_paragraphs().size())) {
            if (paragraph == null || paragraph.equals("")) {
                lines_of_text[line_count] = "";
            } else {
                String[] words = Line_Splitter.fast_split(paragraph, ' ');
                if (words.length == 0) continue;
                words[0] = "    " + words[0];
                float[] word_widths = Line_Splitter.word_widths(words, line_measurer);
                int word_index = 0;
                if (reading_first_paragraph) {
                    if (!(text_boundaries[2] >= words.length-1)) {
                        word_index = text_boundaries[2]+1;
                    }
                }
                Object[] split_return;
                split_return = Line_Splitter.split(line_measurer, formatting, words, word_index, paragraph_index, line_count, text_boundaries, lines_of_text, word_widths);
                paragraph_index = (Integer)split_return[2];
                line_count = (Integer)split_return[3];
                if ((Boolean)split_return[1]) {break A;}
            }
            line_count++;
            if (line_count == lines_of_text.length) { // paragraph finished exactly.
                text_boundaries[3] = text_boundaries[0];
                text_boundaries[4] = paragraph_index+1;
                text_boundaries[5] = -1;
                break;
            } else { // next paragraph
                lines_of_text[line_count] = "";
            }
            paragraph_index++;
            reading_first_paragraph = false;
        }
        if (line_count < lines_of_text.length) { // chapter finished.
            text_boundaries[3] = text_boundaries[0]+1;
            text_boundaries[4] = -1;
            text_boundaries[5] = -1;
            for (int i=line_count; i < lines_of_text.length; i++) {
                lines_of_text[i] = "";
            }
        }
        book.get_chapter(text_boundaries[0]).add_page(false, new Page(lines_of_text));
        book.get_chapter(text_boundaries[0]).add_boundary(false, text_boundaries);
        return lines_of_text;
    }
    private boolean load_prev_and_next_pages(int chapter) {
        Chapter c = book.get_chapter(chapter);
        if (!c.first_page_loaded) {
            initialize_chapter(chapter);
            c.first_page_loaded = true;
        }
        if (!c.last_page_loaded) {
            Integer[] last_loaded_boundaries = c.get_last_boundary();
            if (last_loaded_boundaries[3] > chapter) {
                c.last_page_loaded = true;
            } else {
                get_next_page_lines(last_loaded_boundaries);
            }
        }
        return c.first_page_loaded && c.last_page_loaded;
    }
    private void initialize_chapter(int chapter) {
        Integer[] i = new Integer[6];
        i[3] = chapter;
        i[4] = -1;
        i[5] = -1;
        get_next_page_lines(i);
    }
    private class Load_All_Pages extends Thread {
        Response_Callback<Void> pages_loaded_callback;
        int mod;
        int cores;

        Load_All_Pages(Response_Callback<Void> pages_loaded_callback, int mod, int cores) {
            super();
            this.pages_loaded_callback = pages_loaded_callback;
            this.mod=mod;
            this.cores=cores;
            start();
        }
        public void run() {
            long start_time = System.currentTimeMillis();
            boolean stop_loop;
            A: while(true) {
                stop_loop = true;
                for (int i=0; i < book.number_of_chapters(); i++) {
                    if (i%cores != mod) continue;
                    if (!load_prev_and_next_pages(i)) {
                        stop_loop = false;
                    }
                }
                if (stop_loop) {
                    break A;
                }
            }
            cores_finished++;
            if (cores_finished == cores) {
                pages_loaded_callback.on_response(null);
                cores_finished = 0;
            }
        }
    }

}
