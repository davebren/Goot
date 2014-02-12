package com.project.gutenberg;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.GutenApplication;
import com.project.gutenberg.book.Book;
import com.project.gutenberg.book.pagination.Page_Splitter;
import com.project.gutenberg.book.parsing.epub_parser.Epub_Parser;
import com.project.gutenberg.book.view.android.Android_Book_View;
import com.project.gutenberg.catalog.browsing.Home_Navigation_Adapter;
import com.project.gutenberg.layout.action_bar.Action_Bar_Handler;
import com.project.gutenberg.layout.navigation_drawer.Drawer_Adapter;
import com.project.gutenberg.util.*;

import nl.siegmann.epublib.epub.EpubReader;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.*;

@EActivity(R.layout.home)
public class Home extends RootActivity {
    protected static Shared_Prefs prefs;
    private final Activity context = this;
    public static int screen_height;
    public static int screen_width;
    private Action_Bar_Handler action_bar_handler;

    @ViewById Size_Change_Callback_Linear_Layout home;
    @ViewById ExpandableListView home_navigation_list;
    @ViewById DrawerLayout drawer_layout;
    @ViewById ExpandableListView drawer_list;

    private Book current_book;
    private Android_Book_View current_book_view;

    protected static int pure_activity_height; // does not include action bar, etc...
    protected static int pure_activity_width;

    private ActionBarDrawerToggle drawer_toggle;
    private Drawer_Adapter drawer_adapter;
    Home_Navigation_Adapter home_navigation_adapter;
    Response_Callback<Void> action_bar_ready_callback;

