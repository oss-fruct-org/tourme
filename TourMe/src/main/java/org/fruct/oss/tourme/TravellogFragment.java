package org.fruct.oss.tourme;

import android.app.Fragment;
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

public class TravellogFragment extends Fragment {

    private DBHelper dbHelper;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.activity_travellog, container, false);

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

                do {
                    Log.d("db",
                            "name = " + c.getString(nameColIndex)
                                    + " " + c.getString(latitudeColIndex) + " " + c.getString(longitudeColIndex) + " " + c.getString(imageColIndex)
                    );

                } while (c.moveToNext());
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

}
