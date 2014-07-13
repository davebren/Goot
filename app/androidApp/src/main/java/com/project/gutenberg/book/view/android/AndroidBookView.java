package com.project.gutenberg.book.view.android;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.project.gutenberg.SharedPrefs;
import com.project.gutenberg.book.Chapter;
import com.project.gutenberg.book.page_flipping.android.simple.SimplePageFlipper;
import com.project.gutenberg.book.pagination.android.AndroidLineMeasurer;
import com.project.gutenberg.book.view.BookView;
import com.project.gutenberg.layout.action_bar.ActionBarHandler;
import com.project.gutenberg.R;
import com.project.gutenberg.book.Book;
import com.project.gutenberg.util.TypefaceMappings;

public class AndroidBookView extends BookView {
    protected int background_color = Color.parseColor("#f7f5f5");
    private int standard_text_grey = Color.parseColor("#4e4b47");
    private SharedPrefs prefs;
    private ViewGroup page_holder;
    private Context context;
    protected TextPaint text_painter;
    private ActionBarHandler action_bar_handler;

    public AndroidBookView(Book book, Context context, SharedPrefs prefs, LinearLayout.LayoutParams fill_screen_params, int view_width, int view_height, int flip_style, ActionBarHandler action_bar_handler) {
        super(view_width, view_height, (int)(context.getResources().getDimensionPixelSize(R.dimen.book_default_font_size)*prefs.getBookFontScale()));
        this.prefs = prefs;
        this.context = context;
        this.action_bar_handler = action_bar_handler;
        text_painter = new TextPaint();
        text_painter.setAntiAlias(true);

        initialize_page_holder(fill_screen_params);
        initialize_page_views();
        lineMeasurer = new AndroidLineMeasurer(text_painter);
        this.flipStyle = flip_style;
        this.book = book;
        initializePageFlipper();
    }
    private void initialize_page_holder(LinearLayout.LayoutParams fill_screen_params) {
        page_holder = new RelativeLayout(context);
        page_holder.setLayoutParams(fill_screen_params);
    }
    private void initialize_page_views() {
        text_painter.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.book_default_font_size)*prefs.getBookFontScale());
        text_painter.setColor(standard_text_grey);
        String typeface = prefs.getTypeface();
        String typeface_path = TypefaceMappings.getFileName(typeface);
        text_painter.setTypeface(Typeface.createFromAsset(context.getAssets(), TypefaceMappings.getFileName(prefs.getTypeface())));
        prevPage = new AndroidPageView(context, this, prevPageStackId);
        currentPage = new AndroidPageView(context, this, currentPageStackId);
        nextPage = new AndroidPageView(context, this, nextPageStackId);
        currentPage.addView(0);
        nextPage.addView(0);
    }
    public ViewGroup getPageHolder() {
        return page_holder;
    }
    public void initializePageFlipper() {
        pageFlipper = new SimplePageFlipper(this, context, prevPage, currentPage, nextPage, book);
    }
    public void loading_hook_completed_receiver(String[] linesOfText, int stackId) {

    }
    public void loadingHookCompletedReceiver(Book book) {
        String[][] lines_of_text = new String[3][];
        Chapter current_chapter = book.getCurrentChapter();
        lines_of_text[1] = current_chapter.peekCurrentPage().getPageText();
        if (!book.getCurrentChapter().onFirstPage()) {
            lines_of_text[0] = current_chapter.peekPreviousPage().getPageText();
        }
        else if (!book.onFirstChapter()) {
            lines_of_text[0] = book.peekPreviousChapter().peekLastPage().getPageText();
        }
        else {
            lines_of_text[0] = new String[0];
        }

        if (!book.getCurrentChapter().onLastPage()) lines_of_text[2] = current_chapter.peekNextPage().getPageText();
        else if (!book.onLastChapter()) lines_of_text[2] = book.peekNextChapter().peekCurrentPage().getPageText();
        else lines_of_text[2] = new String[0];
        setPrevCurrentNextPageLines(lines_of_text);
    }
    public ActionBarHandler getActionBarHandler() {
        return action_bar_handler;
    }
}
