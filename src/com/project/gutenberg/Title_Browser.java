package com.project.gutenberg;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.project.gutenberg.util.Response_Callback;

public class Title_Browser {
    ListView list_view;
    Title_Adapter adapter;
    Response_Callback<Void> book_opened_callback;

    public Title_Browser(Activity context, Response_Callback<Void> book_opened_callback) {
        list_view = (ListView)context.getLayoutInflater().inflate(R.layout.list_browser, null);
        adapter = new Title_Adapter(context, new String[] {"plato's republic"});
        list_view.setAdapter(adapter);
        this.book_opened_callback = book_opened_callback;
        list_view.setOnItemClickListener(book_click_listener);
    }
    public ListView get_list_view() {
        return list_view;
    }
    AdapterView.OnItemClickListener book_click_listener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> a, View v, int position, long l) {
            book_opened_callback.on_response(null);
        }
    };

}
