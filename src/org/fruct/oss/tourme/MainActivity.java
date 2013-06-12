package org.fruct.oss.tourme;

import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.LayoutParams;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

public class MainActivity extends FragmentActivity implements
		ActionBar.OnNavigationListener, ViewFactory {

	public static Context context = null;
	
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	
	/*ImageSwitcher slideshow;
	TextView caption;
	Runnable slideshowRunnable;
	private int slideshowIndex = 0; // ImageSwitcher current image Id from this array:
	// FIXME: you should always have array with drawable's Ids
	int[] images = {R.drawable.one, R.drawable.two, R.drawable.three}; // FIXME: add images dynamically
	String[] images_caption = {"Marble carrier", "Kizhi island", "Some cool stuff"}; // FIXME: add texts dynamically. MUST be exact size like 'images' array
	*/
	private ListView drawer;
	ActionBarDrawerToggle drawerToggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = getApplicationContext();		
		
		DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerToggle = new ActionBarDrawerToggle (this,	drawerLayout, R.drawable.ic_drawer,
				R.string.drawer_open, R.string.drawer_close) {
			// Do nothing with title
		};
		
		drawerLayout.setDrawerListener(drawerToggle);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		
		// Fill in the drawer by string array
		drawer = (ListView) findViewById(R.id.left_drawer);
		String[] drawerItems = getResources().getStringArray(R.array.drawer_items);
		drawer.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item,
				drawerItems));
		// TODO: make 3 last elements look different
		drawer.setOnItemClickListener(new ListView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Intent intent = MainActivity.drawerItemSwitch(position); // TODO
				if (intent != null) 
					startActivity(intent);	
			}
		});
		
		/*TestFragmentAdapter mAdapter;
	    ViewPager mPager;
	    PageIndicator mIndicator;
		
		mAdapter = new TestFragmentAdapter(getSupportFragmentManager());

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
		
		//Set the pager with an adapter
		 ViewPager pager = (ViewPager)findViewById(R.id.titles);
		 pager.setAdapter(new TestAdapter(getSupportFragmentManager()));
		
		 //Bind the title indicator to the adapter
		 TitlePageIndicator titleIndicator = (TitlePageIndicator)findViewById(R.id.titles);
		 titleIndicator.setViewPager(pager);*/
		
		// Slideshow		
		/*slideshow = (ImageSwitcher) findViewById(R.id.ImageSwitcher01);
		slideshow.setFactory(this);
        slideshow.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        slideshow.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
        slideshow.setImageDrawable(getResources().getDrawable(images[slideshowIndex]));
        caption = (TextView) findViewById(R.id.image_caption);
        caption.setText(images_caption[slideshowIndex]);
        
        // Change image by timeout
        slideshowRunnable = new Runnable() {
            //int i = 0;
            public void run() {
            	if (slideshowIndex == images.length-1)
            		slideshowIndex = 0;            	
            	else
            		slideshowIndex++;
            	
            	slideshow.setImageDrawable(getResources().getDrawable(images[slideshowIndex]));
            	caption.setText(images_caption[slideshowIndex]);
            	slideshow.postDelayed(this, 10000); // Slide after 10 secs
            }
        };
        
        slideshow.postDelayed(slideshowRunnable, 10000); // Start first slide after 10 secs
        
        // Set onFling listener (fling\swipe from right to left and vice versa)
        final GestureDetector gdt = new GestureDetector(new GestureListener());
        slideshow.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				gdt.onTouchEvent(event);
				return true;
			}			
		});*/
	}
	
	/**
	 * @author alexander
	 * @param id - number in drawer (clicked)
	 * @return intent to start activity
	 * 
	 */
	public static Intent drawerItemSwitch(int id) {
		Intent intent = null;
    	
		// I know, it's kinda bicycle, but I dunno how to do better
		switch(id) {
			// Goto Main
			case(0):
				intent = new Intent(context, MainActivity.class);
				break;
			case(1):
				Toast.makeText(context, "I told you to FIX that!..", Toast.LENGTH_SHORT).show(); // TODO
				break;
			case(2):
				intent = new Intent(context, NearbyActivity.class);
				break;
			case(3):
				intent = new Intent(context, MapActivity.class);
				break;
			case(4):
				Toast.makeText(context, "Opening practical info...", Toast.LENGTH_SHORT).show(); // TODO
				break;
			case(5):
				intent = new Intent(context, FavourActivity.class);
				break;
			case(6):
				intent = new Intent(context, TravellogActivity.class);
				break;
			case(7):
				Toast.makeText(context, "Switching to on[off]line mode...", Toast.LENGTH_SHORT).show(); // TODO
				break;
			case(8):
				Toast.makeText(context, "Going to plan new trip...", Toast.LENGTH_SHORT).show(); // TODO
				break;
			case(9):
				Toast.makeText(context, "Opening Settings...", Toast.LENGTH_SHORT).show(); // TODO:
			default:
				break;
		}
		
		return intent;
	}
	
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }
    
    

    /*private class GestureListener extends SimpleOnGestureListener {
    	private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    	
    	@Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
    		if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            	// Swipe right to left
            	slideshowIndex = slideshowIndex < images.length-1 ? ++slideshowIndex : 0;
            	//slideshow.setImageDrawable(getResources().getDrawable(getResources().getIdentifier(images[slideshowIndex], null, getPackageName())));
            	slideshow.setImageDrawable(getResources().getDrawable(images[slideshowIndex]));
            	caption.setText(images_caption[slideshowIndex]);
            	return false; 
            }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                // Swipe left to right
            	slideshowIndex = slideshowIndex > 0 ? --slideshowIndex : 0;
            	slideshow.setImageDrawable(getResources().getDrawable(images[slideshowIndex]));
            	caption.setText(images_caption[slideshowIndex]);
            	return false;
            }

            // Swiping top-bottom
            if(e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                // Swipe from bottom to top
            	// Hide buttons
            	ViewGroup buttons = (ViewGroup) getLayoutInflater().inflate(R.id.menu_list_layout, null);
            	buttons.setVisibility(View.VISIBLE);            	
            	return false;
            }  else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                // Swipe from top to bottom
            	// Show buttons
            	ViewGroup buttons = (ViewGroup) getLayoutInflater().inflate(R.id.menu_list_layout, null);
            	buttons.setVisibility(View.GONE);
            	return false;
            }
            return false;
        }
    }*/

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
		
		if (drawerToggle.onOptionsItemSelected(item)) {
	          return true;
	        }
	        // Handle your other action bar items...

	   //return super.onOptionsItemSelected(item);
		
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
			case (R.id.add_data):
				Intent myInt = new Intent(this, MapChooserActivity.class);
				startActivity(myInt);
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
	}
	
	public void openTravelpedia(View view) {
		Intent intent = new Intent(this, TravellogActivity.class);
	}
	
	public void openNearby(View view) {
		Intent intent = new Intent(this, NearbyActivity.class);
		startActivity(intent);
	}
	public void openPracticalInfo(View view) {
		//Intent intent = new Intent(this, TravellogActivity.class);
	}
	public void openPhrasebook(View view) {
		//Intent intent = new Intent(this, TravellogActivity.class);
	}
	


}
