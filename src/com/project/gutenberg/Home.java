package com.project.gutenberg;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.*;
import android.widget.*;
import com.project.gutenberg.book.Book;
import com.project.gutenberg.book.pagination.PageSplitter;
import com.project.gutenberg.book.parsing.epub_parser.EpubParser;
import com.project.gutenberg.book.view.android.AndroidBookView;
import com.project.gutenberg.catalog.browsing.HomeNavigationAdapter;
import com.project.gutenberg.layout.action_bar.ActionBarHandler;
import com.project.gutenberg.layout.navigation_drawer.DrawerAdapter;
import com.project.gutenberg.util.*;

import nl.siegmann.epublib.epub.EpubReader;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.*;

@EActivity(R.layout.home)
public class Home extends RootActivity {
    protected static SharedPrefs prefs;
    private final Activity context = this;
    public static int screenHeight;
    public static int screenWidth;
    private ActionBarHandler actionBarHandler;

    @ViewById SizeChangeCallbackLinearLayout home;
    @ViewById ExpandableListView home_navigation_list;
    @ViewById DrawerLayout drawer_layout;
    @ViewById ExpandableListView drawer_list;

    private Book currentBook;
    private AndroidBookView currentBookView;

    protected static int pureActivityHeight; // does not include action bar, etc...
    protected static int pureActivityWidth;

    private ActionBarDrawerToggle drawerToggle;
    private DrawerAdapter drawerAdapter;
    HomeNavigationAdapter homeNavigationAdapter;
    ResponseCallback<Void> actionBarReadyCallback;

