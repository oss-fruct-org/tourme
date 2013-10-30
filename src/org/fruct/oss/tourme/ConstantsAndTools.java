package org.fruct.oss.tourme;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Currency;
import java.util.Locale;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

public class ConstantsAndTools {
	
	// SharedPreferences
	public static String SHARED_PREFERENCES = "TOURME_PREFERENCES";
	public static String ONLINE_MODE = "ONLINE_MODE";
	public static String SOCICAL_NETWORKS_CONNECTED = "SOCICAL_NETWORKS_CONNECTED";
	public static String IS_FIRST_LAUNCH = "IS_FIRST_LAUNCH";
	
	// Intent extras
	public static String ARTICLE_ID = "ARTICLE_ID";
	public static String ARTICLE_TITLE = "ARTICLE_TITLE";
	public static String ARTICLE_COORDINATES = "ARTICLE_COORDINATES";
	
	public static String FRAGMENT_TO_OPEN = "FRAGMENT_TO_OPEN";
	
	// TAG
	public static String TAG = "org.fruct.oss.tourme";
	
	// Gallery
	public static int IMAGES_TO_DOWNLOAD_AND_SHOW = 10; // Images to download for home screen gallery
	
	// Wikipedia articles
	public static int ARTICLES_AMOUNT = 500; 
	public static int ARTICLES_MAXIMUM_PER_TIME = 50; // 50 items is maximum per one iteration (API limitation)
	public static int ARTICLES_RADIUS = 20000; // 20 km is maximum (API limitation)
	public static final String[] AVAILABLE_LOCALES = new String[] {"ar", "bg", "ca", "cs", "da", "de", "en", "eo",
		"es", "fa", "fi", "fr", "he", "hu", "id", "it", "ja", "ko", "lt", "ms", "nl", "no", "nn", "pl", "pt", "ro",
		"ru", "sk", "sl", "sr", "sv", "tr", "uk", "vi", "vo", "war", "zh"};
	
	
	/**
	 * Convert kilometers to miles
	 * @param kms value in kilometers
	 * @return double value in miles
	 */
	public static double kilometersToMiles(double kms) {
		return kms*0.62137;
	}


	/**
	 * Get 'Last-Modified' from HTTP header in separated thread
	 * @return String with data or "no updates" 
	 */
	/*public String getRemoteFileModificationDate(String requestURL) {
		String x = null;
	    ExecutorService es = Executors.newSingleThreadExecutor();
	    Future<String> result = es.submit(new Callable<String>() {
	    public String call() throws Exception {
	    	try {
				
				URL url = new URL (requestURL);
				URLConnection connector = url.openConnection();
				connector.connect();
	
				return connector.getHeaderField("Last-Modified");	
			} catch (Exception e) {
				Log.e("ERROR retrieving HTTP header", e.getMessage());
				return "No updates";
			}	        
	    }
	});
	*/


	/**
	 * Check for network availability
	 * @return true if network connection is available else false
	 */
	public static boolean isOnline(Context context) {
	    ConnectivityManager cm =
	        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}


	/** 
	 * Check for SD card
	 * @return true if SD available, else false 
	 */
	public boolean checkForSD() {
		String state = Environment.getExternalStorageState();
		
		if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
			return false;

		if (Environment.MEDIA_MOUNTED.equals(state))
			return true;

		return false;		
	}
	
	/**
	 * Get device language
	 * @param context
	 * @return language code
	 */
	public static String getLocale(Context context) {
		Locale current = context.getResources().getConfiguration().locale;
		String lang = current.getLanguage();		
		
		if (Arrays.asList(AVAILABLE_LOCALES).contains(lang))		
			return lang;
		
		// If locale is not supported
		return "en";
	}
	

	/**
	 * Get device currency
	 * @param context
	 * @return currency code
	 */
	public static String getDeviceCurrency(Context context) {
		Currency currency = Currency.getInstance(Locale.getDefault());		
		
		return currency.getCurrencyCode();
	}

	   /**
     * Load JSON from assets folder
     * @param filename name of file to load
     * @param cont context
     * @return loaded string
     */
    public static String loadJSONFromAsset(String filename, Context cont) {
        String json;
        try {
            InputStream is = cont.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        return json;
    }
	
	
}