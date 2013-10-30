package org.fruct.oss.tourme;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class HomeFragment extends Fragment {

	public static Context context = null;
	ViewPager viewPager = null;
	
	TextView currencyView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.activity_home, container, false);

		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		
		// Init the gallery		
		viewPager = (ViewPager) view.findViewById(R.id.viewPager);
		ImageAdapter adapter = new ImageAdapter(getActivity());
		viewPager.setAdapter(adapter);
		
		context = getActivity();
		
		final int delayTime = 5000;
		final Handler h = new Handler();
		
		// Loop sliding
		final Runnable r = new Runnable() {
			@Override
			public void run() {
				int current = viewPager.getCurrentItem();
				
				// Slide to first at the end
				if (current == viewPager.getAdapter().getCount()-1)
					current = -1;
				
				viewPager.setCurrentItem(current + 1);
				h.postDelayed(this, delayTime);
		}};

		// Stop sliding when touching and continue after
		viewPager.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				h.removeCallbacks(r);
				// If event is end, wait at current page for 2 dealyTime intervals
				if (event.getAction() == MotionEvent.ACTION_UP) {
					h.postDelayed(r, delayTime*2);
				}
				return false;
			}
		});
		
		// Run sliding after delayTime
		h.postDelayed(r, delayTime);	
		
		// Set onClick listeners
		View.OnClickListener l = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				buttonSwitchListener(v);
			}
		};
		
		TextView b = (TextView) view.findViewById(R.id.openTravelpedia);
		TextView b1 = (TextView) view.findViewById(R.id.openNearby);
		TextView b2 = (TextView) view.findViewById(R.id.openPracticalInfo);
		TextView b3 = (TextView) view.findViewById(R.id.openPhrasebook);
		b.setOnClickListener(l);
		b1.setOnClickListener(l);
		b2.setOnClickListener(l);
		b3.setOnClickListener(l);
		
		GetAndFillCurrency cur = new GetAndFillCurrency();
		cur.execute();
		
		currencyView = (TextView) view.findViewById(R.id.currency);
		
	}
	
	private class GetAndFillCurrency extends AsyncTask<Void, Integer, String> {

		@Override
		protected String doInBackground(Void... params) {
			try {
				Context context = getActivity();
				
				Uri.Builder b = Uri.parse("http://rate-exchange.appspot.com/currency").buildUpon();
				b.appendQueryParameter("to", String.valueOf(ConstantsAndTools.getDeviceCurrency(context)));
				b.appendQueryParameter("from", "EUR"); // FIXME: get currency of country
				Uri uri = b.build();
				
				
				
				 // Create a URL for the desired page
			    URL url = new URL(uri.toString());

			    // Read all the text returned by the server
			    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			    String str = in.readLine(); // Only 1 line of text
			    in.close();
				
			    return str;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return null;			
		}
		
		// Parse JSON file
		@Override
		public void onPostExecute(String result) {
			String currency = getResources().getString(R.string.not_available);
			
			try {
				JSONObject resultObject = new JSONObject(result);
				String from = resultObject.getString("from");
				String to = resultObject.getString("to");
				double rate = resultObject.getDouble("rate");
				
				currency = String.format("1 %s = %.2f %s", from, rate, to);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			currencyView.setText(currency);
		}
		
		
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setHasOptionsMenu(true);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.activity_home, menu);		
	}
	

	/**
	 * Walk around: executes an action for TextView onClick event
	 * @param v View emited onClick event (TextViews)
	 */
	private void buttonSwitchListener(View v) {
		
		FragmentManager fm = null;
		Fragment f = null;
		FragmentTransaction ft = null;
		
		switch(v.getId()) {
			case(R.id.openTravelpedia):
				// TODO
				break;
			case(R.id.openNearby):
				f = new NearbyFragment();
				break;
			case(R.id.openPracticalInfo):
				// TODO
				break;
			case(R.id.openPhrasebook):
				// TODO
				break;
		}
		
		if (f != null) {
			fm = getFragmentManager();
			ft = fm.beginTransaction();
			ft.replace(R.id.fragment_container, f);
			ft.addToBackStack(null);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();
		}		
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {

		FragmentManager fm = null;
		Fragment f = null;
		FragmentTransaction ft = null;
		
		switch(item.getItemId()) {
			case(R.id.menu_map):
				f = new MapFragment();
				break;
			case (R.id.menu_favourites):
				f = new FavouritesFragment();
				break;
			default:
				break;
		}

		if (f != null) {
			fm = getFragmentManager();
			ft = fm.beginTransaction();
			ft.replace(R.id.fragment_container, f);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();
		}
		
		return super.onOptionsItemSelected(item);		
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
	
}