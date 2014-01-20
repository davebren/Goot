package com.project.gutenberg;

import android.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import com.project.gutenberg.book.view.android.Android_Book_View;

public class Action_Bar_Handler {
    private ActionBar action_bar;
    private SearchView search_view;
    private MenuItem search_item;
    private MenuItem page_indicator;
    private MenuItem chapter_indicator;
    private Spinner chapter_indicator_spinner;
    private final double spinner_portion = 0.75;
    private String[] chapter_titles;
    private Android_Book_View book_view;
    private boolean ignore_spinner_selection = true;

    public Action_Bar_Handler(Menu menu, ActionBar action_bar) {
        this.action_bar = action_bar;
        page_indicator =  menu.findItem(R.id.menu_page_indicator);
        chapter_indicator = menu.findItem(R.id.menu_chapter_indicator);
        chapter_indicator_spinner = (Spinner)chapter_indicator.getActionView();
        chapter_indicator_spinner.setMinimumHeight((int)(action_bar.getHeight()*spinner_portion));
        search_item = menu.findItem(R.id.menu_search);
        search_view = (SearchView)search_item.getActionView();
        search_view.setOnQueryTextListener(query_listener);
    }
    public void set_book_view_menu(Android_Book_View book_view) {
        search_item.setVisible(false);
        page_indicator.setVisible(true);
        chapter_indicator.setVisible(true);
        this.book_view = book_view;
    }
    public void set_home_view_menu() {
        action_bar.setTitle("GutenDroid");
        search_item.setVisible(true);
        page_indicator.setVisible(false);
        chapter_indicator.setVisible(false);
        chapter_titles = null;
    }
    public void set_title_browsing_menu() {
        action_bar.setTitle("Browse By Title");
        search_item.setVisible(true);
        page_indicator.setVisible(false);
        chapter_indicator.setVisible(false);
        chapter_titles = null;
    }

    public void set_title_author(String title, String author) {
        action_bar.setTitle(title + " by " + author);
    }
    public void set_page(int page_number) {
        page_indicator.setTitle("page " + page_number);
    }
    public void set_chapter_title(int chapter_index) {
        Log.d("gutendroid", "set_chapter_title, " + chapter_index + ", " + chapter_titles.length + ", " + (chapter_titles != null));
        if (chapter_titles != null && chapter_titles.length > chapter_index && chapter_index > -1) {
            ignore_spinner_selection = true;
            chapter_indicator_spinner.setSelection(chapter_index);
        }
    }
    public void initialize_spinner_chapters(String[] chapter_titles, int current_chapter) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(action_bar.getThemedContext(),
                android.R.layout.simple_spinner_item, android.R.id.text1,
                chapter_titles);
        chapter_indicator_spinner.setAdapter(adapter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chapter_indicator_spinner.setPrompt(chapter_titles[current_chapter]);
        this.chapter_titles = chapter_titles;
        chapter_indicator_spinner.setOnItemSelectedListener(spinner_listener);
    }
    private AdapterView.OnItemSelectedListener spinner_listener = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (ignore_spinner_selection) {
                ignore_spinner_selection = false;
                return;
            }
            if (book_view == null) return;
            book_view.get_page_flipper().jump_to_chapter(i);
        }
        public void onNothingSelected(AdapterView<?> adapterView) {}
    };
    SearchView.OnQueryTextListener query_listener = new SearchView.OnQueryTextListener() {
        public boolean onQueryTextChange(String query) {
            return true;
        }
        public boolean onQueryTextSubmit(String query) {
            return true;
        }
    };
}
