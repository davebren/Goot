package com.project.gutenberg.layout.action_bar;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.GutenApplication;
import com.project.gutenberg.Home;
import com.project.gutenberg.book.view.android.AndroidBookView;
import com.project.gutenberg.catalog.browsing.HomeNavigationAdapter;
import com.project.gutenberg.R;
import com.project.gutenberg.util.TypefaceSpan;

public class ActionBarHandler {
    private ActionBar actionBar;
    private SearchView searchView;
    private MenuItem searchItem;
    private MenuItem pageIndicator;
    private MenuItem chapterIndicator;
    private Spinner chapterIndicatorSpinner;
    private final double spinner_portion = 0.75;
    private String[] chapterTitles;
    private AndroidBookView bookView;
    int totalPages =0;
    int currentPage =0;
    Home activity;
    public static boolean ignoreSpinnerSelection = true;
    private boolean titleBrowsingShowing = false;
    private boolean downloadsBrowsingShowing = false;
    private boolean authorBrowsingShowing = false;

    private HomeNavigationAdapter homeNavigationAdapter;

    Menu menu;

    public ActionBarHandler(Menu menu, Home activity) {
        this.activity = activity;
        this.menu = menu;
        actionBar = activity.getActionBar();
        setViews();
    }
    private void setViews() {
        pageIndicator =  menu.findItem(R.id.menu_page_indicator);
        pageIndicator.setOnMenuItemClickListener(pageIndicatorClickListener);
        chapterIndicator = menu.findItem(R.id.menu_chapter_indicator);
        chapterIndicatorSpinner = (Spinner) chapterIndicator.getActionView();
        chapterIndicatorSpinner.setMinimumHeight((int) (actionBar.getHeight() * spinner_portion));
        searchItem = menu.findItem(R.id.menu_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(queryListener);
    }
    public void setHomeNavigationAdapter(HomeNavigationAdapter home_navigation_adapter) {
        this.homeNavigationAdapter = home_navigation_adapter;
    }
    public void setBookViewMenu(AndroidBookView book_view) {
        searchItem.setVisible(false);
        pageIndicator.setVisible(true);
        chapterIndicator.setVisible(true);
        this.bookView = book_view;
    }
    public void setHomeViewMenu() {
        setTitle(activity.getString(R.string.app_name));
        searchItem.setVisible(false);
        pageIndicator.setVisible(false);
        chapterIndicator.setVisible(false);
        chapterTitles = null;
    }
    public void setBrowseMenu() {
        actionBar.setTitle("");
        searchItem.setVisible(true);
    }
    public void setTitleBrowsingMenu() {
        titleBrowsingShowing = true;
        setTitle("Browse By Title");
        searchItem.setVisible(true);
        pageIndicator.setVisible(false);
        chapterIndicator.setVisible(false);
        chapterTitles = null;
    }
    public void setAuthorBrowsingMenu() {
        authorBrowsingShowing = true;
        setTitle("Browse By Author");
        searchItem.setVisible(true);
        pageIndicator.setVisible(false);
        chapterIndicator.setVisible(false);
        chapterTitles = null;
    }
    public void setDownloadsBrowsingMenu() {
        downloadsBrowsingShowing = true;
        setTitle("Downloads");
        searchItem.setVisible(true);
        pageIndicator.setVisible(false);
        chapterIndicator.setVisible(false);
        chapterTitles = null;
    }
    private void setTitle(String title) {
        SpannableString s = new SpannableString(title);
        s.setSpan(new TypefaceSpan(((GutenApplication) activity.getApplicationContext()).typeface), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        actionBar.setTitle(s);
    }
    public void setBookTitle(String title) {
        setTitle(title);
    }
    public void setPage(int pageNumber) {
        currentPage = pageNumber;
        SpannableString s = new SpannableString("page\n" + pageNumber);
        s.setSpan(new TypefaceSpan(((GutenApplication) activity.getApplicationContext()).typeface), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        pageIndicator.setTitle(s);
    }
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
    public void setChapterTitle(int chapter_index) {
        if (chapterTitles != null && chapterTitles.length > chapter_index && chapter_index > -1) {
            ignoreSpinnerSelection = true;
            chapterIndicatorSpinner.setSelection(chapter_index);
        }
    }
    public void initializeSpinnerChapters(String[] chapterTitles, int currentChapter) {
        for (int i=0; i < chapterTitles.length; i++) {
            if (chapterTitles[i].length() > 14) {
                chapterTitles[i] = chapterTitles[i].substring(0,14) + "...";
            }
        }
        ArrayAdapter<String> adapter = new ChapterSpinnerAdapter(actionBar.getThemedContext(),
                android.R.layout.simple_spinner_item, android.R.id.text1,
                chapterTitles);
        chapterIndicatorSpinner.setAdapter(adapter);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chapterIndicatorSpinner.setSelection(currentChapter);
        this.chapterTitles = chapterTitles;
        chapterIndicatorSpinner.setOnItemSelectedListener(spinnerListener);
    }

    private AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (ignoreSpinnerSelection) {
                ignoreSpinnerSelection = false;
                return;
            }
            if (bookView == null) return;
            bookView.getPageFlipper().jumpToChapter(i);
        }
        public void onNothingSelected(AdapterView<?> adapterView) {}
    };
    SearchView.OnQueryTextListener queryListener = new SearchView.OnQueryTextListener() {
        public boolean onQueryTextChange(String query) {
            if (titleBrowsingShowing && homeNavigationAdapter != null) {
                homeNavigationAdapter.filterTitles(query);
            }
            if (downloadsBrowsingShowing && homeNavigationAdapter != null) {
                homeNavigationAdapter.filterDownloads(query);
            }
            if (authorBrowsingShowing && homeNavigationAdapter != null) {
                homeNavigationAdapter.filterAuthors(query);
            }
            return true;
        }
        public boolean onQueryTextSubmit(String query) {
            return true;
        }
    };
    MenuItem.OnMenuItemClickListener pageIndicatorClickListener = new MenuItem.OnMenuItemClickListener() {
        public boolean onMenuItemClick(MenuItem item) {
            Toast.makeText(activity, "page " + currentPage + " out of " + totalPages, 2500).show();
            return true;
        }
    };
}