    public void onCreate(Bundle savedInstanceState) {
        prefs = new Shared_Prefs(context);
        if (prefs.get_orientation().equals("portrait"))setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        TAG = Home.class.getName();
        Display display = getWindowManager().getDefaultDisplay();
        screen_height = display.getHeight();
        screen_width = display.getWidth();
        pure_activity_height = screen_height;
        pure_activity_width = screen_width;
        super.onCreate(savedInstanceState);
    }
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (drawer_toggle == null)return;
        drawer_toggle.syncState();
    }
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        drawer_toggle.onConfigurationChanged(config);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawer_toggle.onOptionsItemSelected(item)) return true;
        return super.onOptionsItemSelected(item);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        action_bar_handler = new Action_Bar_Handler(menu, getActionBar(), this);
        action_bar_handler.set_home_view_menu();
        if (home_navigation_adapter != null) home_navigation_adapter.set_action_bar_handler(action_bar_handler);
        if (action_bar_ready_callback != null) action_bar_ready_callback.on_response(null);
        return super.onCreateOptionsMenu(menu);
    }
    public void onDestroy() {
        Action_Time_Analysis.log();
        super.onDestroy();
    }
    @AfterViews
    void setup_views() {
        home.set_response_callback(size_change_callback);
        home_navigation_adapter = new Home_Navigation_Adapter(this, home_navigation_list, book_opened_callback);
        home_navigation_list.setAdapter(home_navigation_adapter);
        if (action_bar_handler != null) home_navigation_adapter.set_action_bar_handler(action_bar_handler);
        drawer_adapter = new Drawer_Adapter(this,drawer_list);
        drawer_list.setAdapter(drawer_adapter);
        drawer_toggle = new ActionBarDrawerToggle(this, drawer_layout,R.drawable.ic_drawer,R.string.nav_drawer_open,R.string.nav_drawer_closed) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                if (drawer_adapter.orientation_change()) {
                    drawer_adapter.changes_made();
                    if (prefs.get_orientation().equals("portrait"))setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    return;
                }
                if (drawer_adapter.changes_made()) refresh_book();
            }
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawer_layout.setDrawerListener(drawer_toggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        drawer_layout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        if (prefs.get_open_book() != -999)  {
            if (action_bar_handler == null) {
                action_bar_ready_callback = new Response_Callback<Void>() {
                    public void on_response(Void aVoid) {
                        refresh_book();
                    }
                };
            } else refresh_book();
        }
    }
    private Response_Callback<Integer[]> size_change_callback = new Response_Callback<Integer[]>() {
        public void on_response(Integer[] dimensions) {
            pure_activity_height = dimensions[1];
            pure_activity_width = dimensions[0];
        }
    };
    Response_Callback<String> book_opened_callback = new Response_Callback<String>() {
        public void on_response(String v) {
            open_book(v);
        }
    };
    private void open_book(String book_id) {
        nl.siegmann.epublib.domain.Book b = null;
        File file = new File(Environment.getExternalStorageDirectory().toString() + "/eskimo_apps/gutendroid/epub_no_images/"+ book_id + ".epub.noimages");
        try {
            InputStream is = new FileInputStream(file);
            b = new EpubReader().readEpub(is);
            is.close();
        } catch (FileNotFoundException e) {
            if (current_book == null) {
                Toast.makeText(this,context.getString(R.string.open_book_file_not_found),3500).show();
            }
            return;
        } catch(IOException e) {
            if (current_book == null) {
                Toast.makeText(this,context.getString(R.string.open_book_io_exception),3500).show();
            }
            return;
        }
        Epub_Parser parser = new Epub_Parser(b);
        current_book = parser.parse_book();
        if (current_book == null) {
            Toast.makeText(this,context.getString(R.string.open_book_corrupted_file),3500).show();
            file.delete();
            return;
        }
        home.removeView(home_navigation_list);
        ProgressBar progress = (ProgressBar)home.findViewById(R.id.home_progress);
        progress.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams fill_screen_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, screen_height - getActionBar().getHeight());
        current_book_view = new Android_Book_View(current_book, context, prefs, fill_screen_params, screen_width, fill_screen_params.height, 0, action_bar_handler);
        Page_Splitter page_splitter = new Page_Splitter(current_book, current_book_view.get_formatting(), current_book_view.get_line_measurer(), prefs.get_last_chapter(prefs.get_open_book()));
        page_splitter.paginate(pages_loaded_callback);
        prefs.set_open_book(Integer.valueOf(book_id));

    }

    private Response_Callback<Void> pages_loaded_callback = new Response_Callback<Void>() {
        public void on_response(Void v) {
            runOnUiThread(new Runnable() {
                public void run() {
                    current_book.set_containing_page(prefs.get_last_chapter(prefs.get_open_book()),prefs.get_last_paragraph(prefs.get_open_book()),prefs.get_last_word(prefs.get_open_book()));
                    current_book_view.loading_hook_completed_receiver(current_book);
                    ProgressBar progress = (ProgressBar)home.findViewById(R.id.home_progress);
                    progress.setVisibility(View.GONE);
                    home.addView(current_book_view.get_page_holder());
                    Action_Bar_Handler.ignore_spinner_selection=true;
                    action_bar_handler.set_book_view_menu(current_book_view);
                    action_bar_handler.initialize_spinner_chapters(current_book.get_chapters(),prefs.get_last_chapter(prefs.get_open_book()));
                    action_bar_handler.set_page(current_book.get_page_number());
                    action_bar_handler.set_book_title(current_book.get_title());
                    action_bar_handler.set_total_pages(current_book.get_number_of_pages());
                }
            });
        }
    };
    public void onBackPressed() {
        if (current_book != null) {
            close_book();
            prefs.set_open_book(-999);
        } else if (home_navigation_adapter.get_previous_expanded_group() != -1) {
            home_navigation_list.collapseGroup(home_navigation_adapter.get_previous_expanded_group());
        } else  super.onBackPressed();
    }
    private void close_book() {
        home.removeView(current_book_view.get_page_holder());
        Integer[] boundaries = current_book.close();
        Action_Bar_Handler.ignore_spinner_selection=true;
        prefs.set_last_chapter(prefs.get_open_book(), boundaries[0]);
        prefs.set_last_paragraph(prefs.get_open_book(), boundaries[1]);
        prefs.set_last_word(prefs.get_open_book(), boundaries[2]);
        home.addView(home_navigation_list);
        current_book = null;
        current_book_view = null;
        action_bar_handler.set_home_view_menu();
    }
    private void refresh_book() {
        if (current_book != null) {
            close_book();
        }
        open_book("" + prefs.get_open_book());
    }
    public void on_active_subscription() {
        Toast.makeText(this, getString(R.string.thank_you),3500).show();
    }

}