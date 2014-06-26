package com.project.gutenberg.catalog.browsing;

import android.app.DownloadManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.GutenApplication;
import com.project.gutenberg.Home;
import com.project.gutenberg.R;
import com.project.gutenberg.catalog.database.CatalogByAuthorDB;
import com.project.gutenberg.catalog.database.CatalogByTitleDB;
import com.project.gutenberg.layout.DownloaderLayout;
import com.project.gutenberg.layout.action_bar.ActionBarHandler;
import com.project.gutenberg.library.BookResource;
import com.project.gutenberg.util.ResponseCallback;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class HomeNavigationAdapter extends BaseExpandableListAdapter {
    Home home;
    LayoutInflater inflater;

    Cursor byTitleCursor;
    Cursor byAuthorCursor;
    Cursor byDownloadsCursor;

    int byTitleCount;
    int byDownloadsCount;
    int byAuthorCount;

    Typeface typeface;
    ExpandableListView listView;

    final int by_title_index = 0;
    final int by_author_index = 1;
    final int by_category_index = 2;
    final int by_downloads_index = 2;

    DownloadManager downloadManager;
    HashMap<String, Pair<Long,Double>> downloadIds = new HashMap<String,Pair<Long,Double>>();
    HashMap<String, Void> downloadedBooks = new HashMap<String,Void>();
    ContextWrapper contextWrapper;
    Handler handler;
    ResponseCallback<String> bookOpenedCallback;

    ActionBarHandler actionBarHandler;
    CatalogByTitleDB catalogByTitleDb;
    CatalogByAuthorDB catalogByAuthorDB;

    static int previousExpandedGroup = -1;

    public HomeNavigationAdapter(Home home, ExpandableListView listView, ResponseCallback<String> bookOpenedCallback) {
        this.home = home;
        this.bookOpenedCallback = bookOpenedCallback;
        catalogByTitleDb = ((GutenApplication) this.home.getApplicationContext()).catalogByTitleDB;
        catalogByAuthorDB = ((GutenApplication) this.home.getApplicationContext()).catalogByAuthorDB;
        contextWrapper = new ContextWrapper(this.home);
        inflater = (LayoutInflater) this.home.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.byTitleCursor = ((GutenApplication) this.home.getApplicationContext()).catalogByTitleDB.get_title_cursor();
        this.byAuthorCursor = ((GutenApplication) this.home.getApplicationContext()).catalogByAuthorDB.getAuthorCursor();
        byDownloadsCursor = catalogByTitleDb.get_title_cursor(DownloadedRetriever.retrieveBookIds());
        byDownloadsCount = byDownloadsCursor.getCount();
        byTitleCount = byTitleCursor.getCount();
        byAuthorCount = byAuthorCursor.getCount();
        typeface = ((GutenApplication) this.home.getApplicationContext()).typeface;
        this.listView = listView;
        listView.setOnChildClickListener(childListener);
        downloadManager = (DownloadManager) this.home.getSystemService(Context.DOWNLOAD_SERVICE);
        File directory = new File(Environment.getExternalStorageDirectory() + "/eskimo_apps/gutendroid/epub_no_images/");
        if (!directory.exists()) directory.mkdirs();
        handler = new Handler();
        handler.postDelayed(checkDownloadStatus,25);
    }
    public int getPreviousExpandedGroup() {
        return previousExpandedGroup;
    }
    public void onGroupExpanded(int groupPosition){
        super.onGroupExpanded(groupPosition);
        if(groupPosition != previousExpandedGroup && previousExpandedGroup != -1){
            listView.collapseGroup(previousExpandedGroup);
        }
        previousExpandedGroup = groupPosition;
        if (actionBarHandler == null) return;
        if (groupPosition == by_title_index) actionBarHandler.setTitleBrowsingMenu();
        if (groupPosition == by_author_index) actionBarHandler.setAuthorBrowsingMenu();
        if (groupPosition == by_downloads_index) actionBarHandler.setDownloadsBrowsingMenu();
        home.invalidateOptionsMenu();
    }
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
        if (groupPosition == previousExpandedGroup) {
            previousExpandedGroup = -1;
            actionBarHandler.setHomeViewMenu();
        }
        home.invalidateOptionsMenu();
    }
    public boolean setActionBarHandler(ActionBarHandler actionBarHandler) {
        actionBarHandler.setHomeNavigationAdapter(this);
        this.actionBarHandler = actionBarHandler;
        if (previousExpandedGroup == -1) return false;
        if (previousExpandedGroup == by_title_index) actionBarHandler.setTitleBrowsingMenu();
        if (previousExpandedGroup == by_author_index) actionBarHandler.setAuthorBrowsingMenu();
        if (previousExpandedGroup == by_downloads_index) actionBarHandler.setDownloadsBrowsingMenu();
        return true;
    }
    public void filterTitles(String search) {
        byTitleCursor = catalogByTitleDb.getTitleCursor(search);
        byTitleCount = byTitleCursor.getCount();
        notifyDataSetChanged();
    }
    public void filterDownloads(String search) {
        byDownloadsCursor = catalogByTitleDb.getTitleCursor(search, DownloadedRetriever.retrieveBookIds());
        byDownloadsCount = byDownloadsCursor.getCount();
        notifyDataSetChanged();
    }
    public void filterAuthors(String search) {
        byAuthorCursor = catalogByAuthorDB.getAuthorCursor(search);
        byAuthorCount = byAuthorCursor.getCount();
        notifyDataSetChanged();
    }
    public int getGroupCount() {
        return 3;
    }
    public int getChildrenCount(int groupPosition) {
        if (groupPosition == by_title_index) return byTitleCount;
        if (groupPosition == by_downloads_index) return byDownloadsCount;
        if (groupPosition == by_author_index) return byAuthorCount;
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
        if (index == by_title_index) s = home.getResources().getString(R.string.nav_title_header);
        if (index == by_author_index) s = home.getResources().getString(R.string.nav_author_header);
        //if (index == by_category_index) s = home.getResources().getString(R.string.nav_category_header);
        if (index == by_downloads_index) s = home.getResources().getString(R.string.nav_downloads_header);
        ((TextView)row).setText(s);
        ((TextView)row).setTypeface(((GutenApplication) home.getApplicationContext()).typeface);
        return row;
    }
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {
        Cursor cursor=null;
        if (groupPosition == by_title_index) cursor = byTitleCursor;
        else if (groupPosition == by_downloads_index) cursor = byDownloadsCursor;
        else if (groupPosition == by_author_index) cursor = byAuthorCursor;
        else return null;
        cursor.moveToPosition(childPosition);
        return getChildView(new BookResource(cursor), view);
    }
    private View getChildView(BookResource book, View view) {
        if (view == null) view = inflater.inflate(R.layout.home_navigation_book_item,null);
        if (downloadIds.containsKey(book.getId())) {
            ((DownloaderLayout)view).setPercentage(downloadIds.get(book.getId()).second);
        }
        TextView title = (TextView)view.findViewById(R.id.home_navigation_book_item_left);
        TextView author = (TextView)view.findViewById(R.id.home_navigation_book_item_right);
        Button open = (Button)view.findViewById(R.id.home_navigation_book_item_open);
        title.setText(book.getTitle());
        author.setText(book.getAuthor());
        if (fileExists(book.getId() + ".epub.noimages")) {
            open.setVisibility(View.VISIBLE);
            open.setTypeface(typeface);
            open.setTag(book.getId());
            open.setOnClickListener(openBookListener);
        } else {
            open.setVisibility(View.GONE);
        }
        title.setTypeface(typeface);
        author.setTypeface(typeface);
        return view;
    }
    private ExpandableListView.OnChildClickListener childListener = new ExpandableListView.OnChildClickListener() {
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            Cursor cursor=null;
            if (groupPosition == by_title_index) cursor = byTitleCursor;
            if (groupPosition == by_downloads_index) cursor = byDownloadsCursor;
            if (groupPosition == by_author_index) cursor = byAuthorCursor;
            cursor.moveToPosition(childPosition);
            BookResource book = new BookResource(cursor);
            if (downloadIds.containsKey(book.getId())) return true;
            downloadBook(book);
            handler.postDelayed(checkDownloadStatus, 25);
            return true;
        }
    };
    private Button.OnClickListener openBookListener = new Button.OnClickListener() {
        public void onClick(View v) {
            String bookId = (String)v.getTag();
            bookOpenedCallback.onResponse(bookId);
        }
    };
    public void downloadBook(BookResource book) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse("http://www.gutenberg.org/ebooks/" + book.getId() + ".epub.noimages"));
        request.setTitle(book.getTitle()).setDestinationInExternalPublicDir("/eskimo_apps/gutendroid/epub_no_images/", "" + book.getId() + ".epub.noimages");
        long downloadId = downloadManager.enqueue(request);
        downloadIds.put(book.getId(), new Pair<Long, Double>(downloadId, 0.0));

    }
    private Runnable checkDownloadStatus = new Runnable() {
        public void run() {
            if (downloadIds.size() == 0) return;
                LinkedList<Pair<String,Double>> entriesToModify = new LinkedList<Pair<String, Double>>();
                LinkedList<String> entriesToRemove = new LinkedList<String>();
            for (Map.Entry<String, Pair<Long,Double>> entry : downloadIds.entrySet()) {
                DownloadManager.Query q = new DownloadManager.Query();
                q.setFilterById(entry.getValue().first);
                Cursor cursor = downloadManager.query(q);
                if (cursor == null) continue;
                cursor.moveToFirst();
                double bytesDownloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                double bytesTotal = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                double percentage = bytesDownloaded / bytesTotal;
                entriesToModify.add(new Pair<String, Double>(entry.getKey(), percentage));
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                if (status != DownloadManager.STATUS_PAUSED && status != DownloadManager.STATUS_PENDING && status != DownloadManager.STATUS_RUNNING) {
                    entriesToRemove.add(entry.getKey());
                    downloadedBooks.put(entry.getKey(), null);
                    makeFileReadOnly(entry.getKey() + ".epub.noimages");
                }
                notifyDataSetChanged();
            }
            for (Pair<String,Double> entry : entriesToModify) {
                downloadIds.put(entry.first, new Pair<Long, Double>(downloadIds.get(entry.first).first, entry.second));
            }
            for (String entry : entriesToRemove) {
                downloadIds.remove(entry);
            }
            handler.postDelayed(this,40);
        }
    } ;
    public boolean fileExists(String name){
        String path = Environment.getExternalStorageDirectory().toString() + "/eskimo_apps/gutendroid/epub_no_images/" + name;
        return new File(path).exists();
    }
    public boolean makeFileReadOnly(String name){
        String path = Environment.getExternalStorageDirectory().toString() + "/eskimo_apps/gutendroid/epub_no_images/" + name;
        return new File(path).setReadOnly();
    }
}
