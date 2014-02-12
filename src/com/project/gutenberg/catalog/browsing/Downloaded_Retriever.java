package com.project.gutenberg.catalog.browsing;

import android.os.Environment;

import java.io.File;
import java.util.LinkedList;

public class Downloaded_Retriever {
    public static String[] retrieve_book_ids() {
        String path = Environment.getExternalStorageDirectory().toString()+"/eskimo_apps/gutendroid/epub_no_images";
        File f = new File(path);
        if (!f.exists()) return new String[0];
        File[] files = f.listFiles();
        LinkedList<String> ids = new LinkedList<String>();
        for (File file : files) {
            if (!file.getName().contains("epub")) continue;
            String id = file.getName().substring(0,file.getName().lastIndexOf('.'));
            if (id.contains(".")) id = id.substring(0,id.lastIndexOf('.'));
            ids.add("etext" + id);
        }
        return ids.toArray(new String[ids.size()]);
    }
}
