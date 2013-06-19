package org.fruct.oss.tourme;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

public class MapFragment extends Fragment {

	private ArrayList<Integer> selectedCategories;

	private WebView myWebView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.activity_map, container, false);

		return view;
	}
	

	/*
	 * Init webView with map after the view creation It'll not work in onCreate
	 * etc
	 */
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		myWebView = (WebView) view.findViewById(R.id.mapview);

		// FIXME: Will it work fine?
		String url = Environment.getExternalStorageDirectory()
				+ "/osmdroid/Mapnik";
		myWebView.getSettings().setJavaScriptEnabled(true);
		myWebView.loadUrl("file:///android_asset/map.html");
		myWebView.addJavascriptInterface(new AppInterface(getActivity()), "Android"); // TODO FIXME
		// url = "http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png";
		Log.v("Map tiles URL: ", url);
		myWebView.loadUrl("javascript:setUrl(" + url + ")");
	}
	
	
	public void switchOnlineOfflineMode() {
		int mode = 0; // TODO: get current mode
		String urlToLoad = "javascript:";
		
		if (mode == 0)
			urlToLoad = urlToLoad + "setOnlineLayer();";
		else
			urlToLoad = urlToLoad + "setOfflineLayer();";
			
		myWebView.loadUrl(urlToLoad);		
	}
	
	
	/**
	 * Bind this javascript interface to make
	 * interaction between JS and Java (predefined functions)
	 * @author alexander
	 *
	 */
	public class AppInterface {
		Context cont;
		
	    AppInterface(Context c) {
	        cont = c;
	    }

	    /** Show a toast from the web page */
	    @JavascriptInterface
	    public void showToast(String toast) {
	        Toast.makeText(cont, toast, Toast.LENGTH_SHORT).show();
	    }
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
	

	public void addMarker(String group, String lat, String lon,
			String description) {
		// FIXME: group? What group? What will happen when group isn't exist?
		myWebView.loadUrl("javascript:addMarker('" + group + "', " + lat + ", "
				+ lon + ", '" + description + "');"); // TODO: what if aposotrophe is in name?
		Log.e("js", "javascript:addMarker(" + group + ", " + lat + ", " + lon
				+ ", '" + description + "');");
	}
	

	/**
	 * Categories chooser
	 */
	@SuppressLint("ValidFragment")
	public class PointsCategoriesDialog extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			selectedCategories = new ArrayList<Integer>(); // Where we track the
															// selected items
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			// Set the dialog title
			builder.setTitle(R.string.map_points_categories_title)

					// TODO: get selected items by default or from
					// SharedPreferences
					// boolean[] selectedCategoriesByDefault;

					// Specify the list array, the items to be selected by
					// default (null for none),
					// and the listener through which to receive callbacks when
					// items are selected
					.setMultiChoiceItems(R.array.map_points_categories, null,
							new DialogInterface.OnMultiChoiceClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which, boolean isChecked) {
									if (isChecked) {
										// If the user checked the item, add it
										// to the selected items
										selectedCategories.add(which);
									} else if (selectedCategories
											.contains(which)) {
										// Else, if the item is already in the
										// array, remove it
										selectedCategories.remove(Integer
												.valueOf(which));
									}
								}
							})

					// Set the action buttons
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									// User clicked OK, so save the
									// mSelectedItems results somewhere
									// or return them to the component that
									// opened the dialog
									// TODO: do something
									// System.out.print("asdasda");
									// myWebView.loadUrl("javascript:setZoom(1)");
									String[] testArray = getResources()
											.getStringArray(
													R.array.map_points_categories);
									System.out.print(testArray);
								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									return;
								}
							});

			return builder.create();
		}
	}
	
	
	/* YandexPoints getAndShowPoints = new
	 * YandexPoints("банкоматы петрозаводск", 20) {
	 * 
	 * @Override public void onPostExecute(String result) { ArrayList<PointInfo>
	 * points = this.openAndParse();
	 * 
	 * for (int i = 0; i < points.size(); i++) { try { PointInfo curPoint =
	 * points.get(i); addMarker("sight-2", curPoint.lat, curPoint.lon,
	 * curPoint.name); // TODO: what of no name, but point must be on map?
	 * Log.e("Marker", curPoint.name); } catch (Exception e) {
	 * Log.e("Error showing point", "!!"); } } } };
	 * 
	 * getAndShowPoints.execute();
	 * 
	 * break; case R.id.map_menu_nearby: Toast.makeText(cont, "Jusst a test",
	 * Toast.LENGTH_SHORT).show(); //String Urlik =
	 * "http://api.wikilocation.org/articles?lat="+ // 61.78333 + "&lng=" +
	 * 34.33333 + "&limit=2&radius=3000&locale=ru&format=json";
	 * //FindWikiArticle dwn = new FindWikiArticle(); //dwn.execute(Urlik);
	 * 
	 * // FIXME Intent intent = new Intent(this, NearbyActivity.class);
	 * startActivity(intent); break; }
	 * 
	 * 
	 * return super.onOptionsItemSelected(item); }
	 */

	/*// TODO FIXME SCREW ECLIPSE
	 * class FindWikiArticle extends AsyncTask<String, Integer, String> {
	 * 
	 * HttpResponse response; String jsonString = null;
	 * 
	 * @Override protected String doInBackground(String... sUrl) { try { String
	 * url = sUrl[0];
	 * 
	 * StringBuilder builder = new StringBuilder(); HttpClient client = new
	 * DefaultHttpClient(); HttpGet httpGet = new HttpGet(url); HttpResponse
	 * response = client.execute(httpGet); StatusLine statusLine =
	 * response.getStatusLine(); int statusCode = statusLine.getStatusCode(); if
	 * (statusCode == 200) { HttpEntity entity = response.getEntity();
	 * InputStream content = entity.getContent(); BufferedReader reader = new
	 * BufferedReader( new InputStreamReader(content)); String line; while
	 * ((line = reader.readLine()) != null) { builder.append(line); } jsonString
	 * = builder.toString(); } else { Log.e("Err", "Failed to download file"); }
	 * 
	 * } catch (Exception e) { Log.e("ERROR downloading file", e.getMessage());
	 * // Show Toast message on UI thread
	 * 
	 * runOnUiThread(new Runnable() { public void run() { Toast.makeText(cont,
	 * "Klingon: Fyah REerf NEtwfk Err", Toast.LENGTH_SHORT).show(); } });
	 * 
	 * }
	 * 
	 * return null; }
	 * 
	 * @Override protected void onCancelled() { Log.i("DWNLD", "cancelled"); }
	 * 
	 * @Override protected void onPreExecute() { super.onPreExecute();
	 * Log.i("DWNLD", "preExec"); }
	 * 
	 * @Override protected void onProgressUpdate(Integer... progress) {
	 * super.onProgressUpdate(progress); Log.i("DWNLD", "ProgressUpd"); // TODO
	 * Set progress }
	 * 
	 * @Override protected void onPostExecute(String str) {
	 * super.onPostExecute("OK");
	 * 
	 * JSONObject json;
	 * 
	 * String articleUrl = null; String articleTitle = null; try { json = new
	 * JSONObject(jsonString); JSONArray elems = json.getJSONArray("articles");
	 * JSONObject link = elems.getJSONObject(0);
	 * 
	 * articleUrl = link.get("mobileurl").toString(); articleTitle =
	 * link.get("title").toString(); Log.e("url", articleUrl); Log.e("title",
	 * articleTitle); } catch (JSONException e) { }
	 * 
	 * if (articleUrl != null) { // Open ArticleActivity for an article Intent
	 * myInt = new Intent(cont, ArticleActivity.class);
	 * myInt.putExtra(ConstantsAndTools.ARTICLE_ID, articleUrl);
	 * myInt.putExtra(ConstantsAndTools.ARTICLE_TITLE, articleTitle);
	 * startActivity(myInt); }
	 * 
	 * } }
	 */
}