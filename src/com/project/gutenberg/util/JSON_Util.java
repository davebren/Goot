package com.project.gutenberg.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

public class JSON_Util {
	public static String inputstream_toString(InputStream is) {
		String result = "";
		try{
	        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
	        StringBuilder sb = new StringBuilder();
	        String line = null;
	        while ((line = reader.readLine()) != null) {
	                sb.append(line);
	        }
	        is.close();
	 
	        result=sb.toString();
	        return result;
	        
		}catch(Exception e){
	        Log.e("log_tag", "Error converting result "+e.toString());
	        return null;
		}
	}
	public static JSONArray httpresponse_to_jsonarray(HttpResponse response) throws IllegalStateException, JSONException, IOException {
        Log.d("before entity", "");

		
        HttpEntity entity = response.getEntity();
        Log.d("before content", "");
        InputStream is = entity.getContent();
        String result = inputstream_toString(is);
        //Log.d("json", inputstream_toString(is));
        //JSONArray json = new JSONArray(result);
        return new JSONArray(result);
        
	}

	
}
