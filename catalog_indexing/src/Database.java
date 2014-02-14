import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class Database {
    public static void main(String args[] ) {
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:catalog_by_authors.sqlite");
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String sql = "CREATE TABLE books_by_author " +
                    "(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    " id TEXT NOT NULL, " +
                    " title TEXT NOT NULL, " +
                    " author TEXT NOT NULL)";
            stmt.executeUpdate(sql);
            stmt.close();

            JSONArray titles = titles_json();
            sql = "INSERT INTO books_by_author(id,title,author) " +
                    "VALUES (?, ?, ?);";
            PreparedStatement prep = c.prepareStatement(sql);
            System.out.println(sql);
            System.out.println(titles.length());
            for (int i=0; i < titles.length(); i++) {
                JSONObject title = titles.getJSONObject(i);
                prep.setString(1,title.getString("id"));
                prep.setString(2,title.getString("title"));
                prep.setString(3,title.getString("author"));
                prep.addBatch();
                if (i != 0 && i%200 == 0) {
                    prep.executeBatch();
                    prep = c.prepareStatement(sql);
                    System.out.println(i + " out of " + titles.length());
                }
            }
            prep.executeBatch();
            c.commit();
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Table created successfully");
    }
    static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return encoding.decode(ByteBuffer.wrap(encoded)).toString();
    }
    private static JSONArray titles_json() {
        try {
            String s = readFile("catalog_json_by_author.txt", Charset.forName("UTF-8"));
            return new JSONArray(s);
        } catch (IOException e) {} catch (JSONException e1) {}
        return new JSONArray();
    }

}
