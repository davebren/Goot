package com.project.gutenberg;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.project.gutenberg.book.Book;
import com.project.gutenberg.book.pagination.Page_Splitter;
import com.project.gutenberg.book.parsing.Epub_Parser;
import com.project.gutenberg.book.view.android.Android_Book_View;
import com.project.gutenberg.layout.Action_Bar_Handler;
import com.project.gutenberg.layout.Title_Browser;
import com.project.gutenberg.layout.navigation_drawer.Navigation_Adapter;
import com.project.gutenberg.util.*;

import nl.siegmann.epublib.epub.EpubReader;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;

@EActivity(R.layout.home)
public class Home extends RootActivity {
    protected static Shared_Prefs prefs;
    private final Activity context = this;
    public static int screen_height;
    public static int screen_width;
    private Action_Bar_Handler action_bar_handler;

    @ViewById Size_Change_Callback_Linear_Layout home;
    @ViewById ScrollView home_scroll_view;
    @ViewById LinearLayout home_outer;
    @ViewById TextView home_title_nav;
    @ViewById TextView home_author_nav;
    @ViewById TextView home_category_nav;
    @ViewById TextView home_year_nav;
    @ViewById TextView home_settings_nav;
    @ViewById TextView home_donate_nav;
    @ViewById DrawerLayout drawer_layout;
    @ViewById ExpandableListView drawer_list;

    private Book current_book;
    private Android_Book_View current_book_view;

    protected static int pure_activity_height; // does not include action bar, etc...
    protected static int pure_activity_width;

    private ActionBarDrawerToggle drawer_toggle;
    private Navigation_Adapter drawer_adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize_app();
        Paint text_painter = new Paint();
        text_painter.setTextSize(getResources().getDimensionPixelSize(R.dimen.book_default_font_size)*prefs.get_book_font_scale());
        text_painter.setColor(Color.BLACK);
    }
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
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
        return super.onCreateOptionsMenu(menu);
    }
    public void onDestroy() {
        Action_Time_Analysis.log();
        super.onDestroy();
    }
    @AfterViews
    void setup_views() {
        home.set_response_callback(size_change_callback);
        drawer_adapter = new Navigation_Adapter(this,drawer_list);
        drawer_list.setAdapter(drawer_adapter);
        drawer_toggle = new ActionBarDrawerToggle(this, drawer_layout,R.drawable.ic_drawer,R.string.nav_drawer_open,R.string.nav_drawer_closed) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                if (drawer_adapter.changes_made()) refresh_book();
            }
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawer_layout.setDrawerListener(drawer_toggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        drawer_layout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
    }
    private void initialize_app() {
        prefs = new Shared_Prefs(context);
        TAG = Home.class.getName();
        Display display = getWindowManager().getDefaultDisplay();
        screen_height = display.getHeight();
        screen_width = display.getWidth();
        pure_activity_height = screen_height;
        pure_activity_width = screen_width;
    }
    private Response_Callback<Integer[]> size_change_callback = new Response_Callback<Integer[]>() {
        public void on_response(Integer[] dimensions) {
            pure_activity_height = dimensions[1];
            pure_activity_width = dimensions[0];
        }
    };
    @Click(R.id.home_title_nav) void title_nav_click() {
        home.removeAllViews();
        home.addView(new Title_Browser(context, book_opened_callback).get_list_view());
    }
    Response_Callback<Void> book_opened_callback = new Response_Callback<Void>() {
        public void on_response(Void v) {
            open_book();
        }
    };
    private void open_book() {
        nl.siegmann.epublib.domain.Book b = null;
        try {b = new EpubReader().readEpub(getAssets().open("pg2753.epub"));
        } catch(IOException e) {}
        Epub_Parser parser = new Epub_Parser(b);
        current_book = parser.parse_book();
        home.removeAllViews();
        LinearLayout.LayoutParams fill_screen_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, screen_height - getActionBar().getHeight());
        current_book_view = new Android_Book_View(current_book, context, prefs, fill_screen_params, screen_width, fill_screen_params.height, 0, action_bar_handler);
        Page_Splitter page_splitter = new Page_Splitter(current_book_view, current_book, current_book_view.get_formatting(), current_book_view.get_line_measurer(), prefs.get_last_chapter(-1), prefs.get_last_paragraph(-1), prefs.get_last_word(-1));
        page_splitter.paginate(pages_loaded_callback);

    }
    private Response_Callback<Void> pages_loaded_callback = new Response_Callback<Void>() {
        public void on_response(Void v) {
            runOnUiThread(new Runnable() {
                public void run() {
                    current_book.set_containing_page(prefs.get_last_chapter(-1),prefs.get_last_paragraph(-1),prefs.get_last_word(-1));
                    current_book_view.loading_hook_completed_receiver(current_book);
                    home.addView(current_book_view.get_page_holder());
                    Action_Bar_Handler.ignore_spinner_selection=true;
                    action_bar_handler.set_book_view_menu(current_book_view);
                    action_bar_handler.initialize_spinner_chapters(current_book.get_chapters(),prefs.get_last_chapter(-1));
                    action_bar_handler.set_page(current_book.get_page_number());
                    action_bar_handler.set_book_title(current_book.get_title());
                    action_bar_handler.set_total_pages(current_book.get_number_of_pages());
                }
            });
        }
    };
    public void onBackPressed() {
        if (current_book == null) super.onBackPressed();
        else {
            close_book();
        }
    }
    private void close_book() {
        home.removeView(current_book_view.get_page_holder());
        Integer[] boundaries = current_book.close();
        Action_Bar_Handler.ignore_spinner_selection=true;
        prefs.set_last_chapter(-1, boundaries[0]);
        prefs.set_last_paragraph(-1, boundaries[1]);
        prefs.set_last_word(-1, boundaries[2]);
        home.addView(home_scroll_view);
        current_book = null;
        current_book_view = null;
    }
    private void refresh_book() {
        if (current_book != null) {
            close_book();
            open_book();
        }
    }

}