package com.project.gutenberg;

import android.content.Context;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.project.gutenberg.com.project.gutenberg.util.RootActivity;
import com.project.gutenberg.com.project.gutenberg.util.RootListActivity;
import com.project.gutenberg.com.project.gutenberg.util.SharedPrefs;

import java.util.ArrayList;
import java.util.LinkedHashMap;


/**
 *
 */
public class Home extends RootListActivity {

    protected static SharedPrefs prefs;
    private Context context;
    public static int screen_height;
    public static int screen_width;
    private ListView menu;
    private Menu_Adapter menu_adapter;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize_app();
        setup_views();

    }

    private void setup_views() {
       setContentView(R.layout.home);
        menu = this.getListView();
        String[] headers = new String[5];
        headers[0] = "By Title";
        headers[1] = "By Author";
        headers[2] = "Filter Languages";
        headers[3] = "Dual Language";
        headers[4] = "By Year";
        menu_adapter = new Menu_Adapter(context, headers);
        menu.setAdapter(menu_adapter);
        menu.setOnItemClickListener(menu_click);
    }

    private void initialize_app() {
        context = this;
        prefs = new SharedPrefs(context);
        TAG = Home.class.getName();
        Display display = getWindowManager().getDefaultDisplay();
        screen_height = display.getHeight();
        screen_width = display.getWidth();
        setContentView(R.layout.home);
    }



    private class Menu_Adapter extends ArrayAdapter<Object> {
        private Context context;
        String[] headers;
        public Menu_Adapter(Context context, Object[] headers) {
            super(context, R.layout.home_menu_row, headers);
            this.context = context;
            this.headers = (String[])headers;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout row_view = (LinearLayout)inflater.inflate(R.layout.home_menu_row, parent, false);
            TextView row_view_header = (TextView)row_view.findViewById(R.id.home_menu_row_header);
            row_view_header.setText(headers[position]);
            return row_view;
        }
    }
    private AdapterView.OnItemClickListener menu_click = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        }
    };




}