package com.project.gutenberg.util;


import java.util.LinkedList;

import com.project.gutenberg.Home;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class RootActivity extends Activity
{
    protected String TAG=RootActivity.class.getName();
    protected static LinkedList<ActivityTag> activities = new  LinkedList<ActivityTag>();
    protected static LinkedList<ActivityTag> pref_activities = new LinkedList<ActivityTag>();
    
    protected void onCreate(Bundle savedInstanceState)
    {
        if (TAG.equals(Home.class.getName())) {
            for (ActivityTag a : activities) {
            		if (a.a != null) {
                		a.a.finish();
            		}
            }
            for (ActivityTag a : pref_activities) {
        		if (a.a != null) {
            		a.a.finish();
        		}
            }
            activities.clear();
            pref_activities.clear();
        } else {
        	 int index = -1;
	        for (ActivityTag a : activities) {
	        	index++;
	        	if (a.TAG.equals(TAG)) {
	        		if (a.a != null) {
	            		a.a.finish();
	        	        activities.remove(index);
	            		break;
	        		}
	        	}
	        }

        }
        activities.add(new ActivityTag(TAG, this));
        super.onCreate(savedInstanceState);

    }

    protected void onDestroy()
    {
    	int index = -1;
    	for (ActivityTag a : activities) {
    		index++;
    		if (a.TAG.equals(TAG)) {
        		if (a.a != null) {
        	    	activities.remove(index);
            		break;
        		}
    		}
    	}
    	
        super.onDestroy();
    }

    protected void set_tag(String TAG) {
    	this.TAG = TAG;
    }
    public static void start_activity(Context c, Class<RootActivity> cl) {
    	c.startActivity(new Intent(c, cl));
    }
    
    
}