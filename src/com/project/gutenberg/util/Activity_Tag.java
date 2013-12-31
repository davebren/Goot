package com.project.gutenberg.util;


import android.app.Activity;
import android.content.Intent;
import com.project.gutenberg.Home;

public class Activity_Tag {
	protected final String TAG;
	protected final Activity a;
	protected Activity_Tag(String TAG, Activity a) {
		this.TAG = TAG; this.a = a;
	}
	protected void restart_startup() {
		a.startActivity(new Intent(a.getBaseContext(), Home.class));

		a.finish();
	}
}