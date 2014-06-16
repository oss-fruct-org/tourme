package org.fruct.oss.tourme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Get array with Wikipedia articles about N points for coordinates
 * in radius for locale * 
 * @author alexander
 *
 */
public class WikilocationPoints extends AsyncTask<String, Integer, String> {
	
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

            this.cursor = null;

            if (lat != 0) {
                // Location correction
                double latitudeCorrection = ConstantsAndTools.getDegreesForKilometersLatitude(10);
                double longitudeCorrection = ConstantsAndTools.getDegreesForKilometersLatitude(10);

                where = "latitude < " + Double.toString(lat + latitudeCorrection) +
                        " and latitude > " + Double.toString(lat - latitudeCorrection) +
                        " and longitude < " + Double.toString(lon + latitudeCorrection) +
                        " and longitude > " + Double.toString(lon - latitudeCorrection);
                Log.d("tourme wiki original:", latitude + " " + longitude);
                Log.d("tourme wiki", where);
            }

            String[] columns = new String[] {"latitude", "longitude", "name", "url", "type", "distance"};
            this.cursor = db.query(true, ConstantsAndTools.TABLE_WIKIARTICLES, columns, where, null, null, null, null, null); // FIXME not distinct, filter in wiki class

            if (this.cursor.getCount() != 0) {
                Log.d("tourme wiki", "cursor is not null " + cursor.getColumnCount() + " - " + cursor.toString());
                return null;
            } else {
                Log.d("tourme wiki", "cursor is null");
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

                int cnt = 0;
                // Get
                try {

                    BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        publishProgress(++cnt);
                    }

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

                        publishProgress(++cnt);
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
                        this.cursor = db.query(true, ConstantsAndTools.TABLE_WIKIARTICLES, columns, where, null, null, null, null, null); // FIXME not distinct, filter in wiki class

                        /*if (this.cursor.getCount() != 0) {
                            Log.d("tourme wiki", "cursor NOW is not null");
                        } else {
                            Log.d("tourme wiki", "cursor STILL is null");
                        }*/
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

    @Override
    protected void onProgressUpdate(Integer... value) {
        super.onProgressUpdate(value);
        // Do things when progress updates (task is running)

    }

	// Parse JSON file
	@Override
	public void onPostExecute(String result) {
		// Make here tricks with UI (after class extending)
	}
}
