package com.project.gutenberg.book.view;


public abstract class Page_View {
    protected Book_View book_view;
    protected int page_stack_id;
    public abstract void add_view(int index);
    public abstract void remove_view();
    public abstract void set_page_stack_id(int id);
    public abstract int get_page_stack_id();
    public abstract void invalidate();

}
