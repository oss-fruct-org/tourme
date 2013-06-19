package org.fruct.oss.tourme;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class NearbyFragment extends ListFragment {

/*	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.activity_nearby, container, false);
	}*/
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		// Add items to ListView
		final ArrayList<String> listItems = new ArrayList<String>();
		final ArrayAdapter<String> adapter;
		adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1,
				listItems);
		setListAdapter(adapter);

		// Find and add points
		YandexPoints points = new YandexPoints("банкоматы петрозаводск", 100) { // FIXME TODO
			@Override
			public void onPostExecute(String result) {
				ArrayList<PointInfo> points = this.openAndParse();
				
				for (int i = 0; i < points.size(); i++) {
					try {
						listItems.add(points.get(i).name);
					} catch (Exception e) {}
				}
				
				adapter.notifyDataSetChanged();
			}
		};
		
		points.execute();
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return false;
	}



}
