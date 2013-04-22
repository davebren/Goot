package com.project.gutenberg;

import android.app.ActionBar;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.project.gutenberg.com.project.gutenberg.util.Fonts;
import com.project.gutenberg.com.project.gutenberg.util.RootActivity;
import com.project.gutenberg.com.project.gutenberg.util.Shared_Prefs;

import android.view.View.OnClickListener;


/**
 *
 */
public class Home extends RootActivity {

    protected static Shared_Prefs prefs;
    private Context context;
    public static int screen_height;
    public static int screen_width;
    private static Fonts fonts;
    private ActionBar action_bar;
    private SearchView search_view;
    private LinearLayout[] home_nav;
    private TextView[] home_nav_headers;
    private LinearLayout browse_title;
    private ListView browse_titles_list;

    private LinearLayout outer_layout;
    private ScrollView home_scroll;

    private LayoutInflater inflater;
    private LinearLayout.LayoutParams fill_screen_params;

    private final int num_primary_nav_menus = 6;
    private final int browse_title_index = 0;
    private boolean[] expanded_primary_nav = new boolean[num_primary_nav_menus];


    private int current_menu_depth = 0;

    private Book_View book_view;
    private LinearLayout current_book_holder;
    private int current_book_holder_position = -1;
    private boolean book_view_open = false;

