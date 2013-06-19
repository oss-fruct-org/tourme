package org.fruct.oss.tourme;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;


public class MainActivity extends FragmentActivity implements
		ActionBar.OnNavigationListener, ViewFactory {

	public static Context context = null;
	
	private ListView drawer;
	ActionBarDrawerToggle drawerToggle;
	DrawerLayout drawerLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment_container);
		context = getApplicationContext();
		
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create an instance of ExampleFragment
            HomeFragment firstFragment = new HomeFragment();

            // In case this activity was started with special instructions from an Intent,
            // pass the Intent's extras to the fragment as arguments
            //firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }
		
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerToggle = new ActionBarDrawerToggle (this,	drawerLayout, R.drawable.ic_drawer,
				R.string.drawer_open, R.string.drawer_close) {
			// Do nothing with title
		};
		
		drawerLayout.setDrawerListener(drawerToggle);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		
		// TODO: open drawer at app first open
		
		// Fill in the drawer by string array
		drawer = (ListView) findViewById(R.id.left_drawer);
		String[] drawerItems = getResources().getStringArray(R.array.drawer_items);
		drawer.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, drawerItems));
		// TODO: add 3 more elems (settings etc) and make them look different
		drawer.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				drawerItemSwitch(position);
				//Intent intent = MainActivity.drawerItemSwitch(position);
				//if (intent != null)
				//	startActivity(intent);
			}
		});
	}
	
	
	/**
	 * @author alexander
	 * @param id - number in drawer (clicked)
	 * @return intent to start activity
	 * 
	 */
	public void drawerItemSwitch(int id) {
		Intent intent = null;
		FragmentManager fm = null;
		Fragment f = null;
		FragmentTransaction ft = null;
		
		// I know, it's kinda bicycle, but I dunno how to do better
		switch(id) {
			// Goto Main
			case(0):
				f = new HomeFragment();
				Log.e("Fragment", "home");
				//intent = new Intent(context, MainActivity.class);
				break;
			case(1):
				Toast.makeText(context, "I told you to FIX that!..", Toast.LENGTH_SHORT).show(); // TODO
				break;
			case(2):
				intent = new Intent(context, NearbyActivity.class);
				break;
			case(3):
				Log.e("Fragment", "map");
				f = new MapFragment();							
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
		
		fm = getFragmentManager();
		ft = fm.beginTransaction().replace(R.id.fragment_container, f);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.commit();
		drawerLayout.closeDrawers();
		
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

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		//outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
		//		.getSelectedNavigationIndex());
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
		
		switch(item.getItemId()) {
			case(R.id.menu_map):
				//intent = new Intent(getApplicationContext(), MapActivity.class);
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
		return null;
	}
	
	public void openTravelpedia(View view) {
		drawerItemSwitch(1);
	}
	
	public void openNearby(View view) {
		drawerItemSwitch(2);
	}
	public void openPracticalInfo(View view) {
		drawerItemSwitch(4);
	}
	public void openPhrasebook(View view) {
		Toast.makeText(context, "Nothing implemented yet", Toast.LENGTH_SHORT).show();
	}
	


}
