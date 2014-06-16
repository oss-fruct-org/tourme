package org.fruct.oss.tourme;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class FragmentCurrency extends Fragment {


	TextView currencyView;
    RelativeLayout currencyViewMain;

    TourMeGeocoder geocoder;


	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_currency, container, false);
        setRetainInstance(true);

		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d("tourme", "Currency info...");

        Context context = getActivity();
        geocoder = new TourMeGeocoder(context, MainActivity.currentLatitude, MainActivity.currentLongitude);

		currencyView = (TextView) view.findViewById(R.id.currency);
        currencyViewMain = (RelativeLayout) view.findViewById(R.id.fragment_home_currency_view);

		GetAndFillCurrency cur = new GetAndFillCurrency();
		cur.execute();
	}


	
	private class GetAndFillCurrency extends AsyncTask<Void, Integer, String> {

        private String from;
        private String to;

		@Override
		protected String doInBackground(Void... params) {
			try {
                Log.d("tourme", "Curerncy URL:" + " started");
				Context context = getActivity();

                //TourMeGeocoder geocoder = new TourMeGeocoder(getActivity(), MainActivity.currentLatitude, MainActivity.currentLongitude);
                to = String.valueOf(geocoder.getDeviceCurrency());
                from = String.valueOf(geocoder.getCurrency());
                String preUrl = "http://www.freecurrencyconverterapi.com/api/convert?q=" + from +"-" + to + "&compact=y";
                Uri.Builder b = Uri.parse(preUrl).buildUpon();

                // If user is in his country, hide currency view
                if (to.equals(from))
                    return null;

				Uri uri = b.build();
				
				// Create a URL for the desired page
				URL url = new URL(uri.toString());

                Log.d("tourme", "Curerncy URL:" + url.toString());

				// Read all the text returned by the server
				BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
				String str = in.readLine(); // Only 1 line of text
				in.close();
				
				return str;
			} catch (Exception e) {
				e.printStackTrace();
                Log.e("tourme", e.toString());
			}
			
			return null;			
		}
		
		// Parse JSON file
		@Override
		public void onPostExecute(String result) {
            if (!isAdded())
                return;

            // Hide view if nothing to show
            if (result == null) {
                currencyViewMain.setVisibility(View.GONE);
                return;
            }

			String currency = getResources().getString(R.string.not_available);
			
			try {
                JSONObject resultObject = new JSONObject(result);
    				//String from = resultObject.getString("from");
				//String to = resultObject.getString("to");
                JSONObject val = resultObject.getJSONObject(from+"-"+to);
				double rate = val.getDouble("val");
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
	}

	

	

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	

	@Override
	public void onStart() {
		super.onStart();
	}
	

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

}
