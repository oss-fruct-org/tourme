package org.fruct.oss.tourme;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class FragmentNearby extends Fragment {

	
	NearbyAdapter adapter;

	Context context;

    WikilocationPoints w;

    ListView lv;

    ProgressBar progressBar;

    RelativeLayout bottomPane;
    View gradient;

    boolean isInSeparateActivity = false;

    public FragmentNearby() {}

    public FragmentNearby(boolean isInSeparateActivity) {
        this.isInSeparateActivity = isInSeparateActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view;
        if (isInSeparateActivity) {
            view = inflater.inflate(R.layout.fragment_nearby_activity, container, false);
        } else {
            view = inflater.inflate(R.layout.fragment_nearby, container, false);
        }

        lv = (ListView) view.findViewById(R.id.nearby_list);

        progressBar = (ProgressBar) view.findViewById(R.id.nearby_progress);

        /*gradient = view.findViewById(R.id.nearby_flow);
        bottomPane = (RelativeLayout) view.findViewById(R.id.nearby_bottom_pane);

        if (isInSeparateActivity) {
            bottomPane.setVisibility(View.GONE);
            gradient.setVisibility(View.GONE);
        }*/

        context = getActivity();

        ImageView mapIcon = (ImageView) view.findViewById(R.id.nearby_on_map);
        if (mapIcon != null)
            mapIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), ActivityMap.class);
                    startActivity(i);
                }
            });

        TextView moreInfo = (TextView) view.findViewById(R.id.nearby_show_more);
        if (moreInfo != null)
            moreInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), ActivityNearby.class);
                    startActivity(i);
                }
            });

        downloadAndShow();

        return view;
	}

    public void downloadAndShow() {
        String locale = ConstantsAndTools.getLocale(context);

        lv.setAdapter(null);

        double lon = MainActivity.currentLongitude;
        double lat = MainActivity.currentLatitude;

        w = new WikilocationPoints(getActivity(), lon, lat,
                ConstantsAndTools.ARTICLES_AMOUNT, ConstantsAndTools.ARTICLES_RADIUS, locale) {

            @Override
            public void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(values[0]);
            }

            @Override
            public void onPostExecute(String result){
                if(!isAdded()){
                    return;
                }

                progressBar.setVisibility(View.GONE);

                if (this.cursor.moveToFirst()) {
                    // Get text, image and location
                    int idLatitude = this.cursor.getColumnIndex("latitude");
                    int idLongitude = this.cursor.getColumnIndex("longitude");
                    int idTitle = this.cursor.getColumnIndex("name");
                    int idUrl = this.cursor.getColumnIndex("url");
                    //int idType = this.cursor.getColumnIndex("type");
                    int idDistance = this.cursor.getColumnIndex("distance");

                    final ArrayList<PointInfo> points = new ArrayList<PointInfo>();
                    do {
                        PointInfo pointInfo = new PointInfo();
                        pointInfo.latitude = cursor.getString(idLatitude);
                        pointInfo.longitude = cursor.getString(idLongitude);
                        pointInfo.distance = cursor.getString(idDistance); // FIXME
                        pointInfo.url = cursor.getString(idUrl);
                        pointInfo.title = cursor.getString(idTitle);

                        points.add(pointInfo);

                    } while (this.cursor.moveToNext());

                    adapter = new NearbyAdapter(points, getActivity());
                    if (adapter.getCount() != 0)
                        lv.setAdapter(adapter);

                    if (lv != null) {
                        lv.setOnItemClickListener(new OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                                PointInfo p = points.get(position);
                                Intent i = new Intent(getActivity(), ArticleActivity.class);

                                i.putExtra(ConstantsAndTools.ARTICLE_ID, p.url);
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

                }
            }
        };

        //w.execute();
    }

    public void update() {
        this.downloadAndShow();
    }

    @Override
    public void onPause() {
        super.onPause();

        // Kill points downloading
        w.cancel(true);
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

		    // TODO: icons? favourites?
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
