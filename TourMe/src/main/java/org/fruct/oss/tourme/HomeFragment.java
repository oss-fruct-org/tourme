package org.fruct.oss.tourme;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class HomeFragment extends Fragment {

	public static Context context = null;
	static ViewPager viewPager = null;
	static DisplayImageOptions options;

    ScrollView mainView;

	TextView currencyView;
    RelativeLayout currencyViewMain;
	TextView phraseView;
    RelativeLayout phraseViewMain;
	TextView weatherView;

    TextView countryNameView;
    TextView countryNameMoreView;
    TextView populationView;
    TextView phoneCodeView;
    TextView domainVIew;
    TextView langsView;

    TourMeGeocoder geocoder = null;
	
	ImageLoader imageLoader;

    public static Typeface robotoSlab;
	
	ArrayList<List<String>> imagesArray;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.activity_home, container, false);
        setRetainInstance(true);

		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {	
		context = getActivity();

        geocoder = new TourMeGeocoder(context, MainActivity.currentLatitude, MainActivity.currentLongitude);

		options = new DisplayImageOptions.Builder()
			.showImageForEmptyUri(R.drawable.ic_action_map_nearby)
			.showImageOnFail(R.drawable.ic_action_filter)
			.resetViewBeforeLoading(true)
			.cacheOnDisc(true)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.displayer(new FadeInBitmapDisplayer(300))
			.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
			.build();
		
		imagesArray = new ArrayList<List<String>>();
		
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
				
		viewPager = (ViewPager) view.findViewById(R.id.viewPager);
		viewPager.setAdapter(new ImageAdapter(getActivity(), imagesArray));
		viewPager.setCurrentItem(0);
		
		final int delayTime = 7000;
		final Handler h = new Handler();
		
		// Loop sliding
		final Runnable r = new Runnable() {
			@Override
			public void run() {
				int current = viewPager.getCurrentItem();
				
				// Slide to first at the end
				if (current == viewPager.getAdapter().getCount()-1)
					current = -1;
				
				viewPager.setCurrentItem(current + 1);
				h.postDelayed(this, delayTime);
		}};

		// Stop sliding when touching and continue after
		viewPager.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
                viewPager.getParent().requestDisallowInterceptTouchEvent(true);
				h.removeCallbacks(r);
				// If event is end, wait at current page for 2 dealyTime intervals
				if (event.getAction() == MotionEvent.ACTION_UP) {
                    h.postDelayed(r, delayTime*3);
                    viewPager.getParent().requestDisallowInterceptTouchEvent(false);
                }
                return false;
			}
		});
		
		// Run sliding after delayTime
		h.postDelayed(r, delayTime);	
		
		// Set onClick listeners
		View.OnClickListener l = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				buttonSwitchListener(v);
			}
		};




        robotoSlab = Typeface.createFromAsset(getActivity().getAssets(), "RobotoSlab-Bold.ttf");
        TextView cityName = (TextView) view.findViewById(R.id.viewPagerCityName);
        cityName.setTypeface(robotoSlab);
        try {
            //TourMeGeocoder geocoder = new TourMeGeocoder(getActivity(), MainActivity.currentLatitude, MainActivity.currentLongitude);
            cityName.setText(geocoder.getRegion());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        mainView = (ScrollView) view.findViewById(R.id.fragment_home_main);

		currencyView = (TextView) view.findViewById(R.id.currency);
        currencyViewMain = (RelativeLayout) view.findViewById(R.id.fragment_home_currency_view);
		phraseView = (TextView) view.findViewById(R.id.phrase);
        phraseViewMain = (RelativeLayout) view.findViewById(R.id.fragment_home_phrase);
		weatherView = (TextView) view.findViewById(R.id.weather);

        countryNameView = (TextView) view.findViewById(R.id.fragment_home_country_name);;
        countryNameMoreView = (TextView) view.findViewById(R.id.fragment_home_country_name_more);
        populationView = (TextView) view.findViewById(R.id.fragment_home_country_population);
        phoneCodeView = (TextView) view.findViewById(R.id.fragment_home_country_phone_code);
        domainVIew = (TextView) view.findViewById(R.id.fragment_home_country_domain);
        langsView = (TextView) view.findViewById(R.id.fragment_home_country_language);
		
		randomPhrase();

        GetNearImages images = new GetNearImages();
        images.execute();
		
		GetAndFillCurrency cur = new GetAndFillCurrency();
		cur.execute();
		
		GetAndFillWeather wea = new GetAndFillWeather();
		wea.execute();

        GetAndFIllCountryInfo country = new GetAndFIllCountryInfo();
        country.execute();

        RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.fragment_home_currency);
	}


    /*public static void expandAnimation(final View v) {
        v.measure(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        final int targtetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = v.getHeight();
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? RelativeLayout.LayoutParams.WRAP_CONTENT
                        : (int)(targtetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targtetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }
    */


    private class GetAndFIllCountryInfo extends  AsyncTask<Void, Integer, String> {

        //TourMeGeocoder geocoder;

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


	
	private class GetAndFillCurrency extends AsyncTask<Void, Integer, String> {

        private String from;
        private String to;

		@Override
		protected String doInBackground(Void... params) {
			try {
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
	
	
	private class GetNearImages extends AsyncTask<Void, Integer, String> {

		@Override
		protected String doInBackground(Void... params) {
			try {
                //
                // TODO: write 'isAdded' statement
                //

				Context context = getActivity();
				Double lon = MainActivity.currentLongitude; // TODO FIXME TODO What if location is unavailable?
				Double lat = MainActivity.currentLatitude;
				Log.e("lat", lon + lat + "*");
				
				// See more: http://www.panoramio.com/api/data/api.html
				//http://www.panoramio.com/map/get_panoramas.php?set=public&from=0&to=5&minx=-180&miny=-90&maxx=180&maxy=90&size=medium&mapfilter=true
				
				Uri.Builder b = Uri.parse("http://www.panoramio.com/map/get_panoramas.php").buildUpon();
				b.appendQueryParameter("set", "public");
				b.appendQueryParameter("from", "0");
				b.appendQueryParameter("to", ConstantsAndTools.IMAGES_TO_DOWNLOAD_AND_SHOW + "");
				b.appendQueryParameter("mapfilter", "true");
				b.appendQueryParameter("size", "medium");
				// Workaround: panoramio api doesn't have 'radius' parameter, will use +- 0.5 degree (~50 km)
				b.appendQueryParameter("minx", String.valueOf(lon - 0.5));
				b.appendQueryParameter("maxx", String.valueOf(lon + 0.5));
				b.appendQueryParameter("miny", String.valueOf(lat - 0.5));
				b.appendQueryParameter("maxy", String.valueOf(lat + 0.5));
				Uri uri = b.build();
				
				// Create a URL for the desired page
			    URL url = new URL(uri.toString());

			    // Read all the text returned by the server
			    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			    String str, tempStr = "";
				while ((str = in.readLine()) != null) {
					 tempStr = tempStr + str;
				}
				in.close();
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
			
			imagesArray = new ArrayList<List<String>>();
			
			try {
				JSONArray photos = new JSONObject(result).getJSONArray("photos");
				for (int i = 0; i < photos.length()-1; i++) {
					// EXTRA TODO: see attribution requirements http://www.panoramio.com/api/data/api.html
					JSONObject photoObject = photos.getJSONObject(i);
					imagesArray.add(Arrays.asList(photoObject.getString("photo_file_url"), photoObject.getString("photo_title")));
				}
				viewPager.setAdapter(new ImageAdapter(getActivity(), imagesArray));
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("Near images JSON:", result+"*");
			}
		}
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//this.setHasOptionsMenu(true);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.activity_home, menu);		
	}
	

	/**
	 * Walk around: executes an action for TextView onClick event
	 * @param v View emited onClick event (TextViews)
	 */
	private void buttonSwitchListener(View v) {
		
		FragmentManager fm = null;
		Fragment f = null;
		FragmentTransaction ft = null;
		
		/*switch(v.getId()) {
			case(R.id.openTravelpedia):
				// TODO
				break;
			case(R.id.openNearby):
				f = new NearbyFragment();
				break;
			case(R.id.openPracticalInfo):
				// TODO
				break;
			case(R.id.openPhrasebook):
				// TODO
				break;
		}*/
		
		if (f != null) {
			fm = getFragmentManager();
			ft = fm.beginTransaction();
			ft.replace(R.id.fragment_container, f);
			ft.addToBackStack(null);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();
		}		
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {

		FragmentManager fm = null;
		Fragment f = null;
		FragmentTransaction ft = null;
		
		switch(item.getItemId()) {
			case(R.id.menu_map):
				f = new MapFragment();
				break;
			case (R.id.menu_favourites):
				f = new FavouritesFragment();
				break;
			default:
				break;
		}

		if (f != null) {
			fm = getFragmentManager();
			ft = fm.beginTransaction();
			ft.replace(R.id.fragment_container, f);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();
		}
		
		return super.onOptionsItemSelected(item);		
	}
	

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	

	@Override
	public void onStart() {
		super.onStart();
		// During startup, check if there are arguments passed to the fragment.
		// onStart is a good place to do this because the layout has already
		// been applied to the fragment at this point so we can safely call the
		// method below that sets the article text.
		
		// Bundle args = getArguments();
	}
	

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	

	
	/**
	 * Image adapter for gallery at main screen
	 * @author alexander
	 * TODO: autorotate
	 */
	public class ImageAdapter extends PagerAdapter {
		Context context;
		private ArrayList<List<String>> images;
		private LayoutInflater inflater;
	
		ImageAdapter(Context context, ArrayList<List<String>> imagesArray) {
			this.context = context;
			this.images = imagesArray;
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		@Override
		public int getCount() {
			return images.size();
		}
	
		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}
	
		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);
	        ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
	        imageView.setPadding(0, 0, 0, 0);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
	        final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);
	
	        // Uncomment this to show image captions
	        //TextView imageCaption = (TextView) imageLayout.findViewById(R.id.image_caption);
	        //imageCaption.setText(images.get(position).get(1));
	        
	        
	        imageLoader.displayImage(images.get(position).get(0), imageView, options, new SimpleImageLoadingListener() {
	                @Override
	                public void onLoadingStarted(String imageUri, View view) {
	                        spinner.setVisibility(View.VISIBLE);
	                }
	
	                @Override
	                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
	                        String message = null;
	                        switch (failReason.getType()) {
	                                case IO_ERROR:
	                                        message = "Input/Output error";
	                                        break;
	                                case DECODING_ERROR:
	                                        message = "Image can't be decoded";
	                                        break;
	                                case NETWORK_DENIED:
	                                        message = "Downloads are denied";
	                                        break;
	                                case OUT_OF_MEMORY:
	                                        message = "Out Of Memory error";
	                                        break;
	                                case UNKNOWN:
	                                        message = "Unknown error";
	                                        break;
	                        }
	
	                        spinner.setVisibility(View.GONE);
	                        Log.e("super adapter", "failed"+message);
	                }
	
	                @Override
	                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
	                        spinner.setVisibility(View.GONE);
	                }
	        });
	
	        ((ViewPager) view).addView(imageLayout, 0);
	        return imageLayout;        

		}
	
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}
	}

}
