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
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

public class MapFragment extends Fragment {

	private ArrayList<Integer> selectedCategories;

	private WebView myWebView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.activity_map, container, false);

		return view;
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
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		myWebView = (WebView) view.findViewById(R.id.mapview);

		// FIXME: Will it work fine?
		String url = Environment.getExternalStorageDirectory()
				+ "/osmdroid/Mapnik";
		myWebView.getSettings().setJavaScriptEnabled(true);
		myWebView.loadUrl("file:///android_asset/map.html");
		myWebView.addJavascriptInterface(new AppInterface(getActivity()), "Android"); // TODO FIXME
		// url = "http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png";
		Log.v("Map tiles URL: ", url);
		myWebView.loadUrl("javascript:setUrl(" + url + ")");
		
		
		// TODO: delete
		// Show articles from Wiki on map
		WikilocationPoints w = new WikilocationPoints(61.78f, 34.33f, 2000, 30000, "ru") {
			@Override
			public void onPostExecute(String result){
				ArrayList<PointInfo> points = this.openAndParse();
				
				for (int i = 0; i < points.size(); i ++) {
					PointInfo p = points.get(i);
					addMarker("pyramid", p.latitude, p.longitude, p.title);
				}
			}
		};
		
		w.execute();
		
	}
	
	
	public void switchOnlineOfflineMode() {
		int mode = 0; // TODO: get current mode
		String urlToLoad = "javascript:";
		
		if (mode == 0)
			urlToLoad = urlToLoad + "setOnlineLayer();";
		else
			urlToLoad = urlToLoad + "setOfflineLayer();";
			
		myWebView.loadUrl(urlToLoad);		
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
	public class AppInterface {
		Context cont;
		
	    AppInterface(Context c) {
	        cont = c;
	    }

	    /** Show a toast from the web page */
	    @JavascriptInterface
	    public void showToast(String toast) {
	        Toast.makeText(cont, toast, Toast.LENGTH_SHORT).show();
	    }
	}
	

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	

	@Override
	public void onStart() {
		super.onStart();
		// During startup, check if there are arguments passed to the fragment.
		// onStart is a good place to do this because the layout has already
		// been applied to the fragment at this point so we can safely call the
		// method below that sets the article text.
		
		// Bundle args = getArguments();
	}
	

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	

	public void addMarker(String group, String lat, String lon,
			String description) {
		// FIXME: group? What group? What will happen when group isn't exist?
		myWebView.loadUrl("javascript:addMarker('" + group + "', " + lat + ", "
				+ lon + ", '" + description + "');"); // TODO: what if aposotrophe is in name?
		Log.e("js", "javascript:addMarker(" + group + ", " + lat + ", " + lon
				+ ", '" + description + "');");
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
			case (R.id.map_menu_onoff):
				break;
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