    public void onCreate(Bundle savedInstanceState) {
        prefs = new SharedPrefs(context);
        if (prefs.getOrientation().equals("portrait"))setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        TAG = Home.class.getName();
        Display display = getWindowManager().getDefaultDisplay();
        screenHeight = display.getHeight();
        screenWidth = display.getWidth();
        pureActivityHeight = screenHeight;
        pureActivityWidth = screenWidth;
        super.onCreate(savedInstanceState);
    }
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (drawerToggle == null)return;
        drawerToggle.syncState();
    }
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        drawerToggle.onConfigurationChanged(config);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) return true;
        return super.onOptionsItemSelected(item);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        actionBarHandler = new ActionBarHandler(menu, getActionBar(), this);
        actionBarHandler.setHomeViewMenu();
        if (homeNavigationAdapter != null) homeNavigationAdapter.setActionBarHandler(actionBarHandler);
        if (actionBarReadyCallback != null) actionBarReadyCallback.onResponse(null);
        return super.onCreateOptionsMenu(menu);
    }
    public void onDestroy() {
        ActionTimeAnalysis.log();
        super.onDestroy();
    }
    @AfterViews
    void setupViews() {
        home.setResponseCallback(sizeChangeCallback);
        homeNavigationAdapter = new HomeNavigationAdapter(this, home_navigation_list, bookOpenedCallback);
        home_navigation_list.setAdapter(homeNavigationAdapter);
        if (actionBarHandler != null) homeNavigationAdapter.setActionBarHandler(actionBarHandler);
        drawerAdapter = new DrawerAdapter(this,drawer_list);
        drawer_list.setAdapter(drawerAdapter);
        drawerToggle = new ActionBarDrawerToggle(this, drawer_layout,R.drawable.ic_drawer,R.string.nav_drawer_open,R.string.nav_drawer_closed) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                if (drawerAdapter.orientationChange()) {
                    drawerAdapter.changesMade();
                    if (prefs.getOrientation().equals("portrait"))setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    return;
                }
                if (drawerAdapter.changesMade()) refreshBook();
            }
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawer_layout.setDrawerListener(drawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        drawer_layout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        if (prefs.getOpenBook() != -999)  {
            if (actionBarHandler == null) {
                actionBarReadyCallback = new ResponseCallback<Void>() {
                    public void onResponse(Void aVoid) {
                        refreshBook();
                    }
                };
            } else refreshBook();
        }
    }
    private ResponseCallback<Integer[]> sizeChangeCallback = new ResponseCallback<Integer[]>() {
        public void onResponse(Integer[] dimensions) {
            pureActivityHeight = dimensions[1];
            pureActivityWidth = dimensions[0];
        }
    };
    ResponseCallback<String> bookOpenedCallback = new ResponseCallback<String>() {
        public void onResponse(String v) {
            openBook(v);
        }
    };
    private void openBook(String book_id) {
        nl.siegmann.epublib.domain.Book b = null;
        File file = new File(Environment.getExternalStorageDirectory().toString() + "/eskimo_apps/gutendroid/epub_no_images/"+ book_id + ".epub.noimages");
        try {
            InputStream is = new FileInputStream(file);
            b = new EpubReader().readEpub(is);
            is.close();
        } catch (FileNotFoundException e) {
            if (currentBook == null) {
                Toast.makeText(this,context.getString(R.string.open_book_file_not_found),3500).show();
            }
            return;
        } catch(IOException e) {
            if (currentBook == null) {
                Toast.makeText(this,context.getString(R.string.open_book_io_exception),3500).show();
            }
            return;
        }
        EpubParser parser = new EpubParser(b);
        currentBook = parser.parseBook();
        if (currentBook == null) {
            Toast.makeText(this,context.getString(R.string.open_book_corrupted_file),3500).show();
            file.delete();
            return;
        }
        home.removeView(home_navigation_list);
        ProgressBar progress = (ProgressBar)home.findViewById(R.id.home_progress);
        progress.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams fillScreenParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, screenHeight - getActionBar().getHeight());
        currentBookView = new AndroidBookView(currentBook, context, prefs, fillScreenParams, screenWidth, fillScreenParams.height, 0, actionBarHandler);
        PageSplitter pageSplitter = new PageSplitter(currentBook, currentBookView.getFormatting(), currentBookView.getLineMeasurer(), prefs.getLastChapter(prefs.getOpenBook()));
        pageSplitter.paginate(pagesLoadedCallback);
        prefs.setOpenBook(Integer.valueOf(book_id));

    }

    private ResponseCallback<Void> pagesLoadedCallback = new ResponseCallback<Void>() {
        public void onResponse(Void v) {
            runOnUiThread(new Runnable() {
                public void run() {
                    currentBook.setContainingPage(prefs.getLastChapter(prefs.getOpenBook()), prefs.getLastParagraph(prefs.getOpenBook()), prefs.getLastWord(prefs.getOpenBook()));
                    currentBookView.loadingHookCompletedReceiver(currentBook);
                    ProgressBar progress = (ProgressBar)home.findViewById(R.id.home_progress);
                    progress.setVisibility(View.GONE);
                    home.addView(currentBookView.getPageHolder());
                    ActionBarHandler.ignoreSpinnerSelection =true;
                    actionBarHandler.setBookViewMenu(currentBookView);
                    actionBarHandler.initializeSpinnerChapters(currentBook.getChapters(), prefs.getLastChapter(prefs.getOpenBook()));
                    actionBarHandler.setPage(currentBook.getPageNumber());
                    actionBarHandler.setBookTitle(currentBook.getTitle());
                    actionBarHandler.setTotalPages(currentBook.getNumberOfPages());
                }
            });
        }
    };
    public void onBackPressed() {
        if (currentBook != null) {
            closeBook();
            prefs.setOpenBook(-999);
        } else if (homeNavigationAdapter.getPreviousExpandedGroup() != -1) {
            home_navigation_list.collapseGroup(homeNavigationAdapter.getPreviousExpandedGroup());
        } else  super.onBackPressed();
    }
    private void closeBook() {
        home.removeView(currentBookView.getPageHolder());
        Integer[] boundaries = currentBook.close();
        ActionBarHandler.ignoreSpinnerSelection =true;
        prefs.setLastChapter(prefs.getOpenBook(), boundaries[0]);
        prefs.setLastParagraph(prefs.getOpenBook(), boundaries[1]);
        prefs.setLastWord(prefs.getOpenBook(), boundaries[2]);
        home.addView(home_navigation_list);
        currentBook = null;
        currentBookView = null;
        actionBarHandler.setHomeViewMenu();
    }
    private void refreshBook() {
        if (currentBook != null) {
            closeBook();
        }
        openBook("" + prefs.getOpenBook());
    }
    public void onActiveSubscription() {
        Toast.makeText(this, getString(R.string.thank_you),3500).show();
    }

}