import org.json.JSONArray;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Main {

    public static void main(String[] args){
        Book.sort_by = 1;
        print_file("catalog_json_by_author.txt");
    }
    private static void print_file(String file_name) {
        List<Book> books = read_catalog("catalog.rdf");
        System.out.println("books read: " + books.size());
        List<Book> cleaned_books = new ArrayList<Book>();
        for (Book b : books) {
            if (b.clean()) cleaned_books.add(b) ;
        }
        System.out.println("cleaned books: " + cleaned_books.size());
        Collections.sort(cleaned_books);
        write_json(cleaned_books, file_name);
    }
    public static void write_json(List<Book> books, String file_name) {
        JSONArray json = new JSONArray();
        for (Book b : books) {
            json.put(b.to_json());
        }
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file_name, "UTF-8");
        } catch (FileNotFoundException e) {
        } catch (UnsupportedEncodingException e) {}
        writer.write(json.toString());
        writer.close();
    }

    static final String BOOK_KEY = "etext";
    static final String ID_KEY = "{http://www.w3.org/1999/02/22-rdf-syntax-ns#}ID";
    static final String TITLE_KEY = "title";
    static final String AUTHOR_KEY = "creator";

    public static List<Book> read_catalog(String file) {
        List<Book> items = new ArrayList<Book>();
        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            InputStream in = new FileInputStream(file);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
            Book item = null;

            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    if (startElement.getName().getLocalPart().equals(BOOK_KEY)) {
                        item = new Book();
                        Iterator<Attribute> attributes = startElement.getAttributes();
                        while (attributes.hasNext()) {
                            Attribute attribute = attributes.next();
                            if (attribute.getName().toString().equals(ID_KEY)) {
                                item.setId(attribute.getValue());
                            }
                        }
                    }
                    if (event.isStartElement()) {
                        if (event.asStartElement().getName().getLocalPart().equals(TITLE_KEY)) {
                            event = eventReader.nextEvent();
                            item.setTitle(event.asCharacters().getData());
                            continue;
                        }
                    }
                    if (event.asStartElement().getName().getLocalPart().equals(AUTHOR_KEY)) {
                        event = eventReader.nextEvent();
                        item.setAuthor(event.asCharacters().getData());
                        continue;
                    }
                }
                if (event.isEndElement()) {
                    EndElement endElement = event.asEndElement();
                    if (endElement.getName().getLocalPart().equals(BOOK_KEY)) {
                        System.out.println("book added: " + item);
                        items.add(item);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        return items;
    }
}
