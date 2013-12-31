package com.project.gutenberg.util;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class Action_Time_Analysis {
    private final static String app_name = "gutendroid";
    private static HashMap<String,Long[]> times = new HashMap<String,Long[]>();
    private static HashMap<String, Long> start_times = new HashMap<String,Long>();

    public static void start(String action_key) {
        start_times.put(action_key, System.currentTimeMillis());
    }
    public static void end(String action_key) {
        if (!start_times.containsKey(action_key)) {return;}
        long action_count = 1;
        if (times.containsKey(action_key)) {
            action_count += times.get(action_key)[0];
        }
        Long[] val = new Long[] {action_count,System.currentTimeMillis()-start_times.get(action_key)};
        times.put(action_key,val);
    }
    public static void log() {
        Log.d(app_name, "------------- ACTION TIME ANALYSIS -------------");
        for (Map.Entry<String,Long[]> entry : times.entrySet()) {
            Log.d("gutendroid","------ action: " + entry.getKey() + " -- executed " + entry.getValue()[0] + " times, " + entry.getValue()[1] + "ms, mean = " + entry.getValue()[1]/entry.getValue()[0]);
        }
    }
}
