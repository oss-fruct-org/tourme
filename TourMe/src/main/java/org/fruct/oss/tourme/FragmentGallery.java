package org.fruct.oss.tourme;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FragmentGallery extends Fragment {

	static ViewPager viewPager = null;
	static DisplayImageOptions options;

	ImageLoader imageLoader;

    public static Typeface robotoSlab;
	
	ArrayList<List<String>> imagesArray;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        setRetainInstance(true);

		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {	
		Context context = getActivity();

        TourMeGeocoder geocoder = new TourMeGeocoder(context, MainActivity.currentLatitude, MainActivity.currentLongitude);

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
		
		/*// Set onClick listeners
		View.OnClickListener l = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				buttonSwitchListener(v);
			}
		};*/




        robotoSlab = Typeface.createFromAsset(getActivity().getAssets(), "RobotoSlab-Bold.ttf");
        TextView cityName = (TextView) view.findViewById(R.id.viewPagerCityName);
        cityName.setTypeface(robotoSlab);
        try {
            String title = geocoder.getRegion();

            if (title == null)
                title = geocoder.getCity();

            if (title == null)
                title = geocoder.getCountry();

            cityName.setText(title);
        } catch (NullPointerException e) {
            Log.e("tourme geocoder", e.toString() + " ");
        }

        GetNearImages images = new GetNearImages();
        images.execute();

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
