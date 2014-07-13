package com.project.gutenberg.book.parsing;

import com.project.gutenberg.book.Book;
import com.project.gutenberg.util.ResponseCallback;

import java.util.LinkedList;


public interface BookParser {
    Book parseBook();
    void parseBook(ResponseCallback<Book> callback);
    LinkedList<String> parseChapter(int chapter_index);
}
