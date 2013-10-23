package org.fruct.oss.tourme;

import java.util.ArrayList;
import java.util.List;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class NearbyFragment extends ListFragment {

/*	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.activity_nearby, container, false);
	}*/
	
	NearbyAdapter adapter;
	ArrayList<PointInfo> points;
	
	private LocationManager mLocationManager;
	
	Context context;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
     // Location things
        
        context = getActivity();

		if (context != null) {
			mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);	
		    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3600, 10, mLocationListener); // FIXME 3600000, 1000, provider ?
		}
        
	}
	
	public final LocationListener mLocationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			
			double lon = location.getLongitude();
			double lat = location.getLatitude();
			
			
			// Find and add points
			/*YandexPoints ya = new YandexPoints("банкоматы петрозаводск", 100) { // FIXME TODO
				@Override
				public void onPostExecute(String result) {
					ArrayList<PointInfo> points = this.openAndParse();
					adapter = new NearbyAdapter(points, getActivity());
					setListAdapter(adapter);
				}
			};		
			
			ya.execute();*/
			
			String locale = ConstantsAndTools.getLocale(context);
			
			WikilocationPoints w = new WikilocationPoints(lon, lat, 
					ConstantsAndTools.ARTICLES_AMOUNT, ConstantsAndTools.ARTICLES_RADIUS, locale) {
				@Override
				public void onPostExecute(String result){
					points = this.openAndParse();
					
					/*for (int i = 0; i < points.size(); i ++) {
						adapter.add(points.get(i));
					}
					adapter.notifyDataSetChanged();*/
					
					adapter = new NearbyAdapter(points, getActivity());
					setListAdapter(adapter);	
					
					ListView lv = (ListView) getListView();
					lv.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
							PointInfo p = points.get(position);
							Intent i = new Intent(getActivity(), ArticleActivity.class);
							i.putExtra(ConstantsAndTools.ARTICLE_ID, p.mobileurl);
							i.putExtra(ConstantsAndTools.ARTICLE_TITLE, p.title);
							
							// Put bundle of article's coordinates
							Bundle coords = new Bundle();
							coords.putString("latitude", p.latitude);
							coords.putString("longitude", p.longitude);
							i.putExtra(ConstantsAndTools.ARTICLE_COORDINATES, coords);
							
							startActivity(i);
						}			
					});
					
				}
			};
			
			w.execute();
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}		
	};


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return false;
	}
	
	
	// TODO: Extract to separate java-file
	public class NearbyAdapter extends ArrayAdapter<PointInfo> {
		 
		private List<PointInfo> pointsList;
		private Context context;
		 
		public NearbyAdapter(List<PointInfo> pointsList, Context ctx) {
		    super(ctx, R.layout.nearby_list_item, pointsList);
		    this.pointsList = pointsList;
		    this.context = ctx;
		}
		 
		public View getView(int position, View convertView, ViewGroup parent) {
		     
		    if (convertView == null) {
		        // This a new view we inflate the new layout
		        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		        convertView = inflater.inflate(R.layout.nearby_list_item, parent, false);
		    }

		    // TODO: icons? favourites? click event?
		    TextView tv = (TextView) convertView.findViewById(R.id.nearby_list_item_title);
		    TextView distView = (TextView) convertView.findViewById(R.id.nearby_list_item_descr);
		    
		    PointInfo p = pointsList.get(position);		 
		    
		    tv.setText(p.title);

		    // Don't show 'null' in the field of distance
		    if (p.distance == null) {
		    	distView.setVisibility(View.INVISIBLE);		    	
		    } else {
		    	distView.setText("" + p.distance);
		    	distView.setVisibility(View.VISIBLE);	
		    }
		    	

	        return convertView;
		}
	}

}
