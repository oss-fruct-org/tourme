package org.fruct.oss.tourme;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ViewSwitcher.ViewFactory;
import com.readystatesoftware.systembartint.SystemBarTintManager;


public class MainActivity extends FragmentActivity implements
		ActionBar.OnNavigationListener, ViewFactory, SwipeRefreshLayout.OnRefreshListener {

	public static Context context = null;

    public static Boolean firstLaunch = true;

	static SharedPreferences sh;
	SharedPreferences.Editor ed;
	
	public static LocationManager mLocationManager;
	public static Double currentLatitude = 0d;
	public static Double currentLongitude = 0d;

    SwipeRefreshLayout swipeLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment_container);
		context = getApplicationContext();

        ActionBar actionBar = getActionBar();

        if (actionBar != null) {
            actionBar.hide();
        }

        // Enable translucent statusbar for KitKat [and later]
        if (Build.VERSION.SDK_INT >= 19 ){
            Window w = getWindow();
            w.setFlags(67108864, 67108864);

            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setTintColor(getResources().getColor(R.color.main_turquoise));
            tintManager.setStatusBarAlpha(0);
        }

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        FragmentMap map = new FragmentMap();

        getFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, new FragmentGallery(), "fragment_gallery")
                .add(R.id.fragment_container, new FragmentWeather(), "fragment_weather")
                .add(R.id.fragment_container, new FragmentCurrency(), "fragment_currency")
                .add(R.id.fragment_container, new FragmentPhrase(), "fragment_phrase")
                .add(R.id.fragment_container, new FragmentCountry(), "fragment_country")
                //.add(R.id.fragment_container, map, "fragment_map")
                .add(R.id.fragment_container, new FragmentNearby(), "fragment_nearby")
                .add(R.id.fragment_container, new FragmentTravellog(), "fragment_travellog")
                .commit();

        //map.setMapViewToFullScreen();

		sh = getSharedPreferences(ConstantsAndTools.SHARED_PREFERENCES, 0);

		if (context != null) {
			mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 50, mLocationListener); // 1 min update, 100 km
		    
		    Location lastLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		    if (lastLocation != null) {
			    currentLongitude = lastLocation.getLongitude();
			    currentLatitude = lastLocation.getLatitude();
		    }
		    
		}
	}

    // Swipe layout updater
    @Override
    public void onRefresh() {
        FragmentNearby fragment = (FragmentNearby)(getFragmentManager().findFragmentByTag("fragment_nearby"));
        if (fragment != null)
            fragment.update();

        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                swipeLayout.setRefreshing(false);
            }
        }, 5000);
    }
	
	public final LocationListener mLocationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			currentLongitude = location.getLongitude();
			currentLatitude = location.getLatitude();
			Log.e("Current location is:", currentLongitude + " - " + currentLatitude);
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.e("status", provider + " " + status + extras);
		}
	};

	
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
    }

    
    protected void onNewIntent(Intent intent) {
    	super.onNewIntent(intent);
    	
    	// General way of interaction between ArticleActivity and Map:
    	// ArticleActivity (intent+bundle) -> MainActivity onNewIntent(bundle) -> MapFragment onStart (set[get]Arguments+bundle)
    	
    	// Get data if it has been passed from NearbyActivity (by button 'show on map')
    	if (intent.getExtras() != null && intent.getExtras().containsKey(ConstantsAndTools.ARTICLE_COORDINATES)) {
			Bundle articleInfo = intent.getBundleExtra(ConstantsAndTools.ARTICLE_COORDINATES);
			Fragment mapFragment = new FragmentMap();
			mapFragment.setArguments(articleInfo);
			getFragmentManager().beginTransaction().replace(R.id.fragment_container, mapFragment).commit();
		}

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//getMenuInflater().inflate(R.menu.activity_main, menu);		
		//return super.onCreateOptionsMenu(menu);
		
		return false;
	}

	

	// ViewSwitcher
	@Override
	public View makeView() {

		return null;
	}

	// No actionbar
	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		return false;
	}


}
