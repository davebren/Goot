package com.project.gutenberg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import com.GutenApplication;

public class Home_Navigation_Adapter extends BaseExpandableListAdapter {
    Context context;
    LayoutInflater inflater;

    public Home_Navigation_Adapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public int getGroupCount() {
        return 4;
    }
    public int getChildrenCount(int groupPosition) {
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
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return null;
    }
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
