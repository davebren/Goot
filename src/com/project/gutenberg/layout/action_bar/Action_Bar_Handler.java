package com.project.gutenberg.layout.action_bar;

import android.app.ActionBar;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.GutenApplication;
import com.project.gutenberg.catalog.browsing.Home_Navigation_Adapter;
import com.project.gutenberg.R;
import com.project.gutenberg.book.view.android.Android_Book_View;
import com.project.gutenberg.util.Typeface_Span;

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
    int total_pages=0;
    int current_page=0;
    Context context;
    public static boolean ignore_spinner_selection = true;
    private boolean title_browsing_showing = false;
    private boolean downloads_browsing_showing = false;
    private boolean author_browsing_showing = false;

    private Home_Navigation_Adapter home_navigation_adapter;

    public Action_Bar_Handler(Menu menu, ActionBar action_bar, Context context) {
        this.context = context;
        this.action_bar = action_bar;
        page_indicator =  menu.findItem(R.id.menu_page_indicator);
        page_indicator.setOnMenuItemClickListener(page_indicator_click_listener);
        chapter_indicator = menu.findItem(R.id.menu_chapter_indicator);
        chapter_indicator_spinner = (Spinner)chapter_indicator.getActionView();
        chapter_indicator_spinner.setMinimumHeight((int)(action_bar.getHeight()*spinner_portion));
        search_item = menu.findItem(R.id.menu_search);
        search_view = (SearchView)search_item.getActionView();
        search_view.setOnQueryTextListener(query_listener);
    }
    public void set_home_navigation_adapter(Home_Navigation_Adapter home_navigation_adapter) {
        this.home_navigation_adapter = home_navigation_adapter;
    }
    public void set_book_view_menu(Android_Book_View book_view) {
        search_item.setVisible(false);
        page_indicator.setVisible(true);
        chapter_indicator.setVisible(true);
        this.book_view = book_view;
    }
    public void set_home_view_menu() {
        set_title(context.getString(R.string.app_name));
        search_item.setVisible(false);
        page_indicator.setVisible(false);
        chapter_indicator.setVisible(false);
        chapter_titles = null;
    }
    public void set_browse_menu() {
        action_bar.setTitle("");
        search_item.setVisible(true);
    }
    public void set_title_browsing_menu() {
        title_browsing_showing = true;
        set_title("Browse By Title");
        search_item.setVisible(true);
        page_indicator.setVisible(false);
        chapter_indicator.setVisible(false);
        chapter_titles = null;
    }
    public void set_author_browsing_menu() {
        author_browsing_showing = true;
        set_title("Browse By Author");
        search_item.setVisible(true);
        page_indicator.setVisible(false);
        chapter_indicator.setVisible(false);
        chapter_titles = null;
    }
    public void set_downloads_browsing_menu() {
        downloads_browsing_showing = true;
        set_title("Downloads");
        search_item.setVisible(true);
        page_indicator.setVisible(false);
        chapter_indicator.setVisible(false);
        chapter_titles = null;
    }
    private void set_title(String title) {
        SpannableString s = new SpannableString(title);
        s.setSpan(new Typeface_Span(((GutenApplication)context.getApplicationContext()).typeface), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        action_bar.setTitle(s);
    }
    public void set_book_title(String title) {
        set_title(title);
    }
    public void set_page(int page_number) {
        current_page = page_number;
        SpannableString s = new SpannableString("page\n" + page_number);
        s.setSpan(new Typeface_Span(((GutenApplication)context.getApplicationContext()).typeface), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        page_indicator.setTitle(s);
    }
    public void set_total_pages(int total_pages) {
        this.total_pages = total_pages;
    }
    public void set_chapter_title(int chapter_index) {
        if (chapter_titles != null && chapter_titles.length > chapter_index && chapter_index > -1) {
            ignore_spinner_selection = true;
            chapter_indicator_spinner.setSelection(chapter_index);
        }
    }
    public void initialize_spinner_chapters(String[] chapter_titles, int current_chapter) {
        for (int i=0; i < chapter_titles.length; i++) {
            if (chapter_titles[i].length() > 14) {
                chapter_titles[i] = chapter_titles[i].substring(0,14) + "...";
            }
        }
        ArrayAdapter<String> adapter = new Chapter_Spinner_Adapter(action_bar.getThemedContext(),
                android.R.layout.simple_spinner_item, android.R.id.text1,
                chapter_titles);
        chapter_indicator_spinner.setAdapter(adapter);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chapter_indicator_spinner.setSelection(current_chapter);
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
            if (title_browsing_showing && home_navigation_adapter != null) {
                home_navigation_adapter.filter_titles(query);
            }
            if (downloads_browsing_showing && home_navigation_adapter != null) {
                home_navigation_adapter.filter_downloads(query);
            }
            if (author_browsing_showing && home_navigation_adapter != null) {
                home_navigation_adapter.filter_authors(query);
            }
            return true;
        }
        public boolean onQueryTextSubmit(String query) {
            return true;
        }
    };
    MenuItem.OnMenuItemClickListener page_indicator_click_listener = new MenuItem.OnMenuItemClickListener() {
        public boolean onMenuItemClick(MenuItem item) {
            Toast.makeText(context, "page " + current_page + " out of " + total_pages, 2500).show();
            return true;
        }
    };
}
