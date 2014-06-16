package org.fruct.oss.tourme;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.TextView;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class FragmentWeather extends Fragment {


	TextView weatherView;



	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_weather, container, false);
        setRetainInstance(true);

		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {	

		weatherView = (TextView) view.findViewById(R.id.weather);

		GetAndFillWeather wea = new GetAndFillWeather();
		wea.execute();

	}





	private class GetAndFillWeather extends AsyncTask<Void, Integer, String> {
		
		@Override
		protected String doInBackground(Void... params) {
			try {
				Context context = getActivity();
				
				Double lon = MainActivity.currentLongitude;
				Double lat = MainActivity.currentLatitude;
				
				if (lon == null || lat == null) {
					return null;
				}
				
				Uri.Builder b = Uri.parse("http://api.openweathermap.org/data/2.5/weather").buildUpon();
				b.appendQueryParameter("lon", String.valueOf(lon));
				b.appendQueryParameter("lat", String.valueOf(lat));
				b.appendQueryParameter("units", "metric"); /// FIXME
				b.appendQueryParameter("lang", String.valueOf(ConstantsAndTools.getLocale(getActivity()))); // If isn't supported, it'll be English				
				Uri uri = b.build();
				
				Log.d("Weather URL", uri.toString()+"_");
				
				// Create a URL for the desired page
				URL url = new URL(uri.toString());
				
				// Read all the text returned by the server
				BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
				String str, tempStr = "";
				while ((str = in.readLine()) != null) {
					 tempStr = tempStr + str;
				}
				in.close();
				
				return tempStr;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return null;			
		}
		
		// Parse JSON file
		@Override
		public void onPostExecute(String result) {
            if (!isAdded()) {
                return;
            }

			String wea = getResources().getString(R.string.not_available);
			
			try {
				JSONObject resultObject = new JSONObject(result);
				
				String weatherBrief = resultObject.getJSONArray("weather").getJSONObject(0).getString("description");
				String tempMin = resultObject.getJSONObject("main").getString("temp");
				//String windSpeed = resultObject.getJSONObject("wind").getString("speed");
				
				wea = String.format("%s, %.0f°C", weatherBrief.substring(0,1).toUpperCase() + weatherBrief.substring(1),
						Double.valueOf(tempMin)); // FIXME: locale degrees and units
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("res", result+"*");
			}
			
			weatherView.setText(wea);
		}
	}



	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//this.setHasOptionsMenu(true);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.activity_home, menu);		
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
