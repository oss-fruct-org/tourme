package org.fruct.oss.tourme;

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
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.LayoutParams;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ListView;
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
	
/*	// FIXME: you should always have array with drawable's Ids
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
				Intent intent = MainActivity.drawerItemSwitch(position);
				if (intent != null) 
					startActivity(intent);	
			}
		});

	    ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
	    ImageAdapter adapter = new ImageAdapter(this);
	    viewPager.setAdapter(adapter);
		
	}
	
	/**
	 * Image adapter for gallery at main screen
	 * @author alexander
	 * TODO: autorotate
	 */
	public class ImageAdapter extends PagerAdapter {
		Context context;
		private int[] GalImages = new int[] { R.drawable.one, R.drawable.two,
				R.drawable.three };

		ImageAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {
			return GalImages.length;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == ((ImageView) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView imageView = new ImageView(context);
			imageView.setPadding(0, 0, 0, 0);
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setImageResource(GalImages[position]);
			((ViewPager) container).addView(imageView, 0);
			return imageView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((ImageView) object);
		}
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
/*	
	public class SlidesFragmentAdapter extends FragmentPagerAdapter
			implements IconPagerAdapter {

		private int[] images = new int[] { R.drawable.ic_launcher,
				R.drawable.ab_transparent_tourme, R.drawable.activity_bck
		};

		protected final int[] ICONS = new int[] { R.drawable.ab_bottom_solid_tourme,
				R.drawable.ab_bottom_solid_tourme, R.drawable.ab_bottom_solid_tourme};

		private int mCount = images.length;

		public SlidesFragmentAdapter(FragmentManager fm) { // TODO
			super(fm);
		}

		@Override
		public Fragment getItem(int position) { // TODO
			
			return new SlideFragment(images[position]);
		}

		@Override
		public int getCount() {
			return mCount;
		}

		@Override
		public int getIconResId(int index) {
			return ICONS[index % ICONS.length];
		}

		public void setCount(int count) {
			if (count > 0 && count <= 10) {
				mCount = count;
				notifyDataSetChanged();
			}
		}
	}*/
	

	

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
