package com.project.gutenberg.book.pagination;

import com.project.gutenberg.book.Book;
import com.project.gutenberg.book.Chapter;
import com.project.gutenberg.book.Page;
import com.project.gutenberg.book.view.Book_Formatting;
import com.project.gutenberg.book.view.Book_View;
import com.project.gutenberg.util.Action_Time_Analysis;
import com.project.gutenberg.util.Debug;
import com.project.gutenberg.util.Fast_Line_Splitter;

import java.util.List;
import java.util.ListIterator;

public class Page_Splitter {
    private Book_View book_view;
    private Book book;
    private int current_chapter;
    private int current_paragraph;
    private int current_word;
    protected String[][] prev_current_next_page_lines;
    private Book_Formatting formatting;
    private Line_Measurer line_measurer;
    private final Object add_page_lock = new Object();

    public Page_Splitter(Book_View book_view, Book book, Book_Formatting formatting, Line_Measurer line_measurer, int current_chapter, int current_paragraph, int current_word) {
        this.book_view = book_view;
        this.current_chapter = current_chapter;
        this.current_paragraph = current_paragraph;
        this.current_word = current_word;
        this.formatting = formatting;
        this.line_measurer = line_measurer;
        this.book = book;
        this.book.set_current_chapter(this.current_chapter);
        this.book.set_current_page(0);
        prev_current_next_page_lines = new String[3][formatting.get_lines_per_page()];
    }

    public void paginate() {
        initialize_open_pages();
        Debug.log("first paragraph: " + book.get_chapter(current_chapter).get_paragraphs().getFirst());
        for (int i=0; i < prev_current_next_page_lines.length; i++) {
            for (int j=0; j < prev_current_next_page_lines[i].length; j++) {
                Debug.log("page: " + i + ", " + prev_current_next_page_lines[i][j]);
            }
        }
        book_view.set_prev_current_next_page_lines(prev_current_next_page_lines);
        new Load_All_Pages();
    }
    private void initialize_open_pages() {
        Action_Time_Analysis.start("initialize_open_pages");
        int line_count = 0;
        prev_current_next_page_lines[1][0] = "";
        Integer[] text_boundaries = new Integer[6];  // chapter start, paragraph start, word start, chapter end, paragraph end, word end.
        int paragraph_index = current_paragraph;

        A: for (String paragraph : book.get_chapter(current_chapter).get_paragraphs().subList(current_paragraph, book.get_chapter(current_chapter).get_paragraphs().size())) {
            if (paragraph == null || paragraph.equals("")) {
                prev_current_next_page_lines[1][line_count] = "";
            } else {
                String[] words = paragraph.split(" ");
                words[0] = "     " + words[0];
                for (int i=current_word; i < words.length; i++) {
                    if (line_measurer.measure_width(prev_current_next_page_lines[1][line_count] + " " + words[i]) > formatting.get_line_width()) {
                        Debug.log("measure width (>" + formatting.get_line_width() + "): " + line_measurer.measure_width(prev_current_next_page_lines[1][line_count] + " " + words[i]));
                        i--;
                        line_count++;
                        if (line_count == prev_current_next_page_lines[1].length) { // paragraph cutoff
                            text_boundaries[0] = current_chapter;
                            text_boundaries[1] = current_paragraph;
                            text_boundaries[2] = current_word;
                            text_boundaries[3] = current_chapter;
                            text_boundaries[4] = paragraph_index;
                            text_boundaries[5] = i;
                            break A;
                        } else {
                            prev_current_next_page_lines[1][line_count] = "";
                        }
                    } else {
                        prev_current_next_page_lines[1][line_count] += " " + words[i];
                    }
                }
            }
            line_count++;
            if (line_count == prev_current_next_page_lines[1].length) { // paragraph finished exactly.
                text_boundaries[0] = current_chapter;
                text_boundaries[1] = current_paragraph;
                text_boundaries[2] = current_word;
                text_boundaries[3] = current_chapter;
                text_boundaries[4] = paragraph_index+1;
                text_boundaries[5] = -1;
                break;
            } else { // next paragraph
                prev_current_next_page_lines[1][line_count] = "";
            }
            paragraph_index++;
        }
        if (line_count < prev_current_next_page_lines[1].length) { // chapter finished.
            text_boundaries[0] = current_chapter;
            text_boundaries[1] = current_paragraph;
            text_boundaries[2] = current_word;
            text_boundaries[3] = current_chapter+1;
            text_boundaries[4] = -1;
            text_boundaries[5] = -1;
            for (int i=line_count; i < prev_current_next_page_lines[1].length; i++) {
                prev_current_next_page_lines[1][i] = "";
            }
        }
        book.get_chapter(current_chapter).add_page(false, new Page(prev_current_next_page_lines[1]));
        book.get_chapter(current_chapter).add_boundary(false, text_boundaries);
        prev_current_next_page_lines[2] = get_next_page_lines(book.get_chapter(current_chapter).get_last_boundary());
        prev_current_next_page_lines[0] = get_prev_page_lines(book.get_chapter(current_chapter).get_first_boundary());
        book_view.set_prev_current_next_page_lines(prev_current_next_page_lines);
        Action_Time_Analysis.end("initialize_open_pages");
    }

