package com.project.gutenberg.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;


import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import com.project.gutenberg.SharedPrefs;

public class SoundManager {
	

private  SoundPool soundPool;
private  HashMap<Integer, Integer> resourceLocator;
private  Context context;
private  Vector<Integer> availableSounds = new Vector<Integer>();
private  On_Load loader;
private LinkedList<Integer> streams;
public boolean initialized;
private SharedPrefs prefs;

private final int NUM_SOUNDS = 28;

	public SoundManager(Context context){
		 this.context = context;
		 prefs = new SharedPrefs(context);
	     soundPool = new SoundPool(NUM_SOUNDS, AudioManager.STREAM_MUSIC, 0);
	     resourceLocator = new HashMap<Integer, Integer>();
	     loader = new On_Load(NUM_SOUNDS);
	     soundPool.setOnLoadCompleteListener(loader);
	     streams = new LinkedList<Integer>();
	}


	public void initialize() {
		//add(1, R.raw.ding);
		//add(2, R.raw.buzzer);

		initialized = true;
	}

	public void add(int Index, int SoundID) {
		 availableSounds.add(Index);
		 resourceLocator.put(Index, soundPool.load(context, SoundID, 1));
	}
	public void release() {
		soundPool.release();
	}
	public SoundPool get_sound_pool() {
		return soundPool;
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
		if (!prefs.getMuteSound()) {
			 if(availableSounds.contains(index)){
				 if (loader.check_loaded(index)) {
					 int returner = -1;
				     streams.add(returner = soundPool.play(resourceLocator.get(index), 0.99f, 0.99f, 1, 0, 1f));
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
			soundPool.stop(iterate_streams.next());

		}
	}
	public void stop(int stream_id) {
		soundPool.stop(stream_id);
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
				     streams.add(sound_pool.play(resourceLocator.get(index), 0.99f, 0.99f, 1, 0, 1f));
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