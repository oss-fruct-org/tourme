package org.fruct.oss.tourme;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Get array with Wikipedia articles about N points for coordinates
 * in radius for locale * 
 * @author alexander
 *
 */
public class WikilocationPoints extends AsyncTask<String, Void, String> {
	
	public static Context cont = MainActivity.context;
	
	private double longitude, latitude;
	private int resultsCount, radius;
	private String locale;

	private Uri buildUri (int offset) {
		
		// TODO: locations find
		// TODO: more parameters to serve: especially type, title		
		
		Uri.Builder b = Uri.parse("http://api.wikilocation.org/articles").buildUpon();
		b.appendQueryParameter("lat", String.valueOf(this.latitude));
		b.appendQueryParameter("lng", String.valueOf(this.longitude));
		b.appendQueryParameter("limit", String.valueOf(this.resultsCount));
		b.appendQueryParameter("radius", String.valueOf(this.radius));
		b.appendQueryParameter("locale", this.locale);
		b.appendQueryParameter("format", "json");
		b.appendQueryParameter("offset", String.valueOf(offset));
		//this.url = b.build().toString();
		
		return b.build();
	}

	public WikilocationPoints(double longitude, double latitude, int resultsCount,
			int radius, String locale) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.resultsCount = resultsCount;
		this.radius = radius;
		this.locale = locale;
	}
	
	// Download and save in cache JSON file
	@Override
	protected String doInBackground(String... urls) {
		try {
			
			// TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO
			// TODO: IS IT NECESSARY TO STORE THIS IN DISK AND NOT IN RAM?
			// TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO
			
			// Get cache dir (try to use external memory, if n/a, use
			// internal)
			File cacheDir = cont.getExternalCacheDir();

			if (cacheDir == null) {
				Log.v("EXT_STORAGE", "n/a");
				cacheDir = cont.getCacheDir();
			}

			File cacheFile = new File(cacheDir, "wl.json");

			// Download the file
			
			OutputStream output = new BufferedOutputStream(new FileOutputStream(cacheFile));
			
			for (int offset = 0; offset < ConstantsAndTools.ARTICLES_AMOUNT; offset += ConstantsAndTools.ARTICLES_MAXIMUM_PER_TIME) {
				Uri tempUri = this.buildUri(offset);
				
				String stringUrl = tempUri.toString();
				
				URL url = new URL(stringUrl);
				URLConnection connection = url.openConnection();
				connection.connect();
			
				InputStream input = new BufferedInputStream(url.openStream());				
	
				byte data[] = new byte[1024];
				int count;
	
				// Save file
				while ((count = input.read(data)) != -1) {
					output.write(data, 0, count);
				}
				output.write(",".getBytes()); // Multiple JSONs in one file will be separated by comma
				
				input.close();
			}			

			output.flush();
			output.close();
			
		} catch (Exception e) {
			Log.e("ERROR downloading file", e.getMessage());
		}

		return null;
	}

	// Parse JSON file
	@Override
	public void onPostExecute(String result) {
		// Make here tricks with UI (after class extending)
	}
	
	protected ArrayList<PointInfo> openAndParse() {
		
		ArrayList<PointInfo> points = new ArrayList<PointInfo>();

		// Get cache file (try to get from external storage)
		File cacheDir = cont.getExternalCacheDir();

		if (cacheDir == null) {
			Log.i("EXT_STORAGE", "n/a");
			cacheDir = cont.getCacheDir();
		}

		// Set bufferedReader
		BufferedReader reader = null;
		StringBuilder builder = new StringBuilder();
		
		// File reading
		try {
			File cacheFile = new File(cacheDir, "wl.json");
			reader = new BufferedReader(new FileReader(cacheFile));
			
			for (String line = null; (line = reader.readLine()) != null;)
				builder.append(line).append("\n");
			
			reader.close();
			
		} catch (Exception e) {
			Log.e("file", "can't open or read file");
		}
		
		String myJsonString = builder.toString();

		
		try {		
			// JSON parsing
			JSONArray globalArray;
			
			// FIXME\KILLME SOMEBODY PLEAAAAAASE
			try {
				String fixedString = "[" + myJsonString.substring(0, myJsonString.length()-2) + "]"; // For valid JSON; Viva la wheel reinventing!
				globalArray = new JSONArray(fixedString);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			
			// Iterate through all of received 
			for (int j = 0; j < globalArray.length(); j++) {
			
				//JSONObject articles = object.getJSONObject("articles");
				JSONArray elementsArray = globalArray.getJSONObject(j).getJSONArray("articles");
	
				int len = elementsArray.length();
				JSONObject temp = null;
	
				// TODO: try catch here
				
				// Get all points info and write to PointInfo list
				for (int i = 0; i < len; i++) {
					temp = elementsArray.getJSONObject(i);
					
					//if (!temp.getString("type").equals("")) { // TODO: filter the sh*t; "" - not work!
						PointInfo point = new PointInfo();
						point.type = temp.getString("type");
						point.title = temp.getString("title");
						point.url = temp.getString("url");
						point.mobileurl= temp.getString("mobileurl");
						point.distance = temp.getString("distance");
						point.latitude = temp.getString("lat");
						point.longitude = temp.getString("lng");
					
						points.add(point);
					//}
				}
			}
		} catch (JSONException e) {
			Log.e("file", "parse error"+e.toString());
		}
		
		return points;
	}

}
