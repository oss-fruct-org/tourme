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
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nutiteq.MapView;
import com.nutiteq.components.Components;
import com.nutiteq.components.MapPos;
import com.nutiteq.geometry.Marker;
import com.nutiteq.geometry.VectorElement;
import com.nutiteq.projections.EPSG3857;
import com.nutiteq.rasterlayers.TMSMapLayer;
import com.nutiteq.style.LabelStyle;
import com.nutiteq.style.MarkerStyle;
import com.nutiteq.ui.DefaultLabel;
import com.nutiteq.ui.Label;
import com.nutiteq.ui.MapListener;
import com.nutiteq.utils.UnscaledBitmapLoader;
import com.nutiteq.vectorlayers.MarkerLayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

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

    WikilocationPoints w;

    Context context;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.activity_map, container, false);
		
		context = getActivity();
		
		mapView = (MapView) view.findViewById(R.id.mapView);
		mapView.setComponents(new Components());
		
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
        //mapView.getConstraints().setZoomRange(new Range(10, 16)); // TODO

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

        if (MapView.registerLicense(ConstantsAndTools.NUTITEQ_LICENSE_NO, getActivity()))
            Log.i("registration", "ok");

        MapEventListener mapEventListener = new MapEventListener(getActivity());
        mapView.getOptions().setMapListener(mapEventListener);

        // Show current location marker
        showMyLocationMarker(true);

        return view;
	}

    public class MapEventListener extends MapListener {
        private Context context;

        // activity is often useful to handle click events
        public MapEventListener(Context context) {
            this.context = context;
        }

        @Override
        public void onMapMoved() {
        }

        @Override
        public void onMapClicked(double v, double v2, boolean b) {
        }

        @Override
        public void onVectorElementClicked(VectorElement vectorElement, double v, double v2, boolean b) {
            if (vectorElement.userData.equals("currentLocation"))
                return;
        }

        // Open activity with article
        @Override
        public void onLabelClicked(VectorElement vectorElement, boolean longClick) {
            // userData is URL
            if (vectorElement.userData != null) {
                Intent i = new Intent(getActivity(), ArticleActivity.class);
                i.putExtra(ConstantsAndTools.ARTICLE_ID, vectorElement.userData.toString());
                startActivity(i);
            }
        }
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

            Label markerLabel = new DefaultLabel("", "", ConstantsAndTools.LABEL_STYLE); // FIXME

            MapPos markerLocation = mapLayer.getProjection().fromWgs84(MainActivity.currentLongitude, MainActivity.currentLatitude);

            Marker marker = new Marker(markerLocation, markerLabel, markerStyle, null);
            marker.userData = "currentLocation";

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
			w = new WikilocationPoints(getActivity(), lon, lat,
					ConstantsAndTools.ARTICLES_AMOUNT, ConstantsAndTools.ARTICLES_RADIUS, locale) {
				@Override
				public void onPostExecute(String result){
                    if(!isAdded()){
                        return;
                    }

                    DBHelper dbHelper = new DBHelper(getActivity());
                    SQLiteDatabase db = dbHelper.getReadableDatabase();

                    String where = "";
                    Double lat = MainActivity.currentLatitude;
                    Double lon = MainActivity.currentLongitude;
                    if (lat != 0) {
                        where = "latitude < " + Double.toString(lat - 0.5d) +
                        " and latitude > " + Double.toString(lat + 0.5d) + // TODO: degree depend on location
                        " and longitude < " + Double.toString(lon - 0.5) +
                        " and longitude > " + Double.toString(lon + 0.5d);
                    }

                    String[] columns = new String[] {"latitude", "longitude", "name", "url", "type"};
                    Cursor c = db.query(true, ConstantsAndTools.TABLE_WIKIARTICLES, columns, null, null, null, null, null, null); // FIXME not distinct, filter in wiki class

                    if (c.moveToFirst()) {
                        // Get text, image and location
                        int idLatitude = c.getColumnIndex("latitude");
                        int idLongitude = c.getColumnIndex("longitude");
                        int idTitle = c.getColumnIndex("name");
                        int idUrl = c.getColumnIndex("url");
                        int idType = c.getColumnIndex("type");

                        do {
                            addMarker("wiki", c.getString(idType), c.getString(idLongitude), c.getString(idLatitude),
                                    c.getString(idTitle), c.getString(idUrl), null);
                        } while (c.moveToNext());
                    }
				}
			};
			
			w.execute();
		}
		
	}

    @Override
    public void onPause() {
        super.onPause();

        // Kill points downloading
        w.cancel(true);
    }

	
	/**
	 * 
	 * @param category
	 * @param longitude
	 * @param latitude
	 * @param title
	 * @param description
	 */
	private void addMarker(String layerType, String category, String longitude, String latitude, String title, String url, String description) {

        // Select icon for category
        int iconFilename = R.drawable.ic_launcher;
        if (Arrays.asList(ConstantsAndTools.WIKI_CATEGORIES).contains(category)) {
            iconFilename = context.getResources().getIdentifier(category, "drawable", context.getPackageName());
        }
        Log.e("tourme", "_" + iconFilename + category);

		Bitmap pointMarker = UnscaledBitmapLoader.decodeResource(getResources(), iconFilename); // FIXME: icon category
		MarkerStyle markerStyle = MarkerStyle.builder()
                .setBitmap(pointMarker)
                .setColor(Color.WHITE)
                .build();
		
		// define label what is shown when you click on marker
		Label markerLabel;

        if (title.length() > 40)
            title = title.substring(0, 40).concat("...");

        // Adjust label size to screen density
        float scale = getResources().getDisplayMetrics().density;

        LabelStyle labelStyle = LabelStyle.builder()
            .setBackgroundColor(Color.parseColor("#FF222222"))
            .setBorderColor(Color.parseColor("#FF222222"))
            .setTitleColor(Color.parseColor("#FFFFFFFF"))
            .setDescriptionColor(Color.parseColor("#FFFFFFFF"))
            .setTipSize((int) (10 * scale))
            .setTitleFont(Typeface.create("Roboto", Typeface.BOLD), 14 * scale)
            .setBorderRadius(5)
            .build();

		if (description == null)
			markerLabel = new DefaultLabel(title, "", labelStyle);
		else
			markerLabel = new DefaultLabel(title, description, labelStyle);

		// define location of the marker, it must be converted to base map coordinate system
		MapPos markerLocation = mapLayer.getProjection().fromWgs84(Float.parseFloat(longitude), Float.parseFloat(latitude));

		// create layer and add object to the layer, finally add layer to the map.
		// All overlay layers must be same projection as base layer, so we reuse it
		Marker marker = new Marker(markerLocation, markerLabel, markerStyle, null);
        marker.userData = url;
		
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
			//addMarker("fromArticles", "category?", lon, lat, "[title]", null);
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