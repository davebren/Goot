package com.project.gutenberg.util;


import android.app.Activity;
import android.content.Intent;
import com.project.gutenberg.Home;

public class ActivityTag {
	protected final String TAG;
	protected final Activity a;
	protected ActivityTag(String TAG, Activity a) {
		this.TAG = TAG; this.a = a;
	}
	protected void restartStartup() {
		a.startActivity(new Intent(a.getBaseContext(), Home.class));
		a.finish();
	}
}