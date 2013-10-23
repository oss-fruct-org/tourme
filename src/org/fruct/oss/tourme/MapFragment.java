package org.fruct.oss.tourme;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nutiteq.MapView;
import com.nutiteq.components.Components;
import com.nutiteq.components.MapPos;
import com.nutiteq.geometry.Marker;
import com.nutiteq.projections.EPSG3857;
import com.nutiteq.rasterlayers.TMSMapLayer;
import com.nutiteq.style.MarkerStyle;
import com.nutiteq.ui.DefaultLabel;
import com.nutiteq.ui.Label;
import com.nutiteq.ui.ViewLabel;
import com.nutiteq.utils.UnscaledBitmapLoader;
import com.nutiteq.vectorlayers.MarkerLayer;

public class MapFragment extends Fragment {

	private ArrayList<Integer> selectedCategories;
	
	private MapView mapView;
	private TMSMapLayer mapLayer;
	private MarkerLayer markerLayer;
	
	private LocationManager mLocationManager;
	
	Context context;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.activity_map, container, false);
		
		context = getActivity();
		
		mapView = (MapView) view.findViewById(R.id.mapView);
		mapView.setComponents(new Components());
		
		// Doesn't work without license
		//Bitmap watermark = BitmapFactory.decodeResource(view.getResources(), R.drawable.ic_launcher);
		//MapView.setWatermark(watermark, 0, 0, 1);
		
		// Define base layer. Here we use MapQuest open tiles which are free to use
		// Almost all online maps use EPSG3857 projection
		mapLayer = new TMSMapLayer(new EPSG3857(), 0, 18, 0, "http://otile1.mqcdn.com/tiles/1.0.0/osm/", "/", ".png");
		mapView.getLayers().setBaseLayer(mapLayer);
		 
		// start mapping
		mapView.startMapping();
		      
		markerLayer = new MarkerLayer(mapLayer.getProjection());

		
		mapView.getLayers().addLayer(markerLayer);


		return view;
	}
	
	/**
	 * 
	 * @param category
	 * @param longitude
	 * @param latitude
	 * @param title
	 * @param description
	 */
	private void addMarker(String category, String longitude, String latitude, String title, String description) {

		Bitmap pointMarker = UnscaledBitmapLoader.decodeResource(getResources(), R.drawable.ic_launcher); // FIXME: icon category		
		MarkerStyle markerStyle = MarkerStyle.builder().setBitmap(pointMarker).setSize(0.5f).setColor(Color.WHITE).build();
		
		// define label what is shown when you click on marker
		Label markerLabel;
		
		// TODO: go from maerkerLabel to Wiki article
		
		if (description == null)
			markerLabel = new DefaultLabel(title);
		else
			markerLabel = new DefaultLabel(title, description);
		
		// define location of the marker, it must be converted to base map coordinate system
		MapPos markerLocation = mapLayer.getProjection().fromWgs84(Float.parseFloat(longitude), Float.parseFloat(latitude));

		// create layer and add object to the layer, finally add layer to the map. 
		// All overlay layers must be same projection as base layer, so we reuse it
		if (markerLayer != null) {
			Marker marker = new Marker(markerLocation, markerLabel, markerStyle, null);
			markerLayer.add(marker);
		}		
	}

	
	/**
	 * Clear all markers from markers' layer
	 */
	private void clearMarkers() {
		if (markerLayer != null)
			markerLayer.clear();
	}
	
	
	public final LocationListener mLocationListener = new LocationListener() {	   

		@Override
		public void onLocationChanged(Location location) {
			
			double lon = location.getLongitude();
			double lat = location.getLatitude();
			
			clearMarkers();
			
			// TODO: delete
			// Show articles from Wiki on map
			WikilocationPoints w = new WikilocationPoints(lon, lat, 2000, 30000, "ru") { // TODO
				@Override
				public void onPostExecute(String result){
					ArrayList<PointInfo> points = this.openAndParse();
					
					for (int i = 0; i < points.size(); i ++) {
						PointInfo p = points.get(i);
						
						addMarker(p.type, p.longitude, p.latitude, p.title, null);
						//addMarker("pyramid", p.latitude, p.longitude, p.title);
					}
				}
			};
			
			w.execute();
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
	};


	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set menu (intrested in Actionbar menu items)
		this.setHasOptionsMenu(true);
	}
	

	/*
	 * Init webView with map after the view creation It'll not work in onCreate
	 * etc
	 */
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		
		// Location things
		if (context != null) {
			mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);	
		    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3600, 10, mLocationListener); // FIXME 3600000, 1000, provider ?
		}
		
		/*myWebView = (WebView) view.findViewById(R.id.mapview);

		// FIXME: Will it work fine?
		String url = Environment.getExternalStorageDirectory()
				+ "/osmdroid/Mapnik";
		myWebView.getSettings().setJavaScriptEnabled(true);
		myWebView.loadUrl("file:///android_asset/map.html");
		myWebView.addJavascriptInterface(new AppInterface(getActivity()), "Android"); // TODO FIXME
		// url = "http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png";

		myWebView.loadUrl("javascript:setUrl(" + url + ")");
		
		myWebView.setWebViewClient(new WebViewClient() {
			   public void onPageFinished(WebView view, String url) {
				   
				   // Online\offline mode depends on setting
				   SharedPreferences sh = getActivity().getSharedPreferences(ConstantsAndTools.SHARED_PREFERENCES, 0);
				   if (sh.getBoolean(ConstantsAndTools.ONLINE_MODE, false))
					   myWebView.loadUrl("javascript:setOnlineLayer();");
				   else
					   myWebView.loadUrl("javascript:setOfflineLayer();");
				   
				   myWebView.loadUrl("javascript:setPrepareMode(false);"); // Enable touch-to-add-point
			    }
		});*/
		
	}
	
	
	public void switchOnlineOfflineMode() {
		int mode = 0; // TODO: get current mode
		String urlToLoad = "javascript:";
		
		if (mode == 0)
			urlToLoad = urlToLoad + "setOnlineLayer();";
		else
			urlToLoad = urlToLoad + "setOfflineLayer();";
			
		//myWebView.loadUrl(urlToLoad);		
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.activity_map, menu);
	}
	
	/**
	 * Bind this javascript interface to make
	 * interaction between JS and Java (predefined functions)
	 * @author alexander
	 *
	 */
	/*
	public class AppInterface {
		Context cont;
		
	    AppInterface(Context c) {
	        cont = c;
	    }

	   // Show a toast from the web page
	    @JavascriptInterface
	    public void showToast(String toast) {
	        Toast.makeText(cont, toast, Toast.LENGTH_SHORT).show();
	    }
	}*/
	

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	

	// FIXME all of this
	@Override
	public void onResume() {
		super.onResume();
	
		Bundle args = getArguments();
		if (args != null) {
			// This means transition from ArticleActivity ('show on map' button)
			// Enlarge zoom level on current coordinates
			String lat = args.getString("latitude");
			String lon = args.getString("longitude");
			int zoom = 16; // TODO: is 16 enough?
			
			//myWebView.loadUrl("javascript:centerWithZoomAt(" + lat + "," + lon + ", " + zoom +");");
			//Log.e("launch", "javascript:centerWithZoomAt(" + lat + ", " + lon + ", " + zoom +");");
		}
	}
	

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	

	/*public void addMarker(String group, String lat, String lon,
			String description) {
		// FIXME: group? What group? What will happen when group isn't exist?
		myWebView.loadUrl("javascript:addMarker('" + group + "', " + lat + ", "
				+ lon + ", '" + description + "');"); // TODO: what if aposotrophe is in name?
		//Log.e("js", "javascript:addMarker(" + group + ", " + lat + ", " + lon
		//		+ ", '" + description + "');");
	}*/
	

	/**
	 * Categories chooser
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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		FragmentManager fm = getFragmentManager();
		Fragment f = null;
		FragmentTransaction ft = null;
		PointsCategoriesDialog dia = null;
		
		switch(item.getItemId()) {
			case(R.id.map_menu_filter):
				dia = new PointsCategoriesDialog();				
				break;
			case (R.id.map_menu_nearby):
				f = new NearbyFragment();
				break;
			/*case (R.id.map_menu_onoff):
				break;*/
			default:
				break;
		}

		if (f != null) {
			ft = fm.beginTransaction();
			ft.replace(R.id.fragment_container, f);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();
			return true;
		}
		
		if (dia != null) {
			dia.show(fm, ConstantsAndTools.TAG);
			return true;
		}
		
		return super.onOptionsItemSelected(item);		
	}

	
	
}