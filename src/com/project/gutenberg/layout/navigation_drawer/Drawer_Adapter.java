package com.project.gutenberg.layout.navigation_drawer;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.GutenApplication;
import com.project.gutenberg.R;
import com.project.gutenberg.Shared_Prefs;
import com.project.gutenberg.util.Seekbar_Converter;
import com.project.gutenberg.util.Typeface_Mappings;

public class Drawer_Adapter extends BaseExpandableListAdapter {
    private LayoutInflater inflater;
    private Shared_Prefs prefs;
    private AssetManager assets;
    private Context context;

    private final int orientation_position=0;
    private final int text_size_position=1;
    private final int typeface_position=2;
    private final int value_for_value_position=3;

    private ExpandableListView list_view;

    private TextView font_size_seekbar_label;

    private String initial_orientation;
    private float initial_font_scale;
    private String initial_typeface;

    public Drawer_Adapter(Context context, ExpandableListView list_view) {
        this.context = context;
        this.list_view = list_view;
        prefs = new Shared_Prefs(context);
        assets = context.getAssets();
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        list_view.setOnGroupClickListener(group_listener);
        list_view.setOnChildClickListener(child_listener);
        initial_orientation = prefs.get_orientation();
        initial_font_scale = prefs.get_book_font_scale();
        initial_typeface = prefs.get_typeface();
    }
    public int getGroupCount() {return 4;}
    public int getChildrenCount(int groupPosition) {
        if (groupPosition == typeface_position) return Typeface_Mappings.mappings.length;
        if (groupPosition == orientation_position) return 1;
        if (groupPosition == value_for_value_position) return 3;
        return 0;
    }
    public Object getGroup(int groupPosition) {return null;}
    public Object getChild(int groupPosition, int childPosition) {return null;}
    public long getGroupId(int groupPosition) {return groupPosition;}
    public long getChildId(int groupPosition, int childPosition) {return 0;}
    public boolean hasStableIds() {
        return false;
    }
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (groupPosition == text_size_position) return text_size_item();
        if (groupPosition == typeface_position) return typeface_header();
        if (groupPosition == orientation_position) return orientation_header();
        if (groupPosition == value_for_value_position) return value_for_value_header();
        return inflater.inflate(R.layout.drawer_list_item,null);
    }
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (groupPosition == typeface_position) return typeface_child(childPosition);
        if (groupPosition == value_for_value_position) return value_for_value_child(childPosition);
        return null;
    }
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        if (groupPosition == typeface_position) return true;
        if (groupPosition == orientation_position) return true;
        if (groupPosition == value_for_value_position) return true;
        return false;
    }
    private View text_size_item() {
        View row = inflater.inflate(R.layout.drawer_text_size,null);
        SeekBar seekbar = (SeekBar)row.findViewById(R.id.drawer_text_size_seekbar);
        int seekbar_position = Seekbar_Converter.convert_font_scale_to_seekbar_position(prefs.get_book_font_scale());
        seekbar.setProgress(seekbar_position);
        font_size_seekbar_label = (TextView)row.findViewById(R.id.drawer_text_size_seekbar_label);
        font_size_seekbar_label.setText(Seekbar_Converter.convert_seekbar_position_to_label(seekbar_position));
        font_size_seekbar_label.setTypeface(((GutenApplication)context.getApplicationContext()).typeface);
        ((TextView)row.findViewById(R.id.drawer_text_size_header)).setTypeface(((GutenApplication)context.getApplicationContext()).typeface);
        seekbar.setOnSeekBarChangeListener(font_scale_change_listener);
        return row;
    }
    private View typeface_header() {
        LinearLayout row = (LinearLayout)inflater.inflate(R.layout.drawer_typeface,null);
        TextView header = (TextView)row.findViewById(R.id.drawer_typeface_header);
        header.setText(prefs.get_typeface());
        header.setTypeface(Typeface.createFromAsset(assets, Typeface_Mappings.get_file_name(prefs.get_typeface())));
        return row;
    }
    private View typeface_child(int position) {
        LinearLayout row = (LinearLayout)inflater.inflate(R.layout.drawer_typeface_child,null);
        TextView header = (TextView)row.findViewById(R.id.drawer_typeface_child_header);
        header.setTypeface(Typeface.createFromAsset(assets,Typeface_Mappings.mappings[position][1]));
        header.setText(Typeface_Mappings.mappings[position][0]);
        return row;
    }
    private View orientation_header() {
        LinearLayout row = (LinearLayout)inflater.inflate(R.layout.drawer_orientation,null);
        ToggleButton orientation_switch = (ToggleButton)row.findViewById(R.id.drawer_orientation_switch);
        if (prefs.get_orientation().equalsIgnoreCase("portrait")) orientation_switch.setChecked(false);
        else orientation_switch.setChecked(true);
        orientation_switch.setOnCheckedChangeListener(orientation_listener);
        ((TextView)row.findViewById(R.id.drawer_orientation_label)).setTypeface(((GutenApplication)context.getApplicationContext()).typeface);
        return row;
    }
    private View value_for_value_header() {
        TextView row = (TextView)inflater.inflate(R.layout.drawer_list_item,null);
        Spannable spannable = new SpannableString(context.getResources().getString(R.string.value_for_value));
        spannable.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.value)), 0, 5, 0);
        row.setText(spannable);
        row.setTypeface(((GutenApplication)context.getApplicationContext()).typeface);
        return row;
    }
    private View value_for_value_child(int position) {
        TextView row = (TextView)inflater.inflate(R.layout.drawer_list_item_child,null);
        if (position == 0)row.setText(context.getResources().getText(R.string.message_from_david));
        if (position == 1)row.setText(context.getResources().getText(R.string.one_per_month));
        if (position == 2)row.setText(context.getResources().getText(R.string.five_per_month));
        if (position == 3)row.setText(context.getResources().getText(R.string.twenty_per_month));
        row.setTypeface(((GutenApplication)context.getApplicationContext()).typeface);
        return row;
    }
    private ExpandableListView.OnGroupClickListener group_listener = new ExpandableListView.OnGroupClickListener() {
        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
            return false;
        }
    };
    private ExpandableListView.OnChildClickListener child_listener = new ExpandableListView.OnChildClickListener() {
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            if (groupPosition == typeface_position) {
                prefs.set_typeface(Typeface_Mappings.mappings[childPosition][0]);
                notifyDataSetChanged();
            }
            return true;
        }
    };
    private SeekBar.OnSeekBarChangeListener font_scale_change_listener = new SeekBar.OnSeekBarChangeListener() {
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            prefs.set_book_font_scale(Seekbar_Converter.convert_seekbar_position_to_font_scale(progress));
            font_size_seekbar_label.setText(Seekbar_Converter.convert_seekbar_position_to_label(progress));
        }
        public void onStartTrackingTouch(SeekBar seekBar) {}
        public void onStopTrackingTouch(SeekBar seekBar) {}
    };
    private ToggleButton.OnCheckedChangeListener orientation_listener = new ToggleButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) prefs.set_orientation("landscape");
            else prefs.set_orientation("portrait");
        }
    };
    public boolean orientation_change() {
        return !initial_orientation.equals(prefs.get_orientation());
    }
    public boolean changes_made() {
        boolean change_made = false;
        if (!initial_orientation.equals(prefs.get_orientation())) {
            change_made = true;
            initial_orientation = prefs.get_orientation();
        }
        if (initial_font_scale != prefs.get_book_font_scale()) {
            change_made = true;
            initial_font_scale = prefs.get_book_font_scale();
        }
        if (!initial_typeface.equals(prefs.get_typeface())) {
            change_made = true;
            initial_typeface = prefs.get_typeface();
        }
        return change_made;
    }

}
