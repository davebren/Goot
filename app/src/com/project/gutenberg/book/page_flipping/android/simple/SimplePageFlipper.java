package com.project.gutenberg.book.page_flipping.android.simple;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.project.gutenberg.R;
import com.project.gutenberg.SharedPrefs;
import com.project.gutenberg.book.Book;
import com.project.gutenberg.book.Chapter;
import com.project.gutenberg.book.page_flipping.PageFlipper;
import com.project.gutenberg.book.view.PageView;
import com.project.gutenberg.book.view.android.AndroidBookView;

public class SimplePageFlipper extends PageFlipper {
    final Button rightButton;
    final Button leftButton;
    final ButtonAnimator rightButtonAnimator = new ButtonAnimator();
    final ButtonAnimator leftButtonAnimator = new ButtonAnimator();
    final ButtonAnimationListener leftAnimationListener = new ButtonAnimationListener();
    final ButtonAnimationListener rightAnimationListener = new ButtonAnimationListener();
    Context context;
    SharedPrefs prefs;

    public SimplePageFlipper(AndroidBookView book_view, Context context, PageView prev_page, PageView current_page, PageView next_page, Book book) {
        super(book_view, prev_page, current_page, next_page, book);
        prefs = new SharedPrefs(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        final View buttons = inflater.inflate(R.layout.simple_page_flipper_buttons,null);
        rightButton = (Button)buttons.findViewById(R.id.simple_page_flipper_right);
        leftButton = (Button)buttons.findViewById(R.id.simple_page_flipper_left);
        LinearLayout.LayoutParams fill_screen_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        book_view.getPageHolder().addView(buttons, fill_screen_params);
        rightAnimationListener.setView(rightButton);
        leftAnimationListener.setView(leftButton);
        rightButtonAnimator.getFadeOut().setAnimationListener(rightAnimationListener);
        leftButtonAnimator.getFadeOut().setAnimationListener(leftAnimationListener);
        rightButton.startAnimation(rightButtonAnimator.getFadeOut());
        leftButton.startAnimation(leftButtonAnimator.getFadeOut());
        rightButton.setOnTouchListener(rightButtonListener);
        leftButton.setOnTouchListener(leftButtonListener);
    }
    public void nextPage() {
        Chapter currentChapter = book.getCurrentChapter();
        if (book.onLastChapter() && book.getCurrentChapter().onLastPage()) return;
        currentPage.removeView();
        PageView temp = prevPage;
        prevPage = currentPage;
        currentPage = nextPage;
        nextPage = temp;
        nextPage.addView(0);
        String[][] linesOfText = new String[3][];
        linesOfText[0] = currentChapter.peekCurrentPage().getPageText();
        if (currentChapter.onPenultimatePage()) {
            linesOfText[1] = currentChapter.nextPage().getPageText();
            if (!book.onLastChapter()) {
                linesOfText[2] = book.peekNextChapter().peekCurrentPage().getPageText();
            } else linesOfText[2] = new String[0];
        } else if (currentChapter.onLastPage()) {
            book.nextChapter();
            ((AndroidBookView) bookView).getActionBarHandler().setChapterTitle(book.getCurrentChapterIndex());
            linesOfText[1] = book.getCurrentChapter().peekCurrentPage().getPageText();
            Toast.makeText(context, book.getCurrentChapter().getTitle(), 2500).show();
            if (!book.getCurrentChapter().onLastPage()) {
                linesOfText[2] = book.getCurrentChapter().peekNextPage().getPageText();
            } else linesOfText[2] = new String[0];
        } else {
            linesOfText[1] = currentChapter.nextPage().getPageText();
            linesOfText[2] = currentChapter.peekNextPage().getPageText();
        }
        prevPage.setPageStackId(-1);
        currentPage.setPageStackId(0);
        nextPage.setPageStackId(1);
        bookView.setPrevCurrentNextPageLines(linesOfText);
        ((AndroidBookView) bookView).getActionBarHandler().setPage(book.getPageNumber());
        prefs.setLastPage(prefs.getOpenBook(),book.getCurrentPageBoundaries());
    }
    public void prevPage() {
        Chapter currentChapter = book.getCurrentChapter();
        if (currentChapter.onFirstPage() && book.onFirstChapter()) return;
        nextPage.removeView();
        prevPage.addView(1);
        PageView temp = currentPage;
        currentPage = prevPage;
        prevPage = nextPage;
        nextPage = temp;
        String[][] linesOfText = new String[3][];
        linesOfText[2] = currentChapter.peekCurrentPage().getPageText();
        if (currentChapter.onSecondPage()) {
            linesOfText[1] = currentChapter.previousPage().getPageText();
            if (!book.onFirstChapter()) {
                linesOfText[0] = book.peekPreviousChapter().peekLastPage().getPageText();
            } else linesOfText[0] = new String[0];
        } else if (currentChapter.onFirstPage()) {
            book.previousChapter();
            book.getCurrentChapter().setLastPage();
            linesOfText[1] = book.getCurrentChapter().peekCurrentPage().getPageText();
            ((AndroidBookView) bookView).getActionBarHandler().setChapterTitle(book.getCurrentChapterIndex());
            if (!book.getCurrentChapter().onFirstPage()) {
                linesOfText[0] = book.getCurrentChapter().peekPreviousPage().getPageText();
            } else linesOfText[0] = new String[0];
        } else {
            linesOfText[1] = currentChapter.previousPage().getPageText();
            linesOfText[0] = currentChapter.peekPreviousPage().getPageText();
        }
        prevPage.setPageStackId(-1);
        currentPage.setPageStackId(0);
        nextPage.setPageStackId(1);
        bookView.setPrevCurrentNextPageLines(linesOfText);
        ((AndroidBookView) bookView).getActionBarHandler().setPage(book.getPageNumber());
        prefs.setLastPage(prefs.getOpenBook(),book.getCurrentPageBoundaries());
    }
    public void jumpToChapter(int chapterIndex) {
        String[][] linesOfText = new String[3][];
        book.getCurrentChapter().setFirstPage();
        book.setCurrentChapter(chapterIndex);
        Chapter currentChapter = book.getCurrentChapter();
        currentChapter.setFirstPage();
        linesOfText[1] = currentChapter.peekCurrentPage().getPageText();
        if (!currentChapter.onLastPage()) {
            linesOfText[2] = currentChapter.peekNextPage().getPageText();
        } else if (!book.onLastChapter()) {
            linesOfText[2] = book.peekNextChapter().peekCurrentPage().getPageText();
        } else {
            linesOfText[2] = new String[0];
        }
        if (!book.onFirstChapter()) {
            linesOfText[0] = book.peekPreviousChapter().peekLastPage().getPageText();
        } else {
            linesOfText[0] = new String[0];
        }
        bookView.setPrevCurrentNextPageLines(linesOfText);
        currentPage.removeView();
        nextPage.removeView();
        currentPage.addView(0);
        nextPage.addView(0);
        prevPage.setPageStackId(-1);
        currentPage.setPageStackId(0);
        nextPage.setPageStackId(1);
        ((AndroidBookView) bookView).getActionBarHandler().setPage(book.getPageNumber());
    }
    private View.OnTouchListener rightButtonListener = new View.OnTouchListener(){
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                if (!leftAnimationListener.isAnimating())
                    rightButton.setAlpha(0.35f);
            } else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if (!rightAnimationListener.isAnimating()) {
                    rightButton.setAlpha(0f);
                    nextPage();
                }
            }
            return false;
        }
    };
    private View.OnTouchListener leftButtonListener = new View.OnTouchListener(){
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                if (!leftAnimationListener.isAnimating())
                    leftButton.setAlpha(0.35f);
            } else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if (!leftAnimationListener.isAnimating()) {
                    leftButton.setAlpha(0f);
                    prevPage();
                }
            }
            return false;
        }
    };
}
