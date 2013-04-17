package org.fruct.oss.tourme;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class YandexPoints extends AsyncTask<String, Void, String> {
	
	public static Context cont = MainActivity.context;

	private String url = null;

	//private List<PointInfo> points[]; // List of downloaded points

	public YandexPoints(String query, int resultsCount) {
		// Prepare url and execute downloading and parsing methods
		try {
			query = URLEncoder.encode(query, "utf-8");
		} catch (UnsupportedEncodingException e) {
			Log.e("YandexPoints err:", e.toString());
		}
		
		this.url = "http://psearch-maps.yandex.ru/1.x/" + "?text=" + query
				+ "&format=json&results=" + resultsCount
				+ "&key=AFuBw04BAAAAMTvVXAIAPh1FQcViqX_YMyHSr38laE_"
				+ "zr6YAAAAAAAAAAACv_CRUIU-VJsqPpO-ArbvLPAIs4Q==";
	}
	
	// return downloaded points
	public List<PointInfo>[] getPoints() {
		//return this.points;
		return null; // Have a nice debug!
	}
	
	// Small class for points info
		public class PointInfo {
			public String name;
			public String lon;
			public String lat;
			public String info;
		}
	
	// Download and save in cache JSON file
	@Override
	protected String doInBackground(String... urls) {
		try {
			//URL url = new URL(urls[0]);
			URL url = new URL(this.url);
			Log.e("URL", this.url);

			URLConnection connection = url.openConnection();
			connection.connect();

			// Get cache dir (try to use external memory, if n/a, use
			// internal)
			File cacheDir = cont.getExternalCacheDir();

			if (cacheDir == null) {
				Log.v("EXT_STORAGE", "n/a");
				cacheDir = cont.getCacheDir();
			}

			File cacheFile = new File(cacheDir, "ya.json");

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
		List<PointInfo> pointz = this.openAndParse();
		// Make tricks with UI
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
			File cacheFile = new File(cacheDir, "ya.json");
			reader = new BufferedReader(new FileReader(cacheFile));
			for (String line = null; (line = reader.readLine()) != null;) {
				builder.append(line).append("\n");
			}
		} catch (Exception e) {
			Log.e("file", "can't open or read file");
		}
		String myJsonString = builder.toString();

		// JSON parsing
		JSONObject object;
		try {
			object = new JSONObject(myJsonString);
			JSONObject GeoObjectCollection = object.getJSONObject(
					"response").getJSONObject("GeoObjectCollection");
			JSONArray elementsArray = GeoObjectCollection
					.getJSONArray("featureMember");

			int len = elementsArray.length();
			JSONObject temp = null;
			String[] coord;
			String coordinates = null;
			

			// Get all points info and write to PointInfo list
			for (int i = 0; i < len; i++) {
				temp = elementsArray.getJSONObject(i);
				PointInfo point = new PointInfo();
				point.name = temp.getJSONObject("GeoObject").getString(
						"name");
				coordinates = temp.getJSONObject("GeoObject")
						.getJSONObject("Point").getString("pos");
				coord = coordinates.split(" ");
				point.lat = coord[1];
				point.lon = coord[0];
				Log.e("i", "" + coord[1]);
				points.add(point);
				//add(point);
			}
		} catch (JSONException e) {
			Log.e("file", "parse error");
		}
		
		return points;
	}

}
