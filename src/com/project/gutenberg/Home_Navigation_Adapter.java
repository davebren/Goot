package com.project.gutenberg;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import com.GutenApplication;
import com.project.gutenberg.library.Book_Resource;

public class Home_Navigation_Adapter extends BaseExpandableListAdapter {
    Context context;
    LayoutInflater inflater;
    Cursor by_title_cursor;
    int by_title_count;

    public Home_Navigation_Adapter(Context context, Cursor by_title_cursor) {
        this.context = context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.by_title_cursor = by_title_cursor;
        by_title_cursor.moveToFirst();
        by_title_count = by_title_cursor.getCount();
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
        return false;
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
        if (groupPosition == 0) cursor = by_title_cursor;
        else return null;
        if (childPosition > by_title_cursor.getPosition()) move_cursor_forward(cursor, childPosition);
        else move_cursor_back(cursor,childPosition);
        return getChildView(new Book_Resource(cursor), view);
    }
    private void move_cursor_forward(Cursor cursor, int final_position) {
        while (by_title_cursor.getPosition() < final_position) {
            by_title_cursor.moveToNext();
        }
    }
    private void move_cursor_back(Cursor cursor, int final_position) {
        while (by_title_cursor.getPosition() > final_position) {
            by_title_cursor.moveToPrevious();
        }
    }
    private View getChildView(Book_Resource book, View view) {
        if (view == null) view = inflater.inflate(R.layout.home_navigation_group_item,null);
        ((TextView)view).setText(book.getTitle());
        return view;
    }
}
