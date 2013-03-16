package org.fruct.oss.tourme;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

public class MainActivity extends FragmentActivity implements
		ActionBar.OnNavigationListener, ViewFactory, OnGestureListener {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	
	ImageSwitcher slideshow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		// Set up the dropdown list navigation in the action bar.
		actionBar.setListNavigationCallbacks(
		// Specify a у SpinnerAdapter to populate the dropdown list.
				new ArrayAdapter<String>(getActionBarThemedContextCompat(),
						android.R.layout.simple_list_item_1,
						android.R.id.text1, new String[] {
								getString(R.string.actionbar_main),
								getString(R.string.actionbar_map),
								getString(R.string.actionbar_nearby),
								getString(R.string.actionbar_favour),
								getString(R.string.actionbar_log) }), this);
	
		
		// TODO: remove this
		// An example to open ArticleActivity for an article
		// Use http:// for web source or file:// for local file
		//Intent myInt = new Intent(this, ArticleActivity.class);
		//myInt.putExtra(ConstantsAndTools.ARTICLE_ID, "http://wikipedia.org");
		//startActivity(myInt);
		
		// Slideshow		
		slideshow = (ImageSwitcher) findViewById(R.id.ImageSwitcher01);
		slideshow.setFactory(this);
        slideshow.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        slideshow.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
        slideshow.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher));
        
        /*GestureDetector gestureDetector = new GestureDetector(this);
        slideshow.setOnTouchListener(new View.OnTouchListener() {			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (gestureDetector.onTouchEvent)
				return false;
			}
		});*/
        
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
		getMenuInflater().inflate(R.menu.activity_main, menu);
		
		return true;
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		Intent intent = null;
    	
		// I know, it's kinda bicycle, but I dunno how to do better
		switch(position) {
			// Goto Main
			case(0):
				//intent = new Intent (this, MainActivity.class);
				break;
			// Goto Map
			case(1):
				intent = new Intent (this, MapActivity.class);
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
		    	
    	if (intent != null)
    		startActivity(intent);    
		
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = null;
		
		switch(item.getItemId()) {
			case(R.id.menu_map):
				intent = new Intent(getApplicationContext(), MapActivity.class);
				break;
			case(R.id.menu_favourites):
				intent = new Intent(getApplicationContext(), FavourActivity.class);
				break;
			case(R.id.menu_settings):
				//intent = new Intent(getApplicationContext(), SettingsActivity.class); // TODO
				break;
			case(R.id.menu_onoff_online_mode):
				// Turn on\off online mode (save to shared preferences)
				SharedPreferences settings = getSharedPreferences(ConstantsAndTools.SHARED_PREFERENCES, 0);
				SharedPreferences.Editor editor = settings.edit();
				
				// Check for current state and update
				if (settings.getBoolean("ONLINE_MODE", false) == true)		
					editor.putBoolean(ConstantsAndTools.ONLINE_MODE, false);
				else
					editor.putBoolean(ConstantsAndTools.ONLINE_MODE, true);
				
				editor.commit();
				break;
			default:
				break;
		}
		
		if (intent != null)
			startActivity(intent);
		
		return true;
		
	}

	@Override
	public View makeView() {
		// TODO Auto-generated method stub
		ImageView iView = new ImageView(this);
        iView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        iView.setLayoutParams(new 
                ImageSwitcher.LayoutParams(
                        LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
        iView.setBackgroundColor(0xFF000000);
        return iView;
		//return null;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}


}
