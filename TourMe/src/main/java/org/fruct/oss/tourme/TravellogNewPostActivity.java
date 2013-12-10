package org.fruct.oss.tourme;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Button;
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

        dbHelper = new DBHelper(this);

        Intent fromActivity = getIntent();
        if (fromActivity.getBooleanExtra("show", false)) {
            ((EditText)findViewById(R.id.travellog_edit_text)).setText(fromActivity.getStringExtra("text"));
            (findViewById(R.id.travellog_detect_location_btn)).setVisibility(View.GONE);
            (findViewById(R.id.travellog_location_title)).setVisibility(View.GONE);

            String locationDescription = fromActivity.getStringExtra("locationDescription");
            if (locationDescription.length() != 0)
                ((TextView) findViewById(R.id.travellog_detect_location_txt)).setText(locationDescription);
            else
                ((TextView)findViewById(R.id.travellog_detect_location_txt)).setText(fromActivity.getStringExtra("location"));
            return;
        }

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

			/*if (postText.length() == 0) {
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.travellog_no_text), Toast.LENGTH_SHORT).show();
				return true;
			}*/

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();

            cv.put("name", postText);
            cv.put("longitude", locationLongitude);
            cv.put("latitude", locationLatitude);
            cv.put("image", "uri"); // TODO: apply image
            cv.put("location", "");

            // Try to put pretty look location
            try {
                TourMeGeocoder geocoder = new TourMeGeocoder(getApplicationContext(), Double.parseDouble(locationLatitude), Double.parseDouble(locationLongitude));
                cv.put("location", geocoder.getCity() + ", " + geocoder.getRegion());
            } catch (Exception e) {
                e.printStackTrace();
            }

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

	
