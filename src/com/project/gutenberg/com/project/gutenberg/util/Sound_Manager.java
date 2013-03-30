package com.project.gutenberg.com.project.gutenberg.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import com.project.gutenberg.R;


import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.util.Log;

public class Sound_Manager {
	

private  SoundPool sound_pool; 
private  HashMap<Integer, Integer> resource_locator; 
private  Context context;
private  Vector<Integer> available_sounds = new Vector<Integer>();
private  On_Load loader;
private LinkedList<Integer> streams;
public boolean initialized;
private SharedPrefs prefs;

private final int NUM_SOUNDS = 28;

	public Sound_Manager(Context context){
		 this.context = context;
		 prefs = new SharedPrefs(context);
	     sound_pool = new SoundPool(NUM_SOUNDS, AudioManager.STREAM_MUSIC, 0); 
	     resource_locator = new HashMap<Integer, Integer>(); 
	     loader = new On_Load(NUM_SOUNDS);
	     sound_pool.setOnLoadCompleteListener(loader);
	     streams = new LinkedList<Integer>();
	}


	public void initialize() {
		//add(1, R.raw.ding);
		//add(2, R.raw.buzzer);

		initialized = true;
	}

	public void add(int Index, int SoundID) {
		 available_sounds.add(Index);
		 resource_locator.put(Index, sound_pool.load(context, SoundID, 1));
	}
	public void release() {
		sound_pool.release();
	}
	public SoundPool get_sound_pool() {
		return sound_pool;
	}
	public void clear_streams() { // to use when playing a melody.
		streams.clear();
	}
	public void play_multi(int[] indexes) { // use this when playing a chord.
		streams.clear();
		for (int i=0; i < indexes.length; i++) {
			play(indexes[i]);
		}
		
		
	}
	public int play(int index) { 
		if (!prefs.get_mute_sound()) {
			 if(available_sounds.contains(index)){
				 if (loader.check_loaded(index)) {
					 int returner = -1;
				     streams.add(returner = sound_pool.play(resource_locator.get(index), 0.99f, 0.99f, 1, 0, 1f));
				     return returner;
				 } else {			
					 loader.play_on_load(index);
					 return -1;
				 } 
			 }
		}
		return -1;
	}
	public void stop_all() {
		Iterator<Integer> iterate_streams = streams.iterator();
		while (iterate_streams.hasNext()) {
			sound_pool.stop(iterate_streams.next());

		}
	}
	public void stop(int stream_id) {
		sound_pool.stop(stream_id);
	}
	
	private class On_Load implements OnLoadCompleteListener {
		LinkedList<Integer> play_on_load;
		boolean[] loaded;
		public On_Load(int num_sounds) {
			loaded = new boolean[num_sounds];
			play_on_load = new LinkedList<Integer>();
		}
		public void onLoadComplete(SoundPool sound_pool, int index, int status) {
			loaded[index] = true;
			Iterator<Integer> to_play = play_on_load.iterator();
			int counter = 0;
			while (to_play.hasNext()) {
				if (to_play.next() == index) {
				     streams.add(sound_pool.play(resource_locator.get(index), 0.99f, 0.99f, 1, 0, 1f));
				     play_on_load.remove(counter);
				     break;
				}
				counter++;
			}
		}
		public boolean check_loaded(int index) {
			if (index > -1 && index < loaded.length) {
				return loaded[index];
			} else {
				return false;
			}
		}
		public void play_on_load(int index) {
			play_on_load.add(index);
		}
	}
}