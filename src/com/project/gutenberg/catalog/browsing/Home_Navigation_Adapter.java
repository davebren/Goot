package com.project.gutenberg.catalog.browsing;

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
import com.project.gutenberg.R;
import com.project.gutenberg.catalog.database.Catalog_By_Author_DB;
import com.project.gutenberg.catalog.database.Catalog_By_Title_DB;
import com.project.gutenberg.layout.Downloader_Layout;
import com.project.gutenberg.layout.action_bar.Action_Bar_Handler;
import com.project.gutenberg.library.Book_Resource;
import com.project.gutenberg.util.Response_Callback;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Home_Navigation_Adapter extends BaseExpandableListAdapter {
    Context context;
    LayoutInflater inflater;

    Cursor by_title_cursor;
    Cursor by_author_cursor;
    Cursor by_downloads_cursor;

    int by_title_count;
    int by_downloads_count;
    int by_author_count;

    Typeface typeface;
    ExpandableListView list_view;

    final int by_title_index = 0;
    final int by_author_index = 1;
    final int by_category_index = 2;
    final int by_downloads_index = 2;

    DownloadManager download_manager;
    HashMap<String, Pair<Long,Double>> download_ids = new HashMap<String,Pair<Long,Double>>();
    HashMap<String, Void> downloaded_books = new HashMap<String,Void>();
    ContextWrapper context_wrapper;
    Handler handler;
    Response_Callback<String> book_opened_callback;

    Action_Bar_Handler action_bar_handler;
    Catalog_By_Title_DB catalog_by_title_db;
    Catalog_By_Author_DB catalog_by_author_db;

    int previous_expanded_group = -1;

    public Home_Navigation_Adapter(Context context, ExpandableListView list_view, Response_Callback<String> book_opened_callback) {
        this.context = context;
        this.book_opened_callback = book_opened_callback;
        catalog_by_title_db = ((GutenApplication)context.getApplicationContext()).catalog_by_title;
        catalog_by_author_db = ((GutenApplication)context.getApplicationContext()).catalog_by_author;
        context_wrapper = new ContextWrapper(context);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.by_title_cursor = ((GutenApplication)context.getApplicationContext()).catalog_by_title.get_title_cursor();
        this.by_author_cursor = ((GutenApplication)context.getApplicationContext()).catalog_by_author.get_author_cursor();
        by_downloads_cursor = catalog_by_title_db.get_title_cursor(Downloaded_Retriever.retrieve_book_ids());
        by_downloads_count = by_downloads_cursor.getCount();
        by_title_count = by_title_cursor.getCount();
        by_author_count = by_author_cursor.getCount();
        typeface = ((GutenApplication)context.getApplicationContext()).typeface;
        this.list_view = list_view;
        list_view.setOnChildClickListener(child_listener);
        download_manager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
        File directory = new File(Environment.getExternalStorageDirectory() + "/eskimo_apps/gutendroid/epub_no_images/");
        if (!directory.exists()) directory.mkdirs();
        handler = new Handler();
        handler.postDelayed(check_download_status,25);
    }
    public int get_previous_expanded_group() {
        return previous_expanded_group;
    }
    public void onGroupExpanded(int groupPosition){
        super.onGroupExpanded(groupPosition);
        if(groupPosition != previous_expanded_group && previous_expanded_group != -1){
            list_view.collapseGroup(previous_expanded_group);
        }
        previous_expanded_group = groupPosition;
        if (action_bar_handler == null) return;
        if (groupPosition == by_title_index) action_bar_handler.set_title_browsing_menu();
        if (groupPosition == by_author_index) action_bar_handler.set_author_browsing_menu();
        if (groupPosition == by_downloads_index) action_bar_handler.set_downloads_browsing_menu();
    }
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
        if (groupPosition == previous_expanded_group) {
            previous_expanded_group = -1;
            action_bar_handler.set_home_view_menu();
        }
    }
    public void set_action_bar_handler(Action_Bar_Handler action_bar_handler) {
        action_bar_handler.set_home_navigation_adapter(this);
        this.action_bar_handler = action_bar_handler;
        if (previous_expanded_group == -1) return;
        if (previous_expanded_group == by_title_index) action_bar_handler.set_title_browsing_menu();
        if (previous_expanded_group == by_author_index) action_bar_handler.set_author_browsing_menu();
        if (previous_expanded_group == by_downloads_index) action_bar_handler.set_downloads_browsing_menu();
    }
    public void filter_titles(String search) {
        by_title_cursor = catalog_by_title_db.get_title_cursor(search);
        by_title_count = by_title_cursor.getCount();
        notifyDataSetChanged();
    }
    public void filter_downloads(String search) {
        by_downloads_cursor = catalog_by_title_db.get_title_cursor(search,Downloaded_Retriever.retrieve_book_ids());
        by_downloads_count = by_downloads_cursor.getCount();
        notifyDataSetChanged();
    }
    public void filter_authors(String search) {
        by_author_cursor = catalog_by_author_db.get_author_cursor(search);
        by_author_count = by_author_cursor.getCount();
        notifyDataSetChanged();
    }
    public int getGroupCount() {
        return 3;
    }
    public int getChildrenCount(int groupPosition) {
        if (groupPosition == by_title_index) return by_title_count;
        if (groupPosition == by_downloads_index) return by_downloads_count;
        if (groupPosition == by_author_index) return by_author_count;
        return 0;
    }
    public Object getGroup(int groupPosition) {
        return null;
    }
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }
    public long getGroupId(int groupPosition) {
        return groupPosition;
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
        if (index == by_title_index) s = context.getResources().getString(R.string.nav_title_header);
        if (index == by_author_index) s = context.getResources().getString(R.string.nav_author_header);
        //if (index == by_category_index) s = context.getResources().getString(R.string.nav_category_header);
        if (index == by_downloads_index) s = context.getResources().getString(R.string.nav_downloads_header);
        ((TextView)row).setText(s);
        ((TextView)row).setTypeface(((GutenApplication)context.getApplicationContext()).typeface);
        return row;
    }
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {
        Cursor cursor=null;
        if (groupPosition == by_title_index) cursor = by_title_cursor;
        else if (groupPosition == by_downloads_index) cursor = by_downloads_cursor;
        else if (groupPosition == by_author_index) cursor = by_author_cursor;
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
            if (groupPosition == by_downloads_index) cursor = by_downloads_cursor;
            if (groupPosition == by_author_index) cursor = by_author_cursor;
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
                    make_file_read_only(entry.getKey() + ".epub.noimages");
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
    public boolean make_file_read_only(String name){
        String path = Environment.getExternalStorageDirectory().toString() + "/eskimo_apps/gutendroid/epub_no_images/" + name;
        return new File(path).setReadOnly();
    }
}
