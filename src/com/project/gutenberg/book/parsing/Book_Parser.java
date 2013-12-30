package com.project.gutenberg.book.parsing;

import com.project.gutenberg.book.Book;

import java.util.LinkedList;


public interface Book_Parser {
    Book parse_book();
    LinkedList<String> parse_chapter(int chapter_index);
}
