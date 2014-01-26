package com.project.gutenberg.layout.browsing;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorTreeAdapter;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;
import com.GutenApplication;
import com.project.gutenberg.R;

public class Catalog_Adapter extends SimpleCursorTreeAdapter {
    Context context;
    LayoutInflater inflater;

    public Catalog_Adapter(Context context, int groupLayout, int childLayout, String[] groupFrom,
                           int[] groupTo, String[] childrenFrom, int[] childrenTo) {
        super(context, null, groupLayout, groupFrom, groupTo, childLayout, childrenFrom, childrenTo);
        this.context = context;
        this.inflater = inflater;
    }

    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
}
