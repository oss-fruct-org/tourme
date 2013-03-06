package org.fruct.oss.tourme;


import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MapActivity extends FragmentActivity implements
		ActionBar.OnNavigationListener {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	private MapController mapController;
    private MapView mapView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		// Show the Up button in the action bar.
		actionBar.setDisplayHomeAsUpEnabled(true);

		// Set up the dropdown list navigation in the action bar.
		actionBar.setListNavigationCallbacks(
		// Specify a SpinnerAdapter to populate the dropdown list.
				new ArrayAdapter<String>(getActionBarThemedContextCompat(),
						android.R.layout.simple_list_item_1,
						android.R.id.text1, new String[] {
							getString(R.string.actionbar_main),
							getString(R.string.actionbar_map),
							getString(R.string.actionbar_nearby),
							getString(R.string.actionbar_favour),
							getString(R.string.actionbar_log) }), this);
		
		actionBar.setSelectedNavigationItem(1);
		
		this.initMap();
	}

	private void initMap() {

        MapView mapView = new MapView(this, 256); //constructor

        mapView.setClickable(true);

        mapView.setBuiltInZoomControls(true);

        setContentView(mapView); //displaying the MapView

        mapView.getController().setZoom(12); //set initial zoom-level, depends on your need

        mapView.getController().setCenter(new GeoPoint(61.800322,34.320819)); //This point is in Enschede, Netherlands. You should select a point in your map or get it from user's location.

        mapView.setUseDataConnection(false); //keeps the mapView from loading online tiles using network connection.
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
		getMenuInflater().inflate(R.menu.activity_map, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		Intent intent = null;
    	
		// I know, it's kinda bicycle, but I dunno how to do better
		switch(position) {
			// Goto Main
			case(0):
				intent = new Intent (this, MainActivity.class);
				break;
			// Goto Map
			case(1):
				//intent = new Intent (this, MapActivity.class);
				break;
			// Goto Nearby
			case(2):
				intent = new Intent (this, NearbyActivity.class);
				break;
			// Goto Favourites
			case(3):
				intent = new Intent (this, FavourActivity.class);
				break;
			// Goto Travel Log
			case(4):
				intent = new Intent (this, TravellogActivity.class);
				break;
			default:
				Toast.makeText(getApplicationContext(), "Test", Toast.LENGTH_LONG).show();
				break;
		}
		    	
    	if (intent != null) {
    		startActivity(intent);    
    		finish();
    	}
		
		return true;
	}
		

}
