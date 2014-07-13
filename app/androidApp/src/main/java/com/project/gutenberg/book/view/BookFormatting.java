package com.project.gutenberg.book.view;

public class BookFormatting {
    private int totalWidth;
    private int totalHeight;
    private int marginWidth;
    private int lineWidth;
    private int lineSpacing;
    private int linesPerPage;
    private int[] lineYCoordinates;

    public BookFormatting(int width, int height, int fontSize) {
        totalWidth = width;
        totalHeight = height;
        initializeFormatting(fontSize);
    }

    public void initializeFormatting(int fontSize) {
        marginWidth = (int)(totalWidth *0.05);
        lineWidth = totalWidth - (marginWidth *2);
        lineSpacing = (int)(fontSize*0.5);
        int freeSpace = totalHeight;
        int startingPosition = fontSize + lineSpacing *3;
        freeSpace = freeSpace - (fontSize*2 + lineSpacing *4); // header, footer space.
        linesPerPage = freeSpace/(fontSize+ lineSpacing);
        lineYCoordinates = new int[linesPerPage];
        lineYCoordinates[0] = startingPosition;
        for (int i=1; i < lineYCoordinates.length; i++) {
            lineYCoordinates[i] = lineYCoordinates[i-1]+fontSize+ lineSpacing;
        }

    }
    public int getLinesPerPage() {
        return linesPerPage;
    }
    public int getLineWidth() {
        return lineWidth;
    }
    public int[] getLineYCoordinates() {
        return lineYCoordinates;
    }
    public int getMarginWidth() {
        return marginWidth;
    }
    public int getTotalWidth() {
        return totalWidth;
    }
}
