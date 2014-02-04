package com.project.gutenberg.book.parsing.epub_parser;

import android.util.Log;
import com.project.gutenberg.book.Book;
import com.project.gutenberg.book.Chapter;
import com.project.gutenberg.book.parsing.Book_Parser;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.domain.TOCReference;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Epub_Parser implements Book_Parser {
    private nl.siegmann.epublib.domain.Book epub;
    private Spine spine;
    private LinkedList<Chapter> chapters;

    public Epub_Parser(nl.siegmann.epublib.domain.Book epub) {
        this.epub = epub;
    }
    public Book parse_book() {
        String book_title = epub.getTitle();
        String book_author = "Unknown";
        if (epub.getMetadata().getAuthors().size() != 0)
            book_author = epub.getMetadata().getAuthors().get(0).getLastname();
        spine = new Spine(epub.getTableOfContents());
        if (spine.size() == 0) return null;
        List<TOCReference> table_of_contents = epub.getTableOfContents().getTocReferences();
        initialize_chapters(spine, table_of_contents);
        Book book = new Book(book_title, book_author, chapters);
        return book;
    }
    private void initialize_chapters(Spine epub_spine, List<TOCReference> table_of_contents) {
        Log.d("gutendroid", "initialize_chapters: empty? " + epub_spine.isEmpty());
        Log.d("gutendroid", "initialize_chapters: spine size = " + epub_spine.size());
        Log.d("gutendroid","initialize_chapters: epub contents size = " + epub.getContents().size());
        chapters = new LinkedList<Chapter>();
        HashMap<Integer, Void> removed_chapters = new HashMap<Integer, Void>();
        HashMap<String, Integer> unique_toc = new HashMap<String,Integer>();
        for (int i=0; i < epub_spine.size(); i++) {
            Log.d("gutendroid", "spine resource: " + epub_spine.getResource(i).getId());
            chapters.addLast(new Chapter(this, i));
            chapters.getLast().set_paragraphs(parse_chapter(i));
            if (chapters.getLast().get_paragraphs().size() == 0) {
                chapters.removeLast();
                removed_chapters.put(i,null);
            }
        }
        for (int i=0; i < table_of_contents.size(); i ++) {
            String resource_id = table_of_contents.get(i).getResourceId();
            if (!unique_toc.containsKey(resource_id)) unique_toc.put(resource_id,i);
        }
        List<TOCReference> temp_list = new LinkedList<TOCReference>();
        for (int i=0; i < table_of_contents.size(); i++) {
            if (unique_toc.containsValue(i)) temp_list.add(table_of_contents.get(i));
        }
        table_of_contents = temp_list;
        Log.d("gutendroid", "initialize_chapters.0: " + epub_spine.size() + ", " + table_of_contents.size() + ", " + removed_chapters.size());

        String[] chapter_titles = new String[chapters.size()];
        if (table_of_contents != null && table_of_contents.size()>0) {
            String[] temp_titles = new String[chapters.size()];
            int chapters_skipped = 0;
            for (int i=0; i < table_of_contents.size(); i++) {
                Log.d("gutendroid", "toc resource: " + table_of_contents.get(i).getResource().getId());
                if (removed_chapters.containsKey(i)) {
                    chapters_skipped++;
                    continue;
                }
                String t = format_title(table_of_contents.get(i).getTitle());
                Log.d("gutendroid", "initialize_chapters.1: " + i + ", " + chapters_skipped + ", " + temp_titles.length);
                if (t != null) {
                    temp_titles[i-chapters_skipped] = t;
                } else {
                    temp_titles[i-chapters_skipped] = "";
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
                if (p.text().matches(".*\\w.*")) {
                    para.add(p.text());
                }
            }
            is.close();
            res.close();
        } catch (IOException e) {
        }
        return para;
    }

    private String format_title(String title) {
        String s ="";
        String[] words = title.split(" ");
        for (int i=0; i < words.length; i++) {
            if (is_roman_numeral(words[i])) {
                s += words[i];
            } else {
                s += words[i].substring(0,1).toUpperCase() + words[i].substring(1).toLowerCase();
            }
            if (i < words.length -1) {
                s += " ";
            }
        }
        return s;
    }
    private boolean is_roman_numeral(String s) {   // doesn't check correctness.
        for (int j=0; j < s.length(); j++) {
            char c = s.charAt(j);
            if (c != 'I' && c != 'V' && c != 'X'
                    && c != 'L' && c != 'C' && c != 'D'
                    && c != 'M' && c != '.') return false;
        }
        return true;
    }

}
