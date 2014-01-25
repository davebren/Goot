package com.project.gutenberg.util;

public class Typeface_Mappings {
    public static final String[][] mappings = new String[][] {
            {"Roboto Regular", "fonts/roboto_regular.ttf"},
            {"Roboto Light", "fonts/roboto_light.ttf"},
            {"Roboto Medium","fonts/roboto_medium.ttf"},
            {"Roboto Thin", "fonts/roboto_thin.ttf"}
    };
    public static String get_file_name(String name) {
        for (String[] f : mappings) {
            if (f[0].equals(name)) return f[1];
        }
        return mappings[0][1];
    }
}
