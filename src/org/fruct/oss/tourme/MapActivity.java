package org.fruct.oss.tourme;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
//import org.osmdroid.views.MapController;
//import org.osmdroid.views.MapView;

public class MapActivity extends FragmentActivity implements
		ActionBar.OnNavigationListener {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	//private MapController mapController;
	//private MapView mapView;
	private ArrayList<Integer> selectedCategories;
	private Context cont;

	private WebView myWebView;
	
	private ListView drawer;
	ActionBarDrawerToggle drawerToggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		cont = this;

		this.initMap();

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

	
	public void addMarker(String group, String lat, String lon, String description) {
		// FIXME: group? What group? What will happen when group isn't exist?
		myWebView.loadUrl("javascript:addMarker('"+ group + "', " + lat + ", " + lon + ", '" + description +"');"); // TODO:  what if aposotrophe in name?
		Log.e("js", "javascript:addMarker("+ group + ", " + lat + ", " + lon + ", '" + description +"');");
	}


	@SuppressLint("JavascriptInterface")
	private void initMap() {
		String url = Environment.getExternalStorageDirectory()
				+ "/osmdroid/Mapnik";
		print(url);
		myWebView = (WebView) findViewById(R.id.mapview);
		myWebView.getSettings().setJavaScriptEnabled(true);
		myWebView.loadUrl("file:///android_asset/map.html");

		myWebView.addJavascriptInterface(this, "Android"); // TODO FIXME
		// url = "http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png";
		print(url);
		myWebView.loadUrl("javascript:setUrl(" + url + ")");

		/*
		 * MapView mapView = new MapView(this, 256); //constructor
		 * 
		 * mapView.setClickable(true);
		 * 
		 * mapView.setBuiltInZoomControls(true);
		 * 
		 * setContentView(mapView); //displaying the MapView
		 * 
		 * mapView.getController().setZoom(12); //set initial zoom-level,
		 * depends on your need
		 * 
		 * mapView.getController().setCenter(new GeoPoint(61.800322,34.320819));
		 * //FIXME This point is in Enschede, Netherlands. You should select a
		 * point in your map or get it from user's location.
		 * 
		 * if (getSharedPreferences(ConstantsAndTools.SHARED_PREFERENCES,
		 * 0).getBoolean(ConstantsAndTools.ONLINE_MODE, false))
		 * mapView.setUseDataConnection(true); //keeps the mapView from loading
		 * online tiles using network connection. else
		 * mapView.setUseDataConnection(false);
		 */
		
		
	}

	public void print(String str) {
		System.out.println(str);
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
		// Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
          return true;
        }
        // Handle your other action bar items...

		
		switch (item.getItemId()) {
			case android.R.id.home:
				//NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.map_menu_filter:
				// Show filter dialog
				//PointsCategoriesDialog dialog = new PointsCategoriesDialog();
				//dialog.show(getFragmentManager(), ConstantsAndTools.TAG);
				
				YandexPoints getAndShowPoints = new YandexPoints("банкоматы петрозаводск", 20) {
					@Override
					public void onPostExecute(String result) {
						ArrayList<PointInfo> points = this.openAndParse();
						
						for (int i = 0; i < points.size(); i++) {
							try {
								PointInfo curPoint = points.get(i);
								addMarker("sight-2", curPoint.lat, curPoint.lon, curPoint.name); // TODO: what of no name, but point must be on map?
								Log.e("Marker", curPoint.name);
							} catch (Exception e) {
								Log.e("Error showing point", "!!");
							}
						}
					}
				};
				
				getAndShowPoints.execute();			
				
				break;
			case R.id.map_menu_nearby:
				Toast.makeText(cont,  "Jusst a test", Toast.LENGTH_SHORT).show();
				//String Urlik = "http://api.wikilocation.org/articles?lat="+
				//		61.78333 + "&lng=" + 34.33333 + "&limit=2&radius=3000&locale=ru&format=json";
				//FindWikiArticle dwn = new FindWikiArticle();
				//dwn.execute(Urlik);
				
				// FIXME
				Intent intent = new Intent(this, NearbyActivity.class);
				startActivity(intent);
				break;
		}

		
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		Intent intent = null;

		// I know, it's kinda bicycle, but I dunno how to do better
		switch (position) {
		// Goto Main
		case (0):
			intent = new Intent(this, MainActivity.class);
			break;
		// Goto Map
		case (1):
			// intent = new Intent (this, MapActivity.class);
			break;
		// Goto Nearby
		case (2):
			intent = new Intent(this, NearbyActivity.class);
			break;
		// Goto Favourites
		case (3):
			intent = new Intent(this, FavourActivity.class);
			break;
		// Goto Travel Log
		case (4):
			intent = new Intent(this, TravellogActivity.class);
			break;
		default:
			Toast.makeText(getApplicationContext(), "Test", Toast.LENGTH_LONG)
					.show();
			break;
		}

		if (intent != null) {
			startActivity(intent);
			finish();
		}

		return true;
	}

	/**
	 * Categories chooser
	 */
	/*
	 * public class PointsCategoriesDialog extends DialogFragment {
	 * 
	 * @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
	 * AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	 * builder.setTitle(R.string.map_points_categories_title)
	 * .setItems(R.array.map_points_categories, new
	 * DialogInterface.OnClickListener() { public void onClick(DialogInterface
	 * dialog, int which) { // TODO: do something // 'which' is the selected
	 * item number } }); return builder.create(); } }
	 */

	/**
	 * Show privacy settings dialog
	 */

	@SuppressLint("ValidFragment")
	public class PointsCategoriesDialog extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			selectedCategories = new ArrayList<Integer>(); // Where we track the
															// selected items
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			// Set the dialog title
			builder.setTitle(R.string.map_points_categories_title)

					// TODO: get selected items by default or from
					// SharedPreferences
					// boolean[] selectedCategoriesByDefault;

					// Specify the list array, the items to be selected by
					// default (null for none),
					// and the listener through which to receive callbacks when
					// items are selected
					.setMultiChoiceItems(R.array.map_points_categories, null,
							new DialogInterface.OnMultiChoiceClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which, boolean isChecked) {
									if (isChecked) {
										// If the user checked the item, add it
										// to the selected items
										selectedCategories.add(which);
									} else if (selectedCategories
											.contains(which)) {
										// Else, if the item is already in the
										// array, remove it
										selectedCategories.remove(Integer
												.valueOf(which));
									}
								}
							})

					// Set the action buttons
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									// User clicked OK, so save the
									// mSelectedItems results somewhere
									// or return them to the component that
									// opened the dialog
									// TODO: do something
									// System.out.print("asdasda");
									// myWebView.loadUrl("javascript:setZoom(1)");
									String[] testArray = getResources()
											.getStringArray(
													R.array.map_points_categories);
									System.out.print(testArray);
								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									return;
								}
							});

			return builder.create();
		}
	}

	class FindWikiArticle extends AsyncTask<String, Integer, String> {
		
		HttpResponse response;
		String jsonString = null;

		@Override
		protected String doInBackground(String... sUrl) {
			try {
				String url = sUrl[0];

				StringBuilder builder = new StringBuilder();
		        HttpClient client = new DefaultHttpClient();
		        HttpGet httpGet = new HttpGet(url);
		        HttpResponse response = client.execute(httpGet);
		        StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(content));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    jsonString = builder.toString();
                } else {
                    Log.e("Err", "Failed to download file");
                }				
				
			} catch (Exception e) {
				Log.e("ERROR downloading file", e.getMessage());
				// Show Toast message on UI thread
				
				 runOnUiThread(new Runnable() { public void run() {
				 Toast.makeText(cont, "Klingon: Fyah REerf NEtwfk Err", Toast.LENGTH_SHORT).show();
			}
		});
				 
			}

			return null;
		}

		@Override
		protected void onCancelled() {
			Log.i("DWNLD", "cancelled");
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Log.i("DWNLD", "preExec");
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			super.onProgressUpdate(progress);
			Log.i("DWNLD", "ProgressUpd");
			// TODO Set progress
		}

		@Override
		protected void onPostExecute(String str) {
			super.onPostExecute("OK");
			
			JSONObject json;
			
			String articleUrl = null;
			String articleTitle = null;
			try {
				json = new JSONObject(jsonString);
				JSONArray elems = json.getJSONArray("articles");
				JSONObject link = elems.getJSONObject(0);
				
				articleUrl = link.get("mobileurl").toString();
				articleTitle = link.get("title").toString();
				Log.e("url", articleUrl);
				Log.e("title", articleTitle);
			} catch (JSONException e) {	}
			
			if (articleUrl != null) {
				// Open ArticleActivity for an article
				Intent myInt = new Intent(cont, ArticleActivity.class);
				myInt.putExtra(ConstantsAndTools.ARTICLE_ID, articleUrl);
				myInt.putExtra(ConstantsAndTools.ARTICLE_TITLE, articleTitle);
				startActivity(myInt);
			}	

		}
	}
}