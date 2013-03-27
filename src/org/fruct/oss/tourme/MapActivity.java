package org.fruct.oss.tourme;


import java.util.ArrayList;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class MapActivity extends FragmentActivity implements
ActionBar.OnNavigationListener {

/**
* The serialization (saved instance state) Bundle key representing the
* current dropdown position.
*/
private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
private MapController mapController;
    private MapView mapView;
    private ArrayList<Integer> selectedCategories;
    
@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(R.layout.activity_map);

// Set up the action bar to show a dropdown list.
final ActionBar actionBar = getActionBar();
actionBar.setDisplayShowTitleEnabled(false);
actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
// Show the Up button in the action bar.
//actionBar.setDisplayHomeAsUpEnabled(true);

// Set up the dropdown list navigation in the action bar.
actionBar.setListNavigationCallbacks(
// Specify a SpinnerAdapter to populate the dropdown list.
new ArrayAdapter<String>(getActionBarThemedContextCompat(),
android.R.layout.simple_list_item_1,
android.R.id.text1, new String[] {
getString(R.string.actionbar_main),
getString(R.string.actionbar_map),
getString(R.string.actionbar_nearby),
getString(R.string.actionbar_favour),
getString(R.string.actionbar_log) }), this);

actionBar.setSelectedNavigationItem(1);

this.initMap();
}

private void initMap() {

        MapView mapView = new MapView(this, 256); //constructor

        mapView.setClickable(true);

        mapView.setBuiltInZoomControls(true);

        setContentView(mapView); //displaying the MapView

        mapView.getController().setZoom(12); //set initial zoom-level, depends on your need

        mapView.getController().setCenter(new GeoPoint(61.800322,34.320819)); //FIXME This point is in Enschede, Netherlands. You should select a point in your map or get it from user's location.

        if (getSharedPreferences(ConstantsAndTools.SHARED_PREFERENCES, 0).getBoolean(ConstantsAndTools.ONLINE_MODE, false))
        	mapView.setUseDataConnection(true); //keeps the mapView from loading online tiles using network connection.
        else
        	mapView.setUseDataConnection(false);
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
switch (item.getItemId()) {
case android.R.id.home:
NavUtils.navigateUpFromSameTask(this);
return true;
case R.id.map_menu_filter:
// Show filter dialog
PointsCategoriesDialog dialog = new PointsCategoriesDialog();
dialog.show(getFragmentManager(), ConstantsAndTools.TAG);	
break;
case R.id.map_menu_nearby:
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
switch(position) {
// Goto Main
case(0):
intent = new Intent (this, MainActivity.class);
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

     if (intent != null) {
     startActivity(intent);
     finish();
     }

return true;
}

/**
* Categories chooser
*/
    /*public class PointsCategoriesDialog extends DialogFragment {
@Override
public Dialog onCreateDialog(Bundle savedInstanceState) {
AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
builder.setTitle(R.string.map_points_categories_title)
.setItems(R.array.map_points_categories, new DialogInterface.OnClickListener() {
public void onClick(DialogInterface dialog, int which) {
// TODO: do something
// 'which' is the selected item number
}
});
return builder.create();
}
} */
    
    /**
* Show privacy settings dialog
*/

@SuppressLint("ValidFragment") public class PointsCategoriesDialog extends DialogFragment {
@Override
public Dialog onCreateDialog(Bundle savedInstanceState) {
selectedCategories = new ArrayList<Integer>(); // Where we track the selected items
AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());	
// Set the dialog title
builder.setTitle(R.string.map_points_categories_title)

// TODO: get selected items by default or from SharedPreferences
//boolean[] selectedCategoriesByDefault;

// Specify the list array, the items to be selected by default (null for none),
// and the listener through which to receive callbacks when items are selected
.setMultiChoiceItems(R.array.map_points_categories, null,
new DialogInterface.OnMultiChoiceClickListener() {
@Override
public void onClick(DialogInterface dialog, int which,
boolean isChecked) {
if (isChecked) {
// If the user checked the item, add it to the selected items
selectedCategories.add(which);
} else if (selectedCategories.contains(which)) {
// Else, if the item is already in the array, remove it
selectedCategories.remove(Integer.valueOf(which));
}
}
})

// Set the action buttons
.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
@Override
public void onClick(DialogInterface dialog, int id) {
// User clicked OK, so save the mSelectedItems results somewhere
// or return them to the component that opened the dialog
// TODO: do something
}
})
.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
@Override
public void onClick(DialogInterface dialog, int id) {
return;
}
});

return builder.create();
}
}


}