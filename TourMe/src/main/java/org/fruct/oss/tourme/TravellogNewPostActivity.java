package org.fruct.oss.tourme;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


public class TravellogNewPostActivity extends FragmentActivity {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	
	private static Double lat = 0d;
	private static Double lon = 0d;

    String text, locationLongitude, locationLatitude, imageUri;

    private DBHelper dbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_travellog_new_post);
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		// If social network(s) connected, hide the warn message
		/*SharedPreferences settings = getSharedPreferences(ConstantsAndTools.SHARED_PREFERENCES, 0);
		if (settings.getBoolean(ConstantsAndTools.SOCICAL_NETWORKS_CONNECTED, false)) {
			TextView socialNetworksWarn = (TextView) findViewById(R.id.socical_networks_warn);
			socialNetworksWarn.setVisibility(View.GONE);
		}*/

        dbHelper = new DBHelper(this);

        setLocationVariables();

		ImageButton findLocation = (ImageButton) findViewById(R.id.travellog_detect_location_btn);
		findLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
                setLocationVariables();
			}			
		});
	}

    /**
     * Set location variables for current post
     *
     */
    private void setLocationVariables() {
        lat = MainActivity.currentLatitude;
        lon = MainActivity.currentLongitude;
        if (lat != 0 || lon != 0) {
            locationLatitude = lat.toString().substring(0,5);
            locationLongitude = lon.toString().substring(0,5);

            TextView locationText = (TextView) findViewById(R.id.travellog_detect_location_txt);
            locationText.setText("OK! " + locationLatitude + " " + locationLongitude);
        }
    }


	/**
	 * Backward-compatible version of {@link ActionBar#getThemedContext()} that
	 * simply returns the {@link android.app.Activity} if
	 * <code>getThemedContext</code> is unavailable.
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private Context getActionBarThemedContextCompat() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return getActionBar().getThemedContext();
		} else {
			return this;
		}
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_travellog_new_post, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			//NavUtils.navigateUpFromSameTask(this);
			finish();
			return true;
		case (R.id.travellog_post_send):
			EditText post = (EditText) findViewById(R.id.travellog_edit_text);
            String postText = post.getText().toString();

			if (postText.length() == 0) {
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.travellog_no_text), Toast.LENGTH_SHORT).show();
				return true;
			}

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();

            cv.put("name", postText);
            cv.put("longitude", locationLongitude);
            cv.put("latitude", locationLatitude);
            cv.put("image", "uri"); // TODO: apply image

            Time now = new Time();
            now.setToNow();

            cv.put("date", now.format("%d/%m/%Y").toString());

            // Insert record to database
            if (db != null) {
                long rowID = db.insert(ConstantsAndTools.TABLE_TRAVELLOG, null, cv);
                Log.d("db", "row inserted, ID = " + rowID);

                // Save and close activity
                if (rowID >= 0) {
                    dbHelper.close();
                    finish();
                }
            }

            // Don't close the activity
            dbHelper.close();

			break;
		
		}
		
		return super.onOptionsItemSelected(item);
	}



	
}

	
