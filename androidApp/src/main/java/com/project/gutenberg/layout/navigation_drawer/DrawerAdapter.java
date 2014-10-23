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
import com.project.gutenberg.GutenApplication;
import com.project.gutenberg.Home;
import com.project.gutenberg.R;
import com.project.gutenberg.SharedPrefs;
import com.project.gutenberg.util.SeekbarConverter;
import com.project.gutenberg.util.TypefaceMappings;

public class DrawerAdapter extends BaseExpandableListAdapter {
    private LayoutInflater inflater;
    private SharedPrefs prefs;
    private AssetManager assets;
    private Home home;

    private final int orientation_position=0;
    private final int text_size_position=1;
    private final int typeface_position=2;
    private final int value_for_value_position=3;

    private final int one_dollar_position = 0;
    private final int five_dollar_position = 1;
    private final int twenty_dollar_position = 2;

    private ExpandableListView listView;

    private TextView fontSizeSeekbarLabel;

    private String initialOrientation;
    private float initialFontScale;
    private String initialTypeface;

    public DrawerAdapter(Home home, ExpandableListView listView) {
        this.home = home;
        this.listView = listView;
        prefs = new SharedPrefs(home);
        assets = home.getAssets();
        inflater = (LayoutInflater) home.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listView.setOnGroupClickListener(groupListener);
        listView.setOnChildClickListener(childListener);
        initialOrientation = prefs.getOrientation();
        initialFontScale = prefs.getBookFontScale();
        initialTypeface = prefs.getTypeface();
    }
    public int getGroupCount() {return 4;}
    public int getChildrenCount(int groupPosition) {
        if (groupPosition == typeface_position) return TypefaceMappings.mappings.length;
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
        if (groupPosition == text_size_position) return textSizeItem();
        if (groupPosition == typeface_position) return typefaceHeader();
        if (groupPosition == orientation_position) return orientationHeader();
        if (groupPosition == value_for_value_position) return valueForValueHeader();
        return inflater.inflate(R.layout.drawer_list_item,null);
    }
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (groupPosition == typeface_position) return typefaceChild(childPosition);
        if (groupPosition == value_for_value_position) return valueForValueChild(childPosition);
        return null;
    }
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        if (groupPosition == typeface_position) return true;
        if (groupPosition == orientation_position) return true;
        if (groupPosition == value_for_value_position) return true;
        return false;
    }
    private View textSizeItem() {
        View row = inflater.inflate(R.layout.drawer_text_size,null);
        SeekBar seekbar = (SeekBar)row.findViewById(R.id.drawer_text_size_seekbar);
        int seekbar_position = SeekbarConverter.convertFontScaleToSeekbarPosition(prefs.getBookFontScale());
        seekbar.setProgress(seekbar_position);
        fontSizeSeekbarLabel = (TextView)row.findViewById(R.id.drawer_text_size_seekbar_label);
        fontSizeSeekbarLabel.setText(SeekbarConverter.convertSeekbarPositionToLabel(seekbar_position));
        fontSizeSeekbarLabel.setTypeface(((GutenApplication) home.getApplicationContext()).typeface);
        ((TextView)row.findViewById(R.id.drawer_text_size_header)).setTypeface(((GutenApplication) home.getApplicationContext()).typeface);
        seekbar.setOnSeekBarChangeListener(fontScaleChangeListener);
        return row;
    }
    private View typefaceHeader() {
        LinearLayout row = (LinearLayout)inflater.inflate(R.layout.drawer_typeface,null);
        TextView header = (TextView)row.findViewById(R.id.drawer_typeface_header);
        header.setText(prefs.getTypeface());
        header.setTypeface(Typeface.createFromAsset(assets, TypefaceMappings.getFileName(prefs.getTypeface())));
        return row;
    }
    private View typefaceChild(int position) {
        LinearLayout row = (LinearLayout)inflater.inflate(R.layout.drawer_typeface_child,null);
        TextView header = (TextView)row.findViewById(R.id.drawer_typeface_child_header);
        header.setTypeface(Typeface.createFromAsset(assets, TypefaceMappings.mappings[position][1]));
        header.setText(TypefaceMappings.mappings[position][0]);
        return row;
    }
    private View orientationHeader() {
        LinearLayout row = (LinearLayout)inflater.inflate(R.layout.drawer_orientation,null);
        ToggleButton orientationSwitch = (ToggleButton)row.findViewById(R.id.drawer_orientation_switch);
        if (prefs.getOrientation().equalsIgnoreCase("portrait")) orientationSwitch.setChecked(false);
        else orientationSwitch.setChecked(true);
        orientationSwitch.setOnCheckedChangeListener(orientationListener);
        ((TextView)row.findViewById(R.id.drawer_orientation_label)).setTypeface(((GutenApplication) home.getApplicationContext()).typeface);
        return row;
    }
    private View valueForValueHeader() {
        TextView row = (TextView)inflater.inflate(R.layout.drawer_list_item,null);
        Spannable spannable = new SpannableString(home.getResources().getString(R.string.value_for_value));
        spannable.setSpan(new ForegroundColorSpan(home.getResources().getColor(R.color.value)), 0, 5, 0);
        row.setText(spannable);
        row.setTypeface(((GutenApplication) home.getApplicationContext()).typeface);
        return row;
    }
    private View valueForValueChild(int position) {
        TextView row = (TextView)inflater.inflate(R.layout.drawer_list_item_child,null);
        //if (position == 0)row.setText(home.getResources().getText(R.string.message_from_david));
        if (position == 0)row.setText(home.getResources().getText(R.string.one_per_month));
        if (position == 1)row.setText(home.getResources().getText(R.string.five_per_month));
        if (position == 2)row.setText(home.getResources().getText(R.string.twenty_per_month));
        row.setTypeface(((GutenApplication) home.getApplicationContext()).typeface);
        return row;
    }
    private ExpandableListView.OnGroupClickListener groupListener = new ExpandableListView.OnGroupClickListener() {
        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
            return false;
        }
    };
    private ExpandableListView.OnChildClickListener childListener = new ExpandableListView.OnChildClickListener() {
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            if (groupPosition == typeface_position) {
                prefs.setTypeface(TypefaceMappings.mappings[childPosition][0]);
                notifyDataSetChanged();
            }
            if (groupPosition == value_for_value_position) {
                //if (childPosition == one_dollar_position) new Subscription(1,home);
                //if (childPosition == five_dollar_position) new Subscription(5,home);
                //if (childPosition == twenty_dollar_position) new Subscription(20,home);
            }
            return true;
        }
    };
    private SeekBar.OnSeekBarChangeListener fontScaleChangeListener = new SeekBar.OnSeekBarChangeListener() {
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            prefs.setBookFontScale(SeekbarConverter.convertSeekbarPositionToFontScale(progress));
            fontSizeSeekbarLabel.setText(SeekbarConverter.convertSeekbarPositionToLabel(progress));
        }
        public void onStartTrackingTouch(SeekBar seekBar) {}
        public void onStopTrackingTouch(SeekBar seekBar) {}
    };
    private ToggleButton.OnCheckedChangeListener orientationListener = new ToggleButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) prefs.setOrientation("landscape");
            else prefs.setOrientation("portrait");
        }
    };
    public boolean orientationChange() {
        return !initialOrientation.equals(prefs.getOrientation());
    }
    public boolean changesMade() {
        boolean changeMade = false;
        if (!initialOrientation.equals(prefs.getOrientation())) {
            changeMade = true;
            initialOrientation = prefs.getOrientation();
        }
        if (initialFontScale != prefs.getBookFontScale()) {
            changeMade = true;
            initialFontScale = prefs.getBookFontScale();
        }
        if (!initialTypeface.equals(prefs.getTypeface())) {
            changeMade = true;
            initialTypeface = prefs.getTypeface();
        }
        return changeMade;
    }

}
