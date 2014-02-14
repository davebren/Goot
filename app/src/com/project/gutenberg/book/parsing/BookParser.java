package com.project.gutenberg.book.parsing;

import com.project.gutenberg.book.Book;

import java.util.LinkedList;


public interface BookParser {
    Book parseBook();
    LinkedList<String> parseChapter(int chapter_index);
}
