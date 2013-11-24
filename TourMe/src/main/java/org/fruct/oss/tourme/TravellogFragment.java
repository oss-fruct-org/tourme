package org.fruct.oss.tourme;

import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TravellogFragment extends ListFragment {

    private DBHelper dbHelper;
    TravellogAdapter adapter;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.activity_travellog, container, false);

        ListView mainListView = (ListView) view.findViewById(android.R.id.list);
        mainListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                PostItemInfo item = adapter.getItem(position);
                int itemId = item.id;

                SQLiteDatabase db = dbHelper.getWritableDatabase();
                if (db == null)
                    return false;
                db.delete(ConstantsAndTools.TABLE_TRAVELLOG, "id=" + itemId, null);
                db.close();

                adapter.remove(adapter.getItem(position));
                adapter.notifyDataSetChanged();

                return true;
            }
        });

		return view;
	}

    @Override
    public void onResume() {
        super.onResume();

        // Show entries on launch and every time Travel log appears
        getAndFillTravelLogEntries();
    }

    /**
     * Get and show user-created entries as a list
     * TODO: non-console UI
     * TODO: AsyncTask?
     */
    private void getAndFillTravelLogEntries () {
        // Get all travellog entries from db
        dbHelper = new DBHelper(getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            Cursor c = db.query(ConstantsAndTools.TABLE_TRAVELLOG, null, null, null, null, null, null);

            if (c.moveToFirst()) {

                // Get text, image and location
                int idColIndex = c.getColumnIndex("id");
                int nameColIndex = c.getColumnIndex("name");
                int latitudeColIndex = c.getColumnIndex("latitude");
                int longitudeColIndex = c.getColumnIndex("longitude");
                int imageColIndex = c.getColumnIndex("image");
                int dateColIndex = c.getColumnIndex("date");

                ArrayList<PostItemInfo> points = new ArrayList<PostItemInfo>();

                do {
                    // Fill adapter with points
                    PostItemInfo point = new PostItemInfo();
                    point.text = c.getString(nameColIndex);
                    point.location = c.getString(latitudeColIndex) + " - " + c.getString(longitudeColIndex);
                    point.date = c.getString(dateColIndex);
                    point.id = c.getInt(idColIndex);

                    points.add(point);
                } while (c.moveToNext());

                adapter = new TravellogAdapter(points, getActivity());
                setListAdapter(adapter);

            } else
                Log.d("dbb", "0 rows");
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
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
		inflater.inflate(R.menu.activity_travellog, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case (R.id.travellog_add_entry):
				Intent i = new Intent(getActivity(), TravellogNewPostActivity.class);
				startActivity(i);			
				break;
			default:
				break;
		}
		return true;
			
	}

    public class PostItemInfo {
        public int id;
        public String text;
        public String location;
        public String date;
        public String imageUri;
    }

    /**
     * Aadapter represents TravelLog items
     */
    public class TravellogAdapter extends ArrayAdapter<PostItemInfo> {

        private List<PostItemInfo> itemsList;
        private Context context;

        public TravellogAdapter(List<PostItemInfo> itemsList, Context ctx) {
            super(ctx, R.layout.travellog_list_item, itemsList);
            this.itemsList = itemsList;
            this.context = ctx;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                // This a new view we inflate the new layout
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.travellog_list_item, parent, false);
            }

            TextView title = (TextView) convertView.findViewById(R.id.travellog_list_item_title);
            TextView location = (TextView) convertView.findViewById(R.id.travellog_list_item_location);
            TextView date = (TextView) convertView.findViewById(R.id.travellog_list_item_date);

            PostItemInfo p = itemsList.get(position);

            title.setText(p.text);
            location.setText(p.location);
            date.setText(p.date);

            // TODO: add image and date

            return convertView;
        }
    }

}
