import org.json.JSONException;
import org.json.JSONObject;

public class Book implements Comparable<Book> {
    private String id;
    private String title;
    private String author;

    public static int sort_by = 0;

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String toString() {
        return "Item [" +id + ", title=" + title + ", author=" + author + "]";
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public JSONObject to_json() {
        JSONObject json = new JSONObject();
        try {
            json.put("id",id);
            json.put("title",title);
            json.put("author",author);
        } catch (JSONException e) {}
        return json;
    }
    public boolean clean() {
        if (author == null || author.trim().equals("")) {
            author = "Unknown";
        }
        if (title == null || title.trim().equals("") || title.equals("\"")) return false;
        return true;
    }
    public int compareTo(Book o) {
        if (sort_by == 0) {
            return title.compareTo(o.getTitle());
        } else if (sort_by == 1) {
            return author.compareTo(o.getAuthor());
        }
        return 0;
    }
}