    private String[] get_next_page_lines(Integer[] prev_boundaries) {
        Action_Time_Analysis.start("get_next_page_lines");
        Integer[] text_boundaries = new Integer[6];

        String[] lines_of_text = new String[formatting.get_lines_per_page()];
        lines_of_text[0] = "";
        int line_count = 0;

        if (prev_boundaries[3] >= book.number_of_chapters()) { // book is over.
            return null;
        } else if (prev_boundaries[4] == -1) { // start new chapter
            text_boundaries[0] = prev_boundaries[3];
            text_boundaries[1] = 0;
            text_boundaries[2] = 0;
            //if (!chapters_parsed[text_boundaries[0]].is_parsed()) {
                //chapters_parsed[text_boundaries[0]].parse_chapter(epub_spine.getResource(text_boundaries[0]));
            //}
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
        int parity=0;
        A: for (String paragraph : book.get_chapter(text_boundaries[0]).get_paragraphs().subList(text_boundaries[1], book.get_chapter(text_boundaries[0]).get_paragraphs().size())) {
            parity = (int)(Math.random()*10000);
            if (paragraph == null || paragraph.equals("")) {
                lines_of_text[line_count] = "";
            } else {
                Action_Time_Analysis.start("get_next_page_lines.split");
                String[] words = paragraph.split(" ");
                Action_Time_Analysis.end("get_next_page_lines.split");
                words[0] = "     " + words[0];
                float[] word_widths = Fast_Line_Splitter.word_widths(words,line_measurer);

                int word_index = 0;
                if (reading_first_paragraph) {
                    if (!(text_boundaries[2] >= words.length-1)) {
                        word_index = text_boundaries[2]+1;
                    }
                }
                Object[] split_return;
                split_return = Fast_Line_Splitter.split(line_measurer,formatting,words,word_index,paragraph_index,line_count,text_boundaries,lines_of_text,word_widths);
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
        Action_Time_Analysis.end("get_next_page_lines");
        return lines_of_text;
    }

    private String[] get_prev_page_lines(Integer[] next_boundaries) {
        Action_Time_Analysis.start("get_prev_page_lines");
        Integer[] text_boundaries = new Integer[6];
        String[] lines_of_text = new String[formatting.get_lines_per_page()];
        lines_of_text[0] = "";
        int line_count = lines_of_text.length-1;
        int ending_chapter_index = -1;

        if (next_boundaries[0] == 0 && next_boundaries[1] == 0 && next_boundaries[2] == 0) { // no previous page.
            return null;
        } else if (next_boundaries[1] == 0 && next_boundaries[2] == 0) { // end chapter.
            text_boundaries[5] = -1;
            text_boundaries[4] = -1;
            text_boundaries[3] = next_boundaries[0];
            ending_chapter_index = text_boundaries[3]-1;
        } else if (next_boundaries[2] == 0) { // paragraph ended exactly
            text_boundaries[5] = -1;
            text_boundaries[4] = next_boundaries[1];
            text_boundaries[3] = next_boundaries[0];

            ending_chapter_index = text_boundaries[3];
        } else if (next_boundaries[2] != 0) { // paragraph cut off
            text_boundaries[5] = next_boundaries[2];
            text_boundaries[4] = next_boundaries[1];
            text_boundaries[3] = next_boundaries[0];

            ending_chapter_index = text_boundaries[3];
        }

        int paragraph_index = text_boundaries[4];
        List<String> this_chapter;
        if (text_boundaries[4] == -1) {
            this_chapter = book.get_chapter(ending_chapter_index).get_paragraphs();
            paragraph_index = this_chapter.size()-1;
        } else {
            //chapters_parsed[ending_chapter_index].get_paragraphs().subList(0, text_boundaries[4]-1);
            this_chapter = book.get_chapter(ending_chapter_index).get_paragraphs().subList(0, text_boundaries[4]-1);
        }

        ListIterator<String> it = this_chapter.listIterator(this_chapter.size());
        boolean first_paragraph_read = true;
        lines_of_text[line_count] = "";
        A: while (it.hasPrevious()) {
            String paragraph = it.previous();
            int paragraph_last_line = line_count;
            if (paragraph == null || paragraph.equals("")) {
                lines_of_text[line_count] = "";
            } else {
                String[] words = paragraph.split(" ");
                words[0] = "     " + words[0];
                int word_index = words.length-1;
                if (first_paragraph_read && text_boundaries[5] != -1) {
                    if (!(text_boundaries[5] > words.length)) {
                        word_index = text_boundaries[5]-1;
                    }
                }

                for (int i=word_index; i > -1; i--) {
                    if (line_measurer.measure_width(words[i] + " " + lines_of_text[line_count]) > formatting.get_line_width()) {
                        i++;
                        line_count--;
                        if (line_count == -1) { // paragraph cutoff at top.
                            text_boundaries[0] = ending_chapter_index;
                            text_boundaries[1] = paragraph_index;
                            text_boundaries[2] = i;
                            break A;
                        } else {
                            lines_of_text[line_count] = "";
                        }
                    } else {
                        lines_of_text[line_count] = words[i] + " " + lines_of_text[line_count];
                    }
                }
            }
            line_count--;
            if (line_count == -1) { // paragraph finished exactly.
                text_boundaries[0] = ending_chapter_index;
                text_boundaries[1] = paragraph_index;
                text_boundaries[2] = 0;
                if (paragraph != null && !paragraph.equals("")) {
                    recompile_paragraph(paragraph, lines_of_text, line_count+1, paragraph_last_line);
                }
                break;
            } else { // next paragraph
                lines_of_text[line_count] = "";
                if (paragraph != null && !paragraph.equals("")) {
                    recompile_paragraph(paragraph, lines_of_text, line_count+1, paragraph_last_line);
                }
            }

            first_paragraph_read = false;
            paragraph_index--;
        }
        if (line_count > -1) { // chapter finished.
            text_boundaries[0] = ending_chapter_index;
            text_boundaries[1] = 0;
            text_boundaries[2] = 0;
            for (int i=line_count; i > -1; i--) {
                lines_of_text[i] = "";
            }
        }
        synchronized(add_page_lock) {
            book.get_chapter(ending_chapter_index).add_page(true, new Page(lines_of_text));
            book.get_chapter(ending_chapter_index).add_boundary(true, text_boundaries);
            if (ending_chapter_index == current_chapter) {
                book.increment_current_page();
            }
        }
        Action_Time_Analysis.end("get_prev_page_lines");
        return lines_of_text;
    }

    /*
     *  When backtracking, if the start of a paragraph is found, the paragraph needs to be recompiled in order to fill the first line, and leave whitespace after the last line.
     */
    private void recompile_paragraph(String paragraph, String[] lines_of_text, int start_line, int end_line) {
        Action_Time_Analysis.start("recompile_paragraph");
        String[] words = paragraph.split(" ");
        words[0] = "     " + words[0];
        int line_count = start_line;
        lines_of_text[line_count] = "";
        for (int i=0; i < words.length; i++) {
            if (line_measurer.measure_width(lines_of_text[line_count] + " " + words[i]) > formatting.get_line_width()) {
                i--;
                line_count++;
                if (line_count >= end_line) { // paragraph cutoff
                    break;
                } else {
                    lines_of_text[line_count] = "";
                }
            } else {
                lines_of_text[line_count] += " " + words[i];
            }
        }
        Action_Time_Analysis.end("recompile_paragraph");
    }

    private void initialize_chapter(int chapter) {
        Integer[] i = new Integer[6];
        i[3] = chapter;
        i[4] = -1;
        i[5] = -1;
        get_next_page_lines(i);
    }
    private class Load_All_Pages extends Thread {
        Load_All_Pages() {
            super();
            start();
        }
        public void run() {
            Action_Time_Analysis.start("Load_All_Pages.run");
            Integer[] first_loaded_boundaries;
            Integer[] last_loaded_boundaries;
            long start_time = System.currentTimeMillis();

            for (int i=0; i < book.number_of_chapters(); i++) {
                Action_Time_Analysis.start("Load_All_Pages.loop1");
                Chapter c = book.get_chapter(i);
                if (!c.first_page_loaded) {
                    first_loaded_boundaries = c.get_first_boundary();
                    if (first_loaded_boundaries == null) {
                        c.first_page_loaded = true;
                        initialize_chapter(i);
                    } else if (first_loaded_boundaries[1] == 0 && first_loaded_boundaries[2] == 0) {
                        c.first_page_loaded = true;
                    }
                } else if (!c.last_page_loaded) {
                    last_loaded_boundaries = c.get_last_boundary();
                    if (last_loaded_boundaries[5] == -1 && last_loaded_boundaries[4] == -1) {
                        c.last_page_loaded = true;
                    }
                }
                Action_Time_Analysis.end("Load_All_Pages.loop1");
            }
            boolean stop_loop;
            A: while(true) {
                Action_Time_Analysis.start("Load_All_Pages.loop2");
                stop_loop = true;

                if (!load_prev_and_next_pages(current_chapter)) {
                    stop_loop = false;
                }
                for (int i=0; i < book.number_of_chapters(); i++) {
                    if (!load_prev_and_next_pages(i)) {
                        stop_loop = false;
                    }
                }
                Action_Time_Analysis.end("Load_All_Pages.loop2");
                if (stop_loop) {
                    break A;
                }
            }
            Debug.log("pages loaded in " + (System.currentTimeMillis() -start_time) + " ms.");
            Action_Time_Analysis.end("Load_All_Pages.run");
        }
    }
    private boolean load_prev_and_next_pages(int chapter) {
        boolean stop_loop = false;
        Chapter c = book.get_chapter(chapter);
        if (!c.first_page_loaded) {
            stop_loop = false;
            Integer[] first_loaded_boundaries = c.get_first_boundary();
            if (first_loaded_boundaries[1] == 0 && first_loaded_boundaries[2] == 0) {
                c.first_page_loaded = true;
            } else {
                get_prev_page_lines(first_loaded_boundaries);
            }

        }
        if (!c.last_page_loaded) {
            stop_loop = false;
            Integer[] last_loaded_boundaries = c.get_last_boundary();
            if (last_loaded_boundaries[3] > chapter) {
                c.last_page_loaded = true;
            } else {
                get_next_page_lines(last_loaded_boundaries);
            }
        }
        return stop_loop;
    }
}
