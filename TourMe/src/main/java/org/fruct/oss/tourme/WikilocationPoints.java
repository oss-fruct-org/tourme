package org.fruct.oss.tourme;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.JsonReader;
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

    private DBHelper dbHelper;

    public Cursor cursor = null; // Cursor to existing items (if items exist)

	private Uri buildUri (int offset) {
		
		Uri.Builder b = Uri.parse("http://api.wikilocation.org/articles").buildUpon();
		b.appendQueryParameter("lat", String.valueOf(this.latitude));
		b.appendQueryParameter("lng", String.valueOf(this.longitude));
		b.appendQueryParameter("limit", String.valueOf(this.resultsCount));
		b.appendQueryParameter("radius", String.valueOf(this.radius));
		b.appendQueryParameter("locale", this.locale);
		b.appendQueryParameter("format", "json");
		b.appendQueryParameter("offset", String.valueOf(offset));

		return b.build();
	}

	public WikilocationPoints(Context context, double longitude, double latitude, int resultsCount,
			int radius, String locale) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.resultsCount = resultsCount;
		this.radius = radius;
		this.locale = locale;

        this.dbHelper = new DBHelper(context);
	}
	
	// Download and save in database
	@Override
	protected String doInBackground(String... urls) {
		try {
            if (isCancelled()) {
                return null;
            }

            // Check for point in area
            DBHelper dbHelper = new DBHelper(cont);
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            String where = "";
            Double lat = MainActivity.currentLatitude;
            Double lon = MainActivity.currentLongitude;
            if (lat != 0) {
                where = "latitude < " + Double.toString(lat - 0.5d) +
                        " and latitude > " + Double.toString(lat + 0.5d) + // TODO: degree depend on location
                        " and longitude < " + Double.toString(lon - 0.5) +
                        " and longitude > " + Double.toString(lon + 0.5d);
            }

            String[] columns = new String[] {"latitude", "longitude", "name", "url", "type", "distance"};
            this.cursor = db.query(true, ConstantsAndTools.TABLE_WIKIARTICLES, columns, null, null, null, null, null, null); // FIXME not distinct, filter in wiki class

            if (this.cursor.getCount() != 0) {
                Log.e("tourme", "cursor not null " + cursor.getColumnCount() + " - " + cursor.toString());
                return null;
            } else {
                Log.e("tourme", "cursor is null");
            }


            /*
             * Perform this actions if there are no points for location
             */
            db = dbHelper.getWritableDatabase();

			for (int offset = 0; offset < ConstantsAndTools.ARTICLES_AMOUNT; offset += ConstantsAndTools.ARTICLES_MAXIMUM_PER_TIME) {
				Uri tempUri = this.buildUri(offset);

				String stringUrl = tempUri.toString();
				
				URL url = new URL(stringUrl);

                InputStream input = new BufferedInputStream(url.openStream());
                StringBuilder sb = new StringBuilder();
                String line;

                String json;

                // Get
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
                    while ((line = reader.readLine()) != null)
                        sb.append(line);

                    json = sb.toString();

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

                // Parse
                try {
                    JSONArray elementsArray = new JSONObject(json).getJSONArray("articles");

                    // Check if there are no more points
                    if (elementsArray.length() == 0)
                        return null;

                    // Get all points
                    for (int i = 0; i < elementsArray.length(); i++) {
                        JSONObject temp = elementsArray.getJSONObject(i);
                        ContentValues cv = new ContentValues();

                        String title = temp.getString("title");

                        // Filter Wikipedia articles from bad words
                        if (!ConstantsAndTools.stringContainsItemFromList(title)) {
                            cv.put("service", "wiki");
                            cv.put("latitude", temp.getString("lat"));
                            cv.put("longitude", temp.getString("lng"));
                            cv.put("name", title);
                            cv.put("type", temp.getString("type"));
                            cv.put("url", temp.getString("mobileurl"));
                            cv.put("distance", temp.getString("distance"));
                            //cv.put("timestamp", Long.toString(System.currentTimeMillis() / 1000L)); // FIXME
                            long rowID = db.insert(ConstantsAndTools.TABLE_WIKIARTICLES, null, cv);
                            //Log.d("tourme", "row inserted, ID = " + rowID);
                        }
                        this.cursor = db.query(true, ConstantsAndTools.TABLE_WIKIARTICLES, columns, null, null, null, null, null, null); // FIXME not distinct, filter in wiki class

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
			}
        } catch (Exception e) {
			Log.e("tourme", e.getMessage());
		} finally {
            // TODO: delete duplicates in database
            //deleteDuplicates();
        }

        dbHelper.close();
        return null;
	}

    private void deleteDuplicates(SQLiteDatabase db) {
        // TODO
    }

	// Parse JSON file
	@Override
	public void onPostExecute(String result) {
		// Make here tricks with UI (after class extending)
	}
}
