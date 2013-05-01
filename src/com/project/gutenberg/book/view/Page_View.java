package com.project.gutenberg.book.view;


public abstract class Page_View {
    protected Book_View page_holder;
    protected int page_stack_id;
    public abstract void add_view(int index);
    public abstract void remove_view();

}
