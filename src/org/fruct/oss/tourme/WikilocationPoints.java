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

	private String url = null;

	public WikilocationPoints(float latitude, float longitude, int resultsCount,
			int radius, String locale) {
		
		// TODO: locations find
		// TODO: more parameters to serve: especially type, title
		
		Uri.Builder b = Uri.parse("http://api.wikilocation.org/articles").buildUpon();
		b.appendQueryParameter("lat", String.valueOf(latitude));
		b.appendQueryParameter("lng", String.valueOf(longitude));
		b.appendQueryParameter("limit", String.valueOf(resultsCount));
		b.appendQueryParameter("radius", String.valueOf(radius));
		b.appendQueryParameter("locale", locale);
		b.appendQueryParameter("format", "json");
		this.url = b.build().toString();
	}
	
	// Download and save in cache JSON file
	@Override
	protected String doInBackground(String... urls) {
		try {
			URL url = new URL(this.url);

			URLConnection connection = url.openConnection();
			connection.connect();

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
			InputStream input = new BufferedInputStream(url.openStream());
			OutputStream output = new BufferedOutputStream(
					new FileOutputStream(cacheFile));

			byte data[] = new byte[1024];
			int count;

			// Save file
			while ((count = input.read(data)) != -1) {
				output.write(data, 0, count);
			}

			output.flush();
			output.close();
			input.close();
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
			reader = new BufferedReader(new FileReader(cacheFile)); // TODO: close the reader?
			for (String line = null; (line = reader.readLine()) != null;) {
				builder.append(line).append("\n");
			}
			reader.close();
		} catch (Exception e) {
			Log.e("file", "can't open or read file");
		}
		String myJsonString = builder.toString();
		
		// JSON parsing
		JSONObject object;
		try {
			object = new JSONObject(myJsonString);
			//JSONObject articles = object.getJSONObject("articles");
			JSONArray elementsArray = object.getJSONArray("articles");

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
		} catch (JSONException e) {
			Log.e("file", "parse error"+e.toString());
		}
		
		return points;
	}

}
