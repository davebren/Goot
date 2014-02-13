package com.project.gutenberg.book.view;


public abstract class PageView {
    protected BookView bookView;
    protected int pageStackId;
    public abstract void addView(int index);
    public abstract void removeView();
    public abstract void setPageStackId(int id);
    public abstract int getPageStackId();
    public abstract void invalidate();

}
