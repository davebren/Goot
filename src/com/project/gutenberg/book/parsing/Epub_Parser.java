package com.project.gutenberg.book.parsing;

import com.project.gutenberg.book.Book;
import com.project.gutenberg.book.Chapter;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.domain.TOCReference;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public class Epub_Parser implements Book_Parser {
    private int current_chapter = 0;



    private int current_paragraph = 0;
    private int current_word = 0;

    private nl.siegmann.epublib.domain.Book epub;
    private Spine spine;
    private LinkedList<Chapter> chapters;


    public Epub_Parser(nl.siegmann.epublib.domain.Book epub) {
        this.epub = epub;
    }
    public Epub_Parser(nl.siegmann.epublib.domain.Book epub, int current_chapter, int current_paragraph, int current_word) {
        this.current_chapter = current_chapter;
        this.current_paragraph = current_paragraph;
        this.current_word = current_word;
        this.epub = epub;
    }


    public Book parse_book() {
        String book_title = epub.getTitle();
        String book_author = epub.getMetadata().getAuthors().get(0).getLastname();

        spine = new Spine(epub.getTableOfContents());
        List<TOCReference> table_of_contents = epub.getTableOfContents().getTocReferences();
        initialize_chapters(spine, table_of_contents);
        Book book = new Book(book_title, book_author, chapters);
        return book;
    }
    private void initialize_chapters(Spine epub_spine, List<TOCReference> table_of_contents) {
        chapters = new LinkedList<Chapter>();
        for (int i=0; i < epub_spine.size(); i++) {
            chapters.addLast(new Chapter(this, i));
            if (i == current_chapter) {
                chapters.getLast().set_paragraphs(parse_chapter(i));
            }
        }
        String[] chapter_titles = new String[chapters.size()];
        if (table_of_contents != null && table_of_contents.size()>0) {
            String[] temp_titles = new String[table_of_contents.size()];
            for (int i=0; i < table_of_contents.size(); i++) {
                String t = table_of_contents.get(i).getTitle();
                if (t != null) {
                    temp_titles[i] = t;
                } else {
                    temp_titles[i] = "";
                }
            }
            boolean[] titles_set = new boolean[chapter_titles.length];
            for (int j=0; j < chapter_titles.length; j++) {
                for (int i=0; i < table_of_contents.size(); i++) {
                    if (epub_spine.getResource(j).getId().equals(table_of_contents.get(i).getResourceId())) {
                        if (!titles_set[j]) {
                            chapters.get(j).set_title(temp_titles[i]);
                            titles_set[j] = true;
                        }
                        break;
                    }
                }
                if (!titles_set[j]) {
                    chapters.get(j).set_title("" + (j+1));
                }
            }
        } else {
            for (int i=0; i < chapters.size(); i++) {
                chapters.get(i).set_title("Chapter " + (i+1));
            }
        }
    }
    public LinkedList<String> parse_chapter(int chapter_index) {
        Resource res = spine.getResource(chapter_index);
        LinkedList<String> para = new LinkedList<String>();
        try {
            InputStream is = res.getInputStream();
            Document doc = Jsoup.parse(is, "UTF-8", "");
            Elements paragraphs = doc.getElementsByTag("p");
            for (Element p : paragraphs) {
                para.add(p.text());
            }
            is.close();
            res.close();
        } catch (IOException e) {
        }
        return para;
    }
}
