package com.project.gutenberg;

import android.content.Context;
import android.widget.ArrayAdapter;
import com.project.gutenberg.util.Response_Callback;

public class Title_Adapter extends ArrayAdapter<String>  {
    public Title_Adapter(Context context, String[] titles) {
        super(context, R.layout.browse_titles_row, titles);
    }


}
