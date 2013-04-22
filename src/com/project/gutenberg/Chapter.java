package com.project.gutenberg;

import nl.siegmann.epublib.domain.Resource;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;


public class Chapter {
    private boolean parsed = false;
    private LinkedList<String> paragraphs;
    private int chapter_index = -1;

    protected Chapter(int index) {
        paragraphs = new LinkedList<String>();
        chapter_index = index;
    }
    protected Chapter(int index, Resource res) {
        paragraphs = new LinkedList<String>();
        chapter_index = index;
        parse_chapter(res);
    }
    protected void parse_chapter(Resource res) {
        try {
            InputStream is = res.getInputStream();
            Document doc = Jsoup.parse(is, "UTF-8", "");
            Elements paragraphs = doc.getElementsByTag("p");
            for (Element p : paragraphs) {
                this.paragraphs.add(p.text());
            }
            is.close();
            res.close();
        } catch (IOException e) {
        }
        parsed = true;
    }
    protected LinkedList<String> get_paragraphs() {
        return paragraphs;
    }
    protected boolean is_parsed() {
        return parsed;
    }


}
