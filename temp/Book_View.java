/*package com.project.gutenberg;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.project.gutenberg.util.Debug;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class Book_View {
    private Context context;
    private LayoutInflater inflater;
    private LinearLayout outer_view;
    private RelativeLayout book_holder;
    private Book_Page current_page;
    private Book_Page next_page;
    private Book_Page prev_page;

    private String book_title;
    private String book_author;
    private Book epub_book;
    private Spine epub_spine;


    protected int current_page_chapter = -1;
    protected int current_page_paragraph = -1;
    protected int current_page_word = -1;

    protected int relative_page = 0; // relative to the bookmarked page when book is opened. bookmarked page = 0.
    private int  first_loaded_page = -1;
    private int last_loaded_page = 1;

    private int page_with_currently_loaded = 0;
    private int chapter_with_currently_loaded = 0;

    protected int font_size;
    protected int line_spacing;
    protected int line_width;
    protected int margin_width;
    private int lines_per_page;
    protected int[] text_line_positions;

    protected Paint text_paint;
    private int background_color = Color.parseColor("#f7f5f5");
    private int standard_text_grey = Color.parseColor("#4e4b47");

    protected String[][] lines_of_text;

    private Chapter[] chapters_parsed;
    private String[] chapter_titles;
    private LinkedList<String[]> complete_lines_of_text;
    private LinkedList<Integer[]> complete_text_boundaries;
    private boolean loading_pages = true;
    private Load_All_Pages page_loader;
    private boolean stop_loading = false;

    private Action_Bar_Handler action_bar_handler;







    protected Book_View(Context context, LayoutInflater inflater, boolean download, String URI, AssetManager assets, LinearLayout.LayoutParams fill_screen_params, int book_id, Action_Bar_Handler action_bar_handler) {
        this.context = context;
        this.inflater = inflater;
        this.action_bar_handler = action_bar_handler;
        action_bar_handler.set_book_view_menu();
        page_loader = new Load_All_Pages();
        outer_view = (LinearLayout)inflater.inflate(R.layout.book_view, null);
        book_holder = (RelativeLayout)outer_view.findViewById(R.id.book_holder);
        book_holder.setLayoutParams(fill_screen_params);
        current_page = new Book_Page(context, this, 0);
        next_page = new Book_Page(context, this, 1);
        prev_page = new Book_Page(context, this, -1);

        book_holder.addView(next_page);
        book_holder.addView(current_page);

        // TODO temporary way to test changing page.
        book_holder.setOnClickListener(new View.OnClickListener() {
            int counter = 0;
            public void onClick(View v) {
                if (counter%3 == 0) {
                    prev_page();
                } else {
                    next_page();
                }
                counter++;
            }
        });

        relative_page = 0;
        current_page_chapter = Home.prefs.get_last_chapter(book_id);
        // TODO remove.
        current_page_chapter = 0;
        current_page_paragraph = Home.prefs.get_last_paragraph(book_id);
        current_page_word = Home.prefs.get_last_word(book_id);


        // TODO remove.
        initialize_formatting(Home.pure_activity_width, Home.pure_activity_height);
        try {
            setup_epub_book((new EpubReader()).readEpub(assets.open("pg1497.epub")));
        } catch (IOException e) {

        }
    }

    public void initialize_formatting(int width, int height) {
        margin_width = (int)(width*0.05);
        line_width = width - (margin_width*2);
        font_size = Home.prefs.get_text_size();
        line_spacing = (int)(font_size*0.5);
        text_paint = new Paint();
        text_paint.setTextSize(font_size);
        text_paint.setColor(standard_text_grey);
        int free_space = height;
        int starting_position = font_size + line_spacing*3;
        free_space = free_space - (font_size*2 + line_spacing*4); // header, footer space.
        lines_per_page = free_space/(font_size+line_spacing);
        text_line_positions = new int[lines_per_page];
        text_line_positions[0] = starting_position;
        for (int i=1; i < text_line_positions.length; i++) {
            text_line_positions[i] = text_line_positions[i-1]+font_size+line_spacing;
        }
        lines_of_text = new String[3][lines_per_page];
        complete_lines_of_text = new LinkedList<String[]>();
        complete_text_boundaries = new LinkedList<Integer[]>();
    }


    protected void setup_epub_book(Book epub_book) {
        this.epub_book = epub_book;
        //InputStream epubInputStream = assets.open("pg1497.epub");
        //Android_Book_View book = (new EpubReader()).readEpub(epubInputStream);
        book_title = epub_book.getTitle();
        book_author = epub_book.getMetadata().getAuthors().get(0).getLastname();
        action_bar_handler.set_title_author(book_title, book_author);

        epub_spine = new Spine(epub_book.getTableOfContents());
        List<TOCReference> table_of_contents = epub_book.getTableOfContents().getTocReferences();

        initialize_chapters(epub_spine, table_of_contents);
        initialize_open_pages();
    }
    private void initialize_chapters(Spine epub_spine, List<TOCReference> table_of_contents) {
        chapters_parsed = new Chapter[epub_spine.size()];
        for (int i=0; i < chapters_parsed.length; i++) {
            if (i == current_page_chapter) {
                chapters_parsed[i] = new Chapter(i, epub_spine.getResource(i));
            } else {
                chapters_parsed[i] = new Chapter(i);
            }
        }
        chapter_titles = new String[chapters_parsed.length];
        if (table_of_contents != null && table_of_contents.size()>0) {
            String[] temp_titles = new String[table_of_contents.size()];
            for (int i=0; i < table_of_contents.size(); i++) {
                String t = table_of_contents.get(i).getTitle();
                if (t != null) {
                    temp_titles[i] = t;
                } else {
                    temp_titles[i] = "";
                }
            }
            boolean[] titles_set = new boolean[chapter_titles.length];
            for (int j=0; j < chapter_titles.length; j++) {
                for (int i=0; i < table_of_contents.size(); i++) {
                    if (epub_spine.getResource(j).getId().equals(table_of_contents.get(i).getResourceId())) {
                        if (!titles_set[j]) {
                            chapter_titles[j] = temp_titles[i];

                            titles_set[j] = true;
                        }
                        break;
                    }
                }
                if (!titles_set[j]) {
                    chapter_titles[j] = "" + (j+1);
                }
            }
            action_bar_handler.initialize_spinner_chapters(chapter_titles, current_page_chapter);

        } else {
            for (int i=0; i < chapter_titles.length; i++) {
                chapter_titles[i] = "Chapter " + (i+1);
            }
        }


    }


    private void initialize_open_pages() {
        // setup current chapter.
        int line_count = 0;
        lines_of_text[1][0] = "";
        Integer[] text_boundaries = new Integer[6];  // chapter start, paragraph start, word start, chapter end, paragraph end, word end.
        int paragraph_index = current_page_paragraph;

        A: for (String paragraph : chapters_parsed[current_page_chapter].get_paragraphs().subList(current_page_paragraph, chapters_parsed[current_page_chapter].get_paragraphs().size())) {
            if (paragraph == null || paragraph.equals("")) {
                lines_of_text[1][line_count] = "";
            } else {
                String[] words = paragraph.split(" ");
                words[0] = "     " + words[0];
                for (int i=current_page_word; i < words.length; i++) {
                    if (text_paint.measureText(lines_of_text[1][line_count] + " " + words[i]) > line_width) {
                        i--;
                        line_count++;
                        if (line_count == lines_of_text[1].length) { // paragraph cutoff
                            text_boundaries[0] = current_page_chapter;
                            text_boundaries[1] = current_page_paragraph;
                            text_boundaries[2] = current_page_word;
                            text_boundaries[3] = current_page_chapter;
                            text_boundaries[4] = paragraph_index;
                            text_boundaries[5] = i;
                            break A;
                        } else {
                            lines_of_text[1][line_count] = "";
                        }
                    } else {
                        lines_of_text[1][line_count] += " " + words[i];
                    }
                }
            }
            line_count++;
            if (line_count == lines_of_text[1].length) { // paragraph finished exactly.
                text_boundaries[0] = current_page_chapter;
                text_boundaries[1] = current_page_paragraph;
                text_boundaries[2] = current_page_word;
                text_boundaries[3] = current_page_chapter;
                text_boundaries[4] = paragraph_index+1;
                text_boundaries[5] = -1;
                break;
            } else { // next paragraph
                lines_of_text[1][line_count] = "";
            }
            paragraph_index++;
        }
        if (line_count < lines_of_text[1].length) { // chapter finished.
            text_boundaries[0] = current_page_chapter;
            text_boundaries[1] = current_page_paragraph;
            text_boundaries[2] = current_page_word;
            text_boundaries[3] = current_page_chapter+1;
            text_boundaries[4] = -1;
            text_boundaries[5] = -1;
            for (int i=line_count; i < lines_of_text[1].length; i++) {
                lines_of_text[1][i] = "";
            }
        }
        complete_lines_of_text.add(lines_of_text[1]);
        complete_text_boundaries.add(text_boundaries);
        lines_of_text[2] = get_next_page_lines(complete_text_boundaries.getLast());

        lines_of_text[0] = get_prev_page_lines(complete_text_boundaries.getFirst());

        page_loader.execute("");
        //book_holder.removeView(current_page);
        //book_holder.addView(prev_page);
        for (int i=0; i < lines_of_text.length; i++) {
            if (lines_of_text[i] != null) {
                for (int j=0; j < lines_of_text[i].length; j++) {
                    Debug.log("page " + i + ": " + lines_of_text[i][j]);
                }
            }
        }
        Debug.log("current page: " + page_with_currently_loaded);



    }

    private String[] get_next_page_lines(Integer[] prev_boundaries) {
        Integer[] text_boundaries = new Integer[6];

        String[] lines_of_text = new String[lines_per_page];
        lines_of_text[0] = "";
        int line_count = 0;

        if (prev_boundaries[3] >= chapters_parsed.length) { // book is over.
            return null;
        } else if (prev_boundaries[4] == -1) { // start new chapter
            text_boundaries[0] = prev_boundaries[3];
            text_boundaries[1] = 0;
            text_boundaries[2] = 0;
            if (!chapters_parsed[text_boundaries[0]].is_parsed()) {
                chapters_parsed[text_boundaries[0]].parse_chapter(epub_spine.getResource(text_boundaries[0]));
            }
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
        A: for (String paragraph : chapters_parsed[text_boundaries[0]].get_paragraphs().subList(text_boundaries[1], chapters_parsed[text_boundaries[0]].get_paragraphs().size())) {
            if (paragraph == null || paragraph.equals("")) {
                lines_of_text[line_count] = "";
            } else {
                String[] words = paragraph.split(" ");
                words[0] = "     " + words[0];

                int word_index = 0;
                if (reading_first_paragraph) {
                    if (!(text_boundaries[2] >= words.length-1)) {
                        word_index = text_boundaries[2]+1;
                    }
                }
                for (int i=word_index; i < words.length; i++) {
                    if (text_paint.measureText(lines_of_text[line_count] + " " + words[i]) > line_width) {
                        i--;
                        line_count++;
                        if (line_count == lines_of_text.length) { // paragraph cutoff
                            text_boundaries[3] = text_boundaries[0];
                            text_boundaries[4] = paragraph_index;
                            text_boundaries[5] = i;
                            break A;
                        } else {
                            lines_of_text[line_count] = "";
                        }
                    } else {
                        lines_of_text[line_count] += " " + words[i];
                    }
                }
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
        complete_lines_of_text.addLast(lines_of_text);
        complete_text_boundaries.addLast(text_boundaries);
        return lines_of_text;

    }

    private String[] get_prev_page_lines(Integer[] next_boundaries) {
        Integer[] text_boundaries = new Integer[6];
        String[] lines_of_text = new String[this.lines_of_text[0].length];
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
            if (!chapters_parsed[ending_chapter_index].is_parsed()) {
                chapters_parsed[ending_chapter_index].parse_chapter(epub_spine.getResource(ending_chapter_index));
            }
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
            this_chapter = chapters_parsed[ending_chapter_index].get_paragraphs();
            paragraph_index = this_chapter.size()-1;
        } else {
            //chapters_parsed[ending_chapter_index].get_paragraphs().subList(0, text_boundaries[4]-1);
            this_chapter = chapters_parsed[ending_chapter_index].get_paragraphs().subList(0, text_boundaries[4]-1);
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
                    if (text_paint.measureText(words[i] + " " + lines_of_text[line_count]) > line_width) {
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
        complete_lines_of_text.addFirst(lines_of_text);
        complete_text_boundaries.addFirst(text_boundaries);
        page_with_currently_loaded++;

        return lines_of_text;
    }

    /*
     *  When backtracking, if the start of a paragraph is found, the paragraph needs to be recompiled in order to fill the first line, and leave whitespace after the last line.
     */
    /*
    private void recompile_paragraph(String paragraph, String[] lines_of_text, int start_line, int end_line) {
        String[] words = paragraph.split(" ");
        Debug.log("recompile paragraph: " + start_line + " - " + end_line);
        words[0] = "     " + words[0];
        int line_count = start_line;
        lines_of_text[line_count] = "";
        for (int i=0; i < words.length; i++) {
            if (text_paint.measureText(lines_of_text[line_count] + " " + words[i]) > line_width) {
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
    }

    protected String get_title() {
        return book_title;
    }
    protected String get_author() {
        return book_author;
    }
    protected LinearLayout get_page_holder() {
        return outer_view;
    }
    protected void size_changed() {
    }

    private void prev_page() {
        Debug.log("prev page");

        if (page_with_currently_loaded == 0) {

        } else {
            book_holder.addView(prev_page);
            book_holder.removeView(next_page);

            Book_Page temp = next_page;
            next_page = current_page;
            current_page = prev_page;
            prev_page = temp;

            if (page_with_currently_loaded != 1) {
                lines_of_text[0] = complete_lines_of_text.get(page_with_currently_loaded-2);
            }
            lines_of_text[1] = complete_lines_of_text.get(page_with_currently_loaded-1);
            lines_of_text[2] = complete_lines_of_text.get(page_with_currently_loaded);
            page_with_currently_loaded--;
            current_page.set_page_stack_id(0);
            next_page.set_page_stack_id(1);
            prev_page.set_page_stack_id(-1);


        }
    }
    private void next_page() {
        Debug.log("next page, " + "page_with_currently_loaded: " + page_with_currently_loaded + " / " + complete_lines_of_text.size());

        if (page_with_currently_loaded >= complete_lines_of_text.size()-1) {
            if (loading_pages) { // wait until loaded.
                Debug.log("pause until page loaded");
            } else { // close book.

            }
        } else {
            book_holder.removeView(current_page);
            book_holder.addView(current_page, 0);
            Book_Page temp = current_page;
            current_page = next_page;
            next_page = temp;

            lines_of_text[0] = complete_lines_of_text.get(page_with_currently_loaded);
            lines_of_text[1] = complete_lines_of_text.get(page_with_currently_loaded+1);
            if (page_with_currently_loaded < complete_lines_of_text.size()-2) {
                lines_of_text[2] = complete_lines_of_text.get(page_with_currently_loaded+2);
            }
            page_with_currently_loaded++;

            current_page.set_page_stack_id(0);
            next_page.set_page_stack_id(1);

        }
    }


    private class Load_All_Pages extends AsyncTask<String, String, String> {
        protected String doInBackground(String... params) {
            boolean finished_previous_pages = false;
            boolean finished_next_pages = false;
            Integer[] first_loaded_boundaries;
            Integer[] last_loaded_boundaries;

            long start_time = System.currentTimeMillis();
            int iteration = 0;
            while(true) {
                if (stop_loading) {
                    break;
                }
                iteration++;
                first_loaded_boundaries = complete_text_boundaries.getFirst();
                last_loaded_boundaries = complete_text_boundaries.getLast();
                if (first_loaded_boundaries[0] == 0 && first_loaded_boundaries[1] == 0 && first_loaded_boundaries[2] == 0) {
                    finished_previous_pages  = true;
                }
                if (last_loaded_boundaries[3] == chapters_parsed.length) {
                    finished_next_pages = true;
                }

                Debug.log("load page iteration " + iteration + ": " + finished_previous_pages + ", " + finished_next_pages + ", " +  first_loaded_boundaries[0] +  ", " + first_loaded_boundaries[1] + ", " + first_loaded_boundaries[2]);
                if (finished_previous_pages && finished_next_pages) {
                    break;
                }
                if (!finished_previous_pages) {
                    get_prev_page_lines(first_loaded_boundaries);
                }
                if (!finished_next_pages) {
                    get_next_page_lines(last_loaded_boundaries);
                }
            }

            Debug.log(complete_lines_of_text.size() + " pages loaded in " + (System.currentTimeMillis() -start_time) + " ms.");

            return null;
        }

        protected void onPreExecute() {
            loading_pages = true;
            super.onPreExecute();
        }

        protected void onPostExecute(String unused) {
            loading_pages = false;
        }
    }

    protected void kill_book() {
        if (page_loader != null) {
            stop_loading = true;
            page_loader.cancel(true);
        }
        complete_lines_of_text.clear();
        complete_text_boundaries.clear();
    }








}
*/