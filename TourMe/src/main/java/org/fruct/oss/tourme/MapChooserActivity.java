package org.fruct.oss.tourme;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MapChooserActivity extends Activity {
	
	List<ImageView> markers = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mapchooser);
		
		context = getApplicationContext();
		
		final RelativeLayout layout = (RelativeLayout) findViewById(R.id.mapchooser);
		
		ImageView region = (ImageView) findViewById(R.id.region_image);
		markers = new ArrayList<ImageView>(); // Markers array
		
		// Set onTouchListener, add marker on click, delete marker on click
		region.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent e) {
				final ImageView marker = new ImageView(context);
				marker.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_location));
				markers.add(marker);
				layout.addView(marker);
				marker.setX(e.getX());
				marker.setY(e.getY());
				marker.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						marker.setVisibility(View.GONE);
						markers.remove(marker);
						setMarkersInfo();
					}});	
				
				setMarkersInfo();
				
				return false;
			}});
		
		
		
		// List with places
		List<Sightseen> sight = new ArrayList<Sightseen>();
		// FIXME bug with dpi
		sight.add(new Sightseen(0.0, 0.0, 260, 550, "Petrozavodsk"));
		sight.add(new Sightseen(0.0, 0.0, 78, 545, "Sortavala"));
		sight.add(new Sightseen(0.0, 0.0, 254, 511, "Kondopoga"));
		sight.add(new Sightseen(0.0, 0.0, 301, 528, "Kizhi"));
				
		
		// Locate places on 'map;
		for (Sightseen i : sight) {
			TextView placeLabel = new TextView(context);
			placeLabel.setText(i.label);
			layout.addView(placeLabel);
			placeLabel.setX(i.screenCoordX);
			placeLabel.setY(i.screenCoordY);
		}
	}
	
	

	private Context context;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {	
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map_chooser, menu);
		return true;
	}

	private class Sightseen {
		// Geographical coordinates
		private double geoCoordX = 0; 
		private double geoCoordY = 0;
		
		// Screen coordinates in dpi
		private int screenCoordX = 0;
		private int screenCoordY = 0;
		
		private String label = null;
		
		public Sightseen (double realX, double realY, int screenX, int screenY, String label){
			this.geoCoordX = realX;
			this.geoCoordY = realY;
			
			this.screenCoordX = screenX;
			this.screenCoordY = screenY;	
		
			this.label = label;
		}
	}
	
	private void setMarkersInfo() {
		TextView regionList = (TextView) findViewById(R.id.region_list);
		regionList.setText(markers.size() + " markers\n" + "About " + markers.size()*20 + "Mb of data"); // TODO: stringify
	}
}
