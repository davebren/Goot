package com.project.gutenberg.layout.navigation_drawer;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.project.gutenberg.R;
import com.project.gutenberg.util.Shared_Prefs;
import com.project.gutenberg.util.Typeface_Mappings;

public class Navigation_Adapter extends BaseExpandableListAdapter {
    private LayoutInflater inflater;
    private Shared_Prefs prefs;
    private AssetManager assets;
    private Context context;

    private final int orientation_position=0;
    private final int text_size_position=1;
    private final int typeface_position=2;

    private ExpandableListView list_view;

    public Navigation_Adapter(Context context, ExpandableListView list_view) {
        this.context = context;
        this.list_view = list_view;
        prefs = new Shared_Prefs(context);
        assets = context.getAssets();
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        list_view.setOnGroupClickListener(group_listener);
        list_view.setOnChildClickListener(child_listener);
    }
    public int getGroupCount() {return 3;}
    public int getChildrenCount(int groupPosition) {
        if (groupPosition == typeface_position) return Typeface_Mappings.mappings.length;
        if (groupPosition == orientation_position) return 1;
        return 0;
    }
    public Object getGroup(int groupPosition) {return null;}
    public Object getChild(int groupPosition, int childPosition) {return null;}
    public long getGroupId(int groupPosition) {return 0;}
    public long getChildId(int groupPosition, int childPosition) {return 0;}
    public boolean hasStableIds() {
        return false;
    }
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (groupPosition == text_size_position) return text_size_item();
        if (groupPosition == typeface_position) return typeface_header();
        if (groupPosition == orientation_position) return orientation_header();
        return inflater.inflate(R.layout.drawer_list_item,null);
    }
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (groupPosition == typeface_position) return typeface_child(childPosition);
        if (groupPosition == orientation_position) return orientation_child();
        return null;
    }
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        if (groupPosition == typeface_position) return true;
        if (groupPosition == orientation_position) return true;
        return false;
    }
    private View text_size_item() {
        View row = inflater.inflate(R.layout.drawer_text_size,null);
        return row;
    }
    private View typeface_header() {
        LinearLayout row = (LinearLayout)inflater.inflate(R.layout.drawer_typeface,null);
        TextView header = (TextView)row.findViewById(R.id.drawer_typeface_header);
        header.setText(prefs.get_typeface());
        header.setTypeface(Typeface.createFromAsset(assets,"fonts/"+Typeface_Mappings.get_file_name(prefs.get_typeface())));
        return row;
    }
    private View typeface_child(int position) {
        LinearLayout row = (LinearLayout)inflater.inflate(R.layout.drawer_typeface_child,null);
        TextView header = (TextView)row.findViewById(R.id.drawer_typeface_child_header);
        header.setTypeface(Typeface.createFromAsset(assets,"fonts/"+Typeface_Mappings.mappings[position][1]));
        header.setText(Typeface_Mappings.mappings[position][0]);
        return row;
    }
    private View orientation_header() {
        LinearLayout row = (LinearLayout)inflater.inflate(R.layout.drawer_typeface,null);
        TextView header = (TextView)row.findViewById(R.id.drawer_typeface_header);
        String orientation_text= "";
        if (prefs.get_orientation().equalsIgnoreCase("portrait")) orientation_text = context.getResources().getString(R.string.portrait);
        if (prefs.get_orientation().equalsIgnoreCase("landscape")) orientation_text = context.getResources().getString(R.string.landscape);
        header.setText(orientation_text);
        return row;
    }
    private View orientation_child() {
        LinearLayout row = (LinearLayout)inflater.inflate(R.layout.drawer_typeface_child,null);
        TextView header = (TextView)row.findViewById(R.id.drawer_typeface_child_header);
        String orientation_text= "";
        if (prefs.get_orientation().equals("portrait")) orientation_text = context.getResources().getString(R.string.landscape);
        if (prefs.get_orientation().equals("landscape")) orientation_text = context.getResources().getString(R.string.portrait);
        header.setText(orientation_text);
        return row;
    }

    private ExpandableListView.OnGroupClickListener group_listener = new ExpandableListView.OnGroupClickListener() {
        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
            return false;
        }
    };
    private ExpandableListView.OnChildClickListener child_listener = new ExpandableListView.OnChildClickListener() {
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            if (groupPosition == orientation_position) {
                if (prefs.get_orientation().equalsIgnoreCase("portrait")) {prefs.set_orientation("landscape");
                } else if (prefs.get_orientation().equalsIgnoreCase("landscape")) prefs.set_orientation("portrait");
                notifyDataSetChanged();
            }
            if (groupPosition == typeface_position) {
                prefs.set_typeface(Typeface_Mappings.mappings[childPosition][0]);
                notifyDataSetChanged();
            }
            return true;
        }
    };


}
