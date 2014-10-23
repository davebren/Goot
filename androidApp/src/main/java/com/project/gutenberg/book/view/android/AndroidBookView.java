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
    protected final int BACKGROUND_COLOR = Color.parseColor("#f7f5f5");
    private final int STANDARD_TEXT_GRAY = Color.parseColor("#4e4b47");
    private SharedPrefs prefs;
    private ViewGroup pageHolder;
    private Context context;
    protected TextPaint textPainter;
    private ActionBarHandler actionBarHandler;


    public AndroidBookView(Book book, Context context, SharedPrefs prefs, LinearLayout.LayoutParams fillScreenParams, int viewWidth, int viewHeight, int flipStyle, ActionBarHandler actionBarHandler) {
        super(viewWidth, viewHeight, (int)(context.getResources().getDimensionPixelSize(R.dimen.book_default_font_size)*prefs.getBookFontScale()));
        this.prefs = prefs;
        this.context = context;
        this.actionBarHandler = actionBarHandler;
        textPainter = new TextPaint();
        textPainter.setAntiAlias(true);

        initializePageHolder(fillScreenParams);
        initializePageViews();
        lineMeasurer = new AndroidLineMeasurer(textPainter);
        this.flipStyle = flipStyle;
        this.book = book;
        initializePageFlipper();
    }
    private void initializePageHolder(LinearLayout.LayoutParams fillScreenParams) {
        pageHolder = new RelativeLayout(context);
        pageHolder.setLayoutParams(fillScreenParams);
    }
    private void initializePageViews() {
        textPainter.setTextSize(formatting.getFontSize());
        textPainter.setColor(STANDARD_TEXT_GRAY);
        textPainter.setTypeface(Typeface.createFromAsset(context.getAssets(), TypefaceMappings.getFileName(prefs.getTypeface())));
        prevPage = new AndroidPageView(context, this, prevPageStackId);
        currentPage = new AndroidPageView(context, this, currentPageStackId);
        nextPage = new AndroidPageView(context, this, nextPageStackId);
        currentPage.addView(0);
        nextPage.addView(0);
    }
    public ViewGroup getPageHolder() {
        return pageHolder;
    }
    public void initializePageFlipper() {
        pageFlipper = new SimplePageFlipper(this, context, prevPage, currentPage, nextPage, book);
    }
    public void loading_hook_completed_receiver(String[] linesOfText, int stackId) {

    }
    public void loadingHookCompletedReceiver(Book book) {
        String[][] linesOfText = new String[3][];
        Chapter currentChapter = book.getCurrentChapter();
        linesOfText[1] = currentChapter.peekCurrentPage().getPageText();
        if (!book.getCurrentChapter().onFirstPage()) {
            linesOfText[0] = currentChapter.peekPreviousPage().getPageText();
        }
        else if (!book.onFirstChapter()) {
            linesOfText[0] = book.peekPreviousChapter().peekLastPage().getPageText();
        }
        else {
            linesOfText[0] = new String[0];
        }

        if (!book.getCurrentChapter().onLastPage()) linesOfText[2] = currentChapter.peekNextPage().getPageText();
        else if (!book.onLastChapter()) linesOfText[2] = book.peekNextChapter().peekCurrentPage().getPageText();
        else linesOfText[2] = new String[0];
        setPrevCurrentNextPageLines(linesOfText);
    }
    public ActionBarHandler getActionBarHandler() {
        return actionBarHandler;
    }
}
