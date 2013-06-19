package org.fruct.oss.tourme;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class HomeFragment extends Fragment {

	public static Context context = null;	
	ViewPager viewPager = null;

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
	}
	
	
	public void openTravelpedia(View view) {
		//drawerItemSwitch(1);
	}
	
	public void openNearby(View view) {
		//drawerItemSwitch(2);
	}
	public void openPracticalInfo(View view) {
		//drawerItemSwitch(4);
	}
	public void openPhrasebook(View view) {
		//Toast.makeText(context, "Nothing implemented yet", Toast.LENGTH_SHORT).show();
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