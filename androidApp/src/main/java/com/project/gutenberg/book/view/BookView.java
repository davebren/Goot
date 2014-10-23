package com.project.gutenberg.book.view;

import com.project.gutenberg.book.Book;
import com.project.gutenberg.book.page_flipping.PageFlipper;
import com.project.gutenberg.book.pagination.LineMeasurer;

public abstract class BookView {
    protected BookFormatting formatting;
    protected LineMeasurer lineMeasurer;
    protected PageFlipper pageFlipper;
    protected String[][] prevCurrentNextPageLines;
    protected PageView prevPage;
    protected PageView currentPage;
    protected PageView nextPage;
    protected final int prevPageStackId = -1;
    protected final int currentPageStackId = 0;
    protected final int nextPageStackId = 1;

    protected Book book;
    protected int flipStyle;

    protected BookView(int width, int height, int fontSize) {
        formatting = new BookFormatting(width, height, fontSize);
        prevCurrentNextPageLines = new String[3][formatting.getLinesPerPage()];
    }
    public String[] getPageLines(int pageIndex) {
        return prevCurrentNextPageLines[pageIndex];
    }
    public BookFormatting getFormatting() {
        return formatting;
    }
    public LineMeasurer getLineMeasurer() {
        return lineMeasurer;
    }
    public void setPrevCurrentNextPageLines(String[][] prevCurrentNextPageLines) {
        this.prevCurrentNextPageLines = prevCurrentNextPageLines;
    }
    public void setPrevCurrentNextPageLines(String[] lines, int index) {
        this.prevCurrentNextPageLines[index] = lines;
    }
    public abstract void initializePageFlipper();
    public abstract void loading_hook_completed_receiver(String[] linesOfText, int stackId);
    public Book getBook() {
        return book;
    }
    public PageFlipper getPageFlipper() {
        return pageFlipper;
    }
}
