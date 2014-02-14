package com.project.gutenberg.book.parsing.epub_parser;

import com.project.gutenberg.book.Book;
import com.project.gutenberg.book.Chapter;
import com.project.gutenberg.book.parsing.BookParser;
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

public class EpubParser implements BookParser {
    private nl.siegmann.epublib.domain.Book epub;
    private Spine spine;
    private LinkedList<Chapter> chapters;

    public EpubParser(nl.siegmann.epublib.domain.Book epub) {
        this.epub = epub;
    }
    public Book parseBook() {
        String bookTitle = epub.getTitle();
        String bookAuthor = "Unknown";
        if (epub.getMetadata().getAuthors().size() != 0)
            bookAuthor = epub.getMetadata().getAuthors().get(0).getLastname();
        spine = new Spine(epub.getTableOfContents());
        if (spine.size() == 0) return null;
        List<TOCReference> tableOfContents = epub.getTableOfContents().getTocReferences();
        initializeChapters(spine, tableOfContents);
        Book book = new Book(bookTitle, bookAuthor, chapters);
        return book;
    }
    private void initializeChapters(Spine epubSpine, List<TOCReference> tableOfContents) {
        chapters = new LinkedList<Chapter>();
        HashMap<Integer, Void> removedChapters = new HashMap<Integer, Void>();
        HashMap<String, Integer> uniqueTOC = new HashMap<String,Integer>();
        for (int i=0; i < epubSpine.size(); i++) {
            chapters.addLast(new Chapter(this, i));
            chapters.getLast().setParagraphs(parseChapter(i));
            if (chapters.getLast().getParagraphs().size() == 0) {
                chapters.removeLast();
                removedChapters.put(i, null);
            }
        }
        for (int i=0; i < tableOfContents.size(); i ++) {
            String resourceID = tableOfContents.get(i).getResourceId();
            if (!uniqueTOC.containsKey(resourceID)) uniqueTOC.put(resourceID,i);
        }
        List<TOCReference> tempList = new LinkedList<TOCReference>();
        for (int i=0; i < tableOfContents.size(); i++) {
            if (uniqueTOC.containsValue(i)) tempList.add(tableOfContents.get(i));
        }
        tableOfContents = tempList;

        String[] chapterTitles = new String[chapters.size()];
        if (tableOfContents != null && tableOfContents.size()>0) {
            String[] tempTitles = new String[chapters.size()];
            int chaptersSkipped = 0;
            for (int i=0; i < tableOfContents.size(); i++) {
                if (removedChapters.containsKey(i)) {
                    chaptersSkipped++;
                    continue;
                }
                String t = formatTitle(tableOfContents.get(i).getTitle());
                if (t != null) {
                    tempTitles[i-chaptersSkipped] = t;
                } else {
                    tempTitles[i-chaptersSkipped] = "";
                }
            }
            boolean[] titlesSet = new boolean[chapterTitles.length];
            for (int j=0; j < chapterTitles.length; j++) {
                for (int i=0; i < tableOfContents.size(); i++) {
                    if (epubSpine.getResource(j).getId().equals(tableOfContents.get(i).getResourceId())) {
                        if (!titlesSet[j]) {
                            chapters.get(j).setTitle(tempTitles[i]);
                            titlesSet[j] = true;
                        }
                        break;
                    }
                }
                if (!titlesSet[j]) chapters.get(j).setTitle("" + (j + 1));
            }
        } else
            for (int i=0; i < chapters.size(); i++) chapters.get(i).setTitle("Chapter " + (i + 1));
    }
    public LinkedList<String> parseChapter(int chapterIndex) {
        Resource res = spine.getResource(chapterIndex);
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

    private String formatTitle(String title) {
        String s ="";
        String[] words = title.split(" ");
        for (int i=0; i < words.length; i++) {
            if (isRomanNumeral(words[i])) {
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
    private boolean isRomanNumeral(String s) {   // doesn't check correctness.
        for (int j=0; j < s.length(); j++) {
            char c = s.charAt(j);
            if (c != 'I' && c != 'V' && c != 'X'
                    && c != 'L' && c != 'C' && c != 'D'
                    && c != 'M' && c != '.') return false;
        }
        return true;
    }

}
