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
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class FragmentCountry extends Fragment {

    TourMeGeocoder geocoder;

    TextView countryNameView;
    TextView countryNameMoreView;
    TextView populationView;
    TextView phoneCodeView;
    TextView domainVIew;
    TextView langsView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_country, container, false);
        setRetainInstance(true);

		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {

        geocoder = new TourMeGeocoder(getActivity(), MainActivity.currentLatitude, MainActivity.currentLongitude);

        countryNameView = (TextView) view.findViewById(R.id.fragment_home_country_name);;
        countryNameMoreView = (TextView) view.findViewById(R.id.fragment_home_country_name_more);
        populationView = (TextView) view.findViewById(R.id.fragment_home_country_population);
        phoneCodeView = (TextView) view.findViewById(R.id.fragment_home_country_phone_code);
        domainVIew = (TextView) view.findViewById(R.id.fragment_home_country_domain);
        langsView = (TextView) view.findViewById(R.id.fragment_home_country_language);

        GetAndFIllCountryInfo country = new GetAndFIllCountryInfo();
        country.execute();
	}




    private class GetAndFIllCountryInfo extends  AsyncTask<Void, Integer, String> {

        @Override
        protected String doInBackground(Void... voids) {

            try {
                Context context = getActivity();
                Double lat = MainActivity.currentLatitude;
                Double lon = MainActivity.currentLongitude;

                if (lon == null || lat == null) {
                    return null;
                }

                //geocoder = new TourMeGeocoder(context, lat, lon);
                String countryCode = geocoder.getCountryCode();

                String preUrl = "http://restcountries.eu/rest/v1/alpha?codes=" + countryCode;
                Uri.Builder b = Uri.parse(preUrl).buildUpon();
                Uri uri = b.build();

                Log.e("Weather URL", uri.toString()+"_");

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

            try {
                JSONObject resultObject = new JSONArray(result).getJSONObject(0);

                // Use localized country name from geocoder
                String countryName = geocoder.getCountry();
                countryNameView.setText(countryName);

                // Get alternative spellings of country name
                JSONArray altSpellings = resultObject.getJSONArray("altSpellings");
                String strAltSpellings = null;
                for (int i = 0; i < altSpellings.length() - 1; i++) {
                    String name = altSpellings.getString(i);
                    if (name.length() > 2)
                        if (strAltSpellings == null)
                            strAltSpellings = name;
                    else
                        strAltSpellings = strAltSpellings + ", " + name;
                }
                countryNameMoreView.setText(strAltSpellings);

                // Get population
                String population = resultObject.getInt("population") + "";
                populationView.setText(population);

                // Get phonde code
                JSONArray phoneCodes = resultObject.getJSONArray("callingCodes");
                String strPhoneCodes = null;
                for (int i = 0; i < phoneCodes.length(); i++) {
                    String code = phoneCodes.getString(i);
                    if (strPhoneCodes == null)
                        strPhoneCodes = code;
                    else
                        strPhoneCodes = strPhoneCodes + ", " + code;
                }
                phoneCodeView.setText(strPhoneCodes);

                // Get domain
                JSONArray domain = resultObject.getJSONArray("topLevelDomain");
                String strDomain = null;
                for (int i = 0; i < domain.length(); i++) {
                    String dom = domain.getString(i);
                    if (strDomain == null)
                        strDomain = dom;
                    else
                        strDomain = strDomain + ", " + dom;
                }
                domainVIew.setText(strDomain);

                // Get languages
                JSONArray lang = resultObject.getJSONArray("languages");
                String strLangs = null;
                for (int i = 0; i < lang.length(); i++) {
                    String lan = lang.getString(i);
                    if (strLangs == null)
                        strLangs = lan;
                    else
                        strLangs = strLangs + ", " + lan;
                }
                langsView.setText(strLangs);

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("res", result+"*");
            }
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
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	



}
