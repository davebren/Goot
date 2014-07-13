package com.project.gutenberg.book.pagination;

import com.project.gutenberg.book.Book;
import com.project.gutenberg.book.Chapter;
import com.project.gutenberg.book.Page;
import com.project.gutenberg.book.view.BookFormatting;
import com.project.gutenberg.util.Cores;
import com.project.gutenberg.util.ResponseCallback;

public class PageSplitter {
    private Book book;
    private int currentChapter;
    protected String[][] prevCurrentNextPageLines;
    private BookFormatting formatting;
    private LineMeasurer lineMeasurer;
    static int coresFinished = 0;

    public PageSplitter(Book book, BookFormatting formatting, LineMeasurer lineMeasurer, int currentChapter) {
        this.formatting = formatting;
        this.lineMeasurer = lineMeasurer;
        this.book = book;
        this.book.setCurrentChapter(currentChapter);
        prevCurrentNextPageLines = new String[3][formatting.getLinesPerPage()];
    }
    public void paginate(ResponseCallback<Void> pagesLoadedCallback) {
        int cores = Cores.getNumCores();
        for (int i=0; i < cores; i++) {
            new LoadAllPages(pagesLoadedCallback, i, cores);
        }
    }
    private String[] getNextPageLines(Integer[] prevBoundaries) {
        Integer[] textBoundaries = new Integer[6];

        String[] linesOfText = new String[formatting.getLinesPerPage()];
        linesOfText[0] = "";
        int lineCount = 0;

        if (prevBoundaries[3] >= book.numberOfChapters()) { // book is over.
            return null;
        } else if (prevBoundaries[4] == -1) { // start new chapter
            textBoundaries[0] = prevBoundaries[3]; // chapter start index
            textBoundaries[1] = 0; // paragraph start index
            textBoundaries[2] = -1; // word start index
        } else if (prevBoundaries[5] == -1) { // start new paragraph
            textBoundaries[0] = prevBoundaries[3];
            textBoundaries[1] = prevBoundaries[4];
            textBoundaries[2] = -1;
        } else {
            textBoundaries[0] = prevBoundaries[3];
            textBoundaries[1] = prevBoundaries[4];
            textBoundaries[2] = prevBoundaries[5];
        }
        int paragraphIndex = textBoundaries[1];
        boolean readingFirstParagraph = true;
        A: for (String paragraph : book.getChapter(textBoundaries[0]).getParagraphs().subList(textBoundaries[1], book.getChapter(textBoundaries[0]).getParagraphs().size())) {
            if (paragraph == null || paragraph.equals("")) {
                linesOfText[lineCount] = "";
            } else {
                String[] words = LineSplitter.fastSplit(paragraph, ' ');
                if (words.length == 0) continue;
                words[0] = "    " + words[0];
                float[] word_widths = LineSplitter.wordWidths(words, lineMeasurer);
                int word_index = 0;
                if (readingFirstParagraph) {
                    if (!(textBoundaries[2] >= words.length-1)) {
                        word_index = textBoundaries[2]+1;
                    }
                }
                Object[] splitReturn;
                splitReturn = LineSplitter.split(lineMeasurer, formatting, words, word_index, paragraphIndex, lineCount, textBoundaries, linesOfText, word_widths);
                paragraphIndex = (Integer)splitReturn[2];
                lineCount = (Integer)splitReturn[3];
                if ((Boolean)splitReturn[1]) {break A;}
            }
            lineCount++;
            if (lineCount == linesOfText.length) { // paragraph finished exactly.
                textBoundaries[3] = textBoundaries[0];
                textBoundaries[4] = paragraphIndex+1;
                textBoundaries[5] = -1;
                break;
            } else { // next paragraph
                linesOfText[lineCount] = "";
            }
            paragraphIndex++;
            readingFirstParagraph = false;
        }
        if (lineCount < linesOfText.length) { // chapter finished.
            textBoundaries[3] = textBoundaries[0]+1;
            textBoundaries[4] = -1;
            textBoundaries[5] = -1;
            for (int i=lineCount; i < linesOfText.length; i++) {
                linesOfText[i] = "";
            }
        }
        book.getChapter(textBoundaries[0]).addPage(false, new Page(linesOfText));
        book.getChapter(textBoundaries[0]).addBoundary(false, textBoundaries);
        return linesOfText;
    }
    private boolean loadPrevAndNextPages(int chapter) {
        Chapter c = book.getChapter(chapter);
        if (!c.firstPageLoaded) {
            initializeChapter(chapter);
            c.firstPageLoaded = true;
        }
        if (!c.lastPageLoaded) {
            Integer[] lastLoadedBoundaries = c.getLastBoundary();
            if (lastLoadedBoundaries[3] > chapter) {
                c.lastPageLoaded = true;
            } else {
                getNextPageLines(lastLoadedBoundaries);
            }
        }
        return c.firstPageLoaded && c.lastPageLoaded;
    }
    private void initializeChapter(int chapter) {
        Integer[] i = new Integer[6];
        i[3] = chapter;
        i[4] = -1;
        i[5] = -1;
        getNextPageLines(i);
    }
    private class LoadAllPages extends Thread {
        ResponseCallback<Void> pagesLoadedCallback;
        int mod;
        int cores;
        LoadAllPages(ResponseCallback<Void> pagesLoadedCallback, int mod, int cores) {
            super();
            this.pagesLoadedCallback = pagesLoadedCallback;
            this.mod=mod;
            this.cores=cores;
            start();
        }
        public void run() {
            boolean stopLoop;
            A: while(true) {
                stopLoop = true;
                for (int i=0; i < book.numberOfChapters(); i++) {
                    if (i%cores != mod) continue;
                    if (!loadPrevAndNextPages(i)) {
                        stopLoop = false;
                    }
                }
                if (stopLoop) {
                    break A;
                }
            }
            coresFinished++;
            if (coresFinished == cores) {
                pagesLoadedCallback.onResponse(null);
                coresFinished = 0;
            }
        }
    }

}
