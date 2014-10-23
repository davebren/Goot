package com.project.gutenberg.book.view.android;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.widget.RelativeLayout;
import com.project.gutenberg.book.view.BookFormatting;
import com.project.gutenberg.book.view.BookView;
import com.project.gutenberg.book.view.PageView;

class AndroidPageView extends PageView {
    private Page page;
    private Paint textPainter;
    AndroidPageView(Context context, AndroidBookView bookView, int pageStackId) {
        this.bookView = bookView;
        page = new Page(context);
        this.pageStackId = pageStackId;
        this.textPainter = bookView.textPainter;
    }
    public void addView(int index) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
        if (index == -1) {

        } else {
            ((AndroidBookView) bookView).getPageHolder().addView(page, index);
        }
    }
    public void removeView() {
        ((AndroidBookView) bookView).getPageHolder().removeView(page);
    }
    public void setPageStackId(int id) {
        pageStackId = id;
    }
    public int getPageStackId() {
        return pageStackId;
    }
    public void invalidate() {
        page.invalidate();
    }
    private class Page extends View {
        public Page(Context context) {
            super(context);
        }
        // TODO add basic kerning.
        public void onDraw(Canvas c) {
            drawBackground(c);
            String[] text = bookView.getPageLines(pageStackId + 1);
            BookFormatting format = bookView.getFormatting();
            int[] lineYCoordinates = format.getLineYCoordinates();
            if (text == null) {return;}
            for (int i=0; i < text.length; i++) {
                if (text[i]!= null) {
                    justify(c, bookView, lineYCoordinates[i], text, i);
                }
            }
        }
        public void drawBackground(Canvas c) {
            c.drawColor(((AndroidBookView) bookView).BACKGROUND_COLOR);
        }
        private void justify(Canvas c, BookView bookView, int yCoordinate, String[] lines, int index) {
            float margin = bookView.getFormatting().getMarginWidth();
            if (lines.length <= index+1 || lines[index+1].startsWith("  ") || lines[index+1].equals("")) {
                c.drawText(lines[index],margin,yCoordinate, textPainter);
            } else {
                float kernSpace = bookView.getFormatting().getLineWidth() - bookView.getLineMeasurer().measureWidth(lines[index]);
                kernSpace = (float)Math.min(kernSpace, bookView.getFormatting().getLineWidth()*.25);
                float kern_size = kernSpace/lines[index].length();
                float[] charWidths = bookView.getLineMeasurer().charWidths(lines[index]);
                int kernsSkipped = 0;
                for (int i=0; i < lines[index].length(); i++) {
                    if (lines[index].charAt(i) != ' ') {
                        kernsSkipped = i;break;
                    }
                }
                for (int i=kernsSkipped; i < lines[index].length();i++) {
                    char ch = lines[index].charAt(i);
                    c.drawText("" + ch, margin + sumArray(charWidths, i) + (i-kernsSkipped)*kern_size, yCoordinate, textPainter);
                    if (ch == '?' || ch == '!' || ch == ',' || ch == '.' || ch == '"' || ch == '\'' || ch == ';' || ch == ':') {
                        kernsSkipped++;
                    }
                }
            }
        }
    }
    private float sumArray(float[] arr, int end_index) {
        float sum = 0;
        for (int i=0; i < end_index; i++) {
            sum += arr[i];
        }
        return sum;
    }


}
