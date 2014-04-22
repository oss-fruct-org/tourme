package org.fruct.oss.tourme;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Get array with Wikipedia articles about N points for coordinates
 * in radius for locale * 
 * @author alexander
 *
 */
public class gets_points extends AsyncTask<String, Void, String> {

    public static Context cont = MainActivity.context;

    private double longitude, latitude;
    private int resultsCount, radius;
    private String locale;
    String token;
    int category;

    private DBHelper dbHelper;

    public Cursor cursor = null; // Cursor to existing items (if items exist)

    public gets_points(Context context, String token, double longitude, double latitude, int radius, String locale, int category) {
        this.token = token;
        this.longitude = longitude;
        this.latitude = latitude;
        this.radius = radius;
        this.locale = locale;
        this.category = category;

        this.dbHelper = new DBHelper(context);
    }

    // Необходимо получить точки из gets
    // Load points
    //Loads points for circle defined by given: category, radius, latitude and longitude; or radius, latitude and longitude; or category.
    //Request should be http://oss.fruct.org/projects/gets/service/loadPoints.php

    //Request:
    /*
        <request>
        <params>
        <auth_token>...</auth_token>
        <latitude>...</latitude>
        <longitude>...</longitude>sss
                <radius>...</radius>
        <category_id>...</category_id>
        </params>
        </request>
    */
    //  auth_token - auth token string (optional)
    //  latitude - float value
    //  longitude - float value
    //  radius - float non-negative value
    //  category_id - id of category - integer (list of category IDs can be obtained by getCategories request)
    //    Points can be retrieved by: only category_name, or latitude and longitude and radius, or combination of both previous possibilities.

    //Response:
    /*
        <response>
         <status>
        <code>...</code>
        <message>...</message>
        </status>
        <content>
        %list of points in kml format%
        </content>
        </response>
    */


    //Полученные данные необходимо отпарсить к формату json

    // Download and save categorys by token in database
    @Override
    protected String doInBackground(String... urls) {

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://oss.fruct.org/projects/gets/service/loadPoints.php");

        try {
            StringEntity se = new StringEntity("<request>" +
                    "    <params>" +
                    "        <auth_token>" + this.token + "</auth_token>" +
                    "        <latitude>" + this.latitude + "</latitude>" +
                    "        <longitude>" + this.longitude + "</longitude>" +
                    "        <radius>" + this.radius + "</radius>" +
                    "        <category_id>" + this.category + "</category_id>" +
                    "    </params>" +
                    "</request>", HTTP.UTF_8);


            se.setContentType("text/xml");
            httppost.setEntity(se);

            HttpResponse httpresponse = httpclient.execute(httppost);
            HttpEntity resEntity = httpresponse.getEntity();

            String response = EntityUtils.toString(resEntity);
            Log.e("tourme getspoints", response + "_");

            /*XmlPullParser xpp = Xml.newPullParser();
            xpp.setInput(new StringReader(response));

            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT){

                String name = null;
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        Log.e("tourme xml", "2" + xpp.getName());
                        name = xpp.getName();
                        if (name.equals("auth_token")) {
                            MainActivity.sh.edit().putString(ConstantsAndTools.GETS_TOKEN, xpp.nextText()).commit();
                            //Log.e("tourme xml", xpp.nextText() + " _");
                        }
                        break;

                }
                eventType = xpp.next();

            }*/


            //Log.e("tourme gets response", EntityUtils.toString(resEntity) + " ");
            //tvData.setText(EntityUtils.toString(resEntity));
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } /*catch (XmlPullParserException e) {
            e.printStackTrace();
        }*/

        return null;
    }

    /*
    //Request:
    <request>
    <params>
        <auth_token>...</auth_token>
    </params>
    </request>

    //Response:
    <response>
    <status>
        <code>...</code>
        <message>...</message>
    </status>
    <content>
        <categories>
            <category>
                <id>...</id>
                <name>...</name>
                <description>...</description>
                <url>...</url>
            </category>
        </categories>
    </content>
    </response>
     */
    protected String doInBackground1(String... urls) {

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://oss.fruct.org/projects/gets/service/getCategories.php");

        try {
            StringEntity se = new StringEntity("<request>" +
                    "    <params>" +
                    "        <auth_token>" + this.token + "</auth_token>" +
                    "    </params>" +
                    "</request>", HTTP.UTF_8);


            se.setContentType("text/xml");
            httppost.setEntity(se);

            HttpResponse httpresponse = httpclient.execute(httppost);
            HttpEntity resEntity = httpresponse.getEntity();

            String response = EntityUtils.toString(resEntity);
            Log.e("tourme getspoints", response + "_");

            /*XmlPullParser xpp = Xml.newPullParser();
            xpp.setInput(new StringReader(response));

            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT){

                String name = null;
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        Log.e("tourme xml", "2" + xpp.getName());
                        name = xpp.getName();
                        if (name.equals("auth_token")) {
                            MainActivity.sh.edit().putString(ConstantsAndTools.GETS_TOKEN, xpp.nextText()).commit();
                            //Log.e("tourme xml", xpp.nextText() + " _");
                        }
                        break;

                }
                eventType = xpp.next();

            }*/


            //Log.e("tourme gets response", EntityUtils.toString(resEntity) + " ");
            //tvData.setText(EntityUtils.toString(resEntity));
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } /*catch (XmlPullParserException e) {
            e.printStackTrace();
        }*/

        return null;
    }

    private void deleteDuplicates(SQLiteDatabase db) {
        // TODO
    }

    // Parse JSON file
    @Override
    public void onPostExecute(String result) {
        // Make here tricks with UI (after class extending)
    }
}
