package com.project.gutenberg;

import android.app.DownloadManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.GutenApplication;
import com.project.gutenberg.layout.Downloader_Layout;
import com.project.gutenberg.library.Book_Resource;
import com.project.gutenberg.util.Response_Callback;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Home_Navigation_Adapter extends BaseExpandableListAdapter {
    Context context;
    LayoutInflater inflater;
    Cursor by_title_cursor;
    int by_title_count;
    Typeface typeface;
    ExpandableListView list_view;

    final int by_title_index = 0;
    final int by_author_index = 1;
    final int by_category_index = 2;
    final int by_downloads_index = 3;
    DownloadManager download_manager;
    HashMap<String, Pair<Long,Double>> download_ids = new HashMap<String,Pair<Long,Double>>();
    HashMap<String, Void> downloaded_books = new HashMap<String,Void>();
    ContextWrapper context_wrapper;
    Handler handler;
    Response_Callback<String> book_opened_callback;


    public Home_Navigation_Adapter(Context context, Cursor by_title_cursor, ExpandableListView list_view, Response_Callback<String> book_opened_callback) {
        this.context = context;
        this.book_opened_callback = book_opened_callback;
        context_wrapper = new ContextWrapper(context);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.by_title_cursor = by_title_cursor;
        by_title_cursor.moveToFirst();
        by_title_count = by_title_cursor.getCount();
        typeface = ((GutenApplication)context.getApplicationContext()).typeface;
        this.list_view = list_view;
        list_view.setOnChildClickListener(child_listener);
        download_manager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
        File directory = new File(Environment.getExternalStorageDirectory() + "/eskimo_apps/gutendroid/epub_no_images/");
        if (!directory.exists()) directory.mkdirs();
        handler = new Handler();
        handler.postDelayed(check_download_status,25);
    }
    public int getGroupCount() {
        return 4;
    }
    public int getChildrenCount(int groupPosition) {
        if (groupPosition == 0) return by_title_count;
        return 0;
    }
    public Object getGroup(int groupPosition) {
        return null;
    }
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }
    public long getGroupId(int groupPosition) {
        return 0;
    }
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }
    public boolean hasStableIds() {
        return false;
    }
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    public View getGroupView(int index, boolean isExpanded, View row, ViewGroup parent) {
        if (row == null) row = inflater.inflate(R.layout.home_navigation_group_item,null);
        String s = "";
        if (index == 0) s = context.getResources().getString(R.string.nav_title_header);
        if (index == 1) s = context.getResources().getString(R.string.nav_author_header);
        if (index == 2) s = context.getResources().getString(R.string.nav_category_header);
        if (index == 3) s = context.getResources().getString(R.string.nav_downloads_header);
        ((TextView)row).setText(s);
        ((TextView)row).setTypeface(((GutenApplication)context.getApplicationContext()).typeface);
        return row;
    }
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {
        Cursor cursor=null;
        if (groupPosition == by_title_index) cursor = by_title_cursor;
        else return null;
        cursor.moveToPosition(childPosition);
        return getChildView(new Book_Resource(cursor), view);
    }
    private View getChildView(Book_Resource book, View view) {
        if (view == null) view = inflater.inflate(R.layout.home_navigation_book_item,null);
        if (download_ids.containsKey(book.getId())) {
            ((Downloader_Layout)view).set_percentage(download_ids.get(book.getId()).second);
        }
        TextView title = (TextView)view.findViewById(R.id.home_navigation_book_item_left);
        TextView author = (TextView)view.findViewById(R.id.home_navigation_book_item_right);
        Button open = (Button)view.findViewById(R.id.home_navigation_book_item_open);
        title.setText(book.getTitle());
        author.setText(book.getAuthor());
        if (file_exists(book.getId() + ".epub.noimages")) {
            open.setVisibility(View.VISIBLE);
            open.setTypeface(typeface);
            open.setTag(book.getId());
            open.setOnClickListener(open_book_listener);
        } else {
            open.setVisibility(View.GONE);
        }
        title.setTypeface(typeface);
        author.setTypeface(typeface);
        return view;
    }
    private ExpandableListView.OnChildClickListener child_listener = new ExpandableListView.OnChildClickListener() {
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            Cursor cursor=null;
            if (groupPosition == by_title_index) cursor = by_title_cursor;
            cursor.moveToPosition(childPosition);
            Book_Resource book = new Book_Resource(cursor);
            if (download_ids.containsKey(book.getId())) return true;
            download_book(book);
            handler.postDelayed(check_download_status, 25);
            return true;
        }
    };
    private Button.OnClickListener open_book_listener = new Button.OnClickListener() {
        public void onClick(View v) {
            String book_id = (String)v.getTag();
            Log.d("gutendroid","open book: " + book_id);
            book_opened_callback.on_response(book_id);
        }
    };
    public void download_book(Book_Resource book) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse("http://www.gutenberg.org/ebooks/" + book.getId() + ".epub.noimages"));
        request.setTitle(book.getTitle()).setDestinationInExternalPublicDir("/eskimo_apps/gutendroid/epub_no_images/", "" + book.getId() + ".epub.noimages");
        long download_id = download_manager.enqueue(request);
        download_ids.put(book.getId(),new Pair<Long,Double>(download_id,0.0));
    }
    private Runnable check_download_status = new Runnable() {
        public synchronized void run() {
            Log.d("gutendroid","check_download_status: " + download_ids.size());
            if (download_ids.size() == 0) return;
            for (Map.Entry<String, Pair<Long,Double>> entry : download_ids.entrySet()) {
                DownloadManager.Query q = new DownloadManager.Query();
                q.setFilterById(entry.getValue().first);
                Cursor cursor = download_manager.query(q);
                cursor.moveToFirst();
                double bytes_downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                double bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                double percentage = bytes_downloaded / bytes_total;
                Log.d("gutendroid", "download : " + percentage);
                Pair<Long, Double> val = new Pair<Long,Double>(entry.getValue().first, percentage);
                download_ids.put(entry.getKey(), val);
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                if (status != DownloadManager.STATUS_PAUSED && status != DownloadManager.STATUS_PENDING && status != DownloadManager.STATUS_RUNNING) {
                    download_ids.remove(entry.getKey());
                    downloaded_books.put(entry.getKey(),null);
                }
                notifyDataSetChanged();
            }
            handler.postDelayed(this,25);
        }
    } ;
    public boolean file_exists(String name){
        String path = Environment.getExternalStorageDirectory().toString() + "/eskimo_apps/gutendroid/epub_no_images/" + name;
        return new File(path).exists();
    }
}
