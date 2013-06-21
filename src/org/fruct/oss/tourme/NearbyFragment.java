package org.fruct.oss.tourme;

import java.util.ArrayList;
import java.util.List;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        
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
		
		WikilocationPoints w = new WikilocationPoints(61.78f, 34.33f, 200, 3000, "ru") {
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
						startActivity(i);
					}			
				});
				
			}
		};
		
		w.execute();
		
		
	}


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
