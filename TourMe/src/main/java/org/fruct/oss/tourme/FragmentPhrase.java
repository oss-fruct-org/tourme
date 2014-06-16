package org.fruct.oss.tourme;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FragmentPhrase extends Fragment {


	TextView phraseView;
    RelativeLayout phraseViewMain;

    TourMeGeocoder geocoder = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_phrase, container, false);
        setRetainInstance(true);

		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {

        geocoder = new TourMeGeocoder(getActivity(), MainActivity.currentLatitude, MainActivity.currentLongitude);

		phraseView = (TextView) view.findViewById(R.id.phrase);
        phraseViewMain = (RelativeLayout) view.findViewById(R.id.fragment_home_phrase);

		randomPhrase();
	}


	
	/**
	 * Show random phrase
	 */
	private void randomPhrase() {

        final DBHelper dbHelper = new DBHelper(getActivity());

        new Thread(new Runnable() {
            public void run() {
                try {
                    // Load phrases from file to db at first launch
                    if (MainActivity.firstLaunch) {

                        String langFrom = geocoder.getDeviceLocaleCode();
                        String langTo = geocoder.getCountryCode();

                        // No phrasebook in current country
                        if (langFrom.equals(langTo))
                            throw new NullPointerException("Languages are the same");

                        InputStream is = null;
                        String filename = "phrasebook-" + langFrom + "-" + langTo;
                        try {
                            is = getActivity().getAssets().open(filename);
                        } catch (NullPointerException e) {
                            Log.e("tourme", e.toString());
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            // If file (e.g. en-de) not found, then try to open vice versa file (e.g. de-en)
                            filename = "phrasebook-" + langTo + "-" + langFrom;
                            is = getActivity().getAssets().open(filename);
                        }

                        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                        SQLiteDatabase db = dbHelper.getWritableDatabase();

                        String line;
                        while ((line = reader.readLine()) != null) {
                            String[] phrases = line.split("\",\"");

                            ContentValues cv = new ContentValues();
                            cv.put("lang1", phrases[0]); // FIXME
                            cv.put("lang2", phrases[1]); // FIXME

                            if (db != null) {
                                db.insert(ConstantsAndTools.TABLE_PHRASEBOOK, null, cv);
                            }
                        }

                        if (db != null) {
                            db.close();
                        }
                    }

                    // When and if phrases are loaded, show random phrase
                    SQLiteDatabase db = dbHelper.getReadableDatabase();
                    String[] columns = new String[] {"lang1", "lang2"};
                    Cursor cursor = db.query(ConstantsAndTools.TABLE_PHRASEBOOK, columns, null, null, null, null, null);
                    int phraseNumber = (int) (Math.random()*cursor.getCount());
                    cursor.move(phraseNumber);
                    final String phraseOrig = cursor.getString(cursor.getColumnIndex("lang1"));
                    final String phraseFore = cursor.getString(cursor.getColumnIndex("lang2"));
                    cursor.close();
                    db.close();

                    // Workaround to change layout size automatically
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            phraseView.setText(phraseOrig.replace("\"", "") + " — " + phraseFore.replace("\"", "")); // TODO: to mdash
                        }
                    });

                } catch (Exception e) {
                    phraseViewMain.setVisibility(View.GONE);
		    	    e.printStackTrace();
                    Log.e("tourme", e.toString());
		      }
		    }
		  }).start();
	}



	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	


	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	



}
