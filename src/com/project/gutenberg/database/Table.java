package com.project.gutenberg.database;

public abstract class Table {
    String name;


    protected abstract String[] get_column_names();

}
