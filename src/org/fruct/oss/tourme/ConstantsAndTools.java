package org.fruct.oss.tourme;

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
}