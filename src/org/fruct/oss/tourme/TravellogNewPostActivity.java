package org.fruct.oss.tourme;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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
		
		ImageButton findLocation = (ImageButton) findViewById(R.id.travellog_detect_location_btn);
		findLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				lat = MainActivity.currentLatitude;
				lon = MainActivity.currentLongitude;
				if (lat !=0 || lon != 0) {
					TextView locationText = (TextView) findViewById(R.id.travellog_detect_location_txt);
					locationText.setText("OK! " + lat.toString().substring(0,5) + " " +lon.toString().substring(0,5));
					Log.e("loc", lat + "*" + lon);
				}
			}			
		});
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
			
			if (post.getText() == null) {
				// TODO
				Toast.makeText(getApplicationContext(), "Write your thougts about location, please", Toast.LENGTH_SHORT).show();
				return true;
			}
			String postText = post.getText().toString();
			Log.e("EditText", postText);
			
			
			// TODO
			break;
		
		}
		
		return super.onOptionsItemSelected(item);
	}

	
}

	