    protected static int pure_activity_height; // does not include action bar, etc...
    protected static int pure_activity_width;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize_app();
        setup_views();
        AssetManager assetManager = getAssets();




    }



    private void setup_views() {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        setContentView(new Home_Outer(context));
        outer_layout = (LinearLayout)findViewById(R.id.home_outer);
        home_scroll = (ScrollView)findViewById(R.id.home_scroll);
        home_scroll.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE && book_view_open) {
                    return true;
                }
                return false;
            }
        });

        String[] headers = new String[num_primary_nav_menus];
        home_nav = new LinearLayout[num_primary_nav_menus];
        home_nav_headers = new TextView[num_primary_nav_menus];
        headers[0] = "Browse: By Title";
        headers[1] = "By Author";
        headers[2] = "By Category";
        headers[3] = "By Year";
        headers[4] = "Settings";
        headers[5] = "Donate";

        for (int i=0; i < headers.length; i++) {
            home_nav[i] = (LinearLayout)inflater.inflate(R.layout.home_menu_row, null).findViewById(R.id.home_menu_row_holder);
            home_nav_headers[i] = (TextView)home_nav[i].findViewById(R.id.home_menu_row_header);
            home_nav_headers[i].setText(headers[i]);
            outer_layout.addView(home_nav[i]);
            final int f_i = i;
            home_nav[i].setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    expand_primary_nav(f_i);
                }
            });
        }
        browse_title = (LinearLayout)inflater.inflate(R.layout.browse_titles, null);
        browse_titles_list = (ListView)browse_title.findViewById(R.id.browse_titles_list);
        browse_titles_list.setLayoutParams(fill_screen_params);
        browse_titles_list.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE && book_view_open) {
                    return true;
                }
                return false;
            }
        });
        String[] titles = new String[100];
        for (int i=0; i < titles.length; i++) {
            titles[i] = "" + i;
        }
        Title_Adapter browse_titles_adapter = new Title_Adapter(context, titles);
        browse_titles_list.setAdapter(browse_titles_adapter);
        browse_titles_list.setOnItemClickListener(title_click);

        home_scroll.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                browse_titles_list.getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });
        browse_titles_list.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event)
            {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }


    private void initialize_app() {
        context = this;
        prefs = new Shared_Prefs(context);
        TAG = Home.class.getName();

        Display display = getWindowManager().getDefaultDisplay();
        screen_height = display.getHeight();
        screen_width = display.getWidth();
        pure_activity_height = screen_height;
        pure_activity_width = screen_width;

        fonts = new Fonts(prefs, context);
        action_bar = getActionBar();
        action_bar.setTitle("Home");
        fill_screen_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, screen_height - action_bar.getHeight());
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        SearchView search_view = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        search_view.setOnQueryTextListener(query_listener);
        return super.onCreateOptionsMenu(menu);
    }

    SearchView.OnQueryTextListener query_listener = new SearchView.OnQueryTextListener() {
        public boolean onQueryTextChange(String query) {
            return true;
        }
        public boolean onQueryTextSubmit(String query) {
            return true;
        }
    };

    private void expand_primary_nav(int index) {
        switch(index) {
            case browse_title_index: toggle_by_title();
                break;
        }

        // close other navs.
        for (int i=0; i < expanded_primary_nav.length; i++) {
            if (i != index && expanded_primary_nav[i]) {
                switch(i) {
                    case browse_title_index: toggle_by_title();
                        break;
                }
            }
        }
    }
    private void toggle_by_title() {
        if (!expanded_primary_nav[browse_title_index]) {
            home_nav[browse_title_index].addView(browse_title);
            action_bar.setTitle("Browse By Title");
            home_scroll.post(new Runnable() {
                public void run() {
                    home_scroll.scrollTo(0, home_nav_headers[browse_title_index].getBottom());
                }
            });
            current_menu_depth = 1;
        } else {
            home_nav[browse_title_index].removeView(browse_title);
            action_bar.setTitle("Home");
            current_menu_depth = 0;
        }
        expanded_primary_nav[browse_title_index] = !expanded_primary_nav[browse_title_index];
    }



    private class Title_Adapter extends ArrayAdapter<Object> {
        private Context context;
        String[] titles;
        public Title_Adapter(Context context, Object[] titles) {
            super(context, R.layout.home_menu_row, titles);
            this.context = context;
            this.titles = (String[])titles;

        }
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout row_view = (LinearLayout)inflater.inflate(R.layout.browse_titles_row, parent, false);
            TextView row_view_header = (TextView)row_view.findViewById(R.id.browse_titles_row_header);
            row_view_header.setText(titles[position]);
            if (position == current_book_holder_position && book_view_open) {
                ((LinearLayout)book_view.get_view().getParent()).removeView(book_view.get_view());
                row_view.addView(book_view.get_view());
                current_book_holder = row_view;
            }

            return row_view;
        }
    }
    private AdapterView.OnItemClickListener title_click = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (current_menu_depth == 2 && book_view_open) {
                current_book_holder.removeView(book_view.get_view());
            }
            if (position != current_book_holder_position) {
                final View row_view = view;
                final int f_position = position;


                book_view_open = true;
                current_menu_depth = 2;
                current_book_holder_position = position;
                current_book_holder = (LinearLayout)view;

                // TODO change arguments to book details retrieved from database.
                book_view = new Book_View(context, inflater, false, "", getAssets(), fill_screen_params, -1);
                ((LinearLayout)view).addView(book_view.get_view());
                action_bar.setTitle(book_view.get_title() + " by " + book_view.get_author());
                browse_titles_list.postDelayed(new Runnable() {
                    public void run() {
                        browse_titles_list.setSelection(f_position);
                        browse_titles_list.scrollBy(0, row_view.findViewById(R.id.browse_titles_row_header).getBottom());
                    }
                },0);
            } else {
                current_menu_depth = 1;
                book_view_open = false;
                current_book_holder_position = -1;
            }
        }
    };

    public void onBackPressed() {
        if (current_menu_depth == 0) {
            super.onBackPressed();
        } else if (current_menu_depth == 1) {
            expand_primary_nav(-1);
        } else if (current_menu_depth == 2) {
            current_book_holder.removeView(book_view.get_view());
            book_view.kill_book();
            book_view = null;
            current_menu_depth = 1;
            book_view_open = false;
            current_book_holder_position = -1;
        }

    }





    private class Home_Outer extends LinearLayout {
        public Home_Outer(Context context) {
            super(context);
            outer_layout = (LinearLayout)inflater.inflate(R.layout.home, null);
            outer_layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
            this.addView(outer_layout);
        }


        public void onSizeChanged (int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            pure_activity_height = h;
            pure_activity_width = w;
            fill_screen_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, h);
            browse_titles_list.setLayoutParams(fill_screen_params);
            if (book_view_open) {
                book_view.size_changed();
            }

        }


    }






}