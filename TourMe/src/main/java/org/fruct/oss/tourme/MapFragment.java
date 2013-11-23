package org.fruct.oss.tourme;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.nutiteq.MapView;
import com.nutiteq.components.Components;
import com.nutiteq.components.MapPos;
import com.nutiteq.geometry.Marker;
import com.nutiteq.projections.EPSG3857;
import com.nutiteq.rasterlayers.TMSMapLayer;
import com.nutiteq.style.MarkerStyle;
import com.nutiteq.ui.DefaultLabel;
import com.nutiteq.ui.Label;
import com.nutiteq.utils.UnscaledBitmapLoader;
import com.nutiteq.vectorlayers.MarkerLayer;

import java.util.ArrayList;

public class MapFragment extends Fragment {

	private ArrayList<Integer> selectedCategories;
	
	private MapView mapView;
	private TMSMapLayer mapLayer;
    private MarkerLayer myLocation = null;
	
	private MarkerLayer markerLayerSights, markerLayerATM, markerLayerWC, markerLayerHotels,
		markerLayerHospitals, markerLayerPolice;	
	private MarkerLayer markerLayerArticle;
	
	private boolean firstLaunch = true;
	private boolean fromArticle = false;
	
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

        // Disable map rotation if need
        SharedPreferences sh = getActivity().getSharedPreferences(ConstantsAndTools.SHARED_PREFERENCES, 0);
        if (sh.getBoolean(ConstantsAndTools.ALLOW_MAP_ROTATION, false))
            mapView.getConstraints().setRotatable(true);
        else
            mapView.getConstraints().setRotatable(false);

        // Set available zoom limits
        //mapView.getConstraints().setZoomRange(new Range(10, 16));

        // Make map smoother
		mapView.getOptions().setDoubleClickZoomIn(true);
		mapView.getOptions().setPreloading(true);
        mapView.getOptions().setSeamlessHorizontalPan(true);
        mapView.getOptions().setTileFading(true);
        mapView.getOptions().setKineticPanning(true);
        mapView.getOptions().setDoubleClickZoomIn(true);
        
        // Configure texture caching
        mapView.getOptions().setTextureMemoryCacheSize(40 * 1024 * 1024);
        mapView.getOptions().setCompressedMemoryCacheSize(8 * 1024 * 1024);
        mapView.getOptions().setPersistentCacheSize(100 * 1024 * 1024); // 100MB cache
		
		// start mapping
		mapView.startMapping();
		      
		markerLayerSights = new MarkerLayer(mapLayer.getProjection());
		markerLayerArticle = new MarkerLayer(mapLayer.getProjection());

		mapView.getLayers().addLayer(markerLayerSights);
		mapView.getLayers().addLayer(markerLayerArticle);

        // Show current location marker
        showMyLocationMarker(true);

		return view;
	}

    /**
     * Show or hide current location marker
     * @param toShow boolean: show or not
     */
    private void showMyLocationMarker(Boolean toShow) {

        // Create layer if not defined
        if (myLocation == null) {
            myLocation = new MarkerLayer(mapLayer.getProjection());

            Bitmap pointMarker = UnscaledBitmapLoader.decodeResource(getResources(), R.drawable.ic_current_location);
            MarkerStyle markerStyle = MarkerStyle.builder().setBitmap(pointMarker).build();

            Label markerLabel = new DefaultLabel(getResources().getString(R.string.me));

            MapPos markerLocation = mapLayer.getProjection().fromWgs84(MainActivity.currentLongitude, MainActivity.currentLatitude);

            Marker marker = new Marker(markerLocation, markerLabel, markerStyle, null);
            myLocation.add(marker);
            mapView.getLayers().addLayer(myLocation);
        }

        // Show or hide
        if (toShow)
            myLocation.setVisible(true);
        else
            myLocation.setVisible(false);

    }
	
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
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {


		Double lon = MainActivity.currentLongitude;
		Double lat = MainActivity.currentLatitude;
		
		// Fly to current location at first launch (after location detection)
		if (firstLaunch && lon != 0 && !fromArticle) {
			Log.e("firstLaunch", ""+lon);
			mapView.setFocusPoint(mapLayer.getProjection().fromWgs84(lon, lat));
			mapView.setZoom(14);
			firstLaunch = false;			
		
			clearMarkers();			

			String locale = ConstantsAndTools.getLocale(context);
			
			// Show articles from Wiki on map
			WikilocationPoints w = new WikilocationPoints(lon, lat,
					ConstantsAndTools.ARTICLES_AMOUNT, ConstantsAndTools.ARTICLES_RADIUS, locale) {
				@Override
				public void onPostExecute(String result){
					ArrayList<PointInfo> points = this.openAndParse();
					
					if (points != null)
						for (int i = 0; i < points.size(); i ++) {
							PointInfo p = points.get(i);
							
							addMarker("wiki", p.type, p.longitude, p.latitude, p.title, null);
						}
				}
			};
			
			w.execute();
		}
		
	}

	
	/**
	 * 
	 * @param category
	 * @param longitude
	 * @param latitude
	 * @param title
	 * @param description
	 */
	private void addMarker(String layerType, String category, String longitude, String latitude, String title, String description) {

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
		Marker marker = new Marker(markerLocation, markerLabel, markerStyle, null);
		
		if (layerType.equals("wiki"))			
			markerLayerSights.add(marker);
		if (layerType.equals("fromArticles"))
			markerLayerArticle.add(marker);
		
		// TODO: other categories

	}

	
	/**
	 * Clear all markers from markers' layer
	 */
	private void clearMarkers() {
		//if (markerLayer != null)
		markerLayerSights.clear();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.activity_map, menu);
	}


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
			fromArticle = true;
			// This means transition from ArticleActivity ('show on map' button)
			// Enlarge zoom level on current coordinates
			String lat = args.getString("latitude");
			String lon = args.getString("longitude");
			int zoom = 16;
			
			mapView.setFocusPoint(mapLayer.getProjection().fromWgs84(Double.parseDouble(lon), Double.parseDouble(lat)));
			mapView.setZoom(zoom);
			addMarker("fromArticles", "category?", lon, lat, "[title]", null);
		}
	}
	

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}


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
								public void onClick(DialogInterface dialog,	int id) {
									// User clicked OK, so save the
									// mSelectedItems results somewhere
									// or return them to the component that
									// opened the dialog
									/*String[] testArray = getResources()
											.getStringArray(
													R.array.map_points_categories);*/
									
									if (selectedCategories.contains(0))
										markerLayerSights.setVisible(true);
									else
										markerLayerSights.setVisible(false);

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