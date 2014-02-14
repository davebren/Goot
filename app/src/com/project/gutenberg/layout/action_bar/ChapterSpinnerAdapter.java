package com.project.gutenberg.layout.action_bar;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.GutenApplication;


public class ChapterSpinnerAdapter extends ArrayAdapter<String> {
    Context context;

    public ChapterSpinnerAdapter(Context context, int resource, int textView, String[] items) {
        super(context, resource, items);
        this.context = context;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        ((TextView) v).setTypeface(((GutenApplication)context.getApplicationContext()).typeface);

        return v;
    }
    public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
        View v =super.getDropDownView(position, convertView, parent);
        ((TextView) v).setTypeface(((GutenApplication)context.getApplicationContext()).typeface);
        return v;
    }